package com.jiezipoi.mall.filter;

import com.jiezipoi.mall.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import jakarta.annotation.Nonnull;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class JwtAuthenticationTokenFilter extends OncePerRequestFilter {
    private final HashSet<String> resourcePaths;

    public JwtAuthenticationTokenFilter() {
        this.resourcePaths = new HashSet<>();
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    @Nonnull HttpServletResponse response,
                                    @Nonnull FilterChain filterChain) throws ServletException, IOException {
        Cookie[] cookies = request.getCookies();
        String jwt = findSessionTokenCookie(cookies);

        if (!StringUtils.hasText(jwt)) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            Claims claims = JwtUtil.parseJWT(jwt);
            String email = claims.getSubject();
            UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken(email, null, null);
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            filterChain.doFilter(request, response);
        } catch (JwtException e) {
            filterChain.doFilter(request, response);
        }
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getServletPath();
        Pattern pattern = Pattern.compile("/.*\\.(js|css|jpg|png)+");
        Matcher matcher = pattern.matcher(path);
        boolean shouldNotFilter = matcher.find();
        System.out.println(request.getServletPath());
        System.out.println(shouldNotFilter);
        return shouldNotFilter;
    }

    public boolean isStaticResource(String url) {
        return false;
    }

    private String findSessionTokenCookie(Cookie[] cookies) {
        String targetCookieName = "session-token";
        Optional<Cookie> optionalCookie = Arrays.stream(cookies)
                .filter(cookie -> cookie.getName().equals(targetCookieName))
                .findFirst();
        if (optionalCookie.isPresent()) {
            Cookie cookie = optionalCookie.get();
            return cookie.getValue();
        } else {
            return null;
        }
    }
}
