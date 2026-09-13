package com.prash.razorPay.merchant.entity;

import com.prash.razorPay.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "customer",
        indexes = {
                @Index(name = "idx_customer_merchant_id", columnList = "merchant_id"),
                @Index(name = "idx_customer_email", columnList = "email")
        })
public class Customer extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "merchant_id")
    private Merchant merchant;

    @Column(length = 50)
    private String name;

    @Column(length = 25)
    private String email;

    @Column(length = 15)
    private String contactNumber;

    private LocalDateTime deletedAt;
}
