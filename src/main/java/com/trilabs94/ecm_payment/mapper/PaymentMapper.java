package com.trilabs94.ecm_payment.mapper;

import com.trilabs94.ecm_payment.dto.PaymentResponse;
import com.trilabs94.ecm_payment.entity.Payment;
import com.trilabs94.ecm_payment.enums.PaymentStatus;
import com.trilabs94.ecm_payment.kafka.event.PaymentCompletedEvent;
import com.trilabs94.ecm_payment.kafka.event.PaymentFailedEvent;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;

@Component
public class PaymentMapper {

    public PaymentResponse toPaymentResponse(Payment payment) {
        if (payment == null) {
            return null;
        }

        return PaymentResponse.builder()
                .reference(payment.getReference())
                .orderId(payment.getOrderId())
                .amount(payment.getAmount())
                .status(payment.getStatus())
                .createdAt(payment.getCreatedAt())
                .build();
    }

    public PaymentCompletedEvent toPaymentCompletedEvent(
            Payment payment,
            String orderReference
    ) {
        if (payment == null) {
            return null;
        }

        return PaymentCompletedEvent.builder()
                .paymentReference(payment.getReference())
                .orderId(payment.getOrderId())
                .orderReference(orderReference)
                .amount(payment.getAmount())
                .status(PaymentStatus.PAID)
                .paidAt(OffsetDateTime.now())
                .build();
    }

    public PaymentFailedEvent toPaymentFailedEvent(
            Payment payment,
            String orderReference,
            String reason
    ) {
        if (payment == null) {
            return null;
        }

        return PaymentFailedEvent.builder()
                .paymentReference(payment.getReference())
                .orderId(payment.getOrderId())
                .orderReference(orderReference)
                .amount(payment.getAmount())
                .status(PaymentStatus.FAILED)
                .failedAt(OffsetDateTime.now())
                .reason(reason)
                .build();
    }
}
