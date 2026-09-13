package com.prash.razorPay.payment.controller;

import com.prash.razorPay.merchant.security.AuthenticatedMerchant;
import com.prash.razorPay.payment.dto.request.PaymentInitRequest;
import com.prash.razorPay.payment.dto.response.PaymentResponse;
import com.prash.razorPay.payment.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RequestMapping("/v1/payments")
@RequiredArgsConstructor
@RestController
public class PaymentController {

    private final PaymentService paymentService;
    //UUID merchantId= UUID.fromString("4a3fd11b-1f19-41aa-83bd-7d9ae700d58d");

    @PostMapping
    public ResponseEntity<PaymentResponse> initiate(
            @Valid @RequestBody PaymentInitRequest request,
            @AuthenticationPrincipal AuthenticatedMerchant authenticatedMerchant
            ){
        return ResponseEntity.status(HttpStatus.CREATED).body(
                paymentService.initiate(authenticatedMerchant.merchantId(),request)
        );
    }

    @PostMapping("/{paymentId}/capture")
    public ResponseEntity<PaymentResponse> capture(
            @AuthenticationPrincipal AuthenticatedMerchant authenticatedMerchant,
            @PathVariable UUID paymentId
    ){
        return ResponseEntity.ok(paymentService.capture(authenticatedMerchant.merchantId(), paymentId));
    }
}
