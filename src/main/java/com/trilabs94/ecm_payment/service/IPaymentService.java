package com.trilabs94.ecm_payment.service;

import com.trilabs94.ecm_payment.dto.PaymentRequestDto;

public interface IPaymentService {
    Integer createPayment(PaymentRequestDto request);
}
