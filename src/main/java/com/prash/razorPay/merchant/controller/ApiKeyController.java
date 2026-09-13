package com.prash.razorPay.merchant.controller;


import com.prash.razorPay.merchant.dto.request.CreateApiKeyRequest;
import com.prash.razorPay.merchant.dto.response.ApiKeyCreateResponse;
import com.prash.razorPay.merchant.dto.response.ApiKeyResponse;
import com.prash.razorPay.merchant.security.AuthenticatedMerchant;
import com.prash.razorPay.merchant.security.util.AuthenticatedMerch;
import com.prash.razorPay.merchant.security.util.JwtUtil;
import com.prash.razorPay.merchant.service.ApiKeyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/v1/merchants/api-keys")
@RequiredArgsConstructor
public class ApiKeyController {

  private final  ApiKeyService apiKeyService;
  private final AuthenticatedMerch authenticatedMerch;


  @PostMapping
  public ResponseEntity<ApiKeyCreateResponse>create(
          @AuthenticationPrincipal AuthenticatedMerchant authenticatedMerchant,
          @RequestBody CreateApiKeyRequest request){
      System.out.println(authenticatedMerchant.merchantId());
      return ResponseEntity.status(HttpStatus.CREATED).body(
              apiKeyService.create(authenticatedMerchant.merchantId(),request)
      );
  }

  @GetMapping
  public ResponseEntity<List<ApiKeyResponse>>listByMerchant(@AuthenticationPrincipal AuthenticatedMerchant auth){
      return ResponseEntity.ok(
              apiKeyService.listByMerchant(auth.merchantId())
      );
  }

  @DeleteMapping("/{keyId}")
  public ResponseEntity<Void>revoke(@AuthenticationPrincipal AuthenticatedMerchant auth,@PathVariable UUID keyId){
      apiKeyService.revoke(auth.merchantId(),keyId);
      return ResponseEntity.noContent().build();

  }

  @PostMapping("/{keyId}/rotate")
  public ResponseEntity<ApiKeyCreateResponse>rotateKey(@AuthenticationPrincipal AuthenticatedMerchant auth, @PathVariable UUID keyId){
      return ResponseEntity.ok(
              apiKeyService.rotate(auth.merchantId(),keyId)
      );
  }

}
