package com.reddate.taianddc.service;

import com.reddate.taianddc.config.ConfigCache;
import com.reddate.taianddc.constant.DDCType;
import com.reddate.taianddc.constant.State;
import com.reddate.taianddc.dto.ddc.DDC721TransferEventBean;
import com.reddate.taianddc.dto.taianchain.TransactionRecepitBean;
import com.reddate.taianddc.util.HexUtils;
import lombok.extern.slf4j.Slf4j;
import org.fisco.bcos.web3j.utils.Numeric;
import org.junit.jupiter.api.Test;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@Slf4j
public class OpbCrossChainServiceTest extends BaseServiceTest{

    String abi = ConfigCache.get().getOpbCrossChainABI();
    String bin = ConfigCache.get().getOpbCrossChainBIN();

    @Test
    public void crossChainTransfer() throws Exception {
        String sender = platformAddress;
        DDCType ddcType = DDCType.TYPE_721;
        String to = "0x057b5061c4e2ebce5482b63def1de5a21a66d1f6";
        BigInteger ddcId = BigInteger.valueOf(2987);
        Boolean isLock = true;
        String data = "0x";
        BigInteger toChainID = BigInteger.valueOf(2);
        String txHash = getOpbCrossChainService().crossChainTransfer(sender, ddcType, ddcId, isLock, toChainID, to, data);
        log.info(txHash);
    }

    @Test
    public void UpdateCrossChainStatus() throws Exception {
        String sender = platformAddress;
        BigInteger crossChainID = BigInteger.valueOf(5);
        State state = State.CROSS_CHAIN_FAILED;
        String remark = "no remark";
        String txHash = getOpbCrossChainService().updateCrossChainStatus(sender,crossChainID,state,remark);
        log.info(txHash);
        assertNotNull(txHash);
        log.info(analyzeRecepit(txHash,abi,bin));
    }
}
