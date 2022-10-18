package com.reddate.taianddc.service;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Multimap;
import com.reddate.taianddc.config.ConfigCache;
import com.reddate.taianddc.constant.DDC1155Functions;
import com.reddate.taianddc.constant.ErrorMessage;
import com.reddate.taianddc.dto.taianchain.ReqCallRpcBean;
import com.reddate.taianddc.dto.taianchain.ReqJsonRpcBean;
import com.reddate.taianddc.dto.taianchain.RespCallRpcBean;
import com.reddate.taianddc.dto.taianchain.RespJsonRpcBean;
import com.reddate.taianddc.exception.DDCException;
import com.reddate.taianddc.listener.SignEventListener;
import com.reddate.taianddc.util.AnalyzeChainInfoUtils;
import lombok.extern.slf4j.Slf4j;
import org.fisco.bcos.web3j.abi.TypeReference;
import org.fisco.bcos.web3j.abi.datatypes.*;
import org.fisco.bcos.web3j.abi.datatypes.generated.Uint256;
import org.fisco.bcos.web3j.tx.txdecode.InputAndOutputResult;
import org.fisco.bcos.web3j.utils.Strings;

import java.math.BigInteger;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
public class DDC1155Service extends BaseService {

    public DDC1155Service(SignEventListener signEventListener) {
        super.signEventListener = signEventListener;
    }

    /**
     * DDC的创建
     *
     * @param sender 调用者地址
     * @param to     接收者账户
     * @param amount DDC数量
     * @param ddcURI DDCURI
     * @param data   附加数据
     * @return 交易哈希
     * @throws Exception Exception
     */
    public String safeMint(String sender, String to, BigInteger amount, String ddcURI, byte[] data) throws Exception {
        checkSenderAddress(sender);

        //验证account为标准address格式
        checkAccountAddress(to);

        //3.检查需要生成的DDC数量是否大于0
        checkDDCAmount(amount);

        Function function = new Function(
                DDC1155Functions.SAFE_MINT,
                Arrays.asList(
                        new Address(to),
                        new Uint256(amount),
                        new Utf8String(ddcURI),
                        new DynamicBytes(data)
                ),
                Collections.<TypeReference<?>>emptyList()
        );

        ReqJsonRpcBean reqJsonRpcBean = assembleDDC1155TransactionByTransaction(sender,function);
        RespJsonRpcBean respJsonRpcBean = restTemplateUtil.sendPost(ConfigCache.get().getOpbGatewayAddress(), reqJsonRpcBean, RespJsonRpcBean.class);
        resultCheck(respJsonRpcBean);
        return (String) respJsonRpcBean.getResult();
    }

    /**
     * DDC的批量创建
     *
     * @param sender  调用者地址
     * @param to      接收者账户
     * @param ddcs DDC信息
     * @param data    附加数据
     * @return 交易哈希
     * @throws Exception Exception
     */
    public String safeMintBatch(String sender, String to, Multimap<BigInteger, String> ddcs, byte[] data) throws Exception {
        checkSenderAddress(sender);

        //验证account为标准address格式
        checkAccountAddress(to);
        List<BigInteger> amounts = new ArrayList<>();
        List<String> ddcURIs = new ArrayList<>();

        ddcs.forEach((key, value) -> {
            //验证accName不为空
            if (null == key || BigInteger.valueOf(0).compareTo(key) == 0) {
                throw new DDCException(ErrorMessage.AMOUNT_IS_EMPTY);
            }

            amounts.add(key);
            ddcURIs.add(value);
        });

        List<Uint256> amountUint256List = amounts.stream().map(Uint256::new).collect(Collectors.toList());
        List<Utf8String> ddcURIUtf8StringList = ddcURIs.stream().map(Utf8String::new).collect(Collectors.toList());
        Function function = new Function(
                DDC1155Functions.SAFE_MINT_BATCH,
                Arrays.asList(
                        new Address(to),
                        new DynamicArray<>(amountUint256List),
                        new DynamicArray<>(ddcURIUtf8StringList),
                        new DynamicBytes(data)
                ),
                Collections.<TypeReference<?>>emptyList()
        );

        ReqJsonRpcBean reqJsonRpcBean = assembleDDC1155TransactionByTransaction(sender,function);
        RespJsonRpcBean respJsonRpcBean = restTemplateUtil.sendPost(ConfigCache.get().getOpbGatewayAddress(), reqJsonRpcBean, RespJsonRpcBean.class);
        resultCheck(respJsonRpcBean);
        return (String) respJsonRpcBean.getResult();
    }

