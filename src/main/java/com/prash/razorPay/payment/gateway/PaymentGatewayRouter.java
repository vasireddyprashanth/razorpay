package com.prash.razorPay.payment.gateway;

import com.prash.razorPay.common.enums.PaymentMethod;
import com.prash.razorPay.payment.dto.response.PaymentResponse;
import com.prash.razorPay.payment.gateway.dto.PaymentRequest;
import com.prash.razorPay.payment.gateway.dto.PaymentResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.UUID;

@RequiredArgsConstructor
@Component
public class PaymentGatewayRouter {

    private final Map<PaymentMethod,PaymentAdapter> paymentAdapters;
    
    public PaymentResult initiate(PaymentRequest request){
        PaymentAdapter adapter = paymentAdapters.get(request.method());
        if (adapter == null) {
            throw new UnsupportedOperationException(
                    "Unsupported payment method: " + request.method()
            );
        }
       return adapter.initiate(request);
    }

    public PaymentResult capture(PaymentMethod method, UUID paymentId) {
        PaymentAdapter adapter = paymentAdapters.get(method);
        if(adapter==null){
            throw new UnsupportedOperationException(
                    "Unsupported payment method: " + method
            );
        }
        return adapter.capture(paymentId);
    }
}
