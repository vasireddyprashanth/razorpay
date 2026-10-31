package com.prash.razorPay.merchant.cache;

import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public interface ApiKeyCache {

     Optional<ApiKeyCacheEntry> get(String apiKey);

     public void put(String apiKey,ApiKeyCacheEntry apiKeyCacheEntry);

     public void evict(String apiKey);
}
