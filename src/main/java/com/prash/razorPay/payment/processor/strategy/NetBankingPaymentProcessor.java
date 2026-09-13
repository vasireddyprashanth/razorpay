package com.prash.razorPay.payment.processor.strategy;

import com.prash.razorPay.common.util.RandomizerUtil;
import com.prash.razorPay.payment.processor.PaymentProcessor;
import com.prash.razorPay.payment.processor.dto.PaymentProcessorRequest;
import com.prash.razorPay.payment.processor.dto.PaymentProcessorResponse;

public class NetBankingPaymentProcessor implements PaymentProcessor {


    @Override
    public PaymentProcessorResponse charge(PaymentProcessorRequest request) {

        final String BANK_CODE_FAIL= "BANK_CODE_FAIL";

        String bankCode= request.methodDetails() != null ?
                request.methodDetails().get("bank").toString() : null;

        //simulation
        if(BANK_CODE_FAIL.equals(bankCode)){
            return new PaymentProcessorResponse.Failure("BANK REJECTED",
                    "Bank rejected the transaction registration"
                    );
        }

        String processorRef= "NBP_PROCESSOR_" + RandomizerUtil.randomBase64(16);

        String redirectRef = "http://REDIRECT_BANK.com/" + processorRef;

        return new PaymentProcessorResponse.Pending(processorRef);
    }
}
