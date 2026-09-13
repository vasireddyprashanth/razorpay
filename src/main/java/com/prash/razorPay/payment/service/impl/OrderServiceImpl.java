package com.prash.razorPay.payment.service.impl;

import com.prash.razorPay.common.enums.OrderStatus;
import com.prash.razorPay.common.exception.BusinessRuleViolationException;
import com.prash.razorPay.common.exception.DuplicateResourceException;
import com.prash.razorPay.common.exception.ResourceNotFoundException;
import com.prash.razorPay.payment.dto.request.CreateOrderRequest;
import com.prash.razorPay.payment.dto.response.OrderResponse;
import com.prash.razorPay.payment.dto.response.PaymentResponse;
import com.prash.razorPay.payment.entity.OrderRecord;
import com.prash.razorPay.payment.entity.Payment;
import com.prash.razorPay.payment.mapper.OrderMapper;
import com.prash.razorPay.payment.mapper.PaymentMapper;
import com.prash.razorPay.payment.repository.OrderRepository;
import com.prash.razorPay.payment.repository.PaymentRepository;
import com.prash.razorPay.payment.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final PaymentRepository paymentRepository;
    private final PaymentMapper paymentMapper;
    private final OrderMapper orderMapper;

    @Value("${payment.order.default-order-expiry-minutes:30}")
    private int defaultOrderExpiryMinutes;

    @Override
    @Transactional
    public OrderResponse create(UUID merchantId, CreateOrderRequest request) {
        if(orderRepository.existsByMerchantIdAndReceipt(merchantId,request.receipt())){
            throw new DuplicateResourceException("ORDER_RECEIPT_DUPLICATE","Order with receipt already exists" + request.receipt());

        }

        OrderRecord order=OrderRecord.builder()
                .receipt(request.receipt())
                .amount(request.amount())
                .merchantId(merchantId)
                .notes(request.notes())
                .orderStatus(OrderStatus.CREATED)
                .expiresAt(request.expiresAt()!=null?request.expiresAt(): LocalDateTime.now().plusMinutes(defaultOrderExpiryMinutes))
                .build();

        order = orderRepository.save(order);

        //TODO publish kafka event about order creation

        return orderMapper.toResponse(order);
    }

    @Override
    public OrderResponse getById(UUID merchantId, UUID orderId) {
        OrderRecord order = orderRepository.findByIdAndMerchantId(orderId, merchantId).orElseThrow(() -> new ResourceNotFoundException("Order", orderId));

        return orderMapper.toResponse(order);
    }

    @Override
    public OrderResponse cancel(UUID merchantId, UUID orderId) {
        OrderRecord order = orderRepository.findByIdAndMerchantId(orderId, merchantId).orElseThrow(() -> new ResourceNotFoundException("Order", orderId));

        if(order.getOrderStatus()==OrderStatus.CANCELLED || order.getOrderStatus()==OrderStatus.PAID){
            throw new BusinessRuleViolationException("ORDER CANNOT CANCEL", "Cannot cancel order with status: " + order.getOrderStatus().name() );
        }

        order.setOrderStatus(OrderStatus.CANCELLED);
        order = orderRepository.save(order);

        return orderMapper.toResponse(order);
    }

    @Override
    public List<PaymentResponse> listPayments(UUID merchantId, UUID orderId) {

        OrderRecord order = orderRepository.findByIdAndMerchantId(orderId, merchantId).orElseThrow(() -> new ResourceNotFoundException("Order", orderId));

        List<Payment> paymentList = paymentRepository.findByOrderId(orderId);

        return paymentMapper.toResponseList(paymentList);
    }


}
