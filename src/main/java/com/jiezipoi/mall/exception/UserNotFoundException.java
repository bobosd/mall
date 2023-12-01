package com.jiezipoi.mall.exception;

public class UserNotFoundException extends Exception {
    private final String userEmail;

    public UserNotFoundException(String userEmail) {
        super("user not found by email: " + userEmail);
        this.userEmail = userEmail;
    }

    public String getUserEmail() {
        return userEmail;
    }
}
