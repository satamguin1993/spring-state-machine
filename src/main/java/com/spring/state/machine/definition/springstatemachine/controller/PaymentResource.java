package com.spring.state.machine.definition.springstatemachine.controller;


import com.spring.state.machine.definition.springstatemachine.domain.Payment;
import com.spring.state.machine.definition.springstatemachine.domain.PaymentEvent;
import com.spring.state.machine.definition.springstatemachine.services.PaymentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/payment")
@Slf4j
public class PaymentResource {

    @Autowired
    PaymentService paymentService;

    @PostMapping
    public ResponseEntity<Payment> createNewPayment(@RequestBody Payment paymentRequest) {
        log.info("Request received for new payment");
        Payment payment = paymentService.newPayment(paymentRequest);
        log.info("New payment created for paymentId={}", payment.getId());
        return ResponseEntity.ok(payment);
    }

    @PatchMapping("/{paymentId}")
    public ResponseEntity<Payment> updatePayment(@PathVariable("paymentId") Long paymentId,
                                                 @RequestParam("event") PaymentEvent event) {
        log.info("Updating payment for paymentId={} event={}", paymentId, event);
        Payment payment = paymentService.updatePaymentStatus(paymentId, event);
        log.info("Payment updated successfully for paymentId={} paymentState={}",
                paymentId,
                payment.getPaymentState());
        return ResponseEntity.ok(payment);
    }

}
