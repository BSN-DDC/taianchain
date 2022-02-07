package com.reddate.ddc.service;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.reddate.ddc.config.ConfigCache;
import com.reddate.ddc.constant.DDC721Functions;
import com.reddate.ddc.constant.ErrorMessage;
import com.reddate.ddc.dto.taianchain.ReqCallRpcBean;
import com.reddate.ddc.dto.taianchain.ReqJsonRpcBean;
import com.reddate.ddc.dto.taianchain.RespCallRpcBean;
import com.reddate.ddc.dto.taianchain.RespJsonRpcBean;
import com.reddate.ddc.exception.DDCException;
import com.reddate.ddc.listener.SignEventListener;
import com.reddate.ddc.util.AddressUtils;
import com.reddate.ddc.util.AnalyzeChainInfoUtils;
import org.fisco.bcos.web3j.tx.txdecode.InputAndOutputResult;
import org.fisco.bcos.web3j.utils.Strings;

import java.math.BigInteger;
import java.util.ArrayList;


public class DDC721Service extends BaseService {

	public DDC721Service(SignEventListener signEventListener) {
		super.signEventListener = signEventListener;
	}

    /**
     * 创建DDC
     * 
     * @param sender 调用者地址
     * @param to     接收者账户
     * @param ddcURI DDC资源标识符
     * @return 交易哈希
     * @throws Exception Exception
     */
    public String mint(String sender,String to, String ddcURI) throws Exception {
        if (Strings.isEmpty(sender)) {
            throw new DDCException(ErrorMessage.SENDER_IS_EMPTY);
        }

        if (!AddressUtils.isValidAddress(sender)) {
            throw new DDCException(ErrorMessage.SENDER_IS_NOT_ADDRESS_FORMAT);
        }

        if (Strings.isEmpty(to)) {
            throw new DDCException(ErrorMessage.TO_ACCOUNT_IS_EMPTY);
        }
        if (Strings.isEmpty(ddcURI)) {
            throw new DDCException(ErrorMessage.DDCURI_IS_EMPTY);
        }
        if (!AddressUtils.isValidAddress(to)) {
            throw new DDCException(ErrorMessage.TO_ACCOUNT_IS_NOT_ADDRESS_FORMAT);
        }

        ArrayList<Object> arrayList = new ArrayList<>();
        arrayList.add(to);
        arrayList.add(ddcURI);

        ReqJsonRpcBean reqJsonRpcBean = assembleDDC721Transaction(sender,DDC721Functions.MINT, arrayList);
        RespJsonRpcBean respJsonRpcBean = restTemplateUtil.sendPost(ConfigCache.get().getOpbGatewayAddress(), reqJsonRpcBean, RespJsonRpcBean.class);

        resultCheck(respJsonRpcBean);
        return (String) respJsonRpcBean.getResult();
    }
    
    /**
     * DDC的安全生成
     * 
     * @param sender 调用者地址
     * @param to     接收者账户
     * @param ddcURI DDC资源标识符
     * @param data 附加数据
     * @return 交易哈希
     * @throws Exception Exception
     */
    public String safeMint(String sender,String to, String ddcURI,byte[] data) throws Exception {
        if (Strings.isEmpty(sender)) {
            throw new DDCException(ErrorMessage.SENDER_IS_EMPTY);
        }

        if (!AddressUtils.isValidAddress(sender)) {
            throw new DDCException(ErrorMessage.SENDER_IS_NOT_ADDRESS_FORMAT);
        }

        if (Strings.isEmpty(to)) {
            throw new DDCException(ErrorMessage.TO_ACCOUNT_IS_EMPTY);
        }
        if (Strings.isEmpty(ddcURI)) {
            throw new DDCException(ErrorMessage.DDCURI_IS_EMPTY);
        }
        if (!AddressUtils.isValidAddress(to)) {
            throw new DDCException(ErrorMessage.TO_ACCOUNT_IS_NOT_ADDRESS_FORMAT);
        }

        ArrayList<Object> arrayList = new ArrayList<>();
        arrayList.add(to);
        arrayList.add(ddcURI);
        // arrayList.add(new String(data));
        arrayList.add(data);

        ReqJsonRpcBean reqJsonRpcBean = assembleDDC721Transaction(sender,DDC721Functions.SAFE_MINT, arrayList);
        RespJsonRpcBean respJsonRpcBean = restTemplateUtil.sendPost(ConfigCache.get().getOpbGatewayAddress(), reqJsonRpcBean, RespJsonRpcBean.class);

        resultCheck(respJsonRpcBean);
        return (String) respJsonRpcBean.getResult();
    }
    


