package com.prash.razorPay.merchant.dto.response;

import com.prash.razorPay.common.enums.BusinessType;
import com.prash.razorPay.common.enums.MerchantStatus;

import java.util.UUID;

public record MerchantResponse(
        UUID id,
        String name,
        String email,
        String businessName,
        BusinessType businessType,
        MerchantStatus status
) {
}
