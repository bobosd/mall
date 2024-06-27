package com.jiezipoi.mall.dto;

import com.jiezipoi.mall.entity.UserPayment;
import com.jiezipoi.mall.enums.PaymentType;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class UserPaymentDTO {
    public UserPaymentDTO() {

    }

    public UserPaymentDTO(UserPayment userPayment) {
        this.userPaymentId = userPayment.getUserPaymentId();
        this.paymentType = userPayment.getPaymentType();
        this.lastFourDigit = userPayment.getCardNumber().substring(userPayment.getCardNumber().length() - 4);
        this.cardHolderName = userPayment.getCardHolderName();
        this.expirationDate = userPayment.getExpirationDate();
        this.defaultPayment = userPayment.isDefaultPayment();
    }

    private Long userPaymentId;
    private PaymentType paymentType;
    private String lastFourDigit;
    private String cardHolderName;
    private LocalDate expirationDate;
    private Boolean defaultPayment;

    public Long getUserPaymentId() {
        return userPaymentId;
    }

    public void setUserPaymentId(Long userPaymentId) {
        this.userPaymentId = userPaymentId;
    }

    public PaymentType getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(PaymentType paymentType) {
        this.paymentType = paymentType;
    }

    public String getLastFourDigit() {
        return lastFourDigit;
    }

    public void setLastFourDigit(String lastFourDigit) {
        this.lastFourDigit = lastFourDigit;
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

    public Boolean isDefaultPayment() {
        return defaultPayment;
    }

    public void setDefaultPayment(Boolean defaultPayment) {
        this.defaultPayment = defaultPayment;
    }

    public String getExpirationInCardFormat() {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("MM/yy");
        return dateTimeFormatter.format(this.expirationDate);
    }
}