    /**
     * 授权DDC
     * 
     * @param sender 调用者地址
     * @param to    授权者账户
     * @param ddcId DDC唯一标识
     * @return 交易哈希
     * @throws Exception Exception
     */
    public String approve(String sender,String to, BigInteger ddcId) throws Exception {
        if (Strings.isEmpty(sender)) {
            throw new DDCException(ErrorMessage.SENDER_IS_EMPTY);
        }

        if (!AddressUtils.isValidAddress(sender)) {
            throw new DDCException(ErrorMessage.SENDER_IS_NOT_ADDRESS_FORMAT);
        }

        if (Strings.isEmpty(to)) {
            throw new DDCException(ErrorMessage.TO_ACCOUNT_IS_EMPTY);
        }
        if (null == ddcId || ddcId.compareTo(new BigInteger("0")) <= 0) {
            throw new DDCException(ErrorMessage.DDCID_IS_WRONG);
        }
        if (!AddressUtils.isValidAddress(to)) {
            throw new DDCException(ErrorMessage.TO_ACCOUNT_IS_NOT_ADDRESS_FORMAT);
        }
        ArrayList<Object> arrayList = new ArrayList<>();
        arrayList.add(to);
        arrayList.add(ddcId);

        ReqJsonRpcBean reqJsonRpcBean = assembleDDC721Transaction(sender,DDC721Functions.APPROVE, arrayList);
        RespJsonRpcBean respJsonRpcBean = restTemplateUtil.sendPost(ConfigCache.get().getOpbGatewayAddress(), reqJsonRpcBean, RespJsonRpcBean.class);
        resultCheck(respJsonRpcBean);
        return (String) respJsonRpcBean.getResult();
    }


    /**
     * 授权查询
     * 
     * @param ddcId DDC唯一标识
     * @return 授权的账户
     * @throws Exception Exception
     */
    public String getApproved(BigInteger ddcId) throws Exception {

        if (null == ddcId) {
            throw new DDCException(ErrorMessage.DDCID_IS_WRONG);
        }

        if(ddcId.compareTo(new BigInteger("0")) <= 0) {
        	throw new DDCException(ErrorMessage.DDCID_IS_WRONG);
        }
        
        ArrayList<Object> arrayList = new ArrayList<>();
        arrayList.add(ddcId);

        ReqJsonRpcBean reqJsonRpcBean = assembleDDC721CallTransaction(DDC721Functions.GET_APPROVED, arrayList);
        RespJsonRpcBean respJsonRpcBean = restTemplateUtil.sendPost(ConfigCache.get().getOpbGatewayAddress(), reqJsonRpcBean, RespJsonRpcBean.class);
        resultCheck(respJsonRpcBean);

        String encodeParams = new ObjectMapper().writeValueAsString(reqJsonRpcBean.getParams().get(1));
        ReqCallRpcBean reqCallRpcBean = JSONObject.parseObject(encodeParams, ReqCallRpcBean.class);
        String encodedFunction = reqCallRpcBean.getData();
        String jsonResult = new ObjectMapper().writeValueAsString(respJsonRpcBean.getResult());
        RespCallRpcBean respCallRpcBean = JSONObject.parseObject(jsonResult, RespCallRpcBean.class);
        callResultCheck(respCallRpcBean);

        InputAndOutputResult inputAndOutputResult = AnalyzeChainInfoUtils.analyzeTransactionOutput(ConfigCache.get().getDdc721ABI(), ConfigCache.get().getDdc721BIN(), encodedFunction, respCallRpcBean.getOutput());

        return inputAndOutputResult.getResult().get(0).getData().toString();
    }

