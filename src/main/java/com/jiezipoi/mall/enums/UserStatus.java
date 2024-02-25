package com.jiezipoi.mall.enums;

import org.apache.ibatis.type.MappedTypes;

public enum UserStatus {
    UNACTIVATED(0), ACTIVATED(1), DELETED(2);

    UserStatus(int code) {
        this.code = code;
    }

    private final int code;

    public int getCode() {
        return code;
    }
}