package com.spring.state.machine.definition.springstatemachine.domain;

public enum PaymentState {

    NEW,
    PRE_AUTH,
    PRE_AUTH_ERROR,
    AUTH,
    AUTH_ERROR;
}
