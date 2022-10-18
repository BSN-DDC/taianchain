package com.reddate.taianddc.service;

import com.alibaba.fastjson.JSONObject;
import com.reddate.taianddc.config.ConfigCache;
import com.reddate.taianddc.constant.ErrorMessage;
import com.reddate.taianddc.constant.FiscoFunctions;
import com.reddate.taianddc.constant.DDCType;
import com.reddate.taianddc.dto.taianchain.*;
import com.reddate.taianddc.exception.DDCException;
import com.reddate.taianddc.listener.SignEventListener;
import com.reddate.taianddc.util.AddressUtils;
import com.reddate.taianddc.util.AnalyzeChainInfoUtils;
import com.reddate.taianddc.util.HexUtils;
import com.reddate.taianddc.util.SignedTransactionsUtils;
import com.reddate.taianddc.util.http.RestTemplateUtil;
import lombok.extern.slf4j.Slf4j;
import org.fisco.bcos.web3j.abi.datatypes.Function;
import org.fisco.bcos.web3j.crypto.gm.sm2.util.encoders.Hex;
import org.fisco.bcos.web3j.protocol.exceptions.TransactionException;
import org.fisco.bcos.web3j.tx.txdecode.BaseException;
import org.fisco.bcos.web3j.tx.txdecode.InputAndOutputResult;
import org.fisco.bcos.web3j.utils.Strings;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

@Slf4j
public class BaseService {

    protected RestTemplateUtil restTemplateUtil = new RestTemplateUtil();

    protected SignEventListener signEventListener;

    public static final String ZeroAddress = "0x0000000000000000000000000000000000000000";
    // 任意地址，用以发送call
    public static final String OneAddress = "0x0000000000000000000000000000000000000001";
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

        ReqJsonRpcBean reqJsonRpcBean = SignedTransactionsUtils.buildTrans(reqTransBean, signEventListener, sender, blockHeight);

