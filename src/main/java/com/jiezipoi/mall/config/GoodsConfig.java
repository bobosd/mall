package com.jiezipoi.mall.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
@ConfigurationProperties(prefix = "goods")
@PropertySource("classpath:/config/goods-config.properties")
public class GoodsConfig {
    private final MallConfig mallConfig;
    private String fileStorePath;
    private String userTempFilePrefix;
    private String coverImageFilename;
    private String userTempDataFilename;

    public GoodsConfig(MallConfig mallConfig) {
        this.mallConfig = mallConfig;
    }

    public String getUserTempDataFilename() {
        return userTempDataFilename;
    }

    public void setUserTempDataFilename(String userTempDataFilename) {
        this.userTempDataFilename = userTempDataFilename;
    }

    public Path getFileStorePath() {
        return Paths.get(mallConfig.getUploadDirectory() + fileStorePath);
    }

    public void setFileStorePath(String fileStorePath) {
        this.fileStorePath = fileStorePath;
    }

    public String getUserTempFilePrefix() {
        return userTempFilePrefix;
    }

    public void setUserTempFilePrefix(String userTempFilePrefix) {
        this.userTempFilePrefix = userTempFilePrefix;
    }

    public String getUserTempFileName(int userName) {
        return getUserTempFilePrefix() + userName;
    }

    public String getCoverImageFilename() {
        return coverImageFilename;
    }

    public void setCoverImageFilename(String coverImageFilename) {
        this.coverImageFilename = coverImageFilename;
    }
}