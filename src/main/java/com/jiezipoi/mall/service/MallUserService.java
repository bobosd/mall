package com.jiezipoi.mall.service;

import com.amazonaws.services.simpleemail.AmazonSimpleEmailService;
import com.amazonaws.services.simpleemail.model.*;
import com.jiezipoi.mall.dao.MallUserDao;
import com.jiezipoi.mall.entity.User;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Service
public class MallUserService {
    private final AmazonSimpleEmailService emailService;
    private final MallUserDao mallUserDao;
    private final TemplateEngine templateEngine;

    public MallUserService(AmazonSimpleEmailService emailService, MallUserDao mallUserDao, TemplateEngine templateEngine) {
        this.emailService = emailService;
        this.mallUserDao = mallUserDao;
        this.templateEngine = templateEngine;
    }

    public void userSignUp(String nickname, String email, String password) {
        createUnactivatedUser(nickname, email, password);
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

    private void createUnactivatedUser(String nickname, String email, String password) {
        String md5Password = DigestUtils.md5DigestAsHex(password.getBytes());
        User user = new User();
        user.setNickName(nickname);
        user.setEmail(email);
        user.setPasswordMd5(md5Password);
        mallUserDao.insertSelective(user);
    }

    private Content generateSignUpEmailContent(String nickname) {
        Context thymeleafContext = new Context();
        thymeleafContext.setVariable("nickName", nickname);
        return new Content(templateEngine.process("email/sign-up-activation.html", thymeleafContext));
    }

    public boolean isExistingEmail(String email) {
        return mallUserDao.selectByEmail(email) != null;
    }
}
