package com.jiezipoi.mall.utils;

import org.springframework.context.i18n.LocaleContextHolder;

import java.util.Locale;

public class Response<T> {
    String message;
    int code;
    T data;

    public Response(String message, int code, T data) {
        this.message = message;
        this.code = code;
        this.data = data;
    }

    public Response() {

    }

    public Response(CommonResponse response) {
        Locale locale = LocaleContextHolder.getLocale();
        this.message = response.getMessage(locale);
        this.code = response.getCode();
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "Result{" +
                "message='" + message + '\'' +
                ", code=" + code +
                ", data=" + data +
                '}';
    }

    public void setResponse(CommonResponse response) {
        Locale locale = LocaleContextHolder.getLocale();
        this.message = response.getMessage(locale);
        this.code = response.getCode();
    }
}