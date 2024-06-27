package com.jiezipoi.mall.enums;

public enum PaymentType {
    VISA("VISA"),
    MASTERCARD("Mastercard"),
    PAYPAL("PayPal"),
    AMERICAN_EXPRESS("American Express");

    private final String name;

    PaymentType(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