    /**
     * 账户授权
     *
     * @param sender   调用者地址
     * @param operator 授权者账户
     * @param approved 授权标识
     * @return 交易哈希
     * @throws Exception Exception
     */
    public String setApprovalForAll(String sender, String operator, Boolean approved) throws Exception {
        checkSenderAddress(sender);

        //验证account为标准address格式
        checkAccountAddress(operator);

        ArrayList<Object> arrayList = new ArrayList<>();
        arrayList.add(operator);
        arrayList.add(approved);

        ReqJsonRpcBean reqJsonRpcBean = assembleDDC1155Transaction(sender, DDC1155Functions.SET_APPROVAL_FOR_ALL, arrayList);
        RespJsonRpcBean respJsonRpcBean = restTemplateUtil.sendPost(ConfigCache.get().getOpbGatewayAddress(), reqJsonRpcBean, RespJsonRpcBean.class);
        resultCheck(respJsonRpcBean);
        return (String) respJsonRpcBean.getResult();
    }

    /**
     * 账户授权查询
     *
     * @param owner    拥有者账户
     * @param operator 授权者账户
     * @return 授权结果
     * @throws Exception Exception
     */
    public String isApprovedForAll(String owner, String operator) throws Exception {
        checkAccountAddress(owner);
        checkAccountAddress(operator);

        ArrayList<Object> arrayList = new ArrayList<>();
        arrayList.add(owner);
        arrayList.add(operator);

        ReqJsonRpcBean reqJsonRpcBean = assembleDDC1155CallTransaction(DDC1155Functions.IS_APPROVED_FOR_ALL, arrayList);
        RespJsonRpcBean respJsonRpcBean = restTemplateUtil.sendPost(ConfigCache.get().getOpbGatewayAddress(), reqJsonRpcBean, RespJsonRpcBean.class);
        resultCheck(respJsonRpcBean);

        String jsonResult = new ObjectMapper().writeValueAsString(respJsonRpcBean.getResult());
        RespCallRpcBean respCallRpcBean = JSONObject.parseObject(jsonResult, RespCallRpcBean.class);
        callResultCheck(respCallRpcBean);

        return respCallRpcBean.getOutput();
    }

    /**
     * DDC的转移
     *
     * @param sender 调用者地址
     * @param from   拥有者账户
     * @param to     接收者账户
     * @param ddcId  DDCID
     * @param amount 数量
     * @param data   附加数据
     * @return 转移结果
     * @throws Exception Exception
     */
    public String safeTransferFrom(String sender, String from, String to, BigInteger ddcId, BigInteger amount, byte[] data) throws Exception {
        checkSenderAddress(sender);
        checkFromAddress(from);
        checkToAddress(to);
        checkDDCID(ddcId);

        Function function = new Function(
                DDC1155Functions.SAFE_TRANSFER_FROM,
                Arrays.asList(
                        new Address(from),
                        new Address(to),
                        new Uint256(ddcId),
                        new Uint256(amount),
                        new DynamicBytes(data)
                ),
                Collections.<TypeReference<?>>emptyList()
        );

        ReqJsonRpcBean reqJsonRpcBean = assembleDDC1155TransactionByTransaction(sender,function);
        RespJsonRpcBean respJsonRpcBean = restTemplateUtil.sendPost(ConfigCache.get().getOpbGatewayAddress(), reqJsonRpcBean, RespJsonRpcBean.class);
        resultCheck(respJsonRpcBean);
        return (String) respJsonRpcBean.getResult();
    }

    /**
     * DDC的批量转移
     *
     * @param sender 调用者地址
     * @param from   拥有者账户
     * @param to     接收者账户
     * @param ddcs   拥有者DDCID集合
     * @param data   附加数据
     * @return 交易哈希
     * @throws Exception Exception
     */
    public String safeBatchTransferFrom(String sender, String from, String to, Map<BigInteger, BigInteger> ddcs, byte[] data) throws Exception {
        checkSenderAddress(sender);
        checkFromAddress(from);
        checkToAddress(to);

        if (null == ddcs) {
            throw new DDCException(ErrorMessage.DDCID_IS_WRONG);
        }

        ArrayList<BigInteger> ddcIds = new ArrayList<>();
        ArrayList<BigInteger> amounts = new ArrayList<>();

        ddcs.forEach((key, value) -> {
            //验证accName不为空
            if (null == key || BigInteger.valueOf(0).compareTo(value) == 0) {
                throw new DDCException(ErrorMessage.AMOUNT_IS_EMPTY);
            }

            ddcIds.add(key);
            amounts.add(value);
        });

        List<Uint256> ddcIdUint256List = ddcIds.stream().map(Uint256::new).collect(Collectors.toList());
        List<Uint256> amountUint256List = amounts.stream().map(Uint256::new).collect(Collectors.toList());
        Function function = new Function(
                DDC1155Functions.SAFE_BATCH_TRANSFER_FROM,
                Arrays.asList(
                        new Address(from),
                        new Address(to),
                        new DynamicArray<>(ddcIdUint256List),
                        new DynamicArray<>(amountUint256List),
                        new DynamicBytes(data)
                ),
                Collections.<TypeReference<?>>emptyList()
        );

        ReqJsonRpcBean reqJsonRpcBean = assembleDDC1155TransactionByTransaction(sender,function);
        RespJsonRpcBean respJsonRpcBean = restTemplateUtil.sendPost(ConfigCache.get().getOpbGatewayAddress(), reqJsonRpcBean, RespJsonRpcBean.class);
        resultCheck(respJsonRpcBean);
        return (String) respJsonRpcBean.getResult();
    }

