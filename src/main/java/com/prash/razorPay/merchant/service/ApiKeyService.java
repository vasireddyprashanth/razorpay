package com.prash.razorPay.merchant.service;

import com.prash.razorPay.merchant.dto.request.CreateApiKeyRequest;
import com.prash.razorPay.merchant.dto.response.ApiKeyCreateResponse;
import com.prash.razorPay.merchant.dto.response.ApiKeyResponse;

import java.util.List;
import java.util.UUID;

public interface ApiKeyService {
    ApiKeyCreateResponse create(UUID merchantId, CreateApiKeyRequest request);

    List<ApiKeyResponse> listByMerchant(UUID merchantId);

    void revoke(UUID merchantId, UUID keyId);

    ApiKeyCreateResponse rotate(UUID merchantId, UUID keyId);
}
