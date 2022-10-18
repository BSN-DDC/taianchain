package com.reddate.taianddc.dto.ddc;

import lombok.Data;

import java.math.BigInteger;

@Data
public class DDC1155MetaTransferSingleEventBean extends BaseEventBean{
    /** 签名者 */
    String operator;

    /** 拥有账户地址 */
    String from;

    /** 接收账户地址 */
    String to;

    /** DDC */
    BigInteger ddcId;

    /** 数量 */
    BigInteger amount;
}
