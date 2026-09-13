package com.prash.razorPay.payment.repository;

import com.prash.razorPay.common.enums.PaymentStatus;
import com.prash.razorPay.payment.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, UUID> {
     List<Payment> findByOrderId(UUID orderId);

     Optional<Payment> findByIdAndMerchantId(UUID paymentId, UUID merchantId);

     List<Payment> findByStatusAndCreatedAtBefore(PaymentStatus paymentStatus, LocalDateTime globalWindow);

//      findById(UUID paymentId);
}