    /**
     * 账户授权
     * 
     * @param sender 调用者地址
     * @param operator 授权者账户
     * @param approved 授权标识
     * @return 交易hash
     * @throws Exception
     */
    public String setApprovalForAll(String sender,String operator, Boolean approved) throws Exception {
        if (Strings.isEmpty(sender)) {
            throw new DDCException(ErrorMessage.SENDER_IS_EMPTY);
        }

        if (!AddressUtils.isValidAddress(sender)) {
            throw new DDCException(ErrorMessage.SENDER_IS_NOT_ADDRESS_FORMAT);
        }

        if (Strings.isEmpty(operator)) {
            throw new DDCException(ErrorMessage.ACCOUNT_IS_EMPTY);
        }
        if (!AddressUtils.isValidAddress(operator)) {
            throw new DDCException(ErrorMessage.ACCOUNT_IS_NOT_ADDRESS_FORMAT);
        }
        if (null == approved) {
            throw new DDCException(ErrorMessage.ACCOUNT_IS_NOT_ADDRESS_FORMAT);
        }
        ArrayList<Object> arrayList = new ArrayList<>();
        arrayList.add(operator);
        arrayList.add(approved);

        ReqJsonRpcBean reqJsonRpcBean = assembleDDC721Transaction(sender,DDC721Functions.SET_APPROVAL_FOR_ALL, arrayList);
        RespJsonRpcBean respJsonRpcBean = restTemplateUtil.sendPost(ConfigCache.get().getOpbGatewayAddress(), reqJsonRpcBean, RespJsonRpcBean.class);
        resultCheck(respJsonRpcBean);
        return (String) respJsonRpcBean.getResult();
    }


    /**
     * 账户授权查询
     *
     * @param owner 拥有者账户
     * @param operator 授权者账户
     * @return 授权标识
     * @throws Exception Exception
     */
    public Boolean isApprovedForAll(String owner, String operator) throws Exception {

        if (Strings.isEmpty(owner) || Strings.isEmpty(operator)) {
            throw new DDCException(ErrorMessage.ACCOUNT_IS_EMPTY);
        }
        if (!AddressUtils.isValidAddress(owner) || !AddressUtils.isValidAddress(operator)) {
            throw new DDCException(ErrorMessage.ACCOUNT_IS_NOT_ADDRESS_FORMAT);
        }

        ArrayList<Object> arrayList = new ArrayList<>();
        arrayList.add(owner);
        arrayList.add(operator);

        ReqJsonRpcBean reqJsonRpcBean = assembleDDC721CallTransaction(DDC721Functions.IS_APPROVED_FOR_ALL, arrayList);
        RespJsonRpcBean respJsonRpcBean = restTemplateUtil.sendPost(ConfigCache.get().getOpbGatewayAddress(), reqJsonRpcBean, RespJsonRpcBean.class);
        resultCheck(respJsonRpcBean);

        String encodeParams = new ObjectMapper().writeValueAsString(reqJsonRpcBean.getParams().get(1));
        ReqCallRpcBean reqCallRpcBean = JSONObject.parseObject(encodeParams, ReqCallRpcBean.class);
        String encodedFunction = reqCallRpcBean.getData();
        String jsonResult = new ObjectMapper().writeValueAsString(respJsonRpcBean.getResult());
        RespCallRpcBean respCallRpcBean = JSONObject.parseObject(jsonResult, RespCallRpcBean.class);
        callResultCheck(respCallRpcBean);

        InputAndOutputResult inputAndOutputResult = AnalyzeChainInfoUtils.analyzeTransactionOutput(ConfigCache.get().getDdc721ABI(), ConfigCache.get().getDdc721BIN(), encodedFunction, respCallRpcBean.getOutput());
        return Boolean.valueOf(inputAndOutputResult.getResult().get(0).getData().toString());
    }


