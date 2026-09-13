package com.prash.razorPay.payment.processor.strategy;

import com.prash.razorPay.common.util.RandomizerUtil;
import com.prash.razorPay.payment.processor.PaymentProcessor;
import com.prash.razorPay.payment.processor.dto.PaymentProcessorRequest;
import com.prash.razorPay.payment.processor.dto.PaymentProcessorResponse;

public class CardPaymentProcessor implements PaymentProcessor {

    public static final String PAN_CARD_DECLINED = " 400000000000002";
    public static final String PAN_CARD_EXPIRED = " 4000000002302223";

    @Override
    public PaymentProcessorResponse charge(PaymentProcessorRequest request) {

        String pan = request.pan();

        if(PAN_CARD_DECLINED.equals(pan)){
            return new PaymentProcessorResponse.Failure("CARD_DECLINED", "Card declined");
        }

        if(PAN_CARD_EXPIRED.equals(pan)){
            return new PaymentProcessorResponse.Failure("CARD_DECLINED", "Card has expired");
        }

        String processorRef = "CARD_PROCESSOR_"+ RandomizerUtil.randomBase64(16);

        return new PaymentProcessorResponse.Pending(processorRef);
    }
}
