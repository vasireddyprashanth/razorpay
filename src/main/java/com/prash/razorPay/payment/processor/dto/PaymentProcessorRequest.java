package com.prash.razorPay.payment.processor.dto;

import com.prash.razorPay.common.entity.Money;
import com.prash.razorPay.common.enums.PaymentMethod;

import java.util.Map;
import java.util.UUID;

public record PaymentProcessorRequest(
        UUID paymentId,
        UUID processingId,
        PaymentMethod method,
        Money amount,
        String pan,
        String expiry,
        Map<String,Object> methodDetails
) {
    public static PaymentProcessorRequest card(UUID paymentId, String pan, String expiry, Money amount, Map<String, Object> details){
        return new PaymentProcessorRequest(paymentId, UUID.randomUUID(), PaymentMethod.CARD,  amount, pan,expiry,details);
    }

    public static  PaymentProcessorRequest nonCard(UUID paymentId, Money amount,Map<String,Object> details, PaymentMethod method){
        return new PaymentProcessorRequest(paymentId, UUID.randomUUID(), method,amount,null, null, details);
    }
}
