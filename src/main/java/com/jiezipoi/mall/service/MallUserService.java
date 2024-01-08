package com.jiezipoi.mall.service;

import com.amazonaws.services.simpleemail.AmazonSimpleEmailService;
import com.amazonaws.services.simpleemail.model.*;
import com.jiezipoi.mall.config.JwtConfig;
import com.jiezipoi.mall.dao.MallUserDao;
import com.jiezipoi.mall.entity.MallUser;
import com.jiezipoi.mall.entity.MallUserRefreshToken;
import com.jiezipoi.mall.enums.UserStatus;
import com.jiezipoi.mall.security.MallUserDetails;
import com.jiezipoi.mall.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class MallUserService {
    private final AmazonSimpleEmailService emailService;
    private final MallUserDao mallUserDao;
    private final TemplateEngine templateEngine;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtConfig jwtConfig;

    @Lazy
    public MallUserService(AmazonSimpleEmailService emailService,
                           MallUserDao mallUserDao,
                           TemplateEngine templateEngine,
                           PasswordEncoder passwordEncoder,
                           AuthenticationManager authenticationManager, JwtConfig jwtConfig) {
        this.emailService = emailService;
        this.mallUserDao = mallUserDao;
        this.templateEngine = templateEngine;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtConfig = jwtConfig;
    }

    public void userSignUp(String nickname, String email, String password) {
        MallUser unactivatedUser = createUnactivatedUser(nickname, email, password);
        String verificationUrl = generateUserVerificationUrl(unactivatedUser);
        sendVerificationEmail(email, nickname);
    }

    private void sendVerificationEmail(String email, String nickname) {
        Destination destination = new Destination().withToAddresses(email);

        Message message = new Message()
                .withSubject(new Content("[JieziCloud] Email verification"))
                .withBody(new Body().withHtml(generateSignUpEmailContent(nickname)));

        SendEmailRequest emailRequest = new SendEmailRequest()
                .withSource("no-reply@jiezicloud.com")
                .withDestination(destination)
                .withMessage(message);
        emailService.sendEmail(emailRequest);
    }

    private String generateUserVerificationUrl(MallUser mallUser) {
        //JwtUtil.generateJWT()
        //TODO: 生成一个refresh token并且储存在DB里，然后使用
        return null;
    }

    private MallUser createUnactivatedUser(String nickname, String email, String password) {
        String BCryptPassword = passwordEncoder.encode(password);
        MallUser user = new MallUser();
        user.setNickName(nickname);
        user.setEmail(email);
        user.setPassword(BCryptPassword);
        user.setUserStatus(UserStatus.UNACTIVATED);
        mallUserDao.insertSelective(user);
        return user;
    }

    private Content generateSignUpEmailContent(String nickname) {
        Context thymeleafContext = new Context();
        thymeleafContext.setVariable("nickName", nickname);
        return new Content(templateEngine.process("email/sign-up-activation.html", thymeleafContext));
    }

    public boolean isExistingEmail(String email) {
        return mallUserDao.selectByEmail(email) != null;
    }

    public MallUser login(String email, String password) {
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(email, password);
        Authentication authentication = authenticationManager.authenticate(authenticationToken);
        MallUserDetails userDetails = (MallUserDetails) authentication.getPrincipal();
        return userDetails.mallUser();
    }

    public MallUser getMallUserByEmail(String email) {
        return mallUserDao.selectByEmail(email);
    }

    /**
     * 创建一个新的 refresh token 并且将其存入数据库
     * @param email 用户邮箱
     * @return 生成的 refresh token
     */
    public String generateRefreshToken(String email) {
        String uuid = UUID.randomUUID().toString().replace("-", "");
        long nowMillis = System.currentTimeMillis();
        Date nowDate = new Date(nowMillis);
        long expireMillis = nowMillis + this.jwtConfig.getRefreshCookieResetAge().toMillis();
        Date expireDate = new Date(expireMillis);
        String refreshToken = JwtUtil.generateJWT(email, nowDate, expireDate);
        String encodedRefreshToken = passwordEncoder.encode(refreshToken);
        MallUserRefreshToken mallUserRefreshToken = new MallUserRefreshToken(uuid, email, encodedRefreshToken, nowDate, expireDate);
        mallUserDao.insertRefreshToken(mallUserRefreshToken);
        return refreshToken;
    }

    public void invalidateRefreshToken(String email, String refreshToken) {
        List<MallUserRefreshToken> refreshTokenList = mallUserDao.selectRefreshTokenByEmail(email);
        Optional<MallUserRefreshToken> optionalMallUserRefreshToken = refreshTokenList.stream()
                .filter((mallUserRefreshToken -> {
                    String encodedRefreshToken = mallUserRefreshToken.getEncodedRefreshToken();
                    return passwordEncoder.matches(refreshToken, encodedRefreshToken);
                }))
                .findFirst();
        if (optionalMallUserRefreshToken.isPresent()) {
            MallUserRefreshToken mallUserRefreshToken = optionalMallUserRefreshToken.get();
            mallUserDao.deleteRefreshToken(mallUserRefreshToken.getUuid());
        }
    }

    public void invalidateRefreshToken(String refreshToken) throws JwtException{
        try {
            Claims claims = JwtUtil.parseJWT(refreshToken);
            String email = claims.getSubject();
            invalidateRefreshToken(email, refreshToken);
        } catch (JwtException ignored) {}
    }

    public String getRefreshTokenEmail(String refreshToken) {
        return "";
    }

    public String generateAccessToken(String email) {
        return JwtUtil.generateJWT(email, this.jwtConfig.getAccessTokenAge());
    }

    public void logout(String refreshToken) {

    }
}