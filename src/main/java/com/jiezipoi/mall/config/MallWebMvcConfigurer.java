package com.jiezipoi.mall.config;

import com.jiezipoi.mall.interceptor.LanguageInterceptor;
import jakarta.annotation.Nonnull;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.File;

@Configuration
public class MallWebMvcConfigurer implements WebMvcConfigurer {
    private final CarouselConfig carouselConfig;
    private final GoodsConfig goodsConfig;

    private final LanguageInterceptor languageInterceptor;

    public MallWebMvcConfigurer(CarouselConfig carouselConfig, GoodsConfig goodsConfig, LanguageInterceptor languageInterceptor) {
        this.languageInterceptor = languageInterceptor;
        this.carouselConfig = carouselConfig;
        this.goodsConfig = goodsConfig;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(languageInterceptor);
    }

    @Override
    public void addResourceHandlers(@Nonnull ResourceHandlerRegistry registry) {
        exposeCarouselDirectory(registry);
        exposeGoodsDirectory(registry);
    }

    private void exposeCarouselDirectory(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/carousel/img/**")
                .addResourceLocations("file:" + carouselConfig.getImageDirectory() + File.separator);
    }

    private void exposeGoodsDirectory(ResourceHandlerRegistry registry) {
        registry.addResourceHandler(goodsConfig.getExposeUrl() + "**")
                .addResourceLocations("file:" + goodsConfig.getGoodsFilePath() + File.separator);
    }
}