package com.reddate.taianddc.dto.ddc;

import lombok.Data;

import java.math.BigInteger;
import java.util.ArrayList;

@Data
public class ReChargeBatchEventBean extends BaseEventBean{
    /** 原链账户地址 */
    String from;

    /** 目标链账户地址 */
    ArrayList<String> toList;

    /** 业务费 */
    ArrayList<BigInteger> amounts;
}
