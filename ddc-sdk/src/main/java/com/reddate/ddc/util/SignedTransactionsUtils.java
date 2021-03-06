package com.reddate.ddc.util;

import com.alibaba.fastjson.JSONObject;
import com.reddate.ddc.constant.ErrorMessage;
import com.reddate.ddc.constant.FiscoFunctions;
import com.reddate.ddc.dto.taianchain.ReqCallRpcBean;
import com.reddate.ddc.dto.taianchain.ReqJsonRpcBean;
import com.reddate.ddc.dto.taianchain.ReqTransBean;
import com.reddate.ddc.exception.DDCException;
import com.reddate.ddc.listener.SignEvent;
import com.reddate.ddc.listener.SignEventListener;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.fisco.bcos.web3j.crypto.EncryptType;
import org.fisco.bcos.web3j.protocol.core.methods.response.AbiDefinition;
import org.fisco.bcos.web3j.tx.TransactionAssembleManager;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

/**
 * @author kuan
 * Created on 21/1/5.
 * @description
 */
public class SignedTransactionsUtils {

    static final BigInteger limit = new BigInteger("0");

    /**
     * @return org.fisco.bcos.web3j.crypto.EncryptType
     * @Author ccDown
     * @Date 18:03 21/1/11
     * @Description 获取fisco链算法，目前默认为secp256k1算法
     * @Param []
     **/
    private static EncryptType getEncryptTypeByAlgorithmType(int algorithmType) {
        return new EncryptType(algorithmType);
    }

    public static int getGroupId() {
        return 1;
    }

    public ReqJsonRpcBean signTransaction(byte[] chainReqBody, SignEventListener signEventListener,String sender, BigInteger blockHeight) throws Exception {
        ReqTransBean reqTransBean = getReqTransBean(chainReqBody);
        if(signEventListener == null) {
        	throw new DDCException(ErrorMessage.NO_SIGN_EVENT_LISTNER);
        }
        return buildTrans(reqTransBean, signEventListener,sender, blockHeight);
    }

    private static ReqTransBean getReqTransBean(byte[] chainReqBody) {
        String s = StringEscapeUtils.unescapeJava(new String(chainReqBody));
        return JSONObject.parseObject(s, ReqTransBean.class);
    }


    public static ReqJsonRpcBean buildTrans(ReqTransBean reqTransData, SignEventListener signEventListener, String sender, BigInteger blockHeight) throws Exception {
        int algorithmType = EncryptType.ECDSA_TYPE;
        EncryptType encryptType = getEncryptTypeByAlgorithmType(algorithmType);
        String abi = reqTransData.getContractAbi();
        String funcName = reqTransData.getFuncName();
        int groupId = getGroupId();
        String contractAddress = reqTransData.getContractAddress();
        List<Object> funcParam = reqTransData.getFuncParam();
        String byteBin = reqTransData.getContractBin();
        //Secp256K1Handle signHandle = new Secp256K1Handle(privateKey, publicKey);

        BigInteger blockLimit = blockHeight.add(limit);
        String signedStr = "";

        String encodeTransaction = "";
        if (StringUtils.isNotEmpty(byteBin)) {
            List<Object> params = reqTransData.getFuncParam();
            encodeTransaction = TransactionAssembleManager.transactionAssembleForDeploy(abi, byteBin, groupId, blockLimit, params);
        } else {
            encodeTransaction = TransactionAssembleManager.transactionAssembleForMethodInvoke(abi, groupId, blockLimit, contractAddress, funcName, funcParam);
        }

        AbiDefinition abiDefinition = null;

        ReqJsonRpcBean reqJsonRpcBean = new ReqJsonRpcBean();
        reqJsonRpcBean.setId(1);
        reqJsonRpcBean.setJsonrpc("2.0");

        if (StringUtils.isNotEmpty(funcName)) {
            abiDefinition = TransactionAssembleManager.getFunctionAbiDefinition(funcName, abi);
            if (abiDefinition.isConstant() || abiDefinition.getStateMutability().equals("view")) {

                reqJsonRpcBean.setMethod(FiscoFunctions.Call);
                List<Object> jsonParams = new ArrayList<>();
                jsonParams.add(1);
                ReqCallRpcBean reqCallRpcBean = new ReqCallRpcBean();
                reqCallRpcBean.setData(encodeTransaction);
                reqCallRpcBean.setFrom(sender);
                reqCallRpcBean.setTo(contractAddress);
                jsonParams.add(reqCallRpcBean);
                reqJsonRpcBean.setParams(jsonParams);

            } else {
                // signedStr = TransactionAssembleManager.signMessageByEncryptType(encodeTransaction, signHandle.getKeyPair(), signHandle.getEncryptType());
            	SignEvent signEvent = new SignEvent();
            	signEvent.setSender(sender);
            	signEvent.setEncodeTransaction(encodeTransaction);
            	signedStr = signEventListener.signEvent(signEvent);

                reqJsonRpcBean.setMethod(FiscoFunctions.SendRawTransaction);
                List<Object> jsonParams = new ArrayList<>();
                jsonParams.add(1);
                jsonParams.add(signedStr);
                reqJsonRpcBean.setParams(jsonParams);
            }
        } else {
            // signedStr = TransactionAssembleManager.signMessageByEncryptType(encodeTransaction, signHandle.getKeyPair(), signHandle.getEncryptType());
        	SignEvent signEvent = new SignEvent();
        	signEvent.setSender(sender);
        	signEvent.setEncodeTransaction(encodeTransaction);
        	signedStr = signEventListener.signEvent(signEvent);

            reqJsonRpcBean.setMethod(FiscoFunctions.SendRawTransaction);
            List<Object> jsonParams = new ArrayList<>();
            jsonParams.add(1);
            jsonParams.add(signedStr);
            reqJsonRpcBean.setParams(jsonParams);

        }

        return reqJsonRpcBean;

    }
}
