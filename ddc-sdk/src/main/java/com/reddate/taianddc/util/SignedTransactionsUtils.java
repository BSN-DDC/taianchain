package com.reddate.taianddc.util;

import com.alibaba.fastjson.JSONObject;
import com.reddate.taianddc.constant.ErrorMessage;
import com.reddate.taianddc.constant.FiscoFunctions;
import com.reddate.taianddc.dto.taianchain.ReqCallRpcBean;
import com.reddate.taianddc.dto.taianchain.ReqJsonRpcBean;
import com.reddate.taianddc.dto.taianchain.ReqTransBean;
import com.reddate.taianddc.exception.DDCException;
import com.reddate.taianddc.listener.SignEvent;
import com.reddate.taianddc.listener.SignEventListener;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.fisco.bcos.web3j.abi.FunctionEncoder;
import org.fisco.bcos.web3j.abi.TypeReference;
import org.fisco.bcos.web3j.abi.datatypes.DynamicArray;
import org.fisco.bcos.web3j.abi.datatypes.Function;
import org.fisco.bcos.web3j.abi.datatypes.Type;
import org.fisco.bcos.web3j.abi.datatypes.generated.AbiTypes;
import org.fisco.bcos.web3j.crypto.EncryptType;
import org.fisco.bcos.web3j.crypto.ExtendedRawTransaction;
import org.fisco.bcos.web3j.crypto.ExtendedTransactionEncoder;
import org.fisco.bcos.web3j.protocol.core.methods.response.AbiDefinition;
import org.fisco.bcos.web3j.tx.AbiUtil;
import org.fisco.bcos.web3j.tx.TransactionAssembleException;
import org.fisco.bcos.web3j.tx.TransactionAssembleManager;
import org.fisco.bcos.web3j.tx.gas.DefaultGasProvider;
import org.fisco.bcos.web3j.tx.txdecode.BaseException;
import org.fisco.bcos.web3j.tx.txdecode.ContractAbiUtil;
import org.fisco.bcos.web3j.tx.txdecode.ContractTypeUtil;
import org.fisco.bcos.web3j.utils.Numeric;