    /**
     * DDC的转移
     *
     * @param sender 调用者地址
     * @param from  拥有者账户
     * @param to    授权者账户
     * @param ddcId DDC唯一标识
     * @param data  附加数据
     * @return 交易hash
     * @throws Exception Exception
     */
    public String safeTransferFrom(String sender,String from, String to, BigInteger ddcId, byte[] data) throws Exception {
        if (Strings.isEmpty(sender)) {
            throw new DDCException(ErrorMessage.SENDER_IS_EMPTY);
        }

        if (!AddressUtils.isValidAddress(sender)) {
            throw new DDCException(ErrorMessage.SENDER_IS_NOT_ADDRESS_FORMAT);
        }

        if (Strings.isEmpty(from)) {
            throw new DDCException(ErrorMessage.FROM_ACCOUNT_IS_EMPTY);
        }
        if (Strings.isEmpty(to)) {
            throw new DDCException(ErrorMessage.TO_ACCOUNT_IS_EMPTY);
        }
        if (!AddressUtils.isValidAddress(from) ) {
            throw new DDCException(ErrorMessage.FROM_ACCOUNT_IS_NOT_ADDRESS_FORMAT);
        }
        if (!AddressUtils.isValidAddress(to) ) {
            throw new DDCException(ErrorMessage.TO_ACCOUNT_IS_NOT_ADDRESS_FORMAT);
        }
        if (null == ddcId || ddcId.compareTo(new BigInteger("0")) <= 0) {
            throw new DDCException(ErrorMessage.DDCID_IS_WRONG);
        }
        ArrayList<Object> arrayList = new ArrayList<>();
        arrayList.add(from);
        arrayList.add(to);
        arrayList.add(ddcId);
        arrayList.add(data);

        ReqJsonRpcBean reqJsonRpcBean = assembleDDC721Transaction(sender,DDC721Functions.SAFE_TRANSFER_FROM, arrayList);
        RespJsonRpcBean respJsonRpcBean = restTemplateUtil.sendPost(ConfigCache.get().getOpbGatewayAddress(), reqJsonRpcBean, RespJsonRpcBean.class);
        resultCheck(respJsonRpcBean);
        return (String) respJsonRpcBean.getResult();
    }


    /**
     * 转移
     *
     * @param sender 调用者地址
     * @param from  拥有者账户
     * @param to    接收者账户
     * @param ddcId ddc唯一标识
     * @return 交易hash
     * @throws Exception Exception
     */
    public String transferFrom(String sender,String from, String to, BigInteger ddcId) throws Exception {
        if (Strings.isEmpty(sender)) {
            throw new DDCException(ErrorMessage.SENDER_IS_EMPTY);
        }

        if (!AddressUtils.isValidAddress(sender)) {
            throw new DDCException(ErrorMessage.SENDER_IS_NOT_ADDRESS_FORMAT);
        }

        if (Strings.isEmpty(from)) {
            throw new DDCException(ErrorMessage.FROM_ACCOUNT_IS_EMPTY);
        }
        if (Strings.isEmpty(to)) {
            throw new DDCException(ErrorMessage.TO_ACCOUNT_IS_EMPTY);
        }
        if (!AddressUtils.isValidAddress(from) ) {
            throw new DDCException(ErrorMessage.FROM_ACCOUNT_IS_NOT_ADDRESS_FORMAT);
        }
        if (!AddressUtils.isValidAddress(to) ) {
            throw new DDCException(ErrorMessage.TO_ACCOUNT_IS_NOT_ADDRESS_FORMAT);
        }
        if (null == ddcId || ddcId.compareTo(new BigInteger("0")) <= 0) {
            throw new DDCException(ErrorMessage.DDCID_IS_WRONG);
        }

        ArrayList<Object> arrayList = new ArrayList<>();
        arrayList.add(from);
        arrayList.add(to);
        arrayList.add(ddcId);

        ReqJsonRpcBean reqJsonRpcBean = assembleDDC721Transaction(sender,DDC721Functions.TRANSFER_FROM, arrayList);
        RespJsonRpcBean respJsonRpcBean = restTemplateUtil.sendPost(ConfigCache.get().getOpbGatewayAddress(), reqJsonRpcBean, RespJsonRpcBean.class);
        resultCheck(respJsonRpcBean);
        return (String) respJsonRpcBean.getResult();
    }


    /**
     * 冻结
     *
     * @param sender 调用者地址
     * @param ddcId DDC唯一标识
     * @return 交易hash
     * @throws Exception Exception
     */
    public String freeze(String sender,BigInteger ddcId) throws Exception {
        if (Strings.isEmpty(sender)) {
            throw new DDCException(ErrorMessage.SENDER_IS_EMPTY);
        }

        if (!AddressUtils.isValidAddress(sender)) {
            throw new DDCException(ErrorMessage.SENDER_IS_NOT_ADDRESS_FORMAT);
        }

        if (null == ddcId || ddcId.compareTo(new BigInteger("0")) <= 0) {
            throw new DDCException(ErrorMessage.DDCID_IS_WRONG);
        }
        ArrayList<Object> arrayList = new ArrayList<>();
        arrayList.add(ddcId);

        ReqJsonRpcBean reqJsonRpcBean = assembleDDC721Transaction(sender,DDC721Functions.FREEZE, arrayList);
        RespJsonRpcBean respJsonRpcBean = restTemplateUtil.sendPost(ConfigCache.get().getOpbGatewayAddress(), reqJsonRpcBean, RespJsonRpcBean.class);
        resultCheck(respJsonRpcBean);
        return (String) respJsonRpcBean.getResult();
    }

