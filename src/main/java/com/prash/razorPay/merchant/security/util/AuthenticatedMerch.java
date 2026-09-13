package com.prash.razorPay.merchant.security.util;

import lombok.Getter;
import lombok.Setter;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.context.annotation.RequestScope;

import java.util.UUID;

@Getter
@Setter
@Component
@RequestScope(proxyMode = ScopedProxyMode.TARGET_CLASS)
public class AuthenticatedMerch {
    UUID merchantId;
}
