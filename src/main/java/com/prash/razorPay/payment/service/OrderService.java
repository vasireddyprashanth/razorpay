package com.prash.razorPay.payment.service;

import com.prash.razorPay.payment.dto.response.OrderResponse;
import com.prash.razorPay.payment.dto.request.CreateOrderRequest;
import com.prash.razorPay.payment.dto.response.PaymentResponse;
import jakarta.validation.constraints.Size;


import java.util.List;
import java.util.UUID;

public interface OrderService {
    OrderResponse create(UUID merchantId, CreateOrderRequest request);
    OrderResponse getById(UUID merchantId, UUID orderId);
    OrderResponse cancel(UUID merchantId, UUID orderId);
    List<PaymentResponse> listPayments(UUID merchantId, UUID orderId);
}
