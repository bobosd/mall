package com.jiezipoi.mall.service;

import com.amazonaws.services.simpleemail.AmazonSimpleEmailService;
import com.amazonaws.services.simpleemail.model.*;
import com.jiezipoi.mall.dao.UserDao;
import com.jiezipoi.mall.entity.User;
import com.jiezipoi.mall.enums.Role;
import com.jiezipoi.mall.enums.UserStatus;
import com.jiezipoi.mall.exception.NotFoundException;
import com.jiezipoi.mall.exception.VerificationCodeNotFoundException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.UUID;

@Service
@Lazy
public class UserService implements UserDetailsService {
    private final AmazonSimpleEmailService emailService;
    private final UserDao mallUserDao;
    private final TemplateEngine templateEngine;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final RoleService roleService;

    @Value("${mall.domain}")
    private String domain;

    public UserService(AmazonSimpleEmailService emailService,
                       UserDao mallUserDao,
                       TemplateEngine templateEngine,
                       PasswordEncoder passwordEncoder,
                       @Lazy AuthenticationManager authenticationManager, JwtService jwtService, RoleService roleService) {
        this.emailService = emailService;
        this.mallUserDao = mallUserDao;
        this.templateEngine = templateEngine;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.roleService = roleService;
    }

    @Transactional
    public void createUser(String nickname, String email, String password) {
        User unactivatedUser = createUnactivatedUser(nickname, email, password);
        String verificationCode = generateAndStoreVerificationCode(unactivatedUser);
        String verificationUrl = generateUserVerificationUrl(verificationCode);
        sendVerificationEmail(email, verificationUrl, nickname);
    }

    private String generateAndStoreVerificationCode(User user) {
        String uuid = UUID.randomUUID().toString().replaceAll("-", "");
        mallUserDao.insertVerificationCode(user.getEmail(), uuid);
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

    private User createUnactivatedUser(String nickname, String email, String password) {
        String BCryptPassword = passwordEncoder.encode(password);
        User user = new User();
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

    public long getUserIdByEmail(String email) throws NotFoundException {
        return mallUserDao.selectUserIdByEmail(email);
    }

    public boolean isExistingEmail(String email) {
        return mallUserDao.selectByEmail(email) != null;
    }

    public User getUserByEmailAndPassword(String email, String password) {
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(email, password);
        Authentication authentication = authenticationManager.authenticate(authenticationToken);
        return (User) authentication.getPrincipal();
    }

    public User getUserByEmail(String email) {
        return mallUserDao.selectByEmail(email);
    }

    /**
     * UserDetails接口的实现，不要动，该实现需要通过用户登录标识（通常情况下是username）获得用户entity。
     * 但是商城里是email，
     * @param email 用户的邮箱
     * @return 通过数据库映射的User对象
     * @throws UsernameNotFoundException 没有对应的用户
     */
    @Override
    public User loadUserByUsername(String email) throws UsernameNotFoundException{
        return getUserByEmail(email);
    }

    public void logout(String refreshToken) {
        this.jwtService.invalidateRefreshToken(refreshToken);
    }

    /**
     * 验证用户是否是激活
     * @param verificationCode 激活码
     */
    @Transactional
    public User activateUser(String verificationCode) throws VerificationCodeNotFoundException {
        String email = mallUserDao.selectEmailByVerificationCode(verificationCode);
        if (email == null) {
            throw new VerificationCodeNotFoundException();
        }
        mallUserDao.deleteVerificationCodeByEmail(email);
        User user = mallUserDao.selectByEmail(email);
        user.setUserStatus(UserStatus.ACTIVATED);
        updateUserStatus(user.getUserId(), UserStatus.ACTIVATED);
        roleService.assignRoleToUser(Role.USER, user.getUserId());
        return user;
    }

    public void updateUserStatus(Long userId, UserStatus status) {
        mallUserDao.updateStatusByPrimaryKey(userId, status);
    }
}