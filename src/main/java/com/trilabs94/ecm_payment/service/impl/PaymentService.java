package com.trilabs94.ecm_payment.service.impl;

import com.trilabs94.ecm_payment.dto.PaymentResponse;
import com.trilabs94.ecm_payment.entity.Payment;
import com.trilabs94.ecm_payment.enums.PaymentStatus;
import com.trilabs94.ecm_payment.kafka.event.OrderCreatedEvent;
import com.trilabs94.ecm_payment.kafka.event.PaymentCompletedEvent;
import com.trilabs94.ecm_payment.kafka.event.PaymentFailedEvent;
import com.trilabs94.ecm_payment.kafka.producer.PaymentEventProducer;
import com.trilabs94.ecm_payment.mapper.PaymentMapper;
import com.trilabs94.ecm_payment.repository.PaymentRepository;
import com.trilabs94.ecm_payment.service.IPaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.EnumSet;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService implements IPaymentService {

    private final PaymentRepository paymentRepository;
    private final PaymentMapper paymentMapper;
    private final PaymentEventProducer paymentEventProducer;

    // ----------------------------------------------------------------
    // 1. Handle OrderCreatedEvent (called by Kafka consumer)
    // ----------------------------------------------------------------
    @Transactional
    @Override
    public void handleOrderCreated(OrderCreatedEvent event) {
        log.info("Handling OrderCreatedEvent for orderId={}, reference={}",
                event.getOrderId(), event.getOrderReference());

        // Idempotent: if there is already a PENDING/PAID payment for this order, skip
        boolean exists = paymentRepository.existsByOrderIdAndStatusIn(
                event.getOrderId(),
                EnumSet.of(PaymentStatus.PENDING, PaymentStatus.PAID)
        );

        if (exists) {
            log.warn("Payment already exists for orderId={}, skip creating new payment", event.getOrderId());
            return;
        }

        Payment payment = Payment.builder()
                .orderId(event.getOrderId())
                .amount(event.getTotalAmount())
                .status(PaymentStatus.PENDING)
                .reference(generatePaymentReference(event.getOrderId()))
                .build();

        payment = paymentRepository.save(payment);

        // Process payment (fake / sync)
        processPayment(payment, event.getOrderReference());
    }

    // ----------------------------------------------------------------
    // 2. Process payment (fake)
    // ----------------------------------------------------------------
    @Transactional
    protected void processPayment(Payment payment, String orderReference) {
        log.info("Processing payment for orderId={}, paymentRef={}",
                payment.getOrderId(), payment.getReference());

        // TODO: replace this logic with a real payment gateway call
        boolean success = true; // currently always succeed for simplicity

        if (success) {
            payment.setStatus(PaymentStatus.PAID);
            payment = paymentRepository.save(payment);

            PaymentCompletedEvent event =
                    buildPaymentCompletedEvent(payment, orderReference);
            paymentEventProducer.sendPaymentCompletedEvent(event);

            log.info("Payment PAID for orderId={}, paymentRef={}",
                    payment.getOrderId(), payment.getReference());
        } else {
            payment.setStatus(PaymentStatus.FAILED);
            payment = paymentRepository.save(payment);

            PaymentFailedEvent event =
                    buildPaymentFailedEvent(payment, orderReference, "Fake gateway error");
            paymentEventProducer.sendPaymentFailedEvent(event);

            log.warn("Payment FAILED for orderId={}, paymentRef={}",
                    payment.getOrderId(), payment.getReference());
        }
    }

    // ----------------------------------------------------------------
    // 3. Retry payment (called by REST API)
    // ----------------------------------------------------------------
    @Transactional
    @Override
    public PaymentResponse retryPayment(String paymentReference, String reason) {
        Payment payment = paymentRepository.findByReference(paymentReference)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Payment not found with reference: " + paymentReference));

        if (payment.getStatus() != PaymentStatus.FAILED) {
            throw new IllegalStateException("Only FAILED payments can be retried");
        }

        log.info("Retrying payment for reference={}, orderId={}",
                payment.getReference(), payment.getOrderId());

        payment.setStatus(PaymentStatus.PENDING);
        payment = paymentRepository.save(payment);

        // Order reference is not stored in payment, using null for now / TODO
        // Later we can store orderReference in payments table if needed.
        processPayment(payment, null);

        return paymentMapper.toPaymentResponse(payment);
    }

    // ----------------------------------------------------------------
    // 4. Query methods for REST API
    // ----------------------------------------------------------------
    @Transactional
    @Override
    public PaymentResponse getPaymentByReference(String reference) {
        Payment payment = paymentRepository.findByReference(reference)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Payment not found with reference: " + reference));

        return paymentMapper.toPaymentResponse(payment);
    }

    @Transactional
    @Override
    public List<PaymentResponse> getPaymentByOrderId(Long orderId) {
        List<Payment> payments = paymentRepository.findByOrderId(orderId);

        return payments.stream()
                .map(paymentMapper::toPaymentResponse)
                .collect(Collectors.toList());
    }

    // ----------------------------------------------------------------
    // 5. Helper build event
    // ----------------------------------------------------------------
    private PaymentCompletedEvent buildPaymentCompletedEvent(Payment payment,
                                                             String orderReference) {
        return PaymentCompletedEvent.builder()
                .paymentReference(payment.getReference())
                .orderId(payment.getOrderId())
                .orderReference(orderReference)
                .amount(payment.getAmount())
                .status(PaymentStatus.PAID)
                .paidAt(OffsetDateTime.now())
                .build();
    }

    private PaymentFailedEvent buildPaymentFailedEvent(Payment payment,
                                                       String orderReference,
                                                       String reason) {
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

    // ----------------------------------------------------------------
    // 6. Helper generate reference (TODO: refine format)
    // ----------------------------------------------------------------
    private String generatePaymentReference(Long orderId) {
        // TODO: later standardize pattern: PAY-YYYY-MM-###### similar to order
        return "PAY-" + OffsetDateTime.now().getYear()
                + "-" + orderId
                + "-" + System.currentTimeMillis();
    }
}