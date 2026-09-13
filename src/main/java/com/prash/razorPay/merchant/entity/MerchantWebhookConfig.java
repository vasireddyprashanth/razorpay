package com.prash.razorPay.merchant.entity;

import com.prash.razorPay.common.entity.BaseEntity;
import jakarta.persistence.*;

import java.util.UUID;


@Entity
@Table(name = "merchant_webhook_config",
        indexes = {
                @Index(name = "idx_webhook_merchant_id", columnList = "merchant_id, enabled")
        })
public class MerchantWebhookConfig extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "merchant_id", nullable = false)
    private Merchant merchant;

    @Column(nullable = false, length = 500)
    private String targetUrl; //www.zara.com/webhook/success

    @Column(length = 225)
    private String webhookSecretHash;

    @Column(nullable = false)
    private Boolean enabled=true;

    @Column(length = 225)
    private String eventTypes; // comma-seperated List of event types to subscribe to

}
