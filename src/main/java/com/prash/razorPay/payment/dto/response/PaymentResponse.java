package com.prash.razorPay.payment.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.prash.razorPay.common.entity.Money;
import com.prash.razorPay.common.enums.PaymentMethod;
import com.prash.razorPay.common.enums.PaymentStatus;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record PaymentResponse(
        UUID id,
        UUID orderId,
        UUID merchantId,
        Money amount,
        PaymentStatus status,
        PaymentMethod method,
        Map<String,Object> notes,
        String cardLastFour,
        String cardBrand,
        String bankReference,
        String errorCode,
        String errorDescription,
        Long refundedAmountPaise,
        LocalDateTime capturedAt,
        LocalDateTime createdAt
) {
}
