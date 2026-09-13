package com.prash.razorPay.common.entity;

import com.prash.razorPay.merchant.entity.ApiKey;
import com.prash.razorPay.merchant.security.AuthenticatedMerchant;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component("auditingAwareImpl")
public class AuditingAwareImpl implements AuditorAware<String> {


    @Override
    public Optional<String> getCurrentAuditor() {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || auth.getPrincipal().equals("anonymousUser")) {
            return Optional.of("SYSTEM");  // default for unauthenticated requests
        }

        Object principal = auth.getPrincipal();

        // If it's an ApiKey, get the keyId
        if (principal instanceof ApiKey) {
            return Optional.of(((ApiKey) principal).getKeyId());
        }

        // If it's an AuthenticatedMerchant, get the merchantId
        if (principal instanceof AuthenticatedMerchant) {
            return Optional.of(((AuthenticatedMerchant) principal).merchantId().toString());
        }

        // If it's a String (username/email), return it
        if (principal instanceof String) {
            return Optional.of((String) principal);
        }

        return Optional.of("UNKNOWN");
    }
}
