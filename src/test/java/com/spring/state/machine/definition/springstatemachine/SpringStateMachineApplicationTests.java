package com.spring.state.machine.definition.springstatemachine;

import com.spring.state.machine.definition.springstatemachine.domain.PaymentEvent;
import com.spring.state.machine.definition.springstatemachine.domain.PaymentState;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.StateMachineFactory;

import java.util.UUID;

@SpringBootTest
class SpringStateMachineApplicationTests {

	@Autowired
	StateMachineFactory<PaymentState, PaymentEvent> factory;

	@Test
	void contextLoads() {
	}

	@Test
	public void testNewStateMachine() {
		StateMachine<PaymentState, PaymentEvent> sm = factory.getStateMachine(UUID.randomUUID());
		sm.start();

		System.out.println(sm.getState().toString());

		sm.sendEvent(PaymentEvent.PRE_AUTHORIZE);
		System.out.println(sm.getState().toString());

		sm.sendEvent(PaymentEvent.PRE_AUTH_APPROVED);
		System.out.println(sm.getState().toString());
	}

}
