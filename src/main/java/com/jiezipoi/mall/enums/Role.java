package com.jiezipoi.mall.enums;

public enum Role {
    ADMIN(1), USER(2);
    private final long value;

    Role(int value) {
        this.value = value;
    }

    public long getValue() {
        return value;
    }
}
