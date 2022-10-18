package com.reddate.taianddc.service;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.reddate.taianddc.config.ConfigCache;
import com.reddate.taianddc.constant.DDC1155Functions;
import com.reddate.taianddc.constant.DDC721Functions;
import com.reddate.taianddc.constant.ErrorMessage;
import com.reddate.taianddc.dto.taianchain.*;
import com.reddate.taianddc.exception.DDCException;
import com.reddate.taianddc.listener.SignEventListener;
import com.reddate.taianddc.util.AnalyzeChainInfoUtils;

import org.fisco.bcos.web3j.abi.TypeReference;
import org.fisco.bcos.web3j.abi.datatypes.*;
import org.fisco.bcos.web3j.abi.datatypes.generated.Uint256;
import org.fisco.bcos.web3j.tx.txdecode.InputAndOutputResult;
import org.fisco.bcos.web3j.utils.Strings;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

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
    public String mint(String sender, String to, String ddcURI) throws Exception {
        checkSenderAddress(sender);
        checkToAddress(to);

        ArrayList<Object> arrayList = new ArrayList<>();
        arrayList.add(to);
        arrayList.add((ddcURI == null || ddcURI.isEmpty())? "": ddcURI);

        ReqJsonRpcBean reqJsonRpcBean = assembleDDC721Transaction(sender, DDC721Functions.MINT, arrayList);
        RespJsonRpcBean respJsonRpcBean = restTemplateUtil.sendPost(ConfigCache.get().getOpbGatewayAddress(), reqJsonRpcBean, RespJsonRpcBean.class);

        resultCheck(respJsonRpcBean);
        return (String) respJsonRpcBean.getResult();
    }

    /**
     * 批量生成DDC
     *
     * @param sender  调用者地址
     * @param to      接收者账户
     * @param ddcURIs DDC资源标识符集合
     * @return 交易哈希
     * @throws Exception Exception
     */
    public String mintBatch(String sender, String to, List<String> ddcURIs) throws Exception {
        checkSenderAddress(sender);
        checkToAddress(to);

        if (ddcURIs.size() == 0) {
            throw new DDCException(ErrorMessage.DDCURI_IS_EMPTY);
        }

        ArrayList<Object> arrayList = new ArrayList<>();
        arrayList.add(to);
        arrayList.add(ddcURIs.stream().map(String::valueOf).collect(Collectors.joining(",")));

        ReqJsonRpcBean reqJsonRpcBean = assembleDDC721Transaction(sender, DDC721Functions.MINT_BATCH, arrayList);
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
     * @param data   附加数据
     * @return 交易哈希
     * @throws Exception Exception
     */
    public String safeMint(String sender, String to, String ddcURI, byte[] data) throws Exception {
        checkSenderAddress(sender);
        checkToAddress(to);

        Function function = new Function(
                DDC721Functions.SAFE_MINT,
                Arrays.asList(
                        new Address(to),
                        new Utf8String(ddcURI),
                        new DynamicBytes(data)
                ),
                Collections.<TypeReference<?>>emptyList()
        );

        ReqJsonRpcBean reqJsonRpcBean = assembleDDC721TransactionByTransaction(sender, function);
        RespJsonRpcBean respJsonRpcBean = restTemplateUtil.sendPost(ConfigCache.get().getOpbGatewayAddress(), reqJsonRpcBean, RespJsonRpcBean.class);

        resultCheck(respJsonRpcBean);
        return (String) respJsonRpcBean.getResult();
    }

    /**
     * DDC的批量安全生成
     *
     * @param sender  调用者地址
     * @param to      接收者账户
     * @param ddcURIs DDC资源标识符集合
     * @param data    附加数据
     * @return 交易哈希
     * @throws Exception Exception
     */
    public String safeMintBatch(String sender, String to, List<String> ddcURIs, byte[] data) throws Exception {
        checkSenderAddress(sender);
        checkToAddress(to);

        if (ddcURIs.size() == 0) {
            throw new DDCException(ErrorMessage.DDCURI_IS_EMPTY);
        }

        List<Utf8String> ddcURIUtf8StringList = ddcURIs.stream().map(Utf8String::new).collect(Collectors.toList());
        Function function = new Function(
                DDC721Functions.SAFE_MINT_BATCH,
                Arrays.asList(
                        new Address(to),
                        new DynamicArray<>(ddcURIUtf8StringList),
                        new DynamicBytes(data)
                ),
                Collections.<TypeReference<?>>emptyList()
        );

        ReqJsonRpcBean reqJsonRpcBean = assembleDDC721TransactionByTransaction(sender,function);
        RespJsonRpcBean respJsonRpcBean = restTemplateUtil.sendPost(ConfigCache.get().getOpbGatewayAddress(), reqJsonRpcBean, RespJsonRpcBean.class);

        resultCheck(respJsonRpcBean);
        return (String) respJsonRpcBean.getResult();
    }

    /**
     * 授权DDC
     *
     * @param sender 调用者地址
     * @param to     授权者账户
     * @param ddcId  DDC唯一标识
     * @return 交易哈希
     * @throws Exception Exception
     */
    public String approve(String sender, String to, BigInteger ddcId) throws Exception {
        checkSenderAddress(sender);
        checkToAddress(to);

       checkDDCID(ddcId);

        ArrayList<Object> arrayList = new ArrayList<>();
        arrayList.add(to);
        arrayList.add(ddcId);

        ReqJsonRpcBean reqJsonRpcBean = assembleDDC721Transaction(sender, DDC721Functions.APPROVE, arrayList);
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
        checkDDCID(ddcId);

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
     * @param sender   调用者地址
     * @param operator 授权者账户
     * @param approved 授权标识
     * @return 交易hash
     * @throws Exception Exception
     */
    public String setApprovalForAll(String sender, String operator, Boolean approved) throws Exception {
        checkSenderAddress(sender);

        checkAccountAddress(operator);
        if (null == approved) {
            throw new DDCException(ErrorMessage.ACCOUNT_IS_NOT_ADDRESS_FORMAT);
        }
        ArrayList<Object> arrayList = new ArrayList<>();
        arrayList.add(operator);
        arrayList.add(approved);

        ReqJsonRpcBean reqJsonRpcBean = assembleDDC721Transaction(sender, DDC721Functions.SET_APPROVAL_FOR_ALL, arrayList);
        RespJsonRpcBean respJsonRpcBean = restTemplateUtil.sendPost(ConfigCache.get().getOpbGatewayAddress(), reqJsonRpcBean, RespJsonRpcBean.class);
        resultCheck(respJsonRpcBean);
        return (String) respJsonRpcBean.getResult();
    }

    /**
     * 账户授权查询
     *
     * @param owner    拥有者账户
     * @param operator 授权者账户
     * @return 授权标识
     * @throws Exception Exception
     */
    public Boolean isApprovedForAll(String owner, String operator) throws Exception {
        checkAccountAddress(owner);
        checkAccountAddress(operator);

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
     * @param from   拥有者账户
     * @param to     授权者账户
     * @param ddcId  DDC唯一标识
     * @param data   附加数据
     * @return 交易hash
     * @throws Exception Exception
     */
    public String safeTransferFrom(String sender, String from, String to, BigInteger ddcId, byte[] data) throws Exception {
        checkSenderAddress(sender);
        checkFromAddress(from);
        checkToAddress(to);
        checkDDCID(ddcId);

        Function function = new Function(
                DDC721Functions.SAFE_TRANSFER_FROM,
                Arrays.asList(
                        new Address(from),
                        new Address(to),
                        new Uint256(ddcId),
                        new DynamicBytes(data)
                ),
                Collections.<TypeReference<?>>emptyList()
        );

        ReqJsonRpcBean reqJsonRpcBean = assembleDDC721TransactionByTransaction(sender,function);
        RespJsonRpcBean respJsonRpcBean = restTemplateUtil.sendPost(ConfigCache.get().getOpbGatewayAddress(), reqJsonRpcBean, RespJsonRpcBean.class);
        resultCheck(respJsonRpcBean);
        return (String) respJsonRpcBean.getResult();
    }

    /**
     * 转移
     *
     * @param sender 调用者地址
     * @param from   拥有者账户
     * @param to     接收者账户
     * @param ddcId  ddc唯一标识
     * @return 交易hash
     * @throws Exception Exception
     */
    public String transferFrom(String sender, String from, String to, BigInteger ddcId) throws Exception {
        checkSenderAddress(sender);
        checkFromAddress(from);
        checkToAddress(to);
        checkDDCID(ddcId);

        ArrayList<Object> arrayList = new ArrayList<>();
        arrayList.add(from);
        arrayList.add(to);
        arrayList.add(ddcId);

        ReqJsonRpcBean reqJsonRpcBean = assembleDDC721Transaction(sender, DDC721Functions.TRANSFER_FROM, arrayList);
        RespJsonRpcBean respJsonRpcBean = restTemplateUtil.sendPost(ConfigCache.get().getOpbGatewayAddress(), reqJsonRpcBean, RespJsonRpcBean.class);
        resultCheck(respJsonRpcBean);
        return (String) respJsonRpcBean.getResult();
    }

    /**
     * 冻结
     *
     * @param sender 调用者地址
     * @param ddcId  DDC唯一标识
     * @return 交易hash
     * @throws Exception Exception
     */
    public String freeze(String sender, BigInteger ddcId) throws Exception {
        checkSenderAddress(sender);
        checkDDCID(ddcId);

        ArrayList<Object> arrayList = new ArrayList<>();
        arrayList.add(ddcId);

        ReqJsonRpcBean reqJsonRpcBean = assembleDDC721Transaction(sender, DDC721Functions.FREEZE, arrayList);
        RespJsonRpcBean respJsonRpcBean = restTemplateUtil.sendPost(ConfigCache.get().getOpbGatewayAddress(), reqJsonRpcBean, RespJsonRpcBean.class);
        resultCheck(respJsonRpcBean);
        return (String) respJsonRpcBean.getResult();
    }

    /**
     * 解冻
     *
     * @param sender 调用者地址
     * @param ddcId  DDC唯一标识
     * @return 交易hash
     * @throws Exception Exception
     */
    public String unFreeze(String sender, BigInteger ddcId) throws Exception {
        checkSenderAddress(sender);
        checkDDCID(ddcId);

        ArrayList<Object> arrayList = new ArrayList<>();
        arrayList.add(ddcId);

        ReqJsonRpcBean reqJsonRpcBean = assembleDDC721Transaction(sender, DDC721Functions.UNFREEZE, arrayList);
        RespJsonRpcBean respJsonRpcBean = restTemplateUtil.sendPost(ConfigCache.get().getOpbGatewayAddress(), reqJsonRpcBean, RespJsonRpcBean.class);
        resultCheck(respJsonRpcBean);
        return (String) respJsonRpcBean.getResult();
    }

    /**
     * 销毁
     *
     * @param sender 调用者地址
     * @param ddcId  DDC唯一标识
     * @return 交易hash
     * @throws Exception Exception
     */
    public String burn(String sender, BigInteger ddcId) throws Exception {
        checkSenderAddress(sender);
        checkDDCID(ddcId);

        ArrayList<Object> arrayList = new ArrayList<>();
        arrayList.add(ddcId);

        ReqJsonRpcBean reqJsonRpcBean = assembleDDC721Transaction(sender, DDC721Functions.BURN, arrayList);
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
        checkAccountAddress(owner);

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
        checkDDCID(ddcId);

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
     * 查询当前DDC的名称
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
     * 设置名称和符号
     *
     * @param sender 调用者
     * @param name 名称
     * @param symbol 符号
     * @return 交易哈希
     * @throws Exception Exception
     */
    public String setNameAndSymbol(String sender, String name, String symbol) throws Exception {
        checkSenderAddress(sender);

        if (Strings.isEmpty(name)) {
            throw new DDCException(ErrorMessage.NAME_IS_EMPTY);
        }
        if (Strings.isEmpty(symbol)) {
            throw new DDCException(ErrorMessage.SYMBOL_IS_EMPTY);
        }

        ArrayList<Object> arrayList = new ArrayList<>();
        arrayList.add(name);
        arrayList.add(symbol);

        ReqJsonRpcBean reqJsonRpcBean = assembleDDC721Transaction(sender, DDC721Functions.SET_NAME_AND_SYMBOL, arrayList);
        RespJsonRpcBean respJsonRpcBean = restTemplateUtil.sendPost(ConfigCache.get().getOpbGatewayAddress(), reqJsonRpcBean, RespJsonRpcBean.class);
        resultCheck(respJsonRpcBean);
        return (String) respJsonRpcBean.getResult();
    }

    /**
     * 获取ddcURI
     *
     * @return DDC资源标识符
     * @throws Exception Exception
     */
    public String ddcURI(BigInteger ddcId) throws Exception {
        checkDDCID(ddcId);

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
     *
     * @param sender 调用者
     * @param ddcId DDC唯一标识
     * @param ddcURI DDC资源标识符
     * @return 交易哈希
     * @throws Exception Exception
     */
    public String setURI(String sender, BigInteger ddcId, String ddcURI) throws Exception {
        checkDDCID(ddcId);

        if (Strings.isEmpty(ddcURI)) {
            throw new DDCException(ErrorMessage.DDCURI_IS_EMPTY);
        }

        ArrayList<Object> arrayList = new ArrayList<>();
        arrayList.add(ddcId);
        arrayList.add(ddcURI);

        ReqJsonRpcBean reqJsonRpcBean = assembleDDC721Transaction(sender, DDC721Functions.SET_URI, arrayList);
        RespJsonRpcBean respJsonRpcBean = restTemplateUtil.sendPost(ConfigCache.get().getOpbGatewayAddress(), reqJsonRpcBean, RespJsonRpcBean.class);
        resultCheck(respJsonRpcBean);

        return (String) respJsonRpcBean.getResult();
    }

    /**
     * 最新DDCID查询
     *
     * @return 最新DDCID
     * @throws Exception Exception
     */
    public BigInteger getLatestDDCId() throws Exception {
        ArrayList<Object> arrayList = new ArrayList<>();

        ReqJsonRpcBean reqJsonRpcBean = assembleDDC721CallTransaction(DDC721Functions.GET_LATEST_DDCID, arrayList);
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
     * Nonce查询 通过调用该方法对签名者账户所对应的最新nonce值进行查询，注：此查询只适用于发起元交易处理业务所对应的nonce值查询
     * @param from DDC拥有者
     * @throws Exception Exception
     * @return 最新Nonce值
     */
    public BigInteger getNonce(String from) throws Exception {
        ArrayList<Object> arrayList = new ArrayList<>();
        arrayList.add(from);
        ReqJsonRpcBean reqJsonRpcBean = assembleDDC721CallTransaction(DDC721Functions.GET_NONCE, arrayList);
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
     * 元交易生成
     *
     * @param sender  调用者地址
     * @param to      接收者账户
     * @param ddcURI DDC资源标识符
     * @param nonce    nonce值
     * @param deadline    过期时间
     * @param sign    签名值
     * @return 交易哈希
     * @throws Exception Exception
     */
    public String metaMint(String sender,String to,String ddcURI, BigInteger nonce,BigInteger deadline,byte[] sign) throws Exception {
        checkSenderAddress(sender);
        checkToAddress(to);
        checkDeadline(deadline);
        checkMetaSign(sign);

        Function function = new Function(
                DDC721Functions.META_MINT,
                Arrays.asList(
                        new Address(to),
                        new Utf8String(ddcURI),
                        new Uint256(nonce),
                        new Uint256(deadline),
                        new DynamicBytes(sign)
                ),
                Collections.<TypeReference<?>>emptyList()
        );

        ReqJsonRpcBean reqJsonRpcBean = assembleDDC721TransactionByTransaction(sender, function);
        RespJsonRpcBean respJsonRpcBean = restTemplateUtil.sendPost(ConfigCache.get().getOpbGatewayAddress(), reqJsonRpcBean, RespJsonRpcBean.class);

        resultCheck(respJsonRpcBean);
        return (String) respJsonRpcBean.getResult();
    }

    /**
     * 元交易安全生成
     *
     * @param sender  调用者地址
     * @param to      接收者账户
     * @param ddcURI DDC资源标识符
     * @param data 附加数据
     * @param nonce    nonce值
     * @param deadline    过期时间
     * @param sign    签名值
     * @return 交易哈希
     * @throws Exception Exception
     */
    public String metaSafeMint(String sender,String to, String ddcURI, byte[] data,BigInteger nonce,BigInteger deadline,byte[] sign) throws Exception {
        checkSenderAddress(sender);
        checkToAddress(to);
        checkDeadline(deadline);
        checkMetaSign(sign);

        Function function = new Function(
                DDC721Functions.META_SAFE_MINT,
                Arrays.asList(
                        new Address(to),
                        new Utf8String(ddcURI),
                        new DynamicBytes(data),
                        new Uint256(nonce),
                        new Uint256(deadline),
                        new DynamicBytes(sign)
                ),
                Collections.<TypeReference<?>>emptyList()
        );

        ReqJsonRpcBean reqJsonRpcBean = assembleDDC721TransactionByTransaction(sender, function);
        RespJsonRpcBean respJsonRpcBean = restTemplateUtil.sendPost(ConfigCache.get().getOpbGatewayAddress(), reqJsonRpcBean, RespJsonRpcBean.class);

        resultCheck(respJsonRpcBean);
        return (String) respJsonRpcBean.getResult();
    }

    /**
     * 元交易批量生成
     *
     * @param sender  调用者地址
     * @param to      接收者账户
     * @param ddcURIs DDC资源标识符集合
     * @param nonce    nonce值
     * @param deadline    过期时间
     * @param sign    签名值
     * @return 交易哈希
     * @throws Exception Exception
     */
    public String metaMintBatch(String sender,String to,List<String> ddcURIs,BigInteger nonce,BigInteger deadline,byte[] sign) throws Exception {
        checkSenderAddress(sender);
        checkToAddress(to);
        if (ddcURIs.size() == 0) {
            throw new DDCException(ErrorMessage.DDCURI_IS_EMPTY);
        }
        checkDeadline(deadline);
        checkMetaSign(sign);

        List<Utf8String> ddcURIUtf8StringList = ddcURIs.stream().map(Utf8String::new).collect(Collectors.toList());

        Function function = new Function(
                DDC721Functions.META_MINT_BATCH,
                Arrays.asList(
                        new Address(to),
                        new DynamicArray<>(ddcURIUtf8StringList),
                        new Uint256(nonce),
                        new Uint256(deadline),
                        new DynamicBytes(sign)
                ),
                Collections.<TypeReference<?>>emptyList()
        );

        ReqJsonRpcBean reqJsonRpcBean = assembleDDC721TransactionByTransaction(sender, function);
        RespJsonRpcBean respJsonRpcBean = restTemplateUtil.sendPost(ConfigCache.get().getOpbGatewayAddress(), reqJsonRpcBean, RespJsonRpcBean.class);

        resultCheck(respJsonRpcBean);
        return (String) respJsonRpcBean.getResult();
    }

    /**
     * 元交易批量安全生成
     *
     * @param sender  调用者地址
     * @param to      接收者账户
     * @param ddcURIs DDC资源标识符集合
     * @param data    附加数据
     * @param nonce    nonce值
     * @param deadline    过期时间
     * @param sign    签名值
     * @return 交易哈希
     * @throws Exception Exception
     */
    public String metaSafeMintBatch(String sender,String to,List<String> ddcURIs,byte[] data,BigInteger nonce,BigInteger deadline,byte[] sign) throws Exception {
        checkSenderAddress(sender);
        checkToAddress(to);
        if (ddcURIs.size() == 0) {
            throw new DDCException(ErrorMessage.DDCURI_IS_EMPTY);
        }
        checkDeadline(deadline);
        checkMetaSign(sign);

        List<Utf8String> ddcURIUtf8StringList = ddcURIs.stream().map(Utf8String::new).collect(Collectors.toList());

        Function function = new Function(
                DDC721Functions.META_SAFE_MINT_BATCH,
                Arrays.asList(
                        new Address(to),
                        new DynamicArray<>(ddcURIUtf8StringList),
                        new DynamicBytes(data),
                        new Uint256(nonce),
                        new Uint256(deadline),
                        new DynamicBytes(sign)
                ),
                Collections.<TypeReference<?>>emptyList()
        );

        ReqJsonRpcBean reqJsonRpcBean = assembleDDC721TransactionByTransaction(sender, function);
        RespJsonRpcBean respJsonRpcBean = restTemplateUtil.sendPost(ConfigCache.get().getOpbGatewayAddress(), reqJsonRpcBean, RespJsonRpcBean.class);

        resultCheck(respJsonRpcBean);
        return (String) respJsonRpcBean.getResult();
    }

    /**
     * 元交易转移 DDC拥有者或DDC授权者通过授权平台方调用该方法对DDC进行元交易转移
     * @param sender 调用者
     * @param from 拥有者账户
     * @param to 接收者账户
     * @param ddcId DDC唯一标识
     * @param nonce nonce值
     * @param deadline 过期时间
     * @param sign 签名值
     * @throws Exception Exception
     * @return 交易哈希
     */
    public String metaTransferFrom(String sender,String from,String to,BigInteger ddcId,BigInteger nonce,BigInteger deadline,byte[] sign) throws Exception {
        checkSenderAddress(sender);
        checkFromAddress(from);
        checkToAddress(to);
        checkDDCID(ddcId);
        checkDeadline(deadline);
        checkMetaSign(sign);

        Function function = new Function(
                DDC721Functions.META_TRANSFER_FROM,
                Arrays.asList(
                        new Address(from),
                        new Address(to),
                        new Uint256(ddcId),
                        new Uint256(nonce),
                        new Uint256(deadline),
                        new DynamicBytes(sign)
                ),
                Collections.<TypeReference<?>>emptyList()
        );

        ReqJsonRpcBean reqJsonRpcBean = assembleDDC721TransactionByTransaction(sender, function);
        RespJsonRpcBean respJsonRpcBean = restTemplateUtil.sendPost(ConfigCache.get().getOpbGatewayAddress(), reqJsonRpcBean, RespJsonRpcBean.class);

        resultCheck(respJsonRpcBean);
        return (String) respJsonRpcBean.getResult();
    }

    /**
     * 元交易安全转移 DDC拥有者或DDC授权者通过授权平台方调用该方法对DDC进行元交易安全转移
     * @param sender 调用者
     * @param from 拥有者账户
     * @param to 接收者账户
     * @param ddcId DDC唯一标识
     * @param data 附加数据
     * @param nonce nonce值
     * @param deadline 过期时间
     * @param sign 签名值
     * @throws Exception Exception
     * @return 交易哈希
     */
    public String metaSafeTransferFrom(String sender,String from,String to,BigInteger ddcId,byte[] data,BigInteger nonce,BigInteger deadline,byte[] sign) throws Exception {
        checkSenderAddress(sender);
        checkFromAddress(from);
        checkToAddress(to);
        checkDDCID(ddcId);
        checkDeadline(deadline);
        checkMetaSign(sign);

        Function function = new Function(
                DDC721Functions.META_SAFE_TRANSFER_FROM,
                Arrays.asList(
                        new Address(from),
                        new Address(to),
                        new Uint256(ddcId),
                        new DynamicBytes(data),
                        new Uint256(nonce),
                        new Uint256(deadline),
                        new DynamicBytes(sign)
                ),
                Collections.<TypeReference<?>>emptyList()
        );

        ReqJsonRpcBean reqJsonRpcBean = assembleDDC721TransactionByTransaction(sender, function);
        RespJsonRpcBean respJsonRpcBean = restTemplateUtil.sendPost(ConfigCache.get().getOpbGatewayAddress(), reqJsonRpcBean, RespJsonRpcBean.class);

        resultCheck(respJsonRpcBean);
        return (String) respJsonRpcBean.getResult();
    }

    /**
     * 元交易销毁 DDC拥有者或DDC授权者可以通过授权平台内用户调用该方法对DDC进行元交易销毁
     * @param sender 调用者
     * @param ddcId DDC唯一标识
     * @param nonce nonce值
     * @param deadline 过期时间
     * @param sign 签名值
     * @throws Exception Exception
     * @return 交易哈希
     */
    public String metaBurn(String sender,BigInteger ddcId,BigInteger nonce,BigInteger deadline,byte[] sign) throws Exception {
        checkSenderAddress(sender);
        checkDDCID(ddcId);
        checkDeadline(deadline);
        checkMetaSign(sign);

        Function function = new Function(
                DDC721Functions.META_BURN,
                Arrays.asList(
                        new Uint256(ddcId),
                        new Uint256(nonce),
                        new Uint256(deadline),
                        new DynamicBytes(sign)
                ),
                Collections.<TypeReference<?>>emptyList()
        );

        ReqJsonRpcBean reqJsonRpcBean = assembleDDC721TransactionByTransaction(sender, function);
        RespJsonRpcBean respJsonRpcBean = restTemplateUtil.sendPost(ConfigCache.get().getOpbGatewayAddress(), reqJsonRpcBean, RespJsonRpcBean.class);

        resultCheck(respJsonRpcBean);
        return (String) respJsonRpcBean.getResult();
    }

    private ReqJsonRpcBean assembleDDC721Transaction(String sender, String functionName, ArrayList<Object> params) throws Exception {
        return assembleTransaction(sender, getBlockNumber(), ConfigCache.get().getDdc721ABI(), ConfigCache.get().getDdc721Address(), functionName, params);
    }

    private ReqJsonRpcBean assembleDDC721CallTransaction(String functionName, ArrayList<Object> params) throws Exception {
        return assembleTransaction(OneAddress, new BigInteger("0"), ConfigCache.get().getDdc721ABI(), ConfigCache.get().getDdc721Address(), functionName, params);
    }

    private ReqJsonRpcBean assembleDDC721TransactionByTransaction(String sender, Function function) throws Exception {
        return assembleTransactionByFunction(sender, getBlockNumber(), ConfigCache.get().getDdc721ABI(), ConfigCache.get().getDdc721Address(), function);
    }

}
