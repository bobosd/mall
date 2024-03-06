package com.jiezipoi.mall.exception;

public class UnactivatedUserException extends Exception {
    private final String userEmail;

    public UnactivatedUserException(String message, String userEmail) {
        super(message);
        this.userEmail = userEmail;
    }

    public String getUserEmail() {
        return userEmail;
    }
}
