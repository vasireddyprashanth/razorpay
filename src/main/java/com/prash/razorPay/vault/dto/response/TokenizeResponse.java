package com.prash.razorPay.vault.dto.response;

import com.prash.razorPay.common.enums.CardBrand;

public record TokenizeResponse(
        String token,
        String lastFour,
        CardBrand cardBrand,
        Integer expiryMonth,
        Integer expiryYear

) {
}
