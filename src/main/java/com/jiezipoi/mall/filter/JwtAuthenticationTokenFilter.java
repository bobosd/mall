package com.jiezipoi.mall.filter;

import com.jiezipoi.mall.config.JwtConfig;
import com.jiezipoi.mall.entity.User;
import com.jiezipoi.mall.exception.UnregisteredRefreshTokenException;
import com.jiezipoi.mall.service.JwtService;
import com.jiezipoi.mall.service.UserService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.annotation.Nonnull;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class JwtAuthenticationTokenFilter extends OncePerRequestFilter {
    private final HashSet<String> resourcePaths;
    private final JwtConfig jwtConfig;
    private final JwtService jwtService;
    private final UserService userService;

    public JwtAuthenticationTokenFilter(HashSet<String> resourcePaths,
                                        JwtConfig jwtConfig,
                                        @Lazy JwtService jwtService,
                                        @Lazy UserService userService) {
        this.resourcePaths = resourcePaths;
        this.jwtConfig = jwtConfig;
        this.jwtService = jwtService;
        this.userService = userService;
    }

    /**
     * 先获得access token和refresh token。如果refresh token为空（过期了）那么肯定没有必要验证了，直接放行
     * 如果有那么就验证access token，如果正确就通过验证，否则就通过refresh token重新生成一个access token并且通过验证
     * 如果此时refresh token临近过期则顺便一起刷新并且更新数据库中的refresh token。
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    @Nonnull HttpServletResponse response,
                                    @Nonnull FilterChain filterChain) throws ServletException, IOException {
        Cookie[] cookies = request.getCookies();
        String accessToken = findCookieValueByName(cookies, jwtConfig.getAccessCookieName());
        String refreshToken = findCookieValueByName(cookies, jwtConfig.getRefreshCookieName());

        try {
            if (accessToken == null) {
                Claims rtClaims = jwtService.parseJWT(refreshToken);
                String email = rtClaims.getSubject();
                accessToken = regenerateAccessJwtCookie(email, refreshToken); //throw exception if refresh token not valid
                setAccessTokenCookie(accessToken, response);
                if (isRefreshTokenNeedToRenew(rtClaims)) {
                    jwtService.invalidateRefreshToken(email, refreshToken);
                    User user = userService.getUserByEmail(email);
                    String renewRefreshToken = jwtService.generateAndStoreRefreshToken(user);
                    setRefreshTokenCookie(renewRefreshToken, response);
                }
            }
            Claims accessClaims = jwtService.parseJWT(accessToken);
            String email = accessClaims.getSubject();
            List<GrantedAuthority> authorities = parseAuthorities(accessClaims);
            UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken(email, null, authorities);
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            filterChain.doFilter(request, response);
        } catch (ExpiredJwtException e) { //发送请求没过期，在服务器过期了。
            System.err.println("Expired at processing?");
            filterChain.doFilter(request, response);
        } catch (JwtException | UnregisteredRefreshTokenException | NullPointerException e) {
            resetJwtCookies(response);
            filterChain.doFilter(request, response);
        }
    }
    private List<GrantedAuthority> parseAuthorities(Claims claims) {
        Object o = claims.get("permission");
        List<?> list = new ArrayList<>((List<?>) o);
        return list.stream()
                .map(Object::toString)
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getServletPath();
        return isResourcePath(path);
    }

    public boolean isResourcePath(String url) {
        return resourcePaths.stream().anyMatch(url::startsWith);
    }

    /**
     * 寻找对应的Cookie，如果没有则为null
     * @param cookies 请求的cookie数组
     * @param cookieName 需要的cookie名称
     * @return 对应的cookie或者null
     */
    private String findCookieValueByName(Cookie[] cookies, String cookieName) {
        if (cookies == null)
            return null;

        Optional<Cookie> optionalCookie = Arrays.stream(cookies)
                .filter(cookie -> cookie.getName().equals(cookieName))
                .findFirst();
        if (optionalCookie.isPresent()) {
            Cookie cookie = optionalCookie.get();
            return cookie.getValue();
        } else {
            return null;
        }
    }

    /**
     * 用于更新access token，依赖refresh token是否有效，需要用到email用于在数据库中查询是否有该token
     * @param email 用户的email
     * @param refreshToken 用户携带的refresh token
     * @return 新的access token
     */
    private String regenerateAccessJwtCookie(String email, String refreshToken) throws UnregisteredRefreshTokenException {
        if (!jwtService.isRegisteredRefreshToken(email, refreshToken)) {
            throw new UnregisteredRefreshTokenException();
        }
        User user = userService.getUserByEmail(email);
        return jwtService.generateAccessToken(user);
    }

    private void setAccessTokenCookie(String accessToken, HttpServletResponse response) {
        ResponseCookie accessTokenCookie = jwtService
                .createJwtCookie(accessToken, jwtConfig.getAccessCookieName(), jwtConfig.getAccessTokenAge());
        response.addHeader(HttpHeaders.SET_COOKIE, accessTokenCookie.toString());
    }

    private void setRefreshTokenCookie(String refreshToken, HttpServletResponse response) {
        ResponseCookie refreshTokenCookie = jwtService
                .createJwtCookie(refreshToken, jwtConfig.getRefreshCookieName(), jwtConfig.getRefreshCookieAge());
        response.addHeader(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString());
    }

    private boolean isRefreshTokenNeedToRenew(Claims refreshToken) {
        Date expireDate = refreshToken.getExpiration();
        Date now = new Date();
        long diff = expireDate.getTime() - now.getTime();
        //如果refresh token剩余时间不足，废除并且生成一个新的
        return diff < jwtConfig.getRefreshCookieResetAge().toMillis();
    }

    private void resetJwtCookies(HttpServletResponse response) {
        ResponseCookie accessCookie = jwtService.createJwtCookie("", jwtConfig.getAccessCookieName(), Duration.ZERO);
        ResponseCookie refreshCookie = jwtService.createJwtCookie("", jwtConfig.getRefreshCookieName(), Duration.ZERO);
        response.addHeader(HttpHeaders.SET_COOKIE, accessCookie.toString());
        response.addHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString());
    }
}