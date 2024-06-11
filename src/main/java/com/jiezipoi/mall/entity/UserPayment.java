package com.jiezipoi.mall.entity;

import com.jiezipoi.mall.enums.PaymentType;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class UserPayment {
    private Long userPaymentId;
    private Long userId;
    private PaymentType paymentType;
    private String cardNumber;
    private String cardHolderName;
    private LocalDate expirationDate;
    private String CVV;
    private LocalDateTime createTime;
    private Boolean defaultPayment;

    public Long getUserPaymentId() {
        return userPaymentId;
    }

    public void setUserPaymentId(Long userPaymentId) {
        this.userPaymentId = userPaymentId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public PaymentType getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(PaymentType paymentType) {
        this.paymentType = paymentType;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public String getCardHolderName() {
        return cardHolderName;
    }

    public void setCardHolderName(String cardHolderName) {
        this.cardHolderName = cardHolderName;
    }

    public LocalDate getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(LocalDate expirationDate) {
        this.expirationDate = expirationDate;
    }

    public String getCVV() {
        return CVV;
    }

    public void setCVV(String CVV) {
        this.CVV = CVV;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public Boolean isDefaultPayment() {
        return defaultPayment;
    }

    public void setDefaultPayment(Boolean defaultPayment) {
        this.defaultPayment = defaultPayment;
    }
}
