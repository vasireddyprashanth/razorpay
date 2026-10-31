package com.prash.razorPay.merchant.cache;

import com.prash.razorPay.common.enums.Environment;
import com.prash.razorPay.merchant.entity.Merchant;

import java.time.LocalDateTime;
import java.util.UUID;

public record ApiKeyCacheEntry(

        UUID merchantId,
        String keyId,
        String keySecretHash,
        String previousKeySecretHash,
        Environment environment,
        LocalDateTime gracePeriodExpiresAt,
        boolean enabled

) {
    public boolean isInGracePeriod(){
        return gracePeriodExpiresAt !=null && LocalDateTime.now().isBefore(gracePeriodExpiresAt);

    }
}