        return reqJsonRpcBean;
    }

    /**
     * 组装交易
     *
     * @param blockHeight 区块高度
     * @param abi 合约abi
     * @param contractAddress 合约地址
     * @param function 合约方法
     * @return ReqJsonRpcBean
     * @throws Exception Exception
     */
    public ReqJsonRpcBean assembleTransactionByFunction(String sender, BigInteger blockHeight, String abi, String contractAddress, Function function) throws Exception {
        ReqTransBean reqTransBean = new ReqTransBean();
        reqTransBean.setGroupId(1);
        reqTransBean.setBlockNumber(blockHeight);
        reqTransBean.setContractAbi(abi);
        reqTransBean.setContractAddress(contractAddress);


        if(signEventListener == null) {
            log.error("assembleTransaction {}",ErrorMessage.NO_SIGN_EVENT_LISTNER);
            throw new DDCException(ErrorMessage.NO_SIGN_EVENT_LISTNER);
        }

        if(sender == null) {
            log.error("assembleTransaction {}",ErrorMessage.SENDER_IS_EMPTY);
            throw new DDCException(ErrorMessage.SENDER_IS_EMPTY);
        }

        ReqJsonRpcBean reqJsonRpcBean = SignedTransactionsUtils.buildTransactionByFunction(reqTransBean, function, signEventListener, sender, blockHeight);

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
    public TransactionRecepitBean getTransactionReceipt(String hash) throws InterruptedException {
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
    	TransactionRecepitBean transactionRecepitBean = getTransactionReceipt(hash);
    	if(transactionRecepitBean == null) {
    		Long queryRecepitTimeout = ConfigCache.get().getQueryRecepitWaitTime();
            Integer queryRecepitRetryCount = ConfigCache.get().getQueryRecepitRetryCount();
            log.debug("query transaction recepit, wait for {} millis, max retry times {}",queryRecepitTimeout,queryRecepitRetryCount);
            for(int i = 0; i < queryRecepitRetryCount; i++) {
            	Thread.sleep(queryRecepitTimeout);
            	transactionRecepitBean = getTransactionReceipt(hash);
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
     * getTransactionByHash
     * @param hash hash
     * @return TransactionInfoBean
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

    public void checkDDCID(BigInteger ddcId) {
        if (null == ddcId) {
            throw new DDCException(ErrorMessage.DDCID_IS_WRONG);
        }

        if (ddcId.compareTo(new BigInteger("0")) <= 0) {
            throw new DDCException(ErrorMessage.DDCID_IS_WRONG);
        }
    }

    public void checkDDCAmount(BigInteger amount) {
        if (null == amount || BigInteger.valueOf(0).compareTo(amount) >= 0) {
            throw new DDCException(ErrorMessage.AMOUNT_IS_EMPTY);
        }
    }

    public void checkMetaSign(byte[] sign) {
        if (sign.length == 0) {
            throw new DDCException(ErrorMessage.SIG_IS_EMPTY);
        }
    }

    public void checkSig(String sig) {
        if (Strings.isEmpty(sig)) {
            throw new DDCException(ErrorMessage.SIG_IS_EMPTY);
        }

        if (!HexUtils.isValid4ByteHash(sig)) {
            throw new DDCException(ErrorMessage.SIG_IS_NOT_4BYTE_HASH);
        }
    }

    public void checkDDCAddress(String ddcAddr) {
        if (Strings.isEmpty(ddcAddr)) {
            throw new DDCException(ErrorMessage.DDC_ADDR_IS_EMPTY);
        }

        if (!AddressUtils.isValidAddress(ddcAddr)) {
            throw new DDCException(ErrorMessage.DDC_ADDR_IS_NOT_ADDRESS_FORMAT);
        }
    }

    public void checkSenderAddress(String address) {
        if (Strings.isEmpty(address)) {
            throw new DDCException(ErrorMessage.SENDER_IS_EMPTY);
        }

        if (!AddressUtils.isValidAddress(address)) {
            throw new DDCException(ErrorMessage.SENDER_IS_NOT_ADDRESS_FORMAT);
        }
    }

    public void checkFromAddress(String address) {
        if (Strings.isEmpty(address)) {
            throw new DDCException(ErrorMessage.FROM_ACCOUNT_IS_EMPTY);
        }

        if (!AddressUtils.isValidAddress(address)) {
            throw new DDCException(ErrorMessage.FROM_ACCOUNT_IS_NOT_ADDRESS_FORMAT);
        }
    }

    public void checkToAddress(String to) {
        if (Strings.isEmpty(to)) {
            throw new DDCException(ErrorMessage.TO_ACCOUNT_IS_EMPTY);
        }

        if (!AddressUtils.isValidAddress(to)) {
            throw new DDCException(ErrorMessage.TO_ACCOUNT_IS_NOT_ADDRESS_FORMAT);
        }
    }

    public void checkAccountAddress(String accountAddress) {
        if (Strings.isEmpty(accountAddress)) {
            throw new DDCException(ErrorMessage.ACCOUNT_IS_EMPTY);
        }
        if (!AddressUtils.isValidAddress(accountAddress)) {
            throw new DDCException(ErrorMessage.ACCOUNT_IS_NOT_ADDRESS_FORMAT);
        }
    }

    public void checkDeadline(BigInteger deadline) {
        if (null == deadline) {
            throw new DDCException(ErrorMessage.DEADLINE_IS_WRONG);
        }

        if (deadline.compareTo(new BigInteger("0")) <= 0) {
            throw new DDCException(ErrorMessage.DEADLINE_IS_WRONG);
        }
    }

    public void checkToChainID(BigInteger toChainID){
        if (null == toChainID){
            throw new DDCException(ErrorMessage.TO_CHAIN_ID_IS_EMPTY);
        }

        if (toChainID.compareTo(new BigInteger("0")) <= 0){
            throw new DDCException(ErrorMessage.TO_CHAIN_ID_IS_WRONG);
        }
    }

    public void checkToCCAddr(String toCCAddr){
        if (Strings.isEmpty(toCCAddr)) {
            throw new DDCException(ErrorMessage.TO_CC_ADDR_IS_EMPTY);
        }
        if (!AddressUtils.isValidAddress(toCCAddr)) {
            throw new DDCException(ErrorMessage.TO_CC_ADDR_IS_NOT_ADDRESS_FORMAT);
        }
    }

    public void checkFuncName(String funcName){
        if (Strings.isEmpty(funcName)) {
            throw new DDCException(ErrorMessage.FUNCNAME_IS_EMPTY);
        }
    }

    public void checkDDCType(DDCType ddcType){
        if (null == ddcType.getType()){
            throw new DDCException(ErrorMessage.DDC_TYPE_IS_WRONG);
        }

        if(ddcType.getType().compareTo(BigInteger.valueOf(0)) != 0 && ddcType.getType().compareTo(BigInteger.valueOf(1)) != 0){
            throw new DDCException(ErrorMessage.DDC_TYPE_IS_WRONG);
        }
    }

    public void checkCrossChainID(BigInteger crossChainID) {
        if (null == crossChainID) {
            throw new DDCException(ErrorMessage.CROSS_CHAIN_ID_IS_WRONG);
        }

        if (crossChainID.compareTo(new BigInteger("0")) <= 0) {
            throw new DDCException(ErrorMessage.CROSS_CHAIN_ID_IS_WRONG);
        }
    }

    /**
     * check result
     */
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

    /**
     * check call result
     * @param respCallRpcBean respCallRpcBean
     */
    public void callResultCheck(RespCallRpcBean respCallRpcBean) {
        if (null == respCallRpcBean) {
            log.error("callResultCheck {}",ErrorMessage.REQUEST_FAILED);
            throw new DDCException(ErrorMessage.REQUEST_FAILED);
        }

        if (respCallRpcBean.getStatus().equals("0x16")) {
            log.error("callResultCheck {}",respCallRpcBean.getStatus());
            throw new DDCException(ErrorMessage.REQUEST_FAILED.getCode(), respCallRpcBean.toString());
        }
    }
}
