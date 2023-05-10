package com.jiezipoi.mall.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@ConfigurationProperties(prefix = "carousel")
@PropertySource("classpath:/config/carousel-config.properties")
public class CarouselConfig {
    private final MallConfig mallConfig;
    private String imageDirectory;

    public CarouselConfig(MallConfig mallConfig) {
        this.mallConfig = mallConfig;
    }

    public String getImageDirectory() {
        return mallConfig.getUploadDirectory() + imageDirectory;
    }

    public void setImageDirectory(String imageDirectory) {
        this.imageDirectory = imageDirectory;
    }
}