package com.jiezipoi.mall.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "mall")
public class MallConfig {
    private String uploadDirectory;
    private String mallDomain;

    public String getUploadDirectory() {
        return uploadDirectory;
    }

    public void setUploadDirectory(String uploadDirectory) {
        this.uploadDirectory = uploadDirectory;
    }

    public String getMallDomain() {
        return mallDomain;
    }

    public void setMallDomain(String mallDomain) {
        this.mallDomain = mallDomain;
    }
}