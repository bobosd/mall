package com.jiezipoi.mall.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("classpath:/config/carousel-config.properties")
public class CarouselConfig {
    private final MallConfig mallConfig;

    @Value("${carousel.image.directory}")
    private String imageDirectory;

    @Value("${carousel.expose-url}")
    private String exposeUrl;

    public CarouselConfig(MallConfig mallConfig) {
        this.mallConfig = mallConfig;
    }

    public String getImageDirectory() {
        return mallConfig.getUploadDirectory() + imageDirectory;
    }

    public String getExposeUrl() {
        return exposeUrl;
    }
}