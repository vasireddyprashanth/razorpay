package com.prash.razorPay.payment.config;

import com.prash.razorPay.common.enums.PaymentMethod;
import com.prash.razorPay.payment.processor.PaymentProcessor;
import com.prash.razorPay.payment.processor.strategy.CardPaymentProcessor;
import com.prash.razorPay.payment.processor.strategy.NetBankingPaymentProcessor;
import com.prash.razorPay.payment.processor.strategy.UpiPaymentProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.util.Map;

@Configuration
public class PaymentProcessorConfig {
    @Bean
    public Map<PaymentMethod, PaymentProcessor> paymentMethodPaymentProcessorMap(){
        return Map.of(
                PaymentMethod.CARD,new CardPaymentProcessor(),
                PaymentMethod.NETBANKING, new NetBankingPaymentProcessor(),
                PaymentMethod.UPI, new UpiPaymentProcessor()
        );
    }
}