    /**
     * DDC的冻结
     *
     * @param sender 调用者地址
     * @param ddcId  DDC唯一标识
     * @return 交易哈希
     * @throws Exception Exception
     */
    public String freeze(String sender, BigInteger ddcId) throws Exception {
        checkSenderAddress(sender);
        checkDDCID(ddcId);

        ArrayList<Object> arrayList = new ArrayList<>();
        arrayList.add(ddcId);

        ReqJsonRpcBean reqJsonRpcBean = assembleDDC1155Transaction(sender, DDC1155Functions.FREEZE, arrayList);
        RespJsonRpcBean respJsonRpcBean = restTemplateUtil.sendPost(ConfigCache.get().getOpbGatewayAddress(), reqJsonRpcBean, RespJsonRpcBean.class);
        resultCheck(respJsonRpcBean);
        return (String) respJsonRpcBean.getResult();
    }

    /**
     * DDC的解冻
     *
     * @param sender 调用者地址
     * @param ddcId  DDC唯一标识
     * @return 交易哈希
     * @throws Exception Exception
     */
    public String unFreeze(String sender, BigInteger ddcId) throws Exception {
        checkSenderAddress(sender);
        checkDDCID(ddcId);

        ArrayList<Object> arrayList = new ArrayList<>();
        arrayList.add(ddcId);

        ReqJsonRpcBean reqJsonRpcBean = assembleDDC1155Transaction(sender, DDC1155Functions.UN_FREEZE, arrayList);
        RespJsonRpcBean respJsonRpcBean = restTemplateUtil.sendPost(ConfigCache.get().getOpbGatewayAddress(), reqJsonRpcBean, RespJsonRpcBean.class);
        resultCheck(respJsonRpcBean);
        return (String) respJsonRpcBean.getResult();
    }

    /**
     * DDC的销毁
     *
     * @param sender 调用者地址
     * @param owner  拥有者账户
     * @param ddcId  DDCID
     * @return 交易哈希
     * @throws Exception Exception
     */
    public String burn(String sender, String owner, BigInteger ddcId) throws Exception {
        checkSenderAddress(sender);
        checkAccountAddress(owner);
        checkDDCID(ddcId);

        ArrayList<Object> arrayList = new ArrayList<>();
        arrayList.add(owner);
        arrayList.add(ddcId);

        ReqJsonRpcBean reqJsonRpcBean = assembleDDC1155Transaction(sender, DDC1155Functions.BURN, arrayList);
        RespJsonRpcBean respJsonRpcBean = restTemplateUtil.sendPost(ConfigCache.get().getOpbGatewayAddress(), reqJsonRpcBean, RespJsonRpcBean.class);
        resultCheck(respJsonRpcBean);
        return (String) respJsonRpcBean.getResult();
    }

    /**
     * DDC的批量销毁
     *
     * @param sender 调用者地址
     * @param owner  拥有者账户
     * @param ddcIds DDCID集合
     * @return 交易哈希
     * @throws Exception Exception
     */
    public String burnBatch(String sender, String owner, List<BigInteger> ddcIds) throws Exception {
        checkSenderAddress(sender);
        checkAccountAddress(owner);

        if (null == ddcIds) {
            throw new DDCException(ErrorMessage.DDCID_IS_WRONG);
        }
        ArrayList<Object> arrayList = new ArrayList<>();
        arrayList.add(owner);
        arrayList.add(ddcIds.stream().map(String::valueOf).collect(Collectors.joining(",")));

        ReqJsonRpcBean reqJsonRpcBean = assembleDDC1155Transaction(sender, DDC1155Functions.BURN_BATCH, arrayList);
        RespJsonRpcBean respJsonRpcBean = restTemplateUtil.sendPost(ConfigCache.get().getOpbGatewayAddress(), reqJsonRpcBean, RespJsonRpcBean.class);
        resultCheck(respJsonRpcBean);
        return (String) respJsonRpcBean.getResult();
    }

