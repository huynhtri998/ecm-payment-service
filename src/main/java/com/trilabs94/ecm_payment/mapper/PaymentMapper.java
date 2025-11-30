package com.trilabs94.ecm_payment.mapper;

import com.trilabs94.ecm_payment.dto.PaymentRequestDto;
import com.trilabs94.ecm_payment.entity.Payment;
import org.springframework.stereotype.Component;

@Component
public class PaymentMapper {

    public Payment toPayment(PaymentRequestDto request) {
        if (request == null) {
            return null;
        }
        return Payment.builder()
                .id(request.getId())
                .paymentMethod(request.getPaymentMethod())
                .amount(request.getAmount())
                .orderId(request.getOrderId())
                .build();
    }
}
