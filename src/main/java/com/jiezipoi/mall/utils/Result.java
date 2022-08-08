package com.jiezipoi.mall.utils;

public class Result<T> {
    String message;
    int code;
    T data;

    public Result(String message, int code, T data) {
        this.message = message;
        this.code = code;
        this.data = data;
    }

    public Result() {

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
}