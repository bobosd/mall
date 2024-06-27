package com.jiezipoi.mall.service;

import com.jiezipoi.mall.component.EmailComponent;
import com.jiezipoi.mall.dao.UserDao;
import com.jiezipoi.mall.entity.User;
import com.jiezipoi.mall.entity.UserVerificationCode;
import com.jiezipoi.mall.enums.Role;
import com.jiezipoi.mall.enums.UserStatus;
import com.jiezipoi.mall.exception.NotFoundException;
import com.jiezipoi.mall.exception.UnactivatedUserException;
import com.jiezipoi.mall.exception.UserNotFoundException;
import com.jiezipoi.mall.exception.VerificationCodeNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
class UserServiceTest {

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @MockBean
    private UserDao userDao;

    @MockBean
    private VerificationCodeService verificationCodeService;

    @MockBean
    private EmailComponent emailComponent;

    @MockBean
    private RoleService roleService;

    @MockBean
    private JwtService jwtService;

    private User createMockUser(String email, String password) {
        User user = new User();
        user.setUserId(1L);
        user.setEmail(email);
        user.setNickName("test");
        user.setPassword(password);
        user.setPermissions(new ArrayList<>());
        return user;
    }

    @Test
    void createUser_ShouldThrowException_WhenEmailExist() {
        String email = "existing@example.com";
        User userInDB = createMockUser(email, "TEST_PASSWORD");
        when(userDao.selectByEmail(email)).thenReturn(userInDB);

        assertThrows(DuplicateKeyException.class, () -> userService.createUser("testUser", email, "PASSWORD"));
        verify(userDao, never()).insertSelective(any(User.class));
        verify(verificationCodeService, never()).generateAndSaveUserActivationCode(anyString());
        verify(emailComponent, never()).sendEmail(anyString(), anyString(), anyString());
    }

    @Test
    void createUser_ShouldCreateUser_WhenEmailNotExist() {
        String email = "new@example.com";
        String password = "PASSWORD";
        String verificationCode = "TEST_VERIFICATION_CODE";
        when(userDao.selectByEmail(email)).thenReturn(null);
        when(verificationCodeService.generateAndSaveResetPasswordCode(email)).thenReturn(verificationCode);
        when(emailComponent.processEmailTemplate(anyString(), anyMap())).thenReturn("TESTING_EMAIL_BODY");
        doNothing().when(emailComponent).sendEmail(anyString(), anyString(), anyString());

        userService.createUser("testUser", email, password);

        verify(userDao).insertSelective(any(User.class));
        verify(verificationCodeService).generateAndSaveUserActivationCode(email);
        verify(emailComponent).sendEmail(anyString(), anyString(), anyString());
    }

    @Test
    void getUserIdByEmail_ShouldReturnUserId_WhenEmailExist() {
        String email = "test@example.com";
        when(userDao.selectUserIdByEmail(email)).thenReturn(1L);
        Long id = userService.getUserIdByEmail(email);
        assertEquals(id, 1L);
        verify(userDao).selectUserIdByEmail(email);
    }

    @Test
    void getUserIdByEmail_ShouldThrowException_WhenEmailNotExist() {
        String email = "test@example.com";
        when(userDao.selectUserIdByEmail(email)).thenReturn(null);

        assertThrows(UserNotFoundException.class, () -> userService.getUserIdByEmail(email));
    }

    @Test
    void isExistingEmail_ShouldReturnFalse_WhenEmailNotExists() {
        String email = "test@example.com";
        User mockUser = createMockUser(email, "TEST_PASSWORD");
        when(userDao.selectByEmail(email)).thenReturn(mockUser);

        assertTrue(userService.isExistingEmail(email));
        verify(userDao).selectByEmail(email);
    }

    @Test
    void isExistingEmail_ShouldReturnTrue_WhenEmailNotExists() {
        String existingEmail = "test@example.com";
        String NotExistingEmail = "test2@example.com";
        User mockUser = createMockUser(existingEmail, "TEST_PASSWORD");
        when(userDao.selectByEmail(existingEmail)).thenReturn(mockUser);

        assertFalse(userService.isExistingEmail(NotExistingEmail));
        verify(userDao).selectByEmail(NotExistingEmail);
    }

