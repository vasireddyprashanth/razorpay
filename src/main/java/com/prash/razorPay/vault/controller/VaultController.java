package com.prash.razorPay.vault.controller;

import com.prash.razorPay.merchant.security.AuthenticatedMerchant;
import com.prash.razorPay.vault.dto.request.TokenizeRequest;
import com.prash.razorPay.vault.dto.response.TokenizeResponse;
import com.prash.razorPay.vault.service.VaultService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/v1/vault")
@RequiredArgsConstructor
public class VaultController {

    private final VaultService vaultService;
  //  UUID merchantId= UUID.fromString("6f3ca207-0b8f-4b6b-83a2-f2e433f9f41a");


    @PostMapping("/tokenize")
    public ResponseEntity<TokenizeResponse> tokenize(
            @AuthenticationPrincipal AuthenticatedMerchant authenticatedMerchant,
            @RequestBody TokenizeRequest request
    ){
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(vaultService.tokenize(request, authenticatedMerchant.merchantId()));
    }
}