    /**
     * 查询当前账户拥有的DDC的数量
     *
     * @param owner 拥有者账户
     * @param ddcId DDCID
     * @return 拥有者账户所对应的DDCID所拥用的数量
     * @throws Exception Exception
     */
    public BigInteger balanceOf(String owner, BigInteger ddcId) throws Exception {
        checkAccountAddress(owner);
        checkDDCID(ddcId);

        ArrayList<Object> arrayList = new ArrayList<>();
        arrayList.add(owner);
        arrayList.add(ddcId);

        ReqJsonRpcBean reqJsonRpcBean = assembleDDC1155CallTransaction(DDC1155Functions.BALANCE_OF, arrayList);
        RespJsonRpcBean respJsonRpcBean = restTemplateUtil.sendPost(ConfigCache.get().getOpbGatewayAddress(), reqJsonRpcBean, RespJsonRpcBean.class);
        resultCheck(respJsonRpcBean);

        String encodeParams = new ObjectMapper().writeValueAsString(reqJsonRpcBean.getParams().get(1));
        ReqCallRpcBean reqCallRpcBean = JSONObject.parseObject(encodeParams, ReqCallRpcBean.class);
        String encodedFunction = reqCallRpcBean.getData();
        String jsonResult = new ObjectMapper().writeValueAsString(respJsonRpcBean.getResult());
        RespCallRpcBean respCallRpcBean = JSONObject.parseObject(jsonResult, RespCallRpcBean.class);
        callResultCheck(respCallRpcBean);

        InputAndOutputResult inputAndOutputResult = AnalyzeChainInfoUtils.analyzeTransactionOutput(ConfigCache.get().getDdc1155ABI(), ConfigCache.get().getDdc1155BIN(), encodedFunction, respCallRpcBean.getOutput());

        return new BigInteger(inputAndOutputResult.getResult().get(0).getData().toString());
    }

    /**
     * 批量查询账户拥有的DDC的数量
     *
     * @param ddcs 拥有者DDCID集合
     * @return 拥有者账户所对应的每个DDCID所拥用的数量
     * @throws Exception Exception
     */
    public List<BigInteger> balanceOfBatch(Multimap<String, BigInteger> ddcs) throws Exception {
        if (null == ddcs || ddcs.size() == 0) {
            throw new DDCException(ErrorMessage.DDCID_IS_WRONG);
        }

        ArrayList<Object> params = new ArrayList<>();

        ArrayList<String> owners = new ArrayList<>();
        ArrayList<String> ddcIds = new ArrayList<>();

        ddcs.forEach((key, value) -> {
            owners.add(key);
            ddcIds.add(String.valueOf(value));
        });
        params.add(String.join(",", owners));
        params.add(String.join(",", ddcIds));

        ReqJsonRpcBean reqJsonRpcBean = assembleDDC1155CallTransaction(DDC1155Functions.BALANCE_OF_BATCH, params);
        RespJsonRpcBean respJsonRpcBean = restTemplateUtil.sendPost(ConfigCache.get().getOpbGatewayAddress(), reqJsonRpcBean, RespJsonRpcBean.class);
        resultCheck(respJsonRpcBean);

        String encodeParams = new ObjectMapper().writeValueAsString(reqJsonRpcBean.getParams().get(1));
        ReqCallRpcBean reqCallRpcBean = JSONObject.parseObject(encodeParams, ReqCallRpcBean.class);
        String encodedFunction = reqCallRpcBean.getData();
        String jsonResult = new ObjectMapper().writeValueAsString(respJsonRpcBean.getResult());
        RespCallRpcBean respCallRpcBean = JSONObject.parseObject(jsonResult, RespCallRpcBean.class);
        callResultCheck(respCallRpcBean);

        InputAndOutputResult inputAndOutputResult = AnalyzeChainInfoUtils.analyzeTransactionOutput(ConfigCache.get().getDdc1155ABI(), ConfigCache.get().getDdc1155BIN(), encodedFunction, respCallRpcBean.getOutput());

        ArrayList<BigInteger> result = new ArrayList<>();
        ArrayList<BigInteger> datas = (ArrayList<BigInteger>) inputAndOutputResult.getResult().get(0).getData();
        datas.forEach(it -> {
            result.add(new BigInteger(it.toString()));
        });
        return result;
    }

