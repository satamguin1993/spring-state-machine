package com.spring.state.machine.definition.springstatemachine.repository;

import com.spring.state.machine.definition.springstatemachine.domain.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
}
