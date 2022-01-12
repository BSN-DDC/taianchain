package com.reddate.ddc.service;

import com.reddate.ddc.DDCSdkClient;
import com.reddate.ddc.config.ConfigCache;
import com.reddate.ddc.dto.taianchain.TransactionRecepitBean;
import com.reddate.ddc.util.AnalyzeChainInfoUtils;
import lombok.extern.slf4j.Slf4j;
import org.fisco.bcos.web3j.protocol.exceptions.TransactionException;
import org.fisco.bcos.web3j.tx.txdecode.BaseException;
import org.fisco.bcos.web3j.tx.txdecode.InputAndOutputResult;
import org.junit.jupiter.api.Test;

@Slf4j
class BaseServiceTest {

    BaseService baseService = new BaseService();

    static {
        new DDCSdkClient().init();
    }

    @Test
    void assembleTransaction() {

    }

    @Test
    void getBlockNumber() {
        System.out.println(baseService.getBlockNumber());
    }

    @Test
    void getTransactionRecepit() throws InterruptedException, BaseException, TransactionException {
        TransactionRecepitBean transactionRecepitBean = baseService.getTransactionRecepit("0x318e2d077722ffc78bfc2efa050ca412425316ba7e0504b47de204687a0b1e2b");
        log.info(transactionRecepitBean.toString());
        InputAndOutputResult inputAndOutputResult = AnalyzeChainInfoUtils.analyzeTransactionOutput(ConfigCache.get().getAuthorityLogicABI(), ConfigCache.get().getAuthorityLogicBIN(), transactionRecepitBean.getInput(), transactionRecepitBean.getOutput());
        log.info(inputAndOutputResult.toString());
    }

    @Test
    void getTransactionByHash() {
    }

    @Test
    void generateKeyPair() {

    }
}