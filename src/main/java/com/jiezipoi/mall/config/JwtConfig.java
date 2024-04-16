package com.jiezipoi.mall.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.time.Duration;

@Configuration
@PropertySource("classpath:/config/jwt-config.properties")
public class JwtConfig {
    private final MallConfig mallConfig;

    @Value("${jwt.access.cookie.age}")
    private Duration accessTokenAge;

    @Value("${jwt.access.cookie.name}")
    private String accessCookieName;

    @Value("${jwt.refresh.cookie.age}")
    private Duration refreshCookieAge;

    @Value("${jwt.refresh.cookie.name}")
    private String refreshCookieName;

    @Value("${jwt.refresh.cookie.reset}")
    private Duration refreshCookieResetAge;

    @Value("${jwt.keys.private-key}")
    private String privateKeyFileName;

    @Value("${jwt.keys.public-key}")
    private String publicKeyFileName;

    @Value("${jwt.keys.dirname}")
    private String keysDirName;

    @Value("${jwt.verify.age}")
    private Duration verificationTokenDuration;

    @Value("${jwt.clock-skew}")
    private Duration clockSkew;

    public JwtConfig(MallConfig mallConfig) {
        this.mallConfig = mallConfig;
    }

    public Duration getAccessTokenAge() {
        return accessTokenAge;
    }

    public Duration getRefreshCookieAge() {
        return refreshCookieAge;
    }

    public Duration getRefreshCookieResetAge() {
        return refreshCookieResetAge;
    }

    public String getAccessCookieName() {
        return accessCookieName;
    }

    public String getRefreshCookieName() {
        return refreshCookieName;
    }

    public Duration getVerificationTokenDuration() {
        return verificationTokenDuration;
    }

    public Duration getClockSkew() {
        return clockSkew;
    }

    @Bean
    public PrivateKey jwtPrivateKey() throws IOException, InvalidKeySpecException, NoSuchAlgorithmException {
        Path file = Paths.get(mallConfig.getUploadDirectory(), this.keysDirName, privateKeyFileName);
        byte[] keyBytes = Files.readAllBytes(file);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PKCS8EncodedKeySpec pkcs8EncodedKeySpec = new PKCS8EncodedKeySpec(keyBytes);
        return keyFactory.generatePrivate(pkcs8EncodedKeySpec);
    }

    @Bean
    public PublicKey jwtPublicKey() throws IOException, InvalidKeySpecException, NoSuchAlgorithmException {
        Path file = Paths.get(mallConfig.getUploadDirectory(), this.keysDirName, publicKeyFileName);
        byte[] keyBytes = Files.readAllBytes(file);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec(keyBytes);
        return keyFactory.generatePublic(x509EncodedKeySpec);
    }
}