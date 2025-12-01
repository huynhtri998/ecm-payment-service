package com.trilabs94.ecm_payment.kafka.consumer;

import com.trilabs94.ecm_payment.kafka.event.OrderCreatedEvent;
import com.trilabs94.ecm_payment.service.impl.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrderCreatedEventConsumer {

    private final PaymentService paymentService;

    @KafkaListener(
            topics = "order-topic",
            groupId = "payment-service"
    )
    public void handleOrderCreated(@Payload OrderCreatedEvent event) {
        log.info("Received OrderCreatedEvent from Kafka: {}", event);

        paymentService.handleOrderCreated(event);
    }
}
