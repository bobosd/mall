package com.jiezipoi.mall.entity;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;

public class UserRefreshToken {

    public UserRefreshToken(String uuid, String mallUserEmail, String encodedRefreshToken, Date createTime,
                            Date expireTime) {
        this.uuid = uuid;
        this.mallUserEmail = mallUserEmail;
        this.encodedRefreshToken = encodedRefreshToken;
        this.createTime = createTime;
        this.expireTime = expireTime;
    }

    private String uuid;
    private String mallUserEmail;
    private String encodedRefreshToken;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date expireTime;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getMallUserEmail() {
        return mallUserEmail;
    }

    public void setMallUserEmail(String mallUserEmail) {
        this.mallUserEmail = mallUserEmail;
    }

    public String getEncodedRefreshToken() {
        return encodedRefreshToken;
    }

    public void setEncodedRefreshToken(String encodedRefreshToken) {
        this.encodedRefreshToken = encodedRefreshToken;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getExpireTime() {
        return expireTime;
    }

    public void setExpireTime(Date expireTime) {
        this.expireTime = expireTime;
    }
}