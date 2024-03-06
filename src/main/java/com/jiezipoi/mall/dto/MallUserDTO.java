package com.jiezipoi.mall.dto;

import com.jiezipoi.mall.entity.MallUser;

public class MallUserDTO {
    private String nickName;
    private String email;
    private String introduceSign;

    public MallUserDTO(MallUser mallUser) {
        this.nickName = mallUser.getNickName();
        this.email = mallUser.getEmail();
        this.introduceSign = mallUser.getIntroduceSign();
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getIntroduceSign() {
        return introduceSign;
    }

    public void setIntroduceSign(String introduceSign) {
        this.introduceSign = introduceSign;
    }
}