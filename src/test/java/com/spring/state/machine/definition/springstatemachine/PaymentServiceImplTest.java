package com.spring.state.machine.definition.springstatemachine;

import com.spring.state.machine.definition.springstatemachine.domain.Payment;
import com.spring.state.machine.definition.springstatemachine.domain.PaymentEvent;
import com.spring.state.machine.definition.springstatemachine.domain.PaymentState;
import com.spring.state.machine.definition.springstatemachine.repository.PaymentRepository;
import com.spring.state.machine.definition.springstatemachine.services.PaymentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.statemachine.StateMachine;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@SpringBootTest
public class PaymentServiceImplTest {

    @Autowired
    PaymentService paymentService;

    @Autowired
    PaymentRepository paymentRepository;

    Payment payment;

    @BeforeEach
     void setUp() {
        System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
        payment = Payment.builder().amount(new BigDecimal("12.99")).build();
    }

    @Transactional
    @RepeatedTest(10)
    @Test
    void preAuth() {
        System.out.println("=========================================");
        Payment savedPayment = paymentService.newPayment(payment);

        System.out.println("Should be NEW");
        System.out.println(savedPayment.getPaymentState());

        StateMachine<PaymentState, PaymentEvent> sm = paymentService.preAuth(savedPayment.getId());

        Payment preAuthedPayment = paymentRepository.getOne(savedPayment.getId());

        System.out.println("Should be PRE_AUTH");
        System.out.println(sm.getState().getId());
        System.out.println(preAuthedPayment);
        System.out.println("=========================================");
    }

}
