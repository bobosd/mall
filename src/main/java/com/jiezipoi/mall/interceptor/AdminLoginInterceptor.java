package com.jiezipoi.mall.interceptor;

import jakarta.annotation.Nonnull;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class AdminLoginInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request,
                             @Nonnull HttpServletResponse response,
                             @Nonnull Object handler) throws Exception {
        String uri = request.getRequestURI();
        if (uri.startsWith("/admin") && request.getSession().getAttribute("nickname") == null) {
            response.sendRedirect(request.getContextPath() + "/admin/login");
            return false;
        }
        return true;
    }
}