package com.reddate.taianddc.dto.ddc;

import com.reddate.taianddc.dto.taianchain.TransactionInfoBean;
import lombok.Data;

@Data
public class BaseEventBean {
    private String timestamp;
    private String blockNumber;
    private String blockHash;
    private TransactionInfoBean transactionInfoBean;
}
