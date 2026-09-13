package com.prash.razorPay.vault.service;

import com.prash.razorPay.common.entity.Money;
import com.prash.razorPay.payment.processor.dto.PaymentProcessorResponse;
import com.prash.razorPay.vault.dto.request.TokenizeRequest;
import com.prash.razorPay.vault.dto.response.TokenizeResponse;

import java.util.Map;
import java.util.UUID;

public interface VaultService {
    TokenizeResponse tokenize(TokenizeRequest request, UUID merchantId);

    PaymentProcessorResponse charge(String token, UUID paymentId, Money amount, Map<String, Object> stringObjectMap);
}
