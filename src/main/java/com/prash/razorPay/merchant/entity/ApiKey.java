package com.prash.razorPay.merchant.entity;


import com.prash.razorPay.common.entity.BaseEntity;
import com.prash.razorPay.common.enums.Environment;
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
@Table(name = "api_key",
        indexes = {
            @Index(name = "idx_api_key_merchant_eng", columnList = "merchant_id, environment, enabled")
        })
public class ApiKey extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "merchant_id", nullable = false)
    private Merchant merchant;

    @Column(nullable = false, length = 200, unique = true)
    private String keyId;

    @Column(nullable = false, length = 2000)
    private String keySecretHash;

    @Column( length = 2000)
    private String previousKeySecretHash;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Environment environment;

    @Column(nullable = false)
    @Builder.Default
    private boolean Enabled=true;


    private LocalDateTime lastUsedAt;
    private LocalDateTime rotatedAt;

    private LocalDateTime gracePeriodExpiresAt;
}
