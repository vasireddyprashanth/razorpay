package com.prash.razorPay.payment.processor;

import com.prash.razorPay.payment.processor.dto.PaymentProcessorRequest;
import com.prash.razorPay.payment.processor.dto.PaymentProcessorResponse;

public interface PaymentProcessor {

    PaymentProcessorResponse charge(PaymentProcessorRequest request);
}
