package com.jiezipoi.mall.service;

import com.jiezipoi.mall.config.JwtConfig;
import com.jiezipoi.mall.dao.JwtDao;
import com.jiezipoi.mall.entity.UserRefreshToken;
import io.jsonwebtoken.*;
import org.springframework.http.ResponseCookie;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.time.Duration;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class JwtService {
    private final PrivateKey privateKey;
    private final PublicKey publicKey;
    private final SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.RS256;
    private final JwtConfig jwtConfig;
    private final PasswordEncoder encoder;
    private final JwtDao jwtDao;

    public JwtService(PrivateKey privateKey, PublicKey publicKey, JwtConfig jwtConfig, PasswordEncoder encoder, JwtDao jwtDao) {
        this.privateKey = privateKey;
        this.publicKey = publicKey;
        this.jwtConfig = jwtConfig;
        this.encoder = encoder;
        this.jwtDao = jwtDao;
    }

    public String generateAccessToken(String email) {
        return generateJWT(generateUUID(), email, jwtConfig.getAccessTokenAge());
    }

    /**
     * 创建一个新的refresh token并且将其存入数据库。
     * @param email 用户邮箱
     * @return 生成的refresh token
     */
    public String generateAndStoreRefreshToken(String email) {
        String uuid = generateUUID();
        long nowMillis = System.currentTimeMillis();
        Date nowDate = new Date(nowMillis);
        long expireMillis = nowMillis + jwtConfig.getRefreshCookieAge().toMillis();
        Date expireDate = new Date(expireMillis);
        String refreshToken = generateJWT(uuid, email, nowDate, expireDate);
        String encodedRefreshToken = encoder.encode(refreshToken);
        UserRefreshToken mallUserUserRefreshToken = new UserRefreshToken(uuid, email, encodedRefreshToken, nowDate, expireDate);
        jwtDao.insertRefreshToken(mallUserUserRefreshToken);
        return refreshToken;
    }


    public void invalidateRefreshToken(String refreshToken) throws JwtException {
        Claims claims = parseJWT(refreshToken);
        String email = claims.getSubject();
        invalidateRefreshToken(email, refreshToken);
    }

    public void invalidateRefreshToken(String email, String refreshToken) {
        List<UserRefreshToken> userRefreshTokenList = jwtDao.selectRefreshTokenByEmail(email);
        Optional<UserRefreshToken> optionalUserRefreshToken = userRefreshTokenList.stream()
                .filter((t) -> {
                    String encodedRefreshToken = t.getEncodedRefreshToken();
                    return encoder.matches(refreshToken, encodedRefreshToken);
                })
                .findFirst();
        if (optionalUserRefreshToken.isPresent()) {
            UserRefreshToken userRefreshToken = optionalUserRefreshToken.get();
            jwtDao.deleteRefreshToken(userRefreshToken.getUuid());
        }
    }

    public ResponseCookie createJwtCookie(String token, String cookieName, Duration expireDuration) {
        return ResponseCookie.from(cookieName, token)
                .sameSite("Strict")
                .path("/")
                .maxAge(expireDuration)
                .secure(true)
                .build();
    }

    private String generateUUID() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }

    private String generateJWT(String jwtId, String subject, Duration timeToLive) {
        JwtBuilder builder = generateJwtBuilder(jwtId, subject, timeToLive);
        return builder.compact();
    }

    private String generateJWT(String jwtId, String subject, Date issueDate, Date expireDate) {
        JwtBuilder builder = generateJwtBuilder(jwtId, subject, issueDate, expireDate);
        return builder.compact();
    }

    private JwtBuilder generateJwtBuilder(String jwtId, String subject, Duration timeToLive) {
        long nowMillis = System.currentTimeMillis();
        Date nowDate = new Date(nowMillis);
        long expMillis = nowMillis + timeToLive.toMillis();
        Date expDate = new Date(expMillis);
        return generateJwtBuilder(jwtId, subject, nowDate, expDate);
    }

    private JwtBuilder generateJwtBuilder(String jwtId, String subject, Date issueDate, Date expireDate) {
        return Jwts.builder()
                .setId(jwtId)
                .setSubject(subject)
                .setIssuer("JieziCloud")
                .setIssuedAt(issueDate)
                .setExpiration(expireDate)
                .signWith(signatureAlgorithm, privateKey);
    }

    public Claims parseJWT(String jwt) {
        return Jwts.parser()
                .setSigningKey(publicKey)
                .parseClaimsJws(jwt)
                .getBody();
    }

    public String generateVerificationToken(String email) {
        return generateJWT(generateUUID(), email, jwtConfig.getVerificationTokenDuration());
    }
}