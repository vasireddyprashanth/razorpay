package com.prash.razorPay.payment.entity;

import com.prash.razorPay.common.entity.BaseEntity;
import com.prash.razorPay.common.enums.OrderStatus;
import com.prash.razorPay.common.entity.Money;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "order_record",
        indexes = {
        @Index(name = "idx_order_record_merchant_id", columnList = "merchant_id")
        })
public class OrderRecord extends BaseEntity {


    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    //no FK - cross-service boundary
    @Column(name = "merchant_id", nullable = false)
    private UUID merchantId;

    @Embedded
    private Money amount;

    @Column(length = 225)
    private String receipt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private OrderStatus orderStatus;

    @Column(nullable = false)
    @Builder.Default
    private Integer attempts=0;

    @JdbcTypeCode((SqlTypes.JSON))
    @Column(columnDefinition = "jsonb")
    private Map<String , Object> notes;

    private LocalDateTime expiresAt;


}
