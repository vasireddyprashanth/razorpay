package com.prash.razorPay.payment.processor;

import com.prash.razorPay.common.enums.PaymentMethod;
import com.prash.razorPay.payment.processor.dto.PaymentProcessorRequest;
import com.prash.razorPay.payment.processor.dto.PaymentProcessorResponse;
import com.prash.razorPay.payment.processor.strategy.CardPaymentProcessor;
import com.prash.razorPay.payment.processor.strategy.NetBankingPaymentProcessor;
import com.prash.razorPay.payment.processor.strategy.UpiPaymentProcessor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class PaymentProcessorRouter {

   private final Map<PaymentMethod, PaymentProcessor>paymentProcessors;

   public PaymentProcessorResponse charge(PaymentProcessorRequest request){
       PaymentProcessor paymentProcessor = paymentProcessors.get(request.method());

       if(paymentProcessor==null){
           throw  new IllegalArgumentException("No payment processor reqistered for method: " + request.method());
       }
     return   paymentProcessor.charge(request);
   }
}
