package com.trilabs94.ecm_payment.service.impl;

import com.trilabs94.ecm_payment.dto.PaymentRequestDto;
import com.trilabs94.ecm_payment.kafka.event.PaymentNotificationRequest;
import com.trilabs94.ecm_payment.kafka.producer.NotificationProducer;
import com.trilabs94.ecm_payment.mapper.PaymentMapper;
import com.trilabs94.ecm_payment.repository.PaymentRepository;
import com.trilabs94.ecm_payment.service.IPaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaymentService implements IPaymentService {

    private final PaymentRepository repository;
    private final PaymentMapper mapper;
    private final NotificationProducer notificationProducer;

    public Integer createPayment(PaymentRequestDto request) {
        var payment = this.repository.save(this.mapper.toPayment(request));

        this.notificationProducer.sendNotification(
                new PaymentNotificationRequest(
                        request.getOrderReference(),
                        request.getAmount(),
                        request.getPaymentMethod(),
                        request.getCustomer().getFirstname(),
                        request.getCustomer().getLastname(),
                        request.getCustomer().getEmail()
                )
        );
        return payment.getId();
    }
}
