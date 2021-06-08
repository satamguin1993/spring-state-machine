package com.spring.state.machine.definition.springstatemachine.services;

import com.spring.state.machine.definition.springstatemachine.domain.Payment;
import com.spring.state.machine.definition.springstatemachine.domain.PaymentEvent;
import com.spring.state.machine.definition.springstatemachine.domain.PaymentState;
import org.springframework.statemachine.StateMachine;

public interface PaymentService {

    Payment newPayment(Payment payment);

     StateMachine<PaymentState, PaymentEvent> preAuth(long paymentId);

     StateMachine<PaymentState, PaymentEvent> authorizePrePayment(long paymentId);

     StateMachine<PaymentState, PaymentEvent> declinePrePayment(long paymentId);

     StateMachine<PaymentState, PaymentEvent> authorizePayment(long paymentId);

     StateMachine<PaymentState, PaymentEvent> declinePayment(long paymentId);

    Payment updatePaymentStatus(Long paymentId, PaymentEvent event);
}
