package com.reddate.ddc.dto.taianchain;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class TransactionInfoBean {
    private String blockHash;
    private String blockNumber;
    private String from;
    private String gas;
    private String gasPrice;
    private String hash;
    private String input;
    private String nonce;
    private String to;
    private String transactionIndex;
    private String value;
}