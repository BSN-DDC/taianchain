package com.reddate.taianddc.dto.taianchain;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import org.fisco.bcos.web3j.protocol.core.methods.response.Log;

/**
 * @author kuan
 * Created on 21/1/23.
 * @description 交易回执信息
 */
@NoArgsConstructor
@Data
public class TransactionRecepitBean {

    private String blockHash;
    private String blockNumber;
    private String contractAddress;
    private String from;
    private String gasUsed;
    private String input;
    private List<Log> logs;
    private String logsBloom;
    private String output;
    private String root;
    private String status;
    private String statusMsg;
    private String to;
    private String transactionHash;
    private String transactionIndex;

}
