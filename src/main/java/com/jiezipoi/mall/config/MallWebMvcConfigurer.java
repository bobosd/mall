package com.jiezipoi.mall.config;

import com.jiezipoi.mall.interceptor.AdminLoginInterceptor;
import com.jiezipoi.mall.interceptor.LanguageInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class MallWebMvcConfigurer implements WebMvcConfigurer {
    public MallWebMvcConfigurer(LanguageInterceptor languageInterceptor, AdminLoginInterceptor adminLoginInterceptor) {
        this.languageInterceptor = languageInterceptor;
        this.adminLoginInterceptor = adminLoginInterceptor;
    }
    private final LanguageInterceptor languageInterceptor;
    private final AdminLoginInterceptor adminLoginInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(languageInterceptor);
        registry.addInterceptor(adminLoginInterceptor)
                .addPathPatterns("/admin/**")
                .excludePathPatterns("/admin/login");
    }
}