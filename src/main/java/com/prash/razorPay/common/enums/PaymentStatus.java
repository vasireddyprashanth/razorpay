package com.prash.razorPay.common.enums;

public enum PaymentStatus {
    CREATED,
    AUTHORIZING,
    AUTHORIZED,
    CAPTURING,
    CAPTURED,
    FAILED,
    CANCELLED,
    REFUNDED,
    PARTIAL_REFUNDED,
    SETTLED,
    AUTH_EXPIRED
}
