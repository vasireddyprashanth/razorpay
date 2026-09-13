package com.prash.razorPay.payment.repository;

import com.prash.razorPay.payment.entity.PaymentTransitionLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface PaymentTransitionLogRepository extends JpaRepository<PaymentTransitionLog, UUID> {
}
