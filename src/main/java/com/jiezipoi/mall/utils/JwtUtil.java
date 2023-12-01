package com.jiezipoi.mall.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Date;
import java.util.UUID;

public class JwtUtil  {
    public static final Duration JWT_DEFAULT_TTL = Duration.ofHours(1); // Time to live 1 hour
    public static final String JWT_KEY = "TEST_KEY";

    public static String generateUUID() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }

    public static String generateJWT(String subject) {
        JwtBuilder builder = generateJwtBuilder(generateUUID(), subject, JWT_DEFAULT_TTL);
        return builder.compact();
    }

    public static String generateJWT(String subject, Duration timeToLive) {
        if (timeToLive == null || timeToLive.isZero()) {
            timeToLive = JWT_DEFAULT_TTL;
        }
        JwtBuilder builder = generateJwtBuilder(generateUUID(), subject, timeToLive);
        return builder.compact();
    }

    public static String generateJWT(String id, String subject, Duration timeToLive) {
        JwtBuilder builder = generateJwtBuilder(id, subject, timeToLive);
        return builder.compact();
    }

    public static SecretKey getSecretKey() {
        byte[] key = JWT_KEY.getBytes(StandardCharsets.UTF_8);
        return new SecretKeySpec(key, "AES");
    }

    private static JwtBuilder generateJwtBuilder(String uuid, String subject, Duration timeToLive) {
        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;
        SecretKey secretKey = getSecretKey();
        long nowMillis = System.currentTimeMillis();
        Date nowDate = new Date(nowMillis);
        long expMillis = nowMillis + timeToLive.toMillis();
        Date expDate = new Date(expMillis);
        return Jwts.builder()
                .setId(uuid)
                .setSubject(subject)
                .setIssuer("JieziCloud")
                .setIssuedAt(nowDate)
                .signWith(signatureAlgorithm, secretKey)
                .setExpiration(expDate);
    }

    public static Claims parseJWT(String jwt) {
        SecretKey secretKey = getSecretKey();
        return Jwts.parser()
                .setSigningKey(secretKey)
                .parseClaimsJws(jwt)
                .getBody();
    }
}
