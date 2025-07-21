package com.netbanking.netbanking_app.repository;

import com.netbanking.netbanking_app.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
}
