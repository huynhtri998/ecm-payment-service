package com.trilabs94.ecm_payment.repository;

import com.trilabs94.ecm_payment.entity.Payment;
import com.trilabs94.ecm_payment.enums.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    Optional<Payment> findByReference(String reference);

    List<Payment> findByOrderId(Long orderId);

    boolean existsByOrderIdAndStatusIn(Long orderId, Collection<PaymentStatus> statuses);

}