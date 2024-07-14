package com.jiezipoi.mall.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.jiezipoi.mall.enums.OrderStatus;
import com.jiezipoi.mall.enums.PaymentStatus;
import com.jiezipoi.mall.enums.PaymentType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Order {
    private Long orderId;
    private String orderNumber;
    private Long userId;
    private BigDecimal totalPrice;
    private PaymentStatus payStatus;
    private PaymentType payType;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime payTime;
    private OrderStatus orderStatus;
    private String extraInfo;
    private String name;
    private String surname;
    private String shippingPhoneNumber;
    private String shippingProvince;
    private String shippingCity;
    private String shippingStreetAddress;
    private String shippingPostalCode;
    private Boolean isDeleted;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }

    public PaymentStatus getPayStatus() {
        return payStatus;
    }

    public void setPayStatus(PaymentStatus payStatus) {
        this.payStatus = payStatus;
    }

    public PaymentType getPayType() {
        return payType;
    }

    public void setPayType(PaymentType payType) {
        this.payType = payType;
    }

    public LocalDateTime getPayTime() {
        return payTime;
    }

    public void setPayTime(LocalDateTime payTime) {
        this.payTime = payTime;
    }

    public OrderStatus getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(OrderStatus orderStatus) {
        this.orderStatus = orderStatus;
    }

    public String getExtraInfo() {
        return extraInfo;
    }

    public void setExtraInfo(String extraInfo) {
        this.extraInfo = extraInfo;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getShippingPhoneNumber() {
        return shippingPhoneNumber;
    }

    public void setShippingPhoneNumber(String shippingPhoneNumber) {
        this.shippingPhoneNumber = shippingPhoneNumber;
    }

    public String getShippingProvince() {
        return shippingProvince;
    }

    public void setShippingProvince(String shippingProvince) {
        this.shippingProvince = shippingProvince;
    }

    public String getShippingCity() {
        return shippingCity;
    }

    public void setShippingCity(String shippingCity) {
        this.shippingCity = shippingCity;
    }

    public String getShippingStreetAddress() {
        return shippingStreetAddress;
    }

    public void setShippingStreetAddress(String shippingStreetAddress) {
        this.shippingStreetAddress = shippingStreetAddress;
    }

    public String getShippingPostalCode() {
        return shippingPostalCode;
    }

    public void setShippingPostalCode(String shippingPostalCode) {
        this.shippingPostalCode = shippingPostalCode;
    }

    public Boolean getDeleted() {
        return isDeleted;
    }

    public void setDeleted(Boolean deleted) {
        isDeleted = deleted;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public LocalDateTime getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
    }
}
