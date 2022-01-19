package com.reddate.ddc.service;

import com.alibaba.fastjson.JSONObject;
import com.reddate.ddc.config.ConfigCache;
import com.reddate.ddc.constant.ErrorMessage;
import com.reddate.ddc.constant.FiscoFunctions;
import com.reddate.ddc.dto.taianchain.*;
import com.reddate.ddc.exception.DDCException;
import com.reddate.ddc.listener.SignEventListener;
import com.reddate.ddc.util.AnalyzeChainInfoUtils;
import com.reddate.ddc.util.SignedTransactionsUtils;
import com.reddate.ddc.util.http.RestTemplateUtil;
import lombok.extern.slf4j.Slf4j;
import org.fisco.bcos.web3j.crypto.gm.sm2.util.encoders.Hex;
import org.fisco.bcos.web3j.protocol.core.methods.response.TransactionReceipt;
import org.fisco.bcos.web3j.protocol.exceptions.TransactionException;
import org.fisco.bcos.web3j.tx.txdecode.BaseException;
import org.fisco.bcos.web3j.tx.txdecode.InputAndOutputResult;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class BaseService {

    protected RestTemplateUtil restTemplateUtil = new RestTemplateUtil();

    protected SignEventListener signEventListener;

    public static final String ZeroAddress = "0x0000000000000000000000000000000000000000";
    /**
     * 组装交易
     *
     * @param blockHeight 区块高度
     * @param abi 合约abi
     * @param contractAddress 合约地址
     * @param funcName 合约名称
     * @param params 合约参数
     * @return ReqJsonRpcBean
     * @throws Exception Exception
     */
    public ReqJsonRpcBean assembleTransaction(String sender, BigInteger blockHeight, String abi, String contractAddress, String funcName, ArrayList<Object> params) throws Exception {
        ReqTransBean reqTransBean = new ReqTransBean();
        reqTransBean.setGroupId(1);
        reqTransBean.setBlockNumber(blockHeight);
        reqTransBean.setContractAbi(abi);
        reqTransBean.setContractAddress(contractAddress);
        reqTransBean.setFuncName(funcName);
        reqTransBean.setFuncParam(params);

        if(signEventListener == null) {
            log.error("assembleTransaction {}",ErrorMessage.NO_SIGN_EVENT_LISTNER);
        	throw new DDCException(ErrorMessage.NO_SIGN_EVENT_LISTNER);
        }

        if(sender == null) {
        	log.error("assembleTransaction {}",ErrorMessage.SENDER_IS_EMPTY);
        	throw new DDCException(ErrorMessage.SENDER_IS_EMPTY);
        }
        
        
        byte[] signedResult = SignedTransactionsUtils.buildTrans(reqTransBean, signEventListener, sender, blockHeight);

        ReqJsonRpcBean reqJsonRpcBean = new ReqJsonRpcBean();
        reqJsonRpcBean.setMethod(FiscoFunctions.SendRawTransaction);
        reqJsonRpcBean.setId(1);
        reqJsonRpcBean.setJsonrpc("2.0");
        List<Object> jsonParams = new ArrayList<>();
        jsonParams.add(1);
        jsonParams.add(new String(signedResult));
        reqJsonRpcBean.setParams(jsonParams);

        return reqJsonRpcBean;
    }

    /**
     * 获取区块高度
     * @return 区块高度
     */
    public BigInteger getBlockNumber() {
        ReqJsonRpcBean reqGetBlockNumberBean = new ReqJsonRpcBean();
        reqGetBlockNumberBean.setMethod(FiscoFunctions.GetBlockNumber);
        ArrayList<Object> params = new ArrayList<>();
        params.add(SignedTransactionsUtils.getGroupId());
        reqGetBlockNumberBean.setParams(params);
        RespJsonRpcBean respJsonRpcBean = restTemplateUtil.sendPost(ConfigCache.get().getOpbGatewayAddress(), reqGetBlockNumberBean, RespJsonRpcBean.class);
        resultCheck(respJsonRpcBean);
        BigInteger blockNumber = getHeight(JSONObject.toJSONString(respJsonRpcBean));
        return blockNumber;
    }

    /**
     * 获取区块信息
     * @param blockNumber 区块高度
     * @return 区块信息
     */
    public BlockInfoBean getBlockInfo(String blockNumber) {
        ReqJsonRpcBean reqGetBlockNumberBean = new ReqJsonRpcBean();
        reqGetBlockNumberBean.setMethod(FiscoFunctions.GetBlockByNumber);
        ArrayList<Object> params = new ArrayList<>();
        params.add(SignedTransactionsUtils.getGroupId());
        params.add(blockNumber);
        params.add(true);
        reqGetBlockNumberBean.setParams(params);
        RespJsonRpcBean respJsonRpcBean = restTemplateUtil.sendPost(ConfigCache.get().getOpbGatewayAddress(), reqGetBlockNumberBean, RespJsonRpcBean.class);
        resultCheck(respJsonRpcBean);
        return JSONObject.parseObject(JSONObject.toJSONString(respJsonRpcBean.getResult()),BlockInfoBean.class);
    }

    /**
     * 获取交易回执
     * @param hash 交易哈希
     * @return 交易回执
     * @throws InterruptedException InterruptedException
     */
    public TransactionRecepitBean getTransactionRecepit(String hash) throws InterruptedException {
        ReqJsonRpcBean reqJsonRpcBean = new ReqJsonRpcBean();
        ArrayList<Object> params = new ArrayList<>();
        reqJsonRpcBean.setMethod(FiscoFunctions.GetTransactionReceipt);
        params.add(SignedTransactionsUtils.getGroupId());
        params.add(hash);
        reqJsonRpcBean.setParams(params);
        RespJsonRpcBean respJsonRpcBean = restTemplateUtil.sendPost(ConfigCache.get().getOpbGatewayAddress(), reqJsonRpcBean, RespJsonRpcBean.class);
        resultCheck(respJsonRpcBean);
        return JSONObject.parseObject(JSONObject.toJSONString(respJsonRpcBean.getResult()),TransactionRecepitBean.class);
    }

    /**
     * 根据交易hash查询交易回执并解析交易回执中的output
     * @param hash 交易哈希
     * @return 解析的output结果
     * @throws InterruptedException InterruptedException
     * @throws BaseException BaseException
     * @throws TransactionException TransactionException
     */
    public InputAndOutputResult analyzeTransactionRecepitOutput(String abi, String bin, String hash) throws InterruptedException, BaseException, TransactionException {
    	TransactionRecepitBean transactionRecepitBean = getTransactionRecepit(hash);
    	if(transactionRecepitBean == null) {
    		Long queryRecepitTimeout = ConfigCache.get().getQueryRecepitWaitTime();
            Integer queryRecepitRetryCount = ConfigCache.get().getQueryRecepitRetryCount();
            log.debug("query transaction recepit, wait for {} millis, max retry times {}",queryRecepitTimeout,queryRecepitRetryCount);
            for(int i = 0; i < queryRecepitRetryCount; i++) {
            	Thread.sleep(queryRecepitTimeout);
            	transactionRecepitBean = getTransactionRecepit(hash);
            	if(transactionRecepitBean != null) {
            		break;
            	}
            }
    	}
        
        if (null == transactionRecepitBean) {
            log.error("analyzeTransactionRecepitOutput {}",ErrorMessage.REQUEST_FAILED);
            throw new DDCException(ErrorMessage.REQUEST_FAILED);
        }

        if (!transactionRecepitBean.getStatus().equals("0x0")) {
            String errorMsg = new String(Hex.decode(transactionRecepitBean.getOutput().substring(2).getBytes(StandardCharsets.UTF_8)));
            log.error("analyzeTransactionRecepitOutput {}",errorMsg);
            throw new DDCException(ErrorMessage.REQUEST_FAILED.getCode(),errorMsg);
        }

        InputAndOutputResult inputAndOutputResult = AnalyzeChainInfoUtils.analyzeTransactionOutput(abi, bin, transactionRecepitBean.getInput(), transactionRecepitBean.getOutput());
        if (inputAndOutputResult.getResult().size() == 0) {
            log.error("analyzeTransactionRecepitOutput {}",ErrorMessage.REQUEST_FAILED);
            throw new DDCException(ErrorMessage.REQUEST_FAILED);
        }
        return inputAndOutputResult;
    }

    /**
     * 获取交易信息
     * @param hash 交易哈希
     * @return 交易信息
     */
    public TransactionInfoBean getTransactionByHash(String hash) {
        ReqJsonRpcBean reqJsonRpcBean = new ReqJsonRpcBean();
        ArrayList<Object> params = new ArrayList<>();
        reqJsonRpcBean.setMethod(FiscoFunctions.GetTransactionByHash);
        params.add(SignedTransactionsUtils.getGroupId());
        params.add(hash);
        reqJsonRpcBean.setParams(params);
        RespJsonRpcBean respJsonRpcBean = restTemplateUtil.sendPost(ConfigCache.get().getOpbGatewayAddress(), reqJsonRpcBean, RespJsonRpcBean.class);
        resultCheck(respJsonRpcBean);
        return JSONObject.parseObject(JSONObject.toJSONString(respJsonRpcBean.getResult()),TransactionInfoBean.class);
    }

    private BigInteger getHeight(String result) {
        try {
            JSONObject getHeightJson = JSONObject.parseObject(result);

            if (null == getHeightJson) {
                return null;
            }

            String blockNumberHexString = getHeightJson.getString("result");
            if (blockNumberHexString.contains("0x")) {
                blockNumberHexString = blockNumberHexString.replace("0x", "");
            }
            return BigInteger.valueOf(Long.parseLong(blockNumberHexString, 16));
        } catch (RuntimeException e) {
            return null;
        }
    }

    // 校验交易结果
    public void resultCheck(RespJsonRpcBean respJsonRpcBean) {
        if (null == respJsonRpcBean) {
            log.error("resultCheck {}",ErrorMessage.REQUEST_FAILED);
            throw new DDCException(ErrorMessage.REQUEST_FAILED);
        }

        if (respJsonRpcBean.getError() != null) {
            log.error("resultCheck {}",respJsonRpcBean.getError().toString());
            throw new DDCException(ErrorMessage.REQUEST_FAILED, respJsonRpcBean.getError());
        }
    }
}
