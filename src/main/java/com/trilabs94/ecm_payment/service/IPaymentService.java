package com.trilabs94.ecm_payment.service;

import com.trilabs94.ecm_payment.dto.PaymentResponse;
import com.trilabs94.ecm_payment.kafka.event.OrderCreatedEvent;

import java.util.List;

public interface IPaymentService {

    void handleOrderCreated(OrderCreatedEvent event);

    PaymentResponse getPaymentByReference(String reference);

    List<PaymentResponse> getPaymentByOrderId(Long orderId);

    PaymentResponse retryPayment(String paymentReference, String reason);
}
