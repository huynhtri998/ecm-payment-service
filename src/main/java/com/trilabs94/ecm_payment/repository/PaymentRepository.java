package com.trilabs94.ecm_payment.repository;

import com.trilabs94.ecm_payment.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, Integer> {

}