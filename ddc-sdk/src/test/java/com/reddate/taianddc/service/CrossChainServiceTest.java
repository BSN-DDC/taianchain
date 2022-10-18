package com.reddate.taianddc.service;

import com.reddate.taianddc.config.ConfigCache;
import com.reddate.taianddc.constant.DDCType;
import lombok.extern.slf4j.Slf4j;

import java.math.BigInteger;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@Slf4j
public class CrossChainServiceTest extends BaseServiceTest{

    String abi = ConfigCache.get().getCrossChainLogicABI();
    String bin = ConfigCache.get().getCrossChainLogicBIN();

//    @Test
//    public void crossChainTransfer() throws Exception {
//        String sender = platformAddress;
//        DDCType ddcType = DDCType.TYPE_721;
//        String signer = "0x9bde88224e7cf3ada6045fc0236d10b8cd5a94da";
//        String to = "0x6922D8af46d5e39c2a15cAa26eE692FCc118aDc5";
//        BigInteger ddcId = BigInteger.valueOf(6);
//        byte[] data = new byte[]{0x11};
//        BigInteger amount = BigInteger.valueOf(1);
//        BigInteger toChainID = BigInteger.valueOf(100001);
//        String toCCAddr = "0x84745E10B77F0aE23e333C4C2bbE2d90455DF8Bf";
//        String funcName = "receiveCrossDDC";
//        String txHash = getCrossChainService().crossChainTransfer(sender, ddcType, signer, to, ddcId, data, amount, toChainID, toCCAddr, funcName);
//        log.info(txHash);
//        assertNotNull(txHash);
//        log.info(analyzeRecepit(txHash,abi,bin));
//    }
//
//    @Test
//    public void UpdateCrossChainStatus() throws Exception {
//        String sender = operatorAddress;
//        BigInteger crossChainID = BigInteger.valueOf(2);
//        BigInteger state = BigInteger.valueOf(2);
//        String remark = "aaaaa";
//        String txHash = getCrossChainService().UpdateCrossChainStatus(sender,crossChainID,state,remark);
//        log.info(txHash);
//        assertNotNull(txHash);
//        log.info(analyzeRecepit(txHash,abi,bin));
//    }

}
