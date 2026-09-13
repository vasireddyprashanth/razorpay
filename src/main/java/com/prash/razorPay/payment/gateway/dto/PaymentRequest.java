package com.prash.razorPay.payment.gateway.dto;

import com.prash.razorPay.common.entity.Money;
import com.prash.razorPay.common.enums.PaymentMethod;
import lombok.Builder;
import lombok.Getter;
import org.springframework.validation.ObjectError;

import java.util.Map;
import java.util.UUID;

@Builder
public record PaymentRequest(
        UUID paymentId,
        UUID orderId,
        UUID merchantId,
        Money amount,
        PaymentMethod method,
        Map<String, Object> methodDetails
) {
}
