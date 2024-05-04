package com.jiezipoi.mall.service;

import com.jiezipoi.mall.dao.VerificationCodeDao;
import com.jiezipoi.mall.entity.UserVerificationCode;
import com.jiezipoi.mall.enums.VerificationActionType;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class VerificationCodeService {
    private final VerificationCodeDao verificationCodeDao;
    private final Duration DEFAULT_EXPIRE_DURATION = Duration.ofHours(24);

    public VerificationCodeService(VerificationCodeDao verificationCodeDao) {
        this.verificationCodeDao = verificationCodeDao;
    }

    public String generateAndSaveUserActivationCode(String email) {
        UserVerificationCode userVerificationCode = createVerificationCode(email, VerificationActionType.USER_ACTIVATION);
        verificationCodeDao.insertSelective(userVerificationCode);
        return userVerificationCode.getVerificationCode();
    }

    public String generateAndSaveResetPasswordCode(String email) {
        UserVerificationCode lastResetPasswordCode = getUserLastCodeByEmailAndType(email, VerificationActionType.RESET_PASSWORD);
        if (lastResetPasswordCode != null) {
            verificationCodeDao.deleteVerificationCodeByCode(lastResetPasswordCode.getVerificationCode());
        }
        UserVerificationCode userVerificationCode = createVerificationCode(email, VerificationActionType.RESET_PASSWORD);
        verificationCodeDao.insertSelective(userVerificationCode);
        return userVerificationCode.getVerificationCode();
    }

    public UserVerificationCode getUserLastCodeByEmailAndType(String email, VerificationActionType actionType) {
        return verificationCodeDao.selectLastVerificationCodeByEmailAndType(email, actionType);
    }

    private UserVerificationCode createVerificationCode(String email, VerificationActionType actionType) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expiredTime = now.plus(DEFAULT_EXPIRE_DURATION);
        String code = generateRandomUUID();
        UserVerificationCode userVerificationCode = new UserVerificationCode();
        userVerificationCode.setEmail(email);
        userVerificationCode.setVerificationCode(code);
        userVerificationCode.setActionType(actionType);
        userVerificationCode.setCreateTime(now);
        userVerificationCode.setExpiredTime(expiredTime);
        return userVerificationCode;
    }

    private String generateRandomUUID() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }

    public UserVerificationCode getVerificationCode(String code) {
        return verificationCodeDao.selectVerificationCodeByCode(code);
    }

    public void setCodeDeleted(String code) {
        verificationCodeDao.deleteVerificationCodeByCode(code);
    }

    public boolean isValidCode(String code) {
        UserVerificationCode verificationCode = getVerificationCode(code);
        if (verificationCode == null || verificationCode.isDeleted()) {
            return false;
        }

        LocalDateTime expireTime = verificationCode.getExpiredTime();
        return !expireTime.isBefore(LocalDateTime.now());
    }
}
