package com.prash.razorPay.payment.statemachine;

import com.prash.razorPay.common.enums.PaymentActor;
import com.prash.razorPay.common.enums.PaymentEvent;
import com.prash.razorPay.common.enums.PaymentStatus;
import com.prash.razorPay.payment.entity.Payment;
import com.prash.razorPay.payment.entity.PaymentTransitionLog;
import com.prash.razorPay.payment.repository.PaymentTransitionLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PaymentTransitionService {

    private final PaymentTransitionLogRepository paymentTransitionLogRepository;
    private final PaymentStateMachine paymentStateMachine;

    public PaymentStatus apply(Payment payment, PaymentEvent event){
        PaymentStatus next = paymentStateMachine.transition(payment.getStatus(), event);

        PaymentTransitionLog paymentTransitionLog=PaymentTransitionLog.builder()
                .payment(payment)
                .fromStatus(payment.getStatus())
                .event(event)
                .toStatus(next)
                .actor(PaymentActor.SYSTEM)
                .occurredAt(LocalDateTime.now())
                .build();

        payment.setStatus(next);

        paymentTransitionLogRepository.save(paymentTransitionLog);

        return next;
    }
}
