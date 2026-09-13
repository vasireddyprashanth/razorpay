package com.prash.razorPay.payment.config;

import com.prash.razorPay.common.enums.PaymentMethod;
import com.prash.razorPay.payment.gateway.PaymentAdapter;
import com.prash.razorPay.payment.gateway.adapter.CardPaymentAdapter;
import com.prash.razorPay.payment.gateway.adapter.NetBankingAdapter;
import com.prash.razorPay.payment.gateway.adapter.UpiPaymentAdapter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.util.Map;

@Configuration
@RequiredArgsConstructor
class PaymentAdapterConfig {

    private final CardPaymentAdapter cardPaymentAdapter;
    private final NetBankingAdapter netBankingAdapter;
    private final UpiPaymentAdapter upiPaymentAdapter;

    @Bean
    public Map< PaymentMethod, PaymentAdapter> paymentAdapterPaymentMethodMap(){
        return Map.of(PaymentMethod.CARD,cardPaymentAdapter,
                PaymentMethod.NETBANKING,netBankingAdapter,
                PaymentMethod.UPI, upiPaymentAdapter

        );
    }
}