    /**
     * 解冻
     *
     * @param sender 调用者地址
     * @param ddcId DDC唯一标识
     * @return 交易hash
     * @throws Exception Exception
     */
    public String unFreeze(String sender,BigInteger ddcId) throws Exception {
        if (Strings.isEmpty(sender)) {
            throw new DDCException(ErrorMessage.SENDER_IS_EMPTY);
        }

        if (!AddressUtils.isValidAddress(sender)) {
            throw new DDCException(ErrorMessage.SENDER_IS_NOT_ADDRESS_FORMAT);
        }

        if (null == ddcId || ddcId.compareTo(new BigInteger("0")) <= 0) {
            throw new DDCException(ErrorMessage.DDCID_IS_WRONG);
        }
        ArrayList<Object> arrayList = new ArrayList<>();
        arrayList.add(ddcId);

        ReqJsonRpcBean reqJsonRpcBean = assembleDDC721Transaction(sender,DDC721Functions.UNFREEZE, arrayList);
        RespJsonRpcBean respJsonRpcBean = restTemplateUtil.sendPost(ConfigCache.get().getOpbGatewayAddress(), reqJsonRpcBean, RespJsonRpcBean.class);
        resultCheck(respJsonRpcBean);
        return (String) respJsonRpcBean.getResult();
    }

    /**
     * 销毁
     *
     * @param sender 调用者地址
     * @param ddcId DDC唯一标识
     * @return 交易hash
     * @throws Exception Exception
     */
    public String burn(String sender,BigInteger ddcId) throws Exception {
        if (Strings.isEmpty(sender)) {
            throw new DDCException(ErrorMessage.SENDER_IS_EMPTY);
        }

        if (!AddressUtils.isValidAddress(sender)) {
            throw new DDCException(ErrorMessage.SENDER_IS_NOT_ADDRESS_FORMAT);
        }

        if (null == ddcId || ddcId.compareTo(new BigInteger("0")) <= 0) {
            throw new DDCException(ErrorMessage.DDCID_IS_WRONG);
        }
        ArrayList<Object> arrayList = new ArrayList<>();
        arrayList.add(ddcId);

        ReqJsonRpcBean reqJsonRpcBean = assembleDDC721Transaction(sender,DDC721Functions.BURN, arrayList);
        RespJsonRpcBean respJsonRpcBean = restTemplateUtil.sendPost(ConfigCache.get().getOpbGatewayAddress(), reqJsonRpcBean, RespJsonRpcBean.class);
        resultCheck(respJsonRpcBean);
        return (String) respJsonRpcBean.getResult();
    }

    /**
     * 查询数量
     *
     * @param owner 拥有者账户
     * @return ddc的数量
     * @throws Exception Exception
     */
    public BigInteger balanceOf(String owner) throws Exception {

        if (Strings.isEmpty(owner)) {
            throw new DDCException(ErrorMessage.ACCOUNT_IS_EMPTY);
        }
        if (!AddressUtils.isValidAddress(owner)) {
            throw new DDCException(ErrorMessage.ACCOUNT_IS_NOT_ADDRESS_FORMAT);
        }

        ArrayList<Object> arrayList = new ArrayList<>();
        arrayList.add(owner);

        ReqJsonRpcBean reqJsonRpcBean = assembleDDC721CallTransaction(DDC721Functions.BALANCE_OF, arrayList);
        RespJsonRpcBean respJsonRpcBean = restTemplateUtil.sendPost(ConfigCache.get().getOpbGatewayAddress(), reqJsonRpcBean, RespJsonRpcBean.class);
        resultCheck(respJsonRpcBean);

        String encodeParams = new ObjectMapper().writeValueAsString(reqJsonRpcBean.getParams().get(1));
        ReqCallRpcBean reqCallRpcBean = JSONObject.parseObject(encodeParams, ReqCallRpcBean.class);
        String encodedFunction = reqCallRpcBean.getData();
        String jsonResult = new ObjectMapper().writeValueAsString(respJsonRpcBean.getResult());
        RespCallRpcBean respCallRpcBean = JSONObject.parseObject(jsonResult, RespCallRpcBean.class);
        callResultCheck(respCallRpcBean);

        InputAndOutputResult inputAndOutputResult = AnalyzeChainInfoUtils.analyzeTransactionOutput(ConfigCache.get().getDdc721ABI(), ConfigCache.get().getDdc721BIN(), encodedFunction, respCallRpcBean.getOutput());
        return new BigInteger(inputAndOutputResult.getResult().get(0).getData().toString());
    }

