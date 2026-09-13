package com.prash.razorPay.payment.entity;

import com.prash.razorPay.common.entity.BaseEntity;
import com.prash.razorPay.common.entity.Money;
import com.prash.razorPay.common.enums.PaymentEvent;
import com.prash.razorPay.common.enums.RefundStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.sql.SQLType;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;


@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name="refund")
public class Refund extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "payment_id", nullable = false)
    private Payment paymentId;

    @Column(nullable = false)
    private UUID merchandId;

    @Embedded
    private Money amount;

    @Enumerated(EnumType.STRING)
    private RefundStatus status=RefundStatus.PENDING;

    @Column(length = 100)
    private String bankReference;

    @Column(length = 255)
    private String errorCode;

    @Column(length = 500)
    private String errorDescription;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private Map<String, Object> notes;


    private LocalDateTime processedAt;
}
