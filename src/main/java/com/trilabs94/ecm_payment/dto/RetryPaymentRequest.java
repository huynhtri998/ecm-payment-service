package com.trilabs94.ecm_payment.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RetryPaymentRequest {
    private String reason;
}
