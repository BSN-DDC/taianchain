package com.reddate.ddc.dto.ddc;

import com.reddate.ddc.dto.taianchain.TransactionInfoBean;
import lombok.Data;

@Data
public class BaseEventBean {
    private String timestamp;
    private String blockNumber;
    private String blockHash;
    private TransactionInfoBean transactionInfoBean;
}
