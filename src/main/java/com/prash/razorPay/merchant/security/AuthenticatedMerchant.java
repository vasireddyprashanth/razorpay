package com.prash.razorPay.merchant.security;

import lombok.Builder;

import java.util.UUID;

@Builder
public record AuthenticatedMerchant(
        UUID merchantId,
        String email,
        String apiKey
) {
}

