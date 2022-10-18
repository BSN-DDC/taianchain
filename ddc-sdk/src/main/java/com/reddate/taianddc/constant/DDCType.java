package com.reddate.taianddc.constant;

import java.math.BigInteger;

public enum DDCType {

    TYPE_721(new BigInteger(String.valueOf(0))),
    TYPE_1155(new BigInteger(String.valueOf(1))),
    ;

    private BigInteger type;

    DDCType(BigInteger type) {
        this.type = type;
    }

    public BigInteger getType() {
        return type;
    }

    public void setType(BigInteger type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return type.toString();
    }
}
