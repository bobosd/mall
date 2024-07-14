package com.jiezipoi.mall.enums;

public enum OrderStatus {
    PENDING_PAYMENT, PAID, PREPARING_SHIPMENT, SHIPPED, DELIVERED, CLOSED;

    public OrderStatus next() {
        return switch (this) {
            case PENDING_PAYMENT -> PAID;
            case PAID -> PREPARING_SHIPMENT;
            case PREPARING_SHIPMENT -> SHIPPED;
            case SHIPPED -> DELIVERED;
            default -> CLOSED;
        };
    }
}
