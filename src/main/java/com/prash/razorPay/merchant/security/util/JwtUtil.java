package com.prash.razorPay.merchant.security.util;

import com.prash.razorPay.merchant.security.AuthenticatedMerchant;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class JwtUtil {

    @Value("${jwt.secret-key}")
    private  String secretKey;


    private SecretKey getSecretKey(){
        return Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
    }

    public String generateAccessToken(String email, UUID merchantId, String role){

        if (role == null || role.isBlank()) {
            throw new IllegalArgumentException("Role cannot be null or blank");
        }

        String authority = role.startsWith("ROLE_") ? role : "ROLE_" + role;

        Instant now = Instant.now();
        return Jwts.builder()
                .subject(email)
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusSeconds(60*100)))
                .claim("merchant_id", merchantId.toString())// store as String                .claim("role", role)
                .claim("role", authority)
                .signWith(getSecretKey())
                .compact();

    }

    public Claims extractAllClaims(String token){
        Claims payload = Jwts.parser()
                .verifyWith(getSecretKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();

        return payload;
    }

    public UUID getMerchantId(Claims claims) {
        return UUID.fromString(claims.get("merchant_id", String.class));
    }





    public Claims verifyAccessToken(String accessToken){
        Claims payload = Jwts.parser()
                .verifyWith(getSecretKey())
                .build()
                .parseSignedClaims(accessToken)
                .getPayload();

        return payload;
    }

    public String extractRole(Claims claims){
        return claims.get("role", String.class);
    }

}
