package com.reddate.taianddc.dto.taianchain;

import lombok.Data;

import java.math.BigInteger;
import java.util.ArrayList;

@Data
public class SignParamsBean {
    BigInteger blockHeight;
    String abi;
    String contractAddress;
    String funcName;
    ArrayList<Object> params;
}
