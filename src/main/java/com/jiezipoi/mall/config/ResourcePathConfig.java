package com.jiezipoi.mall.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashSet;

@Configuration
public class ResourcePathConfig {
    private final CarouselConfig carouselConfig;
    private final GoodsConfig goodsConfig;

    public ResourcePathConfig(CarouselConfig carouselConfig, GoodsConfig goodsConfig) {
        this.carouselConfig = carouselConfig;
        this.goodsConfig = goodsConfig;
    }

    @Bean
    public HashSet<String> resourcePath() {
        HashSet<String> resourcePath = new HashSet<>();
        resourcePath.add(carouselConfig.getExposeUrl());
        resourcePath.add(goodsConfig.getExposeUrl());
        resourcePath.add("/js/");
        resourcePath.add("/styles/");
        resourcePath.add("/assets/");
        return resourcePath;
    }
}