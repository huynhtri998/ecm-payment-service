package com.trilabs94.ecm_payment.kafka.event;

import lombok.*;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderCreatedEvent {

    private Long orderId;
    private String orderReference;
    private Long customerId;
    private BigDecimal totalAmount;
    private OffsetDateTime createdAt;
}