import java.io.IOException;
import java.math.BigInteger;
import java.util.*;

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

    public static ReqJsonRpcBean buildTransactionByFunction(ReqTransBean reqTransData, Function function , SignEventListener signEventListener, String sender, BigInteger blockHeight) {
        ReqJsonRpcBean reqJsonRpcBean = new ReqJsonRpcBean();
        reqJsonRpcBean.setId(1);
        reqJsonRpcBean.setJsonrpc("2.0");

        String encodedFunction = FunctionEncoder.encode(function);
        String contractAddress = reqTransData.getContractAddress();
        Random r = new Random();
        BigInteger randomid = new BigInteger(250, r);
        BigInteger blockNumberLimit = blockHeight.add(new BigInteger(blockLimit.toString()));
        ExtendedRawTransaction extendedRawTransaction = ExtendedRawTransaction.createTransaction(randomid, DefaultGasProvider.GAS_PRICE, DefaultGasProvider.GAS_LIMIT, blockNumberLimit, contractAddress, BigInteger.ZERO, encodedFunction, new BigInteger("1"), BigInteger.valueOf((long)getGroupId()), "");
        byte[] encodedTransaction = ExtendedTransactionEncoder.encode(extendedRawTransaction);
        String encodedDataStr = Numeric.toHexString(encodedTransaction);

        SignEvent signEvent = new SignEvent();
        signEvent.setSender(sender);
        signEvent.setEncodeTransaction(encodedDataStr);
        String signedStr = signEventListener.signEvent(signEvent);

        reqJsonRpcBean.setMethod(FiscoFunctions.SendRawTransaction);
        List<Object> jsonParams = new ArrayList<>();
        jsonParams.add(1);
        jsonParams.add(signedStr);
        reqJsonRpcBean.setParams(jsonParams);
        return reqJsonRpcBean;
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
            encodeTransaction = transactionAssembleForMethodInvoke(abi, groupId, blockLimit, contractAddress, funcName, funcParam);
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
    private static Long blockLimit = 600L;
    public static String transactionAssembleForMethodInvoke(String contractAbi, int groupId, BigInteger blockNumber, String contractAddress, String funcName, List<Object> funcParam) throws IOException, BaseException {
        AbiDefinition abiDefinition = TransactionAssembleManager.getFunctionAbiDefinition(funcName, contractAbi);
        if (Objects.isNull(abiDefinition)) {
            throw new TransactionAssembleException("contract funcName is error");
        } else {
            List<String> funcInputTypes = ContractAbiUtil.getFuncInputType(abiDefinition);
            if (funcParam == null) {
                funcParam = new ArrayList();
            }

            if (funcInputTypes.size() != ((List)funcParam).size()) {
                throw new TransactionAssembleException("contract funcParam size is error");
            } else {
                List<Type> finalInputs = inputFormat(funcInputTypes, (List)funcParam);
                List<String> funOutputTypes = AbiUtil.getFuncOutputType(abiDefinition);
                List<TypeReference<?>> finalOutputs = AbiUtil.outputFormat(funOutputTypes);
                boolean isConstant = abiDefinition.isConstant();
                Function function = new Function(funcName, finalInputs, finalOutputs);
                String encodedFunction = FunctionEncoder.encode(function);
                if (isConstant) {
                    return encodedFunction;
                } else {
                    Random r = new Random();
                    BigInteger randomid = new BigInteger(250, r);
                    BigInteger blockNumberLimit = blockNumber.add(new BigInteger(blockLimit.toString()));
                    ExtendedRawTransaction extendedRawTransaction = ExtendedRawTransaction.createTransaction(randomid, DefaultGasProvider.GAS_PRICE, DefaultGasProvider.GAS_LIMIT, blockNumberLimit, contractAddress, BigInteger.ZERO, encodedFunction, new BigInteger("1"), BigInteger.valueOf((long)groupId), "");
                    byte[] encodedTransaction = ExtendedTransactionEncoder.encode(extendedRawTransaction);
                    String encodedDataStr = Numeric.toHexString(encodedTransaction);
                    return encodedDataStr;
                }
            }
        }
    }

    public static List<Type> inputFormat(List<String> funcInputTypes, List<Object> params) throws BaseException {
        List<Type> finalInputs = new ArrayList();

        for(int i = 0; i < funcInputTypes.size(); ++i) {
            Class<? extends Type> inputType = null;
            Object input = null;
            if (((String)funcInputTypes.get(i)).indexOf("[") != -1 && ((String)funcInputTypes.get(i)).indexOf("]") != -1) {
                List<Object> arrList = new ArrayList(Arrays.asList(params.get(i).toString().split(",", -1)));
                List<Type> arrParams = new ArrayList();

                for(int j = 0; j < arrList.size(); ++j) {
                    inputType = (Class<? extends Type>) AbiTypes.getType(((String)funcInputTypes.get(i)).substring(0, ((String)funcInputTypes.get(i)).indexOf("[")));
                    input = ContractTypeUtil.parseByType(((String)funcInputTypes.get(i)).substring(0, ((String)funcInputTypes.get(i)).indexOf("[")), arrList.get(j).toString());
                    arrParams.add(ContractTypeUtil.generateClassFromInput(input.toString(), inputType));
                }

                finalInputs.add(new DynamicArray<>(arrParams));
            } else {
                inputType = (Class<? extends Type>) AbiTypes.getType((String)funcInputTypes.get(i));
                input = ContractTypeUtil.parseByType((String)funcInputTypes.get(i), params.get(i).toString());
                finalInputs.add(ContractTypeUtil.generateClassFromInput(input.toString(), inputType));
            }
        }

        return finalInputs;
    }
}