    /**
     * 获取ddcURI
     *
     * @param ddcId ddcId
     * @return DDCURI
     * @throws Exception Exception
     */
    public String ddcURI(BigInteger ddcId) throws Exception {
        checkDDCID(ddcId);
        ArrayList<Object> arrayList = new ArrayList<>();
        arrayList.add(ddcId);

        ReqJsonRpcBean reqJsonRpcBean = assembleDDC1155CallTransaction(DDC1155Functions.DDCURI, arrayList);
        RespJsonRpcBean respJsonRpcBean = restTemplateUtil.sendPost(ConfigCache.get().getOpbGatewayAddress(), reqJsonRpcBean, RespJsonRpcBean.class);
        resultCheck(respJsonRpcBean);

        String encodeParams = new ObjectMapper().writeValueAsString(reqJsonRpcBean.getParams().get(1));
        ReqCallRpcBean reqCallRpcBean = JSONObject.parseObject(encodeParams, ReqCallRpcBean.class);
        String encodedFunction = reqCallRpcBean.getData();
        String jsonResult = new ObjectMapper().writeValueAsString(respJsonRpcBean.getResult());
        RespCallRpcBean respCallRpcBean = JSONObject.parseObject(jsonResult, RespCallRpcBean.class);
        callResultCheck(respCallRpcBean);

        InputAndOutputResult inputAndOutputResult = AnalyzeChainInfoUtils.analyzeTransactionOutput(ConfigCache.get().getDdc1155ABI(), ConfigCache.get().getDdc1155BIN(), encodedFunction, respCallRpcBean.getOutput());

        return inputAndOutputResult.getResult().get(0).getData().toString();
    }

    /**
     * 设置URI DDC拥有者和授权者可调用该方法
     *
     * @param sender sender
     * @param owner DDC owner
     * @param ddcId ddcId
     * @param ddcURI ddcURI
     * @return 交易哈希
     * @throws Exception Exception
     */
    public String setURI(String sender, String owner, BigInteger ddcId, String ddcURI) throws Exception {
        checkSenderAddress(sender);
        checkAccountAddress(owner);
        checkDDCID(ddcId);

        if (Strings.isEmpty(ddcURI)) {
            throw new DDCException(ErrorMessage.DDCURI_IS_EMPTY);
        }

        ArrayList<Object> arrayList = new ArrayList<>();
        arrayList.add(owner);
        arrayList.add(ddcId);
        arrayList.add(ddcURI);

        ReqJsonRpcBean reqJsonRpcBean = assembleDDC1155Transaction(sender, DDC1155Functions.SET_URI, arrayList);
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

        ReqJsonRpcBean reqJsonRpcBean = assembleDDC1155CallTransaction(DDC1155Functions.GET_LATEST_DDCID, arrayList);
        RespJsonRpcBean respJsonRpcBean = restTemplateUtil.sendPost(ConfigCache.get().getOpbGatewayAddress(), reqJsonRpcBean, RespJsonRpcBean.class);
        resultCheck(respJsonRpcBean);

        String encodeParams = new ObjectMapper().writeValueAsString(reqJsonRpcBean.getParams().get(1));
        ReqCallRpcBean reqCallRpcBean = JSONObject.parseObject(encodeParams, ReqCallRpcBean.class);
        String encodedFunction = reqCallRpcBean.getData();
        String jsonResult = new ObjectMapper().writeValueAsString(respJsonRpcBean.getResult());
        RespCallRpcBean respCallRpcBean = JSONObject.parseObject(jsonResult, RespCallRpcBean.class);
        callResultCheck(respCallRpcBean);

        InputAndOutputResult inputAndOutputResult = AnalyzeChainInfoUtils.analyzeTransactionOutput(ConfigCache.get().getDdc1155ABI(), ConfigCache.get().getDdc1155ABI(), encodedFunction, respCallRpcBean.getOutput());
        return new BigInteger(inputAndOutputResult.getResult().get(0).getData().toString());
    }

    /**
     * Nonce查询 通过调用该方法对签名者账户所对应的最新nonce值进行查询，注：此查询只适用于发起元交易处理业务所对应的nonce值查询
     *
     * @param from DDC拥有者
     * @throws Exception Exception
     * @return 最新Nonce值
     */
    public BigInteger getNonce(String from) throws Exception {
        ArrayList<Object> arrayList = new ArrayList<>();
        arrayList.add(from);
        ReqJsonRpcBean reqJsonRpcBean = assembleDDC1155CallTransaction(DDC1155Functions.GET_NONCE, arrayList);
        RespJsonRpcBean respJsonRpcBean = restTemplateUtil.sendPost(ConfigCache.get().getOpbGatewayAddress(), reqJsonRpcBean, RespJsonRpcBean.class);

        resultCheck(respJsonRpcBean);

        String encodeParams = new ObjectMapper().writeValueAsString(reqJsonRpcBean.getParams().get(1));
        ReqCallRpcBean reqCallRpcBean = JSONObject.parseObject(encodeParams, ReqCallRpcBean.class);
        String encodedFunction = reqCallRpcBean.getData();
        String jsonResult = new ObjectMapper().writeValueAsString(respJsonRpcBean.getResult());
        RespCallRpcBean respCallRpcBean = JSONObject.parseObject(jsonResult, RespCallRpcBean.class);
        callResultCheck(respCallRpcBean);

        InputAndOutputResult inputAndOutputResult = AnalyzeChainInfoUtils.analyzeTransactionOutput(ConfigCache.get().getDdc1155ABI(), ConfigCache.get().getDdc1155ABI(), encodedFunction, respCallRpcBean.getOutput());
        return new BigInteger(inputAndOutputResult.getResult().get(0).getData().toString());
    }

