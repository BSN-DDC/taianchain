package com.reddate.taianddc.dto.taianchain;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * @description 区块信息
 */
@NoArgsConstructor
@Data
public class BlockInfoBean implements Serializable {

    private String dbHash;
    private List<?> extraData;
    private String gasLimit;
    private String gasUsed;
    private String hash;
    private String logsBloom;
    private String number;
    private String parentHash;
    private String receiptsRoot;
    private String sealer;
    private List<String> sealerList;
    private String stateRoot;
    private String timestamp;
    private List<TransactionInfoBean> transactions;
    private String transactionsRoot;


}
