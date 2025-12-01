package com.trilabs94.ecm_payment.dto;

import com.trilabs94.ecm_payment.enums.PaymentStatus;
import lombok.*;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentResponse {

    private String reference;
    private Long orderId;
    private BigDecimal amount;
    private PaymentStatus status;
    private OffsetDateTime createdAt;
}
