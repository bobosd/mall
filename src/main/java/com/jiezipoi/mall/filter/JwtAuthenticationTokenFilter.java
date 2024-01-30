package com.jiezipoi.mall.filter;

import com.jiezipoi.mall.config.JwtConfig;
import com.jiezipoi.mall.service.JwtService;
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
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Optional;

@Component
public class JwtAuthenticationTokenFilter extends OncePerRequestFilter {
    private final HashSet<String> resourcePaths;
    private final JwtConfig jwtConfig;
    private final JwtService jwtService;

    public JwtAuthenticationTokenFilter(HashSet<String> resourcePaths, JwtConfig jwtConfig, @Lazy JwtService jwtService) {
        this.resourcePaths = resourcePaths;
        this.jwtConfig = jwtConfig;
        this.jwtService = jwtService;
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

        if (!StringUtils.hasText(refreshToken)) {
            filterChain.doFilter(request, response);
            return;
        }

        if (accessToken == null) {
            accessToken = regenerateJwtCookie(response, refreshToken);
        }

        try {
            Claims claims = jwtService.parseJWT(accessToken);
            String email = claims.getSubject();
            UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken(email, null, null);
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            filterChain.doFilter(request, response);
        } catch (ExpiredJwtException e) { //发送请求没过期，在服务器过期了。
            regenerateJwtCookie(response, refreshToken);
            filterChain.doFilter(request, response);
        } catch (JwtException e) {
            filterChain.doFilter(request, response);
        }

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
     * 用于更新身份验证token，依赖refresh token是否有效
     * 如果refresh token没有过期就只更新access token
     * 如果refresh token即将过期则更新则两者都更新
     * 如果refresh token已经过期则退出登入
     *
     * @param response 用于给请求设置token
     * @param refreshToken 用户携带的refresh token
     * @return 新的access token
     */
    private String regenerateJwtCookie(HttpServletResponse response, String refreshToken) {
        try {
            Claims claims = jwtService.parseJWT(refreshToken);
            String email = claims.getSubject();
            Date expireDate = claims.getExpiration();
            Date now = new Date();
            long diff = expireDate.getTime() - now.getTime();
            //如果refresh token剩余时间不足，废除并且生成一个新的
            if (diff < jwtConfig.getRefreshCookieResetAge().toMillis()) {
                jwtService.invalidateRefreshToken(email, refreshToken);
                String renewRefreshToken = jwtService.generateAndStoreRefreshToken(email);
                ResponseCookie refreshTokenCookie = jwtService
                        .createJwtCookie(renewRefreshToken, jwtConfig.getRefreshCookieName(), jwtConfig.getRefreshCookieAge());
                response.addHeader(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString());
            }
            //生成新的AccessToken
            String renewAccessToken = jwtService.generateAccessToken(email);
            ResponseCookie accessTokenCookie = jwtService
                    .createJwtCookie(renewAccessToken, jwtConfig.getAccessCookieName(), jwtConfig.getAccessTokenAge());
            response.addHeader(HttpHeaders.SET_COOKIE, accessTokenCookie.toString());
            return renewAccessToken;
        } catch (JwtException ignored) {}
        return null;
    }
}
