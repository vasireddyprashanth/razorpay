package com.prash.razorPay.payment.service.impl;

import com.prash.razorPay.common.enums.MerchantStatus;
import com.prash.razorPay.common.enums.OrderStatus;
import com.prash.razorPay.common.enums.PaymentEvent;
import com.prash.razorPay.common.enums.PaymentStatus;
import com.prash.razorPay.common.exception.BusinessRuleViolationException;
import com.prash.razorPay.common.exception.ResourceNotFoundException;
import com.prash.razorPay.payment.dto.request.PaymentInitRequest;
import com.prash.razorPay.payment.dto.response.PaymentResponse;
import com.prash.razorPay.payment.entity.OrderRecord;
import com.prash.razorPay.payment.entity.Payment;
import com.prash.razorPay.payment.gateway.PaymentGatewayRouter;
import com.prash.razorPay.payment.gateway.dto.PaymentRequest;
import com.prash.razorPay.payment.gateway.dto.PaymentResult;
import com.prash.razorPay.payment.mapper.PaymentMapper;
import com.prash.razorPay.payment.repository.OrderRepository;
import com.prash.razorPay.payment.repository.PaymentRepository;
import com.prash.razorPay.payment.service.PaymentService;
import com.prash.razorPay.payment.statemachine.PaymentStateMachine;
import com.prash.razorPay.payment.statemachine.PaymentTransitionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class  PaymentServiceImpl implements PaymentService {

    private final OrderRepository orderRepository;
    private final PaymentRepository paymentRepository;
    private final PaymentGatewayRouter paymentGatewayRouter;
    private final PaymentMapper paymentMapper;
    private final PaymentStateMachine paymentStateMachine;
    private final PaymentTransitionService paymentTransitionService;

    @Override
    @Transactional
    public PaymentResponse  initiate(UUID merchantId, PaymentInitRequest request) {

        OrderRecord order = orderRepository.findByIdAndMerchantId(request.orderId(), merchantId)
                .orElseThrow(() -> new ResourceNotFoundException("Order", request.orderId()));

        if(order.getOrderStatus()!= OrderStatus.CREATED && order.getOrderStatus()!=OrderStatus.ATTEMPTED){
            throw new BusinessRuleViolationException("ORDER_NOT_PAYABLE", "Order cannot accept payment in status: " + order.getOrderStatus());
        }

        order.setOrderStatus(OrderStatus.ATTEMPTED);
        order.setAttempts(order.getAttempts()+1);

        Payment payment=Payment.builder()
                .order(order)
                .merchantId(order.getMerchantId())
                .amount(order.getAmount())
                .method(request.method())
                .idempotencyKey(UUID.randomUUID().toString())
                .status(PaymentStatus.CREATED)
                .methodDetails(request.methodDetails())
                .build();

        payment = paymentRepository.save(payment);



        PaymentRequest paymentRequest= PaymentRequest.builder()
                .paymentId(payment.getId())
                .orderId(request.orderId())
                .merchantId(merchantId)
                .amount(payment.getAmount())
                .methodDetails(payment.getMethodDetails())
                .method(request.method())
                .build();

        paymentTransitionService.apply(payment,PaymentEvent.AUTHORIZE_ATTEMPT);

       PaymentResult result= paymentGatewayRouter.initiate(paymentRequest);

       switch (result){
           case PaymentResult.Pending pending -> payment.setProcessorReference(pending.registrationRef());
           case PaymentResult.Failure failure -> {
//               payment.setStatus(PaymentStatus.FAILED);
               paymentTransitionService.apply(payment,PaymentEvent.AUTHORIZE_FAIL);
               payment.setErrorCode(failure.errorCode());
               payment.setErrorDescription(failure.errorDescription());
           }
           case PaymentResult.Success success -> {

           }
       }

     payment=  paymentRepository.save(payment);
       orderRepository.save(order);


        return paymentMapper.toResponse(payment) ;
    }

    @Override
    @Transactional
    public PaymentResponse capture(UUID merchantId, UUID paymentId) {

        Payment payment = paymentRepository.findByIdAndMerchantId(paymentId, merchantId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment", paymentId));

//        payment.setStatus(PaymentStatus.CAPTURING);
        paymentTransitionService.apply(payment,PaymentEvent.CAPTURE_REQUEST);
        PaymentResult paymentResult   = paymentGatewayRouter.capture(payment.getMethod(), paymentId);

        if(paymentResult instanceof PaymentResult.Success success){
            log.info("Payment captured, paymentId: {}",paymentId);

//            payment.setStatus(PaymentStatus.CAPTURED);
            paymentTransitionService.apply(payment,PaymentEvent.CAPTURE_SUCCESS);

            payment.setCapturedAt(LocalDateTime.now());
        }else if (paymentResult instanceof PaymentResult.Failure failure) {
//            payment.setStatus(PaymentStatus.AUTHORIZED);
            paymentTransitionService.apply(payment,PaymentEvent.CAPTURE_FAIL);

            payment.setErrorCode(failure.errorCode());
            payment.setErrorDescription(failure.errorDescription());

            log.info("Payment captured failed, paymentId: {}",paymentId);

        }
         payment = paymentRepository.save(payment);
        return paymentMapper.toResponse(payment);
    }

    @Override
    @Transactional
    public void resolveAuthorization(UUID paymentId, boolean approve, String bankRef, String errorCode, String errorDescription) {

        Payment payment = paymentRepository.findById(paymentId).orElseThrow(() -> new ResourceNotFoundException("Payment", paymentId));

        OrderRecord orderRecord = payment.getOrder();
        if(payment.getStatus()!=PaymentStatus.AUTHORIZING){
            return;
        }

        if(approve){
            paymentTransitionService.apply(payment,PaymentEvent.AUTHORIZE_SUCCESS);
            payment.setBankReference(bankRef);
            payment.setAuthorizedAt(LocalDateTime.now());

            //Autcapture
            paymentTransitionService.apply(payment, PaymentEvent.CAPTURE_REQUEST);
            PaymentResult captureResult = paymentGatewayRouter.capture(payment.getMethod(), paymentId);

            if(captureResult instanceof PaymentResult.Success success){
                paymentTransitionService.apply(payment,PaymentEvent.CAPTURE_SUCCESS);
                payment.setCapturedAt(LocalDateTime.now());
                orderRecord.setOrderStatus(OrderStatus.PAID);
            } else if (captureResult instanceof PaymentResult.Failure failuer) {
                paymentTransitionService.apply(payment,PaymentEvent.CAPTURE_FAIL);
                payment.setErrorCode(errorCode);
                payment.setErrorDescription(errorDescription);
            }
        }
        else{
            paymentTransitionService.apply(payment, PaymentEvent.AUTHORIZE_FAIL);
            payment.setErrorCode(errorCode);
            payment.setErrorDescription(errorDescription);
        }

        paymentRepository.save(payment);
        orderRepository.save(orderRecord);


    }
}
