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
    private String userTempDataFilename;
    private String imageSuffix;

    public String getExposeUrl(Path filePath) {
        Path basePath = getFileStorePath();
        Path relativizePath = basePath.relativize(filePath);
        String normalizePath = relativizePath.toString().replaceAll("\\\\", "/");//替换 '\' 为 '/'
        return this.exposeUrl + normalizePath;
    }

    public void setExposeUrl(String exposeUrl) {
        this.exposeUrl = exposeUrl;
    }

    private String exposeUrl;

    public MallConfig getMallConfig() {
        return mallConfig;
    }

    public String getImageSuffix() {
        return imageSuffix;
    }

    public void setImageSuffix(String imageSuffix) {
        this.imageSuffix = imageSuffix;
    }

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
        return getUserTempFilePrefix() + userName + "/";
    }
}