package com.prash.razorPay.operations.entity;

import com.prash.razorPay.common.entity.BaseEntity;
import jakarta.persistence.Embeddable;

import java.util.UUID;

@Embeddable
public class SettlementPaymentId {

    private UUID paymentId;
    private UUID settlementId;
}
