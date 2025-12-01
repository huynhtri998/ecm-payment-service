package com.trilabs94.ecm_payment.kafka.producer;

import com.trilabs94.ecm_payment.kafka.event.PaymentCompletedEvent;
import com.trilabs94.ecm_payment.kafka.event.PaymentFailedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class PaymentEventProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void sendPaymentCompletedEvent(PaymentCompletedEvent event) {
        String key = event.getOrderId() != null
                ? event.getOrderId().toString()
                : event.getPaymentReference();

        log.info("Sending PaymentCompletedEvent to topic {} with key {}: {}",
                "payment-topic", key, event);

        kafkaTemplate.send("payment-topic", key, event);
    }

    public void sendPaymentFailedEvent(PaymentFailedEvent event) {
        String key = event.getOrderId() != null
                ? event.getOrderId().toString()
                : event.getPaymentReference();

        log.info("Sending PaymentFailedEvent to topic {} with key {}: {}",
                "payment-topic", key, event);

        kafkaTemplate.send("payment-topic", key, event);
    }
}