    /**
     * 元交易安全生成
     *
     * @param sender  调用者地址
     * @param to      接收者账户
     * @param amount  DDC数量
     * @param ddcURI  DDC资源标识符
     * @param data 附加数据
     * @param nonce nonce值
     * @param deadline    过期时间
     * @param sign    签名值
     * @return 交易哈希
     * @throws Exception Exception
     */
    public String metaSafeMint(String sender,String to,BigInteger amount,String ddcURI,byte[] data,BigInteger nonce,BigInteger deadline,byte[] sign) throws Exception {
        checkSenderAddress(sender);
        checkAccountAddress(to);
        checkDDCAmount(amount);
        checkDeadline(deadline);
        checkMetaSign(sign);

        Function function = new Function(
                DDC1155Functions.META_SAFE_MINT,
                Arrays.asList(
                        new Address(to),
                        new Uint256(amount),
                        new Utf8String(ddcURI),
                        new DynamicBytes(data),
                        new Uint256(nonce),
                        new Uint256(deadline),
                        new DynamicBytes(sign)
                ),
                Collections.<TypeReference<?>>emptyList()
        );

        ReqJsonRpcBean reqJsonRpcBean = assembleDDC1155TransactionByTransaction(sender, function);
        RespJsonRpcBean respJsonRpcBean = restTemplateUtil.sendPost(ConfigCache.get().getOpbGatewayAddress(), reqJsonRpcBean, RespJsonRpcBean.class);
        resultCheck(respJsonRpcBean);
        return (String) respJsonRpcBean.getResult();
    }

