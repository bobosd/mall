package com.jiezipoi.mall.entity;

import com.jiezipoi.mall.enums.VerificationActionType;

import java.time.LocalDateTime;

public class UserVerificationCode {
    private String email;
    private String verificationCode;
    private VerificationActionType ActionType;
    private LocalDateTime createTime;
    private LocalDateTime expiredTime;
    private Boolean isDeleted;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getVerificationCode() {
        return verificationCode;
    }

    public void setVerificationCode(String verificationCode) {
        this.verificationCode = verificationCode;
    }

    public VerificationActionType getActionType() {
        return ActionType;
    }

    public void setActionType(VerificationActionType actionType) {
        this.ActionType = actionType;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public LocalDateTime getExpiredTime() {
        return expiredTime;
    }

    public void setExpiredTime(LocalDateTime expiredTime) {
        this.expiredTime = expiredTime;
    }

    public Boolean isDeleted() {
        return isDeleted;
    }

    public void setDeleted(Boolean deleted) {
        isDeleted = deleted;
    }
}
