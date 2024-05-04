package com.jiezipoi.mall.service;

import com.jiezipoi.mall.component.EmailComponent;
import com.jiezipoi.mall.dao.UserDao;
import com.jiezipoi.mall.entity.User;
import com.jiezipoi.mall.entity.UserVerificationCode;
import com.jiezipoi.mall.enums.Role;
import com.jiezipoi.mall.enums.UserStatus;
import com.jiezipoi.mall.exception.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;

@Service
@Lazy
public class UserService implements UserDetailsService {
    private final UserDao mallUserDao;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final RoleService roleService;
    private final EmailComponent emailComponent;
    private final VerificationCodeService verificationCodeService;

    @Value("${mall.domain}")
    private String domain;

    public UserService(UserDao mallUserDao,
                       PasswordEncoder passwordEncoder,
                       @Lazy AuthenticationManager authenticationManager,
                       JwtService jwtService,
                       RoleService roleService,
                       EmailComponent emailComponent,
                       VerificationCodeService verificationCodeService) {
        this.mallUserDao = mallUserDao;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.roleService = roleService;
        this.emailComponent = emailComponent;
        this.verificationCodeService = verificationCodeService;
    }

    @Transactional
    public void createUser(String nickname, String email, String password) {
        if (isExistingEmail(email)) {
            throw new DuplicateKeyException("duplicated email '" + email + "'");
        }
        createUnactivatedUser(nickname, email, password);
        String verificationCode = verificationCodeService.generateAndSaveUserActivationCode(email);
        String verificationUrl = generateUserVerificationUrl(verificationCode);
        sendVerificationEmail(email, verificationUrl, nickname);
    }

    private void sendVerificationEmail(String email, String verificationUrl, String nickname) {
        HashMap<String, Object> variables = new HashMap<>();
        variables.put("nickName", nickname);
        variables.put("verificationUrl", verificationUrl);
        String emailBody = emailComponent.processEmailTemplate("email/sign-up-activation.html", variables);
        emailComponent.sendEmail(email, "verificación de correo", emailBody);
    }

    private String generateUserVerificationUrl(String verificationCode) {
        return this.domain + "/user/activate-account/" + verificationCode;
    }

    private void createUnactivatedUser(String nickname, String email, String password) {
        String BCryptPassword = passwordEncoder.encode(password);
        User user = new User();
        user.setNickName(nickname);
        user.setEmail(email);
        user.setPassword(BCryptPassword);
        user.setUserStatus(UserStatus.UNACTIVATED);
        mallUserDao.insertSelective(user);
    }

    public long getUserIdByEmail(String email) throws UserNotFoundException {
        Long id = mallUserDao.selectUserIdByEmail(email);
        if (id == null) {
            throw new UserNotFoundException(email);
        }
        return id;
    }

    public boolean isExistingEmail(String email) {
        return mallUserDao.selectByEmail(email) != null;
    }

    public User getUserByEmailAndPassword(String email, String password) {
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(email, password);
        Authentication authentication = authenticationManager.authenticate(authenticationToken);
        return (User) authentication.getPrincipal();
    }

    /**
     * 通过用户邮箱获得用户实体类
     * @param email 用户的邮箱
     * @return 用户实体类或者null
     */
    public User getUserByEmail(String email) {
        return mallUserDao.selectByEmail(email);
    }

    /**
     * UserDetails接口的实现，不要动，该实现需要通过用户登录标识（通常情况下是username）获得用户entity。
     * 但是商城里是email，
     *
     * @param email 用户的邮箱
     * @return 通过数据库映射的User对象
     * @throws UsernameNotFoundException 没有对应的用户
     */
    @Override
    public User loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = getUserByEmail(email);
        if (user == null) {
            throw new UsernameNotFoundException("user not found for email " + email);
        }
        return user;
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

    @Transactional
    public void sendResetPasswordEmail(String email) throws UnactivatedUserException, NotFoundException {
        User user = mallUserDao.selectByEmail(email);
        if (user == null) {
            throw new NotFoundException();
        }
        if (user.getUserStatus() == UserStatus.UNACTIVATED) {
            throw new UnactivatedUserException();
        }
        String verificationToken = verificationCodeService.generateAndSaveResetPasswordCode(email);
        String url = generateResetPasswordUrl(verificationToken);
        sendResetPasswordEmail(email, url, user.getNickName());
    }

    private void sendResetPasswordEmail(String email, String resetUrl, String nickName) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("resetUrl", resetUrl);
        map.put("nickname", nickName);
        String bodyStr = emailComponent.processEmailTemplate("email/reset-password.html", map);
        emailComponent.sendEmail(email, "restablecer tu contraseña", bodyStr);
    }

    private String generateResetPasswordUrl(String verificationCode) {
        return this.domain + "/user/reset-password/" + verificationCode;
    }

    public void resetPassword(String token, String password) throws NotFoundException {
        UserVerificationCode verificationCode = verificationCodeService.getVerificationCode(token);
        if (verificationCode == null) {
            throw new NotFoundException();
        }
        if (verificationCode.isDeleted() || verificationCode.getExpiredTime().isBefore(LocalDateTime.now())) {
            throw new CredentialsExpiredException("");
        }
        String email = verificationCode.getEmail();
        password = passwordEncoder.encode(password);
        mallUserDao.updateUserPasswordByEmail(email, password);
        verificationCodeService.setCodeDeleted(token);
    }
}