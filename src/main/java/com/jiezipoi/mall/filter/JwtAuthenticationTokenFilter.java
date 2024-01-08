package com.jiezipoi.mall.filter;

import com.jiezipoi.mall.config.JwtConfig;
import com.jiezipoi.mall.service.MallUserService;
import com.jiezipoi.mall.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.annotation.Nonnull;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
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
    private final MallUserService mallUserService;

    private final JwtConfig jwtConfig;

    public JwtAuthenticationTokenFilter(HashSet<String> resourcePaths,
                                        MallUserService mallUserService, JwtConfig jwtConfig) {
        this.resourcePaths = resourcePaths;
        this.mallUserService = mallUserService;
        this.jwtConfig = jwtConfig;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    @Nonnull HttpServletResponse response,
                                    @Nonnull FilterChain filterChain) throws ServletException, IOException {
        Cookie[] cookies = request.getCookies();
        String accessToken = findCookieValueByName(cookies, jwtConfig.getAccessCookieName());
        String refreshToken = findCookieValueByName(cookies, jwtConfig.getRefreshCookieName());
        if (!StringUtils.hasText(accessToken)) {
            filterChain.doFilter(request, response);
            return;
        }

        String email;
        try {
            Claims claims = JwtUtil.parseJWT(accessToken);
            email = claims.getSubject();
            UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken(email, null, null);
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            filterChain.doFilter(request, response);
        } catch (ExpiredJwtException e) {
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
     */
    private void regenerateJwtCookie(HttpServletResponse response, String refreshToken) {
        try {
            Claims claims = JwtUtil.parseJWT(refreshToken);
            String email = claims.getSubject();
            Date expireDate = claims.getExpiration();
            Date now = new Date();
            long diff = expireDate.getTime() - now.getTime();
            //如果refresh token剩余时间不足，废除并且生成一个新的
            if (diff < jwtConfig.getRefreshCookieResetAge().toMillis()) {
                mallUserService.invalidateRefreshToken(email, refreshToken);
                String renewRefreshToken = mallUserService.generateRefreshToken(email);
                ResponseCookie refreshTokenCookie = JwtUtil
                        .createJwtCookie(renewRefreshToken, jwtConfig.getRefreshCookieName(), jwtConfig.getRefreshCookieAge());
                response.addHeader(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString());
            }
            //生成新的AccessToken
            String renewAccessToken = mallUserService.generateAccessToken(email);
            ResponseCookie accessTokenCookie = JwtUtil
                    .createJwtCookie(renewAccessToken, jwtConfig.getAccessCookieName(), jwtConfig.getAccessTokenAge());
            response.addHeader(HttpHeaders.SET_COOKIE, accessTokenCookie.toString());
        } catch (JwtException ignored) {}
    }
}