    /**
     * 查询拥有者
     *
     * @param ddcId ddc唯一标识
     * @return 拥有者账户
     * @throws Exception Exception
     */
    public String ownerOf(BigInteger ddcId) throws Exception {

        if (null == ddcId || ddcId.compareTo(new BigInteger("0")) <= 0) {
            throw new DDCException(ErrorMessage.DDCID_IS_WRONG);
        }

        ArrayList<Object> arrayList = new ArrayList<>();
        arrayList.add(ddcId);

        ReqJsonRpcBean reqJsonRpcBean = assembleDDC721CallTransaction(DDC721Functions.OWNER_OF, arrayList);
        RespJsonRpcBean respJsonRpcBean = restTemplateUtil.sendPost(ConfigCache.get().getOpbGatewayAddress(), reqJsonRpcBean, RespJsonRpcBean.class);
        resultCheck(respJsonRpcBean);

        String encodeParams = new ObjectMapper().writeValueAsString(reqJsonRpcBean.getParams().get(1));
        ReqCallRpcBean reqCallRpcBean = JSONObject.parseObject(encodeParams, ReqCallRpcBean.class);
        String encodedFunction = reqCallRpcBean.getData();
        String jsonResult = new ObjectMapper().writeValueAsString(respJsonRpcBean.getResult());
        RespCallRpcBean respCallRpcBean = JSONObject.parseObject(jsonResult, RespCallRpcBean.class);
        callResultCheck(respCallRpcBean);

        InputAndOutputResult inputAndOutputResult = AnalyzeChainInfoUtils.analyzeTransactionOutput(ConfigCache.get().getDdc721ABI(), ConfigCache.get().getDdc721BIN(), encodedFunction, respCallRpcBean.getOutput());
        return inputAndOutputResult.getResult().get(0).getData().toString();
    }

    /**
     * DDC名称
     *
     * @return DDC名称
     * @throws Exception Exception
     */
    public String name() throws Exception {

        ArrayList<Object> arrayList = new ArrayList<>();
        ReqJsonRpcBean reqJsonRpcBean = assembleDDC721CallTransaction(DDC721Functions.NAME, arrayList);
        RespJsonRpcBean respJsonRpcBean = restTemplateUtil.sendPost(ConfigCache.get().getOpbGatewayAddress(), reqJsonRpcBean, RespJsonRpcBean.class);
        resultCheck(respJsonRpcBean);

        String encodeParams = new ObjectMapper().writeValueAsString(reqJsonRpcBean.getParams().get(1));
        ReqCallRpcBean reqCallRpcBean = JSONObject.parseObject(encodeParams, ReqCallRpcBean.class);
        String encodedFunction = reqCallRpcBean.getData();
        String jsonResult = new ObjectMapper().writeValueAsString(respJsonRpcBean.getResult());
        RespCallRpcBean respCallRpcBean = JSONObject.parseObject(jsonResult, RespCallRpcBean.class);
        callResultCheck(respCallRpcBean);

        InputAndOutputResult inputAndOutputResult = AnalyzeChainInfoUtils.analyzeTransactionOutput(ConfigCache.get().getDdc721ABI(), ConfigCache.get().getDdc721BIN(), encodedFunction, respCallRpcBean.getOutput());
        return inputAndOutputResult.getResult().get(0).getData().toString();
    }