    /**
     * 元交易批量安全生成
     *
     * @param sender  调用者地址
     * @param to      接收者账户
     * @param ddcs DDC信息合计
     * @param data 附加数据
     * @param nonce nonce值
     * @param deadline    过期时间
     * @param sign    签名值
     * @return 交易哈希
     * @throws Exception Exception
     */
    public String metaSafeMintBatch(String sender,String to,Multimap<BigInteger,String> ddcs,byte[] data,BigInteger nonce,BigInteger deadline,byte[] sign) throws Exception {
        checkSenderAddress(sender);
        checkAccountAddress(to);
        if (ddcs.size() == 0) {
            throw new DDCException(ErrorMessage.DDCS_IS_EMPTY);
        }
        checkDeadline(deadline);
        checkMetaSign(sign);

        List<BigInteger> amountList = new ArrayList<>();
        List<String> ddcURI = new ArrayList<>();
        ddcs.forEach((key, value) -> {
            if (null == key || BigInteger.valueOf(0).compareTo(key) == 0) {
                throw new DDCException(ErrorMessage.AMOUNT_IS_EMPTY);
            }

            amountList.add(key);
            ddcURI.add(value);
        });

        List<Uint256> amountUint256List = amountList.stream().map(Uint256::new).collect(Collectors.toList());
        List<Utf8String> ddcURIUtf8StringList = ddcURI.stream().map(Utf8String::new).collect(Collectors.toList());

        Function function = new Function(
                DDC1155Functions.META_SAFE_MINT_BATCH,
                Arrays.asList(
                        new Address(to),
                        new DynamicArray<>(amountUint256List),
                        new DynamicArray<>(ddcURIUtf8StringList),
                        new DynamicBytes(data),
                        new Uint256(nonce),
                        new Uint256(deadline),
                        new DynamicBytes(sign)
                ),
                Collections.<TypeReference<?>>emptyList()
        );

        ReqJsonRpcBean reqJsonRpcBean = assembleDDC1155TransactionByTransaction(sender, function);
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
     * @param amount 数量
     * @param data 附加数据
     * @param nonce nonce值
     * @param deadline 过期时间
     * @param sign 签名值
     * @throws Exception Exception
     * @return 交易哈希
     */
    public String metaSafeTransferFrom(String sender,String from,String to,BigInteger ddcId,BigInteger amount,byte[] data,BigInteger nonce,BigInteger deadline,byte[] sign) throws Exception {
        checkSenderAddress(sender);
        checkFromAddress(from);
        checkToAddress(to);
        checkDDCID(ddcId);
        checkDDCAmount(amount);
        checkDeadline(deadline);
        checkMetaSign(sign);

        Function function = new Function(
                DDC1155Functions.META_SAFE_TRANSFER_FROM,
                Arrays.asList(
                        new Address(from),
                        new Address(to),
                        new Uint256(ddcId),
                        new Uint256(amount),
                        new DynamicBytes(data),
                        new Uint256(nonce),
                        new Uint256(deadline),
                        new DynamicBytes(sign)
                ),
                Collections.<TypeReference<?>>emptyList()
        );

        ReqJsonRpcBean reqJsonRpcBean = assembleDDC1155TransactionByTransaction(sender, function);
        RespJsonRpcBean respJsonRpcBean = restTemplateUtil.sendPost(ConfigCache.get().getOpbGatewayAddress(), reqJsonRpcBean, RespJsonRpcBean.class);

        resultCheck(respJsonRpcBean);
        return (String) respJsonRpcBean.getResult();
    }

    /**
     * 元交易批量安全转移 DDC拥有者或DDC授权者通过授权平台方调用该方法对DDC进行元交易批量安全转移
     * @param sender 调用者
     * @param from 拥有者账户
     * @param to 接收者账户
     * @param ddcs 拥有者DDCID集合
     * @param data 附加数据
     * @param nonce nonce值
     * @param deadline 过期时间
     * @param sign 签名值
     * @throws Exception Exception
     * @return 交易哈希
     */
    public String metaSafeBatchTransferFrom(String sender,String from,String to,Map<BigInteger,BigInteger> ddcs,byte[] data,BigInteger nonce,BigInteger deadline,byte[] sign) throws Exception {
        checkSenderAddress(sender);
        checkFromAddress(from);
        checkToAddress(to);

        List<BigInteger> ddcIds = new ArrayList<>();
        List<BigInteger> amountList = new ArrayList<>();
        ddcs.forEach((key, value) -> {
            if (null == key || BigInteger.valueOf(0).compareTo(key) == 0) {
                throw new DDCException(ErrorMessage.AMOUNT_IS_EMPTY);
            }

            ddcIds.add(key);
            amountList.add(value);
        });

        checkDeadline(deadline);
        checkMetaSign(sign);

        List<Uint256> ddcIdUint256List = ddcIds.stream().map(Uint256::new).collect(Collectors.toList());
        List<Uint256> amountUint256List = amountList.stream().map(Uint256::new).collect(Collectors.toList());

        Function function = new Function(
                DDC1155Functions.META_SAFE_BATCH_TRANSFER_FROM,
                Arrays.asList(
                        new Address(from),
                        new Address(to),
                        new DynamicArray<>(ddcIdUint256List),
                        new DynamicArray<>(amountUint256List),
                        new DynamicBytes(data),
                        new Uint256(nonce),
                        new Uint256(deadline),
                        new DynamicBytes(sign)
                ),
                Collections.<TypeReference<?>>emptyList()
        );

        ReqJsonRpcBean reqJsonRpcBean = assembleDDC1155TransactionByTransaction(sender, function);
        RespJsonRpcBean respJsonRpcBean = restTemplateUtil.sendPost(ConfigCache.get().getOpbGatewayAddress(), reqJsonRpcBean, RespJsonRpcBean.class);

        resultCheck(respJsonRpcBean);
        return (String) respJsonRpcBean.getResult();
    }

    /**
     * 元交易销毁
     * @param sender 调用者
     * @param owner 拥有者账户
     * @param ddcId DDCID
     * @param nonce nonce值
     * @param deadline 过期时间
     * @param sign 签名值
     * @throws Exception Exception
     * @return 交易哈希
     */
    public String metaBurn(String sender,String owner,BigInteger ddcId, BigInteger nonce,BigInteger deadline,byte[] sign) throws Exception {
        checkSenderAddress(sender);
        checkFromAddress(owner);
        checkDDCID(ddcId);
        checkDeadline(deadline);
        checkMetaSign(sign);

        Function function = new Function(
                DDC1155Functions.META_BURN,
                Arrays.asList(
                        new Address(owner),
                        new Uint256(ddcId),
                        new Uint256(nonce),
                        new Uint256(deadline),
                        new DynamicBytes(sign)
                ),
                Collections.<TypeReference<?>>emptyList()
        );

        ReqJsonRpcBean reqJsonRpcBean = assembleDDC1155TransactionByTransaction(sender, function);
        RespJsonRpcBean respJsonRpcBean = restTemplateUtil.sendPost(ConfigCache.get().getOpbGatewayAddress(), reqJsonRpcBean, RespJsonRpcBean.class);

        resultCheck(respJsonRpcBean);
        return (String) respJsonRpcBean.getResult();
    }

    /**
     * 元交易批量销毁
     * @param sender 调用者
     * @param owner 拥有者账户
     * @param ddcIds DDCID集合
     * @param nonce nonce值
     * @param deadline 过期时间
     * @param sign 签名值
     * @throws Exception Exception
     * @return 交易哈希
     */
    public String metaBurnBatch(String sender,String owner,List<BigInteger> ddcIds,BigInteger nonce,BigInteger deadline,byte[] sign) throws Exception {
        checkSenderAddress(sender);
        checkFromAddress(owner);
        if (ddcIds.size() == 0){
            throw new DDCException(ErrorMessage.DDCID_IS_WRONG);
        }
        for(BigInteger ddcId : ddcIds){
            checkDDCID(ddcId);
        }
        checkDeadline(deadline);
        checkMetaSign(sign);

        List<Uint256> ddcIdUint256List = ddcIds.stream().map(Uint256::new).collect(Collectors.toList());

        Function function = new Function(
                DDC1155Functions.META_BURN_BATCH,
                Arrays.asList(
                        new Address(owner),
                        new DynamicArray<>(ddcIdUint256List),
                        new Uint256(nonce),
                        new Uint256(deadline),
                        new DynamicBytes(sign)
                ),
                Collections.<TypeReference<?>>emptyList()
        );

        ReqJsonRpcBean reqJsonRpcBean = assembleDDC1155TransactionByTransaction(sender, function);
        RespJsonRpcBean respJsonRpcBean = restTemplateUtil.sendPost(ConfigCache.get().getOpbGatewayAddress(), reqJsonRpcBean, RespJsonRpcBean.class);

        resultCheck(respJsonRpcBean);
        return (String) respJsonRpcBean.getResult();
    }

    /**
     * 同步拥有者
     * @param sender 调用者
     * @param ddcIds DDCID集合
     * @param owners 拥有者列表
     * @throws Exception Exception
     * @return 交易哈希
     */
    public String syncDDCOwners(String sender,List<BigInteger> ddcIds,List<List<String>> owners) throws Exception {
        checkSenderAddress(sender);
        if (ddcIds.isEmpty()) {
            throw new DDCException(ErrorMessage.DDCID_IS_WRONG);
        }
        for (BigInteger ddcId : ddcIds) {
            checkDDCID(ddcId);
        }
        if (owners.isEmpty()) {
            throw new DDCException(ErrorMessage.DDCS_OWNER_IS_EMPTY);
        }
        for (List<String> owner : owners) {
            if (owner.isEmpty()){
                throw new DDCException(ErrorMessage.DDCS_OWNER_IS_EMPTY);
            }
        }

        List<Uint256> ddcIdUint256List = ddcIds.stream().map(Uint256::new).collect(Collectors.toList());
        List<DynamicArray<Address>> ownerAddressLists = new ArrayList<>();
        for(List<String> owner : owners) {
            List<Address> ownerAddressList = owner.stream().map(Address::new).collect(Collectors.toList());
            ownerAddressLists.add(new DynamicArray<>(ownerAddressList));
        }

        Function function = new Function(
                DDC1155Functions.SYNC_DDCOWNERS,
                Arrays.asList(
                        new DynamicArray<>(ddcIdUint256List),
                        new DynamicArray<>(ownerAddressLists)
                ),
                Collections.<TypeReference<?>>emptyList()
        );

        ReqJsonRpcBean reqJsonRpcBean = assembleDDC1155TransactionByTransaction(sender, function);
        RespJsonRpcBean respJsonRpcBean = restTemplateUtil.sendPost(ConfigCache.get().getOpbGatewayAddress(), reqJsonRpcBean, RespJsonRpcBean.class);

        resultCheck(respJsonRpcBean);
        return (String) respJsonRpcBean.getResult();
    }

    private ReqJsonRpcBean assembleDDC1155Transaction(String sender, String functionName, ArrayList<Object> params) throws Exception {
        return assembleTransaction(sender, getBlockNumber(), ConfigCache.get().getDdc1155ABI(), ConfigCache.get().getDdc1155Address(), functionName, params);
    }

    private ReqJsonRpcBean assembleDDC1155CallTransaction(String functionName, ArrayList<Object> params) throws Exception {
        return assembleTransaction(OneAddress, new BigInteger("0"), ConfigCache.get().getDdc1155ABI(), ConfigCache.get().getDdc1155Address(), functionName, params);
    }

    private ReqJsonRpcBean assembleDDC1155TransactionByTransaction(String sender, Function function) throws Exception {
        return assembleTransactionByFunction(sender, getBlockNumber(), ConfigCache.get().getDdc1155ABI(), ConfigCache.get().getDdc1155Address(), function);
    }
}
