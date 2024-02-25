package com.jiezipoi.mall.service;

import com.amazonaws.services.simpleemail.AmazonSimpleEmailService;
import com.amazonaws.services.simpleemail.model.*;
import com.jiezipoi.mall.dao.MallUserDao;
import com.jiezipoi.mall.entity.MallUser;
import com.jiezipoi.mall.enums.UserStatus;
import com.jiezipoi.mall.exception.VerificationCodeNotFoundException;
import com.jiezipoi.mall.security.MallUserDetails;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.UUID;

@Service
public class MallUserService {
    private final AmazonSimpleEmailService emailService;
    private final MallUserDao mallUserDao;
    private final TemplateEngine templateEngine;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    @Value("${mall.domain}")
    private String domain;

    public MallUserService(AmazonSimpleEmailService emailService,
                           MallUserDao mallUserDao,
                           TemplateEngine templateEngine,
                           PasswordEncoder passwordEncoder,
                           AuthenticationManager authenticationManager, JwtService jwtService) {
        this.emailService = emailService;
        this.mallUserDao = mallUserDao;
        this.templateEngine = templateEngine;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    public void userSignUp(String nickname, String email, String password) {
        MallUser unactivatedUser = createUnactivatedUser(nickname, email, password);
        String verificationCode = generateAndStoreVerificationCode(unactivatedUser);
        String verificationUrl = generateUserVerificationUrl(verificationCode);
        sendVerificationEmail(email, verificationUrl, nickname);
    }

    private String generateAndStoreVerificationCode(MallUser mallUser) {
        String uuid = UUID.randomUUID().toString().replaceAll("-", "");
        mallUserDao.insertVerificationCode(mallUser.getEmail(), uuid);
        return uuid;
    }

    private void sendVerificationEmail(String email, String verificationUrl, String nickname) {
        Destination destination = new Destination().withToAddresses(email);
        Body htmlContent = new Body().withHtml(generateSignUpEmailContent(nickname, verificationUrl));
        Message message = new Message()
                .withSubject(new Content("[JieziCloud] Email verification"))
                .withBody(htmlContent);

        SendEmailRequest emailRequest = new SendEmailRequest()
                .withSource("no-reply@jiezicloud.com")
                .withDestination(destination)
                .withMessage(message);
        emailService.sendEmail(emailRequest);
    }

    private String generateUserVerificationUrl(String verificationCode) {
        return this.domain + "/user/activate-account/" + verificationCode;
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

    private Content generateSignUpEmailContent(String nickname, String verificationUrl) {
        Context thymeleafContext = new Context();
        thymeleafContext.setVariable("nickName", nickname);
        thymeleafContext.setVariable("verificationUrl", verificationUrl);
        return new Content(templateEngine.process("email/sign-up-activation.html", thymeleafContext));
    }

    public boolean isExistingEmail(String email) {
        return mallUserDao.selectByEmail(email) != null;
    }

    public MallUser findUser(String email, String password) {
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(email, password);
        Authentication authentication = authenticationManager.authenticate(authenticationToken);
        MallUserDetails userDetails = (MallUserDetails) authentication.getPrincipal();
        return userDetails.mallUser();
    }

    public MallUser getMallUserByEmail(String email) {
        return mallUserDao.selectByEmail(email);
    }

    public void logout(String refreshToken) {
        this.jwtService.invalidateRefreshToken(refreshToken);
    }

    /**
     * 验证用户是否是激活
     * @param verificationCode 激活码
     */
    public void verifyUser(String verificationCode) throws VerificationCodeNotFoundException {
        String email = mallUserDao.selectEmailByVerificationCode(verificationCode);
        if (email == null) {
            throw new VerificationCodeNotFoundException();
        }
        mallUserDao.deleteVerificationCodeByEmail(email);
        setUserStatus(email, UserStatus.ACTIVATED);
    }

    public void setUserStatus(String email, UserStatus status) {
        mallUserDao.updateStatusByEmail(email, status);
    }
}