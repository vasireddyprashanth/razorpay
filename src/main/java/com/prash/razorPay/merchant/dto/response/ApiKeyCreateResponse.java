package com.prash.razorPay.merchant.dto.response;

import com.prash.razorPay.common.enums.Environment;

import java.util.UUID;

public record ApiKeyCreateResponse(
        UUID id,
        String keyId,
        String keySecret,
        Environment environment
) {
}
