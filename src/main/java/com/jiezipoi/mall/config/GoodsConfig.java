package com.jiezipoi.mall.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
@PropertySource("classpath:/config/goods-config.properties")
public class GoodsConfig {
    private final MallConfig mallConfig;

    @Value("${goods.file.path}")
    private String goodsFileName;

    @Value("${goods.file.temp.prefix}")
    private String userTempFilePrefix;

    @Value("${goods.file.temp.filename}")
    private String userTempDataFilename;

    @Value("${goods.image-suffix}")
    private String imageSuffix;

    @Value("${goods.expose-url}")
    private String exposeUrl;

    /**
     * 传递一个文件目录，然后返回该目录的暴露url。
     * 首先获得配置文件中列出的文件储存目录，然后通过relativize()方法获得该文件的相对位置，
     * 此时，如果是windows环境则目录的分隔符为反斜杠“\”，所以需要替换掉所有的反斜杠。
     * 然后再把相对路径与配置的暴露路径拼接。
     * @param filePath 已经储存在本地的文件目录
     * @return 外部访问时使用的地址
     */
    public String getExposeUrl(Path filePath) {
        Path basePath = getGoodsFilePath();
        Path relativizePath = basePath.relativize(filePath);
        String normalizePath = relativizePath.toString().replaceAll("\\\\", "/");//替换 '\' 为 '/'
        return this.exposeUrl + normalizePath;
    }

    public String getExposeUrl() {
        return this.exposeUrl;
    }

    public String getImageSuffix() {
        return imageSuffix;
    }

    public GoodsConfig(MallConfig mallConfig) {
        this.mallConfig = mallConfig;
    }

    public String getUserTempDataFilename() {
        return userTempDataFilename;
    }

    public Path getGoodsFilePath() {
        return Paths.get(mallConfig.getUploadDirectory(), goodsFileName);
    }

    public String getUserTempFilePrefix() {
        return userTempFilePrefix;
    }

    public String getUserTempFileName(int userName) {
        return getUserTempFilePrefix() + userName + "/";
    }
}