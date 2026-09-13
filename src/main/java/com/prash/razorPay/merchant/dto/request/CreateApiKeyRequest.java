package com.prash.razorPay.merchant.dto.request;

import com.prash.razorPay.common.enums.Environment;

public record CreateApiKeyRequest(
        Environment environment
) {
}
