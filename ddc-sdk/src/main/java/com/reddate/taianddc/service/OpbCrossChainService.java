package com.reddate.taianddc.service;

import com.reddate.taianddc.config.ConfigCache;
import com.reddate.taianddc.constant.DDCType;
import com.reddate.taianddc.constant.ErrorMessage;
import com.reddate.taianddc.constant.OpbCrossChainFunctions;
import com.reddate.taianddc.constant.State;
import com.reddate.taianddc.dto.taianchain.ReqJsonRpcBean;
import com.reddate.taianddc.dto.taianchain.RespJsonRpcBean;
import com.reddate.taianddc.exception.DDCException;
import com.reddate.taianddc.listener.SignEventListener;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.fisco.bcos.web3j.abi.TypeReference;
import org.fisco.bcos.web3j.abi.datatypes.*;
import org.fisco.bcos.web3j.abi.datatypes.generated.Uint256;
import org.fisco.bcos.web3j.abi.datatypes.generated.Uint64;
import org.fisco.bcos.web3j.abi.datatypes.generated.Uint8;
import org.fisco.bcos.web3j.utils.Numeric;
import org.web3j.utils.Strings;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.Objects;

@Slf4j
public class OpbCrossChainService extends BaseService {

    public OpbCrossChainService(SignEventListener signEventListener) {
        super.signEventListener = signEventListener;
    }

    public String crossChainTransfer(String sender, DDCType ddcType, BigInteger ddcId, Boolean isLock, BigInteger toChainId, String to, String data) throws Exception {
        checkSenderAddress(sender);
        checkToAddress(to);
        checkDDCType(ddcType);
        checkDDCID(ddcId);
        checkToChainID(toChainId);

        // check isLock
        if (Objects.isNull(isLock)) {
            throw new DDCException(ErrorMessage.ISLOCK_IS_EMPTY);
        }
        Function function = new Function(
                OpbCrossChainFunctions.CROSS_CHAIN_TRANSFER,
                Arrays.asList(new Uint8(ddcType.getType()),
                        new Uint256(ddcId),
                        new Bool(isLock),
                        new Uint64(toChainId),
                        new Address(to),
                        new DynamicBytes(encodeDdcData(data))),
                Collections.<TypeReference<?>>emptyList());
        ReqJsonRpcBean reqJsonRpcBean = assembleOpbCrossChainByTransaction(sender, function);

        RespJsonRpcBean respJsonRpcBean = restTemplateUtil.sendPost(ConfigCache.get().getOpbGatewayAddress(), reqJsonRpcBean, RespJsonRpcBean.class);
        resultCheck(respJsonRpcBean);
        return (String) respJsonRpcBean.getResult();
    }

    public String updateCrossChainStatus(String sender, BigInteger crossChainId, State state, String remark) throws Exception {
        checkSenderAddress(sender);
        checkCrossChainID(crossChainId);
        //check state
        if (Objects.isNull(state)) {
            throw new DDCException(ErrorMessage.CROSS_CHAIN_STATE_IS_EMPTY);
        }

        if (state.equals(State.CROSS_CHAIN)) {
            throw new DDCException(ErrorMessage.ILLEGAL_STATE_PARAMETER);
        }
        //check remark
        if (Strings.isEmpty(remark)) {
            throw new DDCException(ErrorMessage.REMARK_IS_EMPTY);
        }

        Function function = new Function(
                OpbCrossChainFunctions.UPDATE_CROSS_CHAIN_STATUS,
                Arrays.asList(new Uint256(crossChainId),
                        new Uint8(state.getState()),
                        new Utf8String(remark)),
                Collections.<TypeReference<?>>emptyList());

        ReqJsonRpcBean reqJsonRpcBean = assembleOpbCrossChainByTransaction(sender, function);
        RespJsonRpcBean respJsonRpcBean = restTemplateUtil.sendPost(ConfigCache.get().getOpbGatewayAddress(), reqJsonRpcBean, RespJsonRpcBean.class);
        resultCheck(respJsonRpcBean);
        return (String) respJsonRpcBean.getResult();
    }

    private ReqJsonRpcBean assembleOpbCrossChainByTransaction(String sender, Function function) throws Exception {
        return assembleTransactionByFunction(sender, getBlockNumber(), ConfigCache.get().getOpbCrossChainABI(), ConfigCache.get().getOpbCrossChainAddress(), function);
    }

    public byte[] encodeDdcData(String ddcData) {
        byte[] data = new byte[0];
        if (StringUtils.isNotEmpty(ddcData) && ddcData.contains("0x")) {
            data = Numeric.hexStringToByteArray(ddcData);
        } else if(StringUtils.isNotEmpty(ddcData)) {
            data = ddcData.getBytes(StandardCharsets.UTF_8);
        }
        return data;
    }
}
