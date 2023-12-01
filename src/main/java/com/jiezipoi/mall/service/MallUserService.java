package com.jiezipoi.mall.service;

import com.amazonaws.services.simpleemail.AmazonSimpleEmailService;
import com.amazonaws.services.simpleemail.model.*;
import com.jiezipoi.mall.dao.MallUserDao;
import com.jiezipoi.mall.entity.MallUser;
import com.jiezipoi.mall.enums.UserStatus;
import com.jiezipoi.mall.security.MallUserDetails;
import com.jiezipoi.mall.utils.JwtUtil;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Service
public class MallUserService {
    private final AmazonSimpleEmailService emailService;
    private final MallUserDao mallUserDao;
    private final TemplateEngine templateEngine;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    @Lazy
    public MallUserService(AmazonSimpleEmailService emailService,
                           MallUserDao mallUserDao,
                           TemplateEngine templateEngine,
                           PasswordEncoder passwordEncoder,
                           AuthenticationManager authenticationManager) {
        this.emailService = emailService;
        this.mallUserDao = mallUserDao;
        this.templateEngine = templateEngine;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
    }

    public void userSignUp(String nickname, String email, String password) {
        MallUser unactivatedUser = createUnactivatedUser(nickname, email, password);
        sendVerificationEmail(email, nickname);
        String token = generateActivationToken(unactivatedUser);
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

    private String generateActivationToken(MallUser user) {
        return ""; //TODO 生成TOKEN并且可以让用户直接登录
    }

    public String login(String email, String password) {
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(email, password);
        authenticationManager.authenticate(authenticationToken);
        return JwtUtil.generateJWT(email);
    }

    public MallUser getMallUserByEmail(String email) {
        return mallUserDao.selectByEmail(email);
    }
}
