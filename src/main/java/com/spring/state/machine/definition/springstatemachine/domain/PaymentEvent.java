package com.spring.state.machine.definition.springstatemachine.domain;

public enum PaymentEvent {

    PRE_AUTHORIZE,
    PRE_AUTH_APPROVED,
    PRE_AUTH_DECLINED,
    AUTHORIZE,
    AUTH_APPROVED,
    AUTH_DECLINED

}
