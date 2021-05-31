package com.spring.state.machine.definition.springstatemachine.services;

import com.spring.state.machine.definition.springstatemachine.domain.Payment;
import com.spring.state.machine.definition.springstatemachine.domain.PaymentEvent;
import com.spring.state.machine.definition.springstatemachine.domain.PaymentState;
import com.spring.state.machine.definition.springstatemachine.repository.PaymentRepository;
import com.spring.state.machine.definition.springstatemachine.services.impl.PaymentServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.state.State;
import org.springframework.statemachine.support.StateMachineInterceptorAdapter;
import org.springframework.statemachine.transition.Transition;
import org.springframework.stereotype.Service;

import java.util.Optional;

@RequiredArgsConstructor
@Service
@Slf4j
public class PaymentStateChangeInterceptor extends StateMachineInterceptorAdapter<PaymentState, PaymentEvent> {

    private final PaymentRepository paymentRepository;

    @Override
    public void preStateChange(State<PaymentState, PaymentEvent> state,
                               Message<PaymentEvent> message,
                               Transition<PaymentState, PaymentEvent> transition,
                               StateMachine<PaymentState, PaymentEvent> stateMachine) {

        Optional.ofNullable(message).ifPresent(msg -> {
            Optional.ofNullable(Long.class.cast(msg.getHeaders()
                    .getOrDefault(PaymentServiceImpl.PAYMENT_ID_HEADER, -1l)))
                    .ifPresent(paymentId -> {
                        Payment payment = paymentRepository.getOne(paymentId);
                        payment.setPaymentState(state.getId());
                        paymentRepository.save(payment);
                    });
        });

    }
}
