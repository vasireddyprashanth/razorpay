package com.prash.razorPay.merchant.entity;

import com.prash.razorPay.common.entity.BaseEntity;
import com.prash.razorPay.common.enums.BusinessType;
import com.prash.razorPay.common.enums.MerchantStatus;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "merchant",
        indexes = {
                @Index(name = "idx_merchant_status", columnList = "status")
        })
public class Merchant extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column( nullable = false, length = 100)
    private String name;

    @Column(unique = true,length = 50, nullable = false)
    private String email;

    @Column(length = 15)
    private String contactNumber;

    @Column(length = 200)
    @Enumerated(EnumType.STRING)
    private BusinessType businessType;

    @Column(length = 100)
    private String businessName;

    @Column(length = 200)
    private String websiteUrl;

    @Column(length = 200,nullable = false)
    @Enumerated(EnumType.STRING)
    private MerchantStatus status=MerchantStatus.PENDING_KYC;

    @Column(length = 20)
    private String gstId;

    @Column(length = 20)
    private String panId;

    private String settlementBankAccount;
    private String getSettlementBankIfsc;
    private String getSettlementBankAccountHolderName;



}
