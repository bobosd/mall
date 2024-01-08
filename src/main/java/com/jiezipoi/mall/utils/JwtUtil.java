package com.jiezipoi.mall.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.http.ResponseCookie;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Date;
import java.util.UUID;

public class JwtUtil  {
    public static final String JWT_KEY = "TEST_KEY";

    public static String generateUUID() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }

    public static String generateJWT(String subject, Duration timeToLive) {
        JwtBuilder builder = generateJwtBuilder(generateUUID(), subject, timeToLive);
        return builder.compact();
    }

    public static String generateJWT(String id, String subject, Duration timeToLive) {
        JwtBuilder builder = generateJwtBuilder(id, subject, timeToLive);
        return builder.compact();
    }

    public static String generateJWT(String subject, Date issueDate, Date expireDate) {
        JwtBuilder builder = generateJwtBuilder(generateUUID(), subject, issueDate, expireDate);
        return builder.compact();
    }

    public static String generateJWT(String id, String subject, Date issueDate, Date expireDate) {
        JwtBuilder builder = generateJwtBuilder(id, subject, issueDate, expireDate);
        return builder.compact();
    }

    public static SecretKey getSecretKey() {
        byte[] key = JWT_KEY.getBytes(StandardCharsets.UTF_8);
        return new SecretKeySpec(key, "AES");
    }

    private static JwtBuilder generateJwtBuilder(String uuid, String subject, Duration timeToLive) {
        long nowMillis = System.currentTimeMillis();
        Date nowDate = new Date(nowMillis);
        long expMillis = nowMillis + timeToLive.toMillis();
        Date expDate = new Date(expMillis);
        return generateJwtBuilder(uuid, subject, nowDate, expDate);
    }

    private static JwtBuilder generateJwtBuilder(String uuid, String subject, Date createTime, Date expireTime) {
        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;
        SecretKey secretKey = getSecretKey();
        return Jwts.builder()
                .setId(uuid)
                .setSubject(subject)
                .setIssuer("JieziCloud")
                .setIssuedAt(createTime)
                .signWith(signatureAlgorithm, secretKey)
                .setExpiration(expireTime);
    }

    public static Claims parseJWT(String jwt) {
        SecretKey secretKey = getSecretKey();
        return Jwts.parser()
                .setSigningKey(secretKey)
                .parseClaimsJws(jwt)
                .getBody();
    }

    public static ResponseCookie createJwtCookie(String jwt, String cookieName, Duration expireDuration) {
        return ResponseCookie.from(cookieName, jwt)
                .httpOnly(true) //不会被JS读取
                .sameSite("Strict")
                .path("/")
                .maxAge(expireDuration)
                .secure(true) //https下才会被上传
                .build();
    }
}