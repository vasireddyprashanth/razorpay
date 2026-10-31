package com.prash.razorPay.merchant.cache;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.Duration;
import java.util.Optional;

@Component
@Slf4j
@RequiredArgsConstructor
public class RedisApiKeyCache implements ApiKeyCache{

    private static final String PREFIX = "apikey:";
    private static final Duration TTL = Duration.ofMinutes(5);

    private final StringRedisTemplate stringRedisTemplate;
    private final ObjectMapper objectMapper;
    @Override
    public Optional<ApiKeyCacheEntry> get(String apiKey) {
        try {

            String json = stringRedisTemplate.opsForValue().get(PREFIX + apiKey);
            if(json==null ) return Optional.empty();

            return Optional.of(objectMapper.readValue(json,ApiKeyCacheEntry.class));

        } catch (Exception e) {
            log.warn("API KEY cache read failed, keyId{}", apiKey);
            return Optional.empty();
        }


    }

    @Override
    public void put(String apiKey, ApiKeyCacheEntry apiKeyCacheEntry) {

        try {
            stringRedisTemplate.opsForValue().set(PREFIX+apiKey,
                    objectMapper.writeValueAsString(apiKeyCacheEntry),
                    TTL);

        } catch (Exception e) {
            log.warn("API KEY cache set failed, keyId{}", apiKey);
            throw new RuntimeException("Redis cache write failed", e);
        }

    }

    @Override
    public void evict(String apiKey) {
        stringRedisTemplate.delete(PREFIX+apiKey);
    }
}
