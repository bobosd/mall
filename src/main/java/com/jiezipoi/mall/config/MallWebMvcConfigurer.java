package com.jiezipoi.mall.config;

import com.jiezipoi.mall.interceptor.AdminLoginInterceptor;
import com.jiezipoi.mall.interceptor.LanguageInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.File;

@Configuration
public class MallWebMvcConfigurer implements WebMvcConfigurer {
    private final CarouselConfig carouselConfig;
    private final GoodsConfig goodsConfig;

    public MallWebMvcConfigurer(LanguageInterceptor languageInterceptor,
                                AdminLoginInterceptor adminLoginInterceptor,
                                CarouselConfig carouselConfig, GoodsConfig goodsConfig) {
        this.languageInterceptor = languageInterceptor;
        this.adminLoginInterceptor = adminLoginInterceptor;
        this.carouselConfig = carouselConfig;
        this.goodsConfig = goodsConfig;
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

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        exposeCarouselDirectory(registry);
        exposeGoodsDirectory(registry);
    }

    private void exposeCarouselDirectory(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/admin/carousel/img/**")
                .addResourceLocations("file:" + carouselConfig.getImageDirectory());
    }

    private void exposeGoodsDirectory(ResourceHandlerRegistry registry) {
        System.out.println("file:" + goodsConfig.getFileStorePath());
        registry.addResourceHandler("/admin/goods/img/**")
                .addResourceLocations("file:" + goodsConfig.getFileStorePath() + File.separator);
    }
}