    /**
     * 获取DDC符号
     *
     * @return DDC运营方符号
     * @throws Exception Exception
     */
    public String symbol() throws Exception {

        ArrayList<Object> arrayList = new ArrayList<>();
        ReqJsonRpcBean reqJsonRpcBean = assembleDDC721CallTransaction(DDC721Functions.SYMBOL, arrayList);
        RespJsonRpcBean respJsonRpcBean = restTemplateUtil.sendPost(ConfigCache.get().getOpbGatewayAddress(), reqJsonRpcBean, RespJsonRpcBean.class);
        resultCheck(respJsonRpcBean);

        String encodeParams = new ObjectMapper().writeValueAsString(reqJsonRpcBean.getParams().get(1));
        ReqCallRpcBean reqCallRpcBean = JSONObject.parseObject(encodeParams, ReqCallRpcBean.class);
        String encodedFunction = reqCallRpcBean.getData();
        String jsonResult = new ObjectMapper().writeValueAsString(respJsonRpcBean.getResult());
        RespCallRpcBean respCallRpcBean = JSONObject.parseObject(jsonResult, RespCallRpcBean.class);
        callResultCheck(respCallRpcBean);

        InputAndOutputResult inputAndOutputResult = AnalyzeChainInfoUtils.analyzeTransactionOutput(ConfigCache.get().getDdc721ABI(), ConfigCache.get().getDdc721BIN(), encodedFunction, respCallRpcBean.getOutput());
        return inputAndOutputResult.getResult().get(0).getData().toString();
    }

    /**
     * 获取ddcURI
     *
     * @return DDC资源标识符
     * @throws Exception Exception
     */
    public String ddcURI(BigInteger ddcId) throws Exception {

        if (null == ddcId || ddcId.compareTo(new BigInteger("0")) <= 0) {
            throw new DDCException(ErrorMessage.DDCID_IS_WRONG);
        }
        ArrayList<Object> arrayList = new ArrayList<>();
        arrayList.add(ddcId);

        ReqJsonRpcBean reqJsonRpcBean = assembleDDC721CallTransaction(DDC721Functions.DDC_URI, arrayList);
        RespJsonRpcBean respJsonRpcBean = restTemplateUtil.sendPost(ConfigCache.get().getOpbGatewayAddress(), reqJsonRpcBean, RespJsonRpcBean.class);
        resultCheck(respJsonRpcBean);

        String encodeParams = new ObjectMapper().writeValueAsString(reqJsonRpcBean.getParams().get(1));
        ReqCallRpcBean reqCallRpcBean = JSONObject.parseObject(encodeParams, ReqCallRpcBean.class);
        String encodedFunction = reqCallRpcBean.getData();
        String jsonResult = new ObjectMapper().writeValueAsString(respJsonRpcBean.getResult());
        RespCallRpcBean respCallRpcBean = JSONObject.parseObject(jsonResult, RespCallRpcBean.class);
        callResultCheck(respCallRpcBean);

        InputAndOutputResult inputAndOutputResult = AnalyzeChainInfoUtils.analyzeTransactionOutput(ConfigCache.get().getDdc721ABI(), ConfigCache.get().getDdc721BIN(), encodedFunction, respCallRpcBean.getOutput());
        return inputAndOutputResult.getResult().get(0).getData().toString();
    }


    /**
     * 设置URI DDC拥有者和授权者可调用该方法
     * @param sender
     * @param ddcId
     * @param ddcURI
     * @return
     * @throws Exception
     */
    public String setURI(String sender, BigInteger ddcId, String ddcURI) throws Exception {
        if (null == ddcId || ddcId.compareTo(new BigInteger("0")) <= 0) {
            throw new DDCException(ErrorMessage.DDCID_IS_WRONG);
        }

        if (Strings.isEmpty(ddcURI)) {
            throw new DDCException(ErrorMessage.DDCURI_IS_EMPTY);
        }

        ArrayList<Object> arrayList = new ArrayList<>();
        arrayList.add(ddcId);
        arrayList.add(ddcURI);

        ReqJsonRpcBean reqJsonRpcBean = assembleDDC721Transaction(sender,DDC721Functions.SET_URI, arrayList);
        RespJsonRpcBean respJsonRpcBean = restTemplateUtil.sendPost(ConfigCache.get().getOpbGatewayAddress(), reqJsonRpcBean, RespJsonRpcBean.class);
        resultCheck(respJsonRpcBean);

        return (String) respJsonRpcBean.getResult();
    }

    private ReqJsonRpcBean assembleDDC721Transaction(String sender,String functionName, ArrayList<Object> params) throws Exception {
        return assembleTransaction(sender,getBlockNumber(), ConfigCache.get().getDdc721ABI(), ConfigCache.get().getDdc721Address(), functionName, params);
    }

    private ReqJsonRpcBean assembleDDC721CallTransaction(String functionName, ArrayList<Object> params) throws Exception {
        return assembleTransaction(OneAddress, new BigInteger("0"), ConfigCache.get().getDdc721ABI(), ConfigCache.get().getDdc721Address(), functionName, params);
    }

}
