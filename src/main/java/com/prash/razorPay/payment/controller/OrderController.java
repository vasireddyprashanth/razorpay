package com.prash.razorPay.payment.controller;


import com.prash.razorPay.merchant.security.AuthenticatedMerchant;
import com.prash.razorPay.payment.dto.request.CreateOrderRequest;
import com.prash.razorPay.payment.dto.response.OrderResponse;
import com.prash.razorPay.payment.service.OrderService;
import jakarta.validation.Valid;
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
@RequiredArgsConstructor
@RequestMapping("/v1/orders")
public class OrderController {

    private final OrderService orderService;
    //private final AuthenticatedMerchant authenticatedMerchant;


    @PostMapping
    public ResponseEntity<OrderResponse>create(@RequestBody @Valid CreateOrderRequest request,
                                               @AuthenticationPrincipal AuthenticatedMerchant authenticatedMerchant){
        return ResponseEntity.status(HttpStatus.CREATED).body(
                orderService.create(authenticatedMerchant.merchantId(), request)
        );
    }
}
