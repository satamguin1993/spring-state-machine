package com.spring.state.machine.definition.springstatemachine.services.impl;

import com.spring.state.machine.definition.springstatemachine.domain.Payment;
import com.spring.state.machine.definition.springstatemachine.domain.PaymentEvent;
import com.spring.state.machine.definition.springstatemachine.domain.PaymentState;
import com.spring.state.machine.definition.springstatemachine.repository.PaymentRepository;
import com.spring.state.machine.definition.springstatemachine.services.PaymentService;
import com.spring.state.machine.definition.springstatemachine.services.PaymentStateChangeInterceptor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.statemachine.support.DefaultStateMachineContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@RequiredArgsConstructor
@Service
@Slf4j
public class PaymentServiceImpl implements PaymentService {

    public static final String PAYMENT_ID_HEADER = "paymentId";

    private final PaymentRepository paymentRepository;
    private final StateMachineFactory<PaymentState, PaymentEvent> stateMachineFactory;
    private final PaymentStateChangeInterceptor paymentStateChangeInterceptor;

    @Override
    public Payment newPayment(Payment payment) {
        payment.setPaymentState(PaymentState.NEW);
        log.info("Creating new payment");
        return paymentRepository.save(payment);
    }

    @Transactional
    @Override
    public StateMachine<PaymentState, PaymentEvent> preAuth(long paymentId) {
        StateMachine<PaymentState, PaymentEvent> sm = build(paymentId);

        sendEvent(paymentId, sm, PaymentEvent.PRE_AUTHORIZE);
        return sm;
    }

    @Transactional
    @Override
    public StateMachine<PaymentState, PaymentEvent> authorizePrePayment(long paymentId) {
        StateMachine<PaymentState, PaymentEvent> sm = build(paymentId);

        //TODO need to check  on the how the state change
        sendEvent(paymentId, sm, PaymentEvent.PRE_AUTH_APPROVED);
        return sm;
    }

    @Transactional
    @Override
    public StateMachine<PaymentState, PaymentEvent> declinePrePayment(long paymentId) {
        StateMachine<PaymentState, PaymentEvent> sm = build(paymentId);

        sendEvent(paymentId, sm, PaymentEvent.PRE_AUTH_DECLINED);
        return sm;
    }

    @Transactional
    @Override
    public StateMachine<PaymentState, PaymentEvent> authorizePayment(long paymentId) {
        StateMachine<PaymentState, PaymentEvent> sm = build(paymentId);

        sendEvent(paymentId, sm, PaymentEvent.AUTH_APPROVED);
        return sm;
    }

    @Transactional
    @Override
    public StateMachine<PaymentState, PaymentEvent> declinePayment(long paymentId) {
        StateMachine<PaymentState, PaymentEvent> sm = build(paymentId);

        sendEvent(paymentId, sm, PaymentEvent.AUTH_DECLINED);
        return sm;
    }

    @Override
    public Payment updatePaymentStatus(Long paymentId, PaymentEvent event) {
        Optional<Payment> optional = paymentRepository.findById(paymentId);
        Payment payment = null;
        if (optional.isPresent()) {
            switch (event) {
                case PRE_AUTHORIZE:
                    preAuth(paymentId);
                    break;
                case PRE_AUTH_APPROVED:
                    authorizePrePayment(paymentId);
                    break;
                case PRE_AUTH_DECLINED:
                    declinePrePayment(paymentId);
                    break;
                case AUTH_APPROVED:
                    authorizePayment(paymentId);
                    break;
                case AUTH_DECLINED:
                    declinePayment(paymentId);
                    break;
                default:
                    log.error("Wrong event has been send paymentId={} event={}", paymentId, event);
            }

            payment = paymentRepository.getOne(paymentId);
            log.info("payment info retrieved for paymentId={} paymentState={}",
                    payment.getId(),
                    payment.getPaymentState());
        }
        return payment;
    }

    /*********************** All Private Methods **************************************/

    private void sendEvent(Long paymentId, StateMachine<PaymentState, PaymentEvent> sm, PaymentEvent event) {
        Message msg = MessageBuilder.withPayload(event)
                .setHeader(PAYMENT_ID_HEADER, paymentId)
                .build();

        sm.sendEvent(msg);
    }

    //retrieve the state and initialize the state machine in the payment state
    private StateMachine<PaymentState, PaymentEvent> build(Long paymentId) {
        Payment payment = paymentRepository.getOne(paymentId);
        StateMachine<PaymentState, PaymentEvent> sm = stateMachineFactory
                .getStateMachine(Long.toString(payment.getId()));

        sm.stop();
        sm.getStateMachineAccessor().doWithAllRegions(sma -> {
            sma.addStateMachineInterceptor(paymentStateChangeInterceptor);
            sma.resetStateMachine(new DefaultStateMachineContext<>(
                    payment.getPaymentState(), null, null, null));
        });
        sm.start();

        return sm;
    }
}