    @Test
    void getUserByEmailAndPassword_ShouldReturnUser_WhenIdentityMatch() {
        String email = "test@example.com";
        String password = "TEST_PASSWORD";
        String encodedPassword = passwordEncoder.encode(password);
        User passwordEncodedUser = createMockUser(email, encodedPassword);
        passwordEncodedUser.setUserStatus(UserStatus.ACTIVATED);

        when(userDao.selectByEmail(email)).thenReturn(passwordEncodedUser);

        User user = userService.getUserByEmailAndPassword(email, password);
        assertEquals(user.getUserId(), passwordEncodedUser.getUserId());
    }

    @Test
    void getUserByEmailAndPassword_ShouldThrowException_WhenIdentityDoNotMatch() {
        String email = "test@example.com";
        String wrongPassword = "WRONG_PASSWORD";
        String encodedPassword = passwordEncoder.encode("TEST_PASSWORD");
        User passwordEncodedUser = createMockUser(email, encodedPassword);
        passwordEncodedUser.setUserStatus(UserStatus.ACTIVATED);
        when(userDao.selectByEmail(email)).thenReturn(passwordEncodedUser);

        assertThrows(BadCredentialsException.class, () -> userService.getUserByEmailAndPassword(email, wrongPassword));
    }

    @Test
    void getUserByEmail_ShouldReturnUser_WhenEmailExist() {
        String email = "test@example.com";
        User user = createMockUser(email, "PASSWORD");
        when(userDao.selectByEmail(email)).thenReturn(user);

        User test = userService.getUserByEmail(email);
        assertEquals(test.getUserId(), user.getUserId());
        verify(userDao).selectByEmail(email);
    }

    @Test
    void getUserByEmail_ShouldReturnNull_WhenEmailNotExist() {
        String email = "test@example.com";
        String notExistingEmail = "test2@example.com";
        when(userDao.selectByEmail(notExistingEmail)).thenReturn(null);

        User test = userService.getUserByEmail(email);

        assertNull(test);
        verify(userDao).selectByEmail(email);
    }

    @Test
    void activateUser_ShouldReturnUser_WhenVerificationCodeExist() throws VerificationCodeNotFoundException {
        String verificationCode = "EXAMPLE_CODE";
        String email = "test@example.com";
        String password = "PASSWORD";
        User user = createMockUser(email, password);
        when(userDao.selectEmailByVerificationCode(verificationCode)).thenReturn(email);
        when(userDao.selectByEmail(email)).thenReturn(user);

        userService.activateUser(verificationCode);

        verify(userDao).deleteByEmail(email);
        verify(userDao).updateStatusByPrimaryKey(eq(1L), any(UserStatus.class));
        verify(roleService).assignRoleToUser(Role.USER, 1L);
    }

    @Test
    void activateUser_ShouldThrowException_WhenVerificationCodeNotExist() {
        String verificationCode = "EXAMPLE_COME";
        String email = "test@example.com";

        when(userDao.selectEmailByVerificationCode(verificationCode)).thenReturn(null);

        assertThrows(VerificationCodeNotFoundException.class, () -> userService.activateUser(verificationCode));

        verify(userDao, never()).deleteByEmail(email);
        verify(roleService, never()).assignRoleToUser(any(Role.class), anyLong());
    }

    @Test
    void updateUserStatus_ShouldUpdateStatus_WhenCalled() {
        long userId = 1L;
        UserStatus status = UserStatus.ACTIVATED;

        userService.updateUserStatus(userId, status);

        verify(userDao).updateStatusByPrimaryKey(userId, status);
    }

    @Test
    void sendResetPasswordEmail_ShouldSendEmail_WhenEmailAndStatusValid() throws UnactivatedUserException, NotFoundException {
        String email = "test@example.com";
        User user = createMockUser(email, "PASSWORD");
        user.setUserStatus(UserStatus.ACTIVATED);
        when(userDao.selectByEmail(email)).thenReturn(user);
        String verificationCode = "VERIFICATION_CODE";
        when(verificationCodeService.generateAndSaveResetPasswordCode(email)).thenReturn(verificationCode);
        when(emailComponent.processEmailTemplate(anyString(), anyMap())).thenReturn("TESTING_EMAIL_BODY");

        userService.sendResetPasswordEmail(email);

        verify(userDao).selectByEmail(email);
        verify(verificationCodeService).generateAndSaveResetPasswordCode(email);
        verify(emailComponent).sendEmail(anyString(), anyString(), anyString());
    }

