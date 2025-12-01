package com.trilabs94.ecm_payment.kafka.event;

import com.trilabs94.ecm_payment.enums.PaymentStatus;
import lombok.*;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentCompletedEvent {

    private String paymentReference;
    private Long orderId;
    private String orderReference;
    private BigDecimal amount;
    private PaymentStatus status;
    private OffsetDateTime paidAt;
}
