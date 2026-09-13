package com.prash.razorPay.merchant.service.Impl;

import com.prash.razorPay.common.exception.ResourceNotFoundException;
import com.prash.razorPay.common.util.HashUtil;
import com.prash.razorPay.common.util.RandomizerUtil;
import com.prash.razorPay.merchant.dto.request.CreateApiKeyRequest;
import com.prash.razorPay.merchant.dto.response.ApiKeyCreateResponse;
import com.prash.razorPay.merchant.dto.response.ApiKeyResponse;
import com.prash.razorPay.merchant.entity.ApiKey;
import com.prash.razorPay.merchant.entity.Merchant;
import com.prash.razorPay.merchant.mapper.ApiKeyMapper;
import com.prash.razorPay.merchant.repository.ApiKeyRepository;
import com.prash.razorPay.merchant.repository.MerchantRepository;
import com.prash.razorPay.merchant.service.ApiKeyService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ApiKeyServiceImpl implements ApiKeyService {

    private final MerchantRepository merchantRepository;
    private final ApiKeyRepository apiKeyRepository;
    private final ApiKeyMapper apiKeyMapper;


    @Override
    @Transactional

    public ApiKeyCreateResponse create(UUID merchantId, CreateApiKeyRequest request) {
        Merchant merchant = merchantRepository.findById(merchantId).orElseThrow(() -> new ResourceNotFoundException("merchant", merchantId));

        String keyId="rzp_"+request.environment().name().toLowerCase()+"_"+ RandomizerUtil.randomBase64(24);
        log.info("GENERATED keyId = [{}]", keyId);

        String rawSecret=RandomizerUtil.randomBase64(32);//TODO: replace with cryptograhic random hex
        String hex = HashUtil.toSha256Hex(rawSecret);

        ApiKey apiKey=ApiKey.builder()
                        .merchant(merchant)
                                .keyId(keyId)
                                        .keySecretHash(hex)
                                                .environment(request.environment()).
                                                            build();

        apiKey=  apiKeyRepository.save(apiKey);

        return new ApiKeyCreateResponse(apiKey.getId(),keyId,rawSecret,request.environment());
    }

    @Override
    public List<ApiKeyResponse> listByMerchant(UUID merchantId) {
        List<ApiKey> merchants = apiKeyRepository.findByMerchantId(merchantId);
        return apiKeyMapper.toResponseList(merchants);
    }

    @Override
    @Transactional
    public void revoke(UUID merchantId, UUID keyId) {
        ApiKey apiKey = apiKeyRepository.findById(keyId)
                .filter(k -> k.getMerchant().getId().equals(merchantId))
                .orElseThrow(() -> new ResourceNotFoundException("ApiKey", keyId));

        apiKey.setEnabled(false);
        apiKeyRepository.save(apiKey);


    }

    @Override
    @Transactional
    public ApiKeyCreateResponse rotate(UUID merchantId, UUID keyId) {
        ApiKey apiKey= apiKeyRepository.findById(keyId)
                .filter(k->k.getMerchant().getId().equals(merchantId))
                .orElseThrow(()->new ResourceNotFoundException("merchantId",merchantId) );

        String newSecretHash=RandomizerUtil.randomBase64(32);
        apiKey.setPreviousKeySecretHash(apiKey.getKeySecretHash());
        apiKey.setKeySecretHash(newSecretHash);
        apiKey.setRotatedAt(LocalDateTime.now());
        apiKey.setGracePeriodExpiresAt(LocalDateTime.now().plusHours(24));

        ApiKey save = apiKeyRepository.save(apiKey);

        //we are not using MapStruct here because we need provide the Raw secret
        return new ApiKeyCreateResponse(apiKey.getId(),apiKey.getKeyId(),newSecretHash,apiKey.getEnvironment());
    }
}