    @Test
    void sendResetPasswordEmail_ShouldThrowNotFoundException_WhenUserDoesNotExist() {
        String email = "nonexistent@example.com";
        when(userDao.selectByEmail(email)).thenReturn(null);

        assertThrows(NotFoundException.class, () -> userService.sendResetPasswordEmail(email));

        verify(userDao).selectByEmail(email);
        verify(verificationCodeService, never()).generateAndSaveResetPasswordCode(anyString());
        verify(emailComponent, never()).sendEmail(anyString(), anyString(), anyString());
    }

    @Test
    void sendResetPasswordEmail_ShouldThrowUnactivatedUserException_WhenUserIsUnactivated() {
        String email = "unactivated@example.com";
        User user = createMockUser(email, "PASSWORD");
        user.setUserStatus(UserStatus.UNACTIVATED);
        when(userDao.selectByEmail(email)).thenReturn(user);

        assertThrows(UnactivatedUserException.class, () -> userService.sendResetPasswordEmail(email));

        verify(userDao).selectByEmail(email);
        verify(verificationCodeService, never()).generateAndSaveResetPasswordCode(anyString());
        verify(emailComponent, never()).sendEmail(anyString(), anyString(), anyString());
    }

    @Test
    void resetPassword_ShouldThrowCredentialsExpiredException_WhenTokenIsExpired() {
        String token = "EXPIRED_TOKEN";
        UserVerificationCode verificationCode = new UserVerificationCode();
        verificationCode.setVerificationCode(token);
        verificationCode.setExpiredTime(LocalDateTime.now().minusHours(1));
        verificationCode.setDeleted(false);
        when(verificationCodeService.getVerificationCode(token)).thenReturn(verificationCode);

        assertThrows(CredentialsExpiredException.class, () -> userService.resetPassword(token, "PASSWORD"));

        verify(verificationCodeService).getVerificationCode(token);
        verifyNoMoreInteractions(verificationCodeService);
        verifyNoInteractions(jwtService, userDao);
    }

    @Test
    void resetPassword_ShouldThrowCredentialsExpiredException_WhenTokenIsDeleted() {
        String token = "DELETED_TOKEN";
        UserVerificationCode verificationCode = new UserVerificationCode();
        verificationCode.setVerificationCode(token);
        verificationCode.setExpiredTime(LocalDateTime.now().plusHours(1));
        verificationCode.setDeleted(true);
        when(verificationCodeService.getVerificationCode(token)).thenReturn(verificationCode);

        assertThrows(CredentialsExpiredException.class, () -> userService.resetPassword(token, "PASSWORD"));

        verify(verificationCodeService).getVerificationCode(token);
        verifyNoMoreInteractions(verificationCodeService);
        verifyNoInteractions(jwtService, userDao);
    }

    @Test
    void resetPassword_ShouldThrowNotFoundException_WhenTokenNotExists() {
        String token = "NOT_FOUND_TOKEN";
        when(verificationCodeService.getVerificationCode(token)).thenReturn(null);

        assertThrows(NotFoundException.class, () -> userService.resetPassword(token, "PASSWORD"));

        verify(verificationCodeService).getVerificationCode(token);
        verifyNoMoreInteractions(verificationCodeService);
        verifyNoInteractions(jwtService, userDao);
    }

    @Test
    void resetPassword_ShouldResetPassword_WhenTokenIsValid() throws NotFoundException {
        String token = "VALID_TOKEN";
        String email = "test@example.com";
        String newPassword = "NEW_PASSWORD";
        UserVerificationCode verificationCode = new UserVerificationCode();
        verificationCode.setVerificationCode(token);
        verificationCode.setExpiredTime(LocalDateTime.now().plusHours(1));
        verificationCode.setDeleted(false);
        verificationCode.setEmail(email);
        when(verificationCodeService.getVerificationCode(token)).thenReturn(verificationCode);

        userService.resetPassword(token, newPassword);

        verify(verificationCodeService).getVerificationCode(token);
        verify(jwtService).invalidateAllRefreshTokenOfUser(email);
        verify(verificationCodeService).setCodeDeleted(token);
    }
}