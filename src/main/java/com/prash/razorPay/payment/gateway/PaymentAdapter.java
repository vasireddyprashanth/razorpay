package com.prash.razorPay.payment.gateway;

import com.prash.razorPay.payment.gateway.dto.PaymentRequest;
import com.prash.razorPay.payment.gateway.dto.PaymentResult;

import java.util.UUID;

public interface PaymentAdapter {

    PaymentResult initiate(PaymentRequest request);

    PaymentResult capture(UUID paymentId);
}
