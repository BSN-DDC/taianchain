package com.reddate.taianddc.constant;

import java.math.BigInteger;

public enum State {

    CROSS_CHAIN(new BigInteger(String.valueOf(0))),
    CROSS_CHAIN_SUCCEED(new BigInteger(String.valueOf(1))),
    CROSS_CHAIN_FAILED(new BigInteger(String.valueOf(2))),
    ;

    private BigInteger state;

    State(BigInteger state) {
        this.state = state;
    }

    public BigInteger getState() {
        return state;
    }

    public void setState(BigInteger state) {
        this.state = state;
    }

    @Override
    public String toString() {
        return state.toString();
    }
}
