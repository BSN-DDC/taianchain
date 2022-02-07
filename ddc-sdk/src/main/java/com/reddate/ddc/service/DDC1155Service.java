package com.reddate.ddc.service;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Multimap;
import com.reddate.ddc.config.ConfigCache;
import com.reddate.ddc.constant.DDC1155Functions;
import com.reddate.ddc.constant.ErrorMessage;
import com.reddate.ddc.dto.taianchain.ReqCallRpcBean;
import com.reddate.ddc.dto.taianchain.ReqJsonRpcBean;
import com.reddate.ddc.dto.taianchain.RespCallRpcBean;
import com.reddate.ddc.dto.taianchain.RespJsonRpcBean;
import com.reddate.ddc.exception.DDCException;
import com.reddate.ddc.listener.SignEventListener;
import com.reddate.ddc.util.AddressUtils;
import com.reddate.ddc.util.AnalyzeChainInfoUtils;
import lombok.extern.slf4j.Slf4j;
import org.fisco.bcos.web3j.tx.txdecode.InputAndOutputResult;
import org.fisco.bcos.web3j.utils.Strings;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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
        if (Strings.isEmpty(sender)) {
            throw new DDCException(ErrorMessage.SENDER_IS_EMPTY);
        }

        if (!AddressUtils.isValidAddress(sender)) {
            throw new DDCException(ErrorMessage.SENDER_IS_NOT_ADDRESS_FORMAT);
        }

        //验证account为标准address格式
        if (Strings.isEmpty(to)) {
            throw new DDCException(ErrorMessage.TO_ACCOUNT_IS_EMPTY);
        }
        if (!AddressUtils.isValidAddress(to)) {
            throw new DDCException(ErrorMessage.ACCOUNT_IS_NOT_ADDRESS_FORMAT);
        }

        //3.	检查需要生成的DDC数量是否大于0
        if (null == amount || BigInteger.valueOf(0).compareTo(amount) >= 0) {
            throw new DDCException(ErrorMessage.AMOUNT_IS_EMPOTY);
        }

        ArrayList<Object> arrayList = new ArrayList<>();
        arrayList.add(to);
        arrayList.add(amount);
        arrayList.add(ddcURI);
        //arrayList.add(new String(data));
        arrayList.add(data);

        ReqJsonRpcBean reqJsonRpcBean = assembleDDC1155Transaction(sender, DDC1155Functions.SAFE_MINT, arrayList);
        RespJsonRpcBean respJsonRpcBean = restTemplateUtil.sendPost(ConfigCache.get().getOpbGatewayAddress(), reqJsonRpcBean, RespJsonRpcBean.class);
        resultCheck(respJsonRpcBean);
        return (String) respJsonRpcBean.getResult();
    }

    /**
     * DDC的批量创建
     *
     * @param sender  调用者地址
     * @param to      接收者账户
     * @param ddcInfo DDC信息
     * @param data    附加数据
     * @return 交易哈希
     * @throws Exception Exception
     */
    public String safeMintBatch(String sender, String to, Multimap<BigInteger, String> ddcInfo, byte[] data) throws Exception {
        if (Strings.isEmpty(sender)) {
            throw new DDCException(ErrorMessage.SENDER_IS_EMPTY);
        }

        if (!AddressUtils.isValidAddress(sender)) {
            throw new DDCException(ErrorMessage.SENDER_IS_NOT_ADDRESS_FORMAT);
        }

        //验证account为标准address格式
        if (Strings.isEmpty(to)) {
            throw new DDCException(ErrorMessage.TO_ACCOUNT_IS_EMPTY);
        }
        if (!AddressUtils.isValidAddress(to)) {
            throw new DDCException(ErrorMessage.ACCOUNT_IS_NOT_ADDRESS_FORMAT);
        }
        List<String> amountList = new ArrayList<>();
        List<String> ddcURI = new ArrayList<>();

        ddcInfo.forEach((key, value) -> {
            //验证accName不为空
            if (null == key || BigInteger.valueOf(0).compareTo(key) == 0) {
                throw new DDCException(ErrorMessage.AMOUNT_IS_EMPOTY);
            }

            amountList.add(key.toString());
            ddcURI.add(value);
        });


        ArrayList<Object> arrayList = new ArrayList<>();
        arrayList.add(to);
        arrayList.add(amountList.stream().collect(Collectors.joining(",")));
        arrayList.add(ddcURI.stream().collect(Collectors.joining(",")));
        //arrayList.add(new String(data));
        arrayList.add(data);

        ReqJsonRpcBean reqJsonRpcBean = assembleDDC1155Transaction(sender, DDC1155Functions.SAFE_MINT_BATCH, arrayList);
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
        if (Strings.isEmpty(sender)) {
            throw new DDCException(ErrorMessage.SENDER_IS_EMPTY);
        }

        if (!AddressUtils.isValidAddress(sender)) {
            throw new DDCException(ErrorMessage.SENDER_IS_NOT_ADDRESS_FORMAT);
        }

        //验证account为标准address格式
        if (Strings.isEmpty(operator)) {
            throw new DDCException(ErrorMessage.TO_ACCOUNT_IS_EMPTY);
        }
        if (!AddressUtils.isValidAddress(operator)) {
            throw new DDCException(ErrorMessage.ACCOUNT_IS_NOT_ADDRESS_FORMAT);
        }

        ArrayList<Object> arrayList = new ArrayList<>();
        arrayList.add(operator);
        arrayList.add(approved);

        ReqJsonRpcBean reqJsonRpcBean = assembleDDC1155Transaction(sender, DDC1155Functions.SetApprovalForAll, arrayList);
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

        //验证account为标准address格式
        if (Strings.isEmpty(owner)) {
            throw new DDCException(ErrorMessage.FROM_ACCOUNT_IS_EMPTY);
        }
        if (Strings.isEmpty(operator)) {
            throw new DDCException(ErrorMessage.TO_ACCOUNT_IS_EMPTY);
        }
        if (!AddressUtils.isValidAddress(owner)) {
            throw new DDCException(ErrorMessage.ACCOUNT_IS_NOT_ADDRESS_FORMAT);
        }
        if (!AddressUtils.isValidAddress(operator)) {
            throw new DDCException(ErrorMessage.ACCOUNT_IS_NOT_ADDRESS_FORMAT);
        }

        ArrayList<Object> arrayList = new ArrayList<>();
        arrayList.add(owner);
        arrayList.add(operator);

        ReqJsonRpcBean reqJsonRpcBean = assembleDDC1155CallTransaction(DDC1155Functions.IsApprovedForAll, arrayList);
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
        if (Strings.isEmpty(sender)) {
            throw new DDCException(ErrorMessage.SENDER_IS_EMPTY);
        }

        if (!AddressUtils.isValidAddress(sender)) {
            throw new DDCException(ErrorMessage.SENDER_IS_NOT_ADDRESS_FORMAT);
        }

        //验证account为标准address格式
        if (Strings.isEmpty(from)) {
            throw new DDCException(ErrorMessage.FROM_ACCOUNT_IS_EMPTY);
        }
        if (Strings.isEmpty(to)) {
            throw new DDCException(ErrorMessage.TO_ACCOUNT_IS_EMPTY);
        }
        if (!AddressUtils.isValidAddress(from) || !AddressUtils.isValidAddress(to)) {
            throw new DDCException(ErrorMessage.ACCOUNT_IS_NOT_ADDRESS_FORMAT);
        }
        if (null == ddcId) {
            throw new DDCException(ErrorMessage.DDCID_IS_WRONG);
        }

        ArrayList<Object> arrayList = new ArrayList<>();
        arrayList.add(from);
        arrayList.add(to);
        arrayList.add(ddcId);
        arrayList.add(amount);
        arrayList.add(data);

        ReqJsonRpcBean reqJsonRpcBean = assembleDDC1155Transaction(sender, DDC1155Functions.SafeTransferFrom, arrayList);
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
        if (!AddressUtils.isValidAddress(from) || !AddressUtils.isValidAddress(to)) {
            throw new DDCException(ErrorMessage.ACCOUNT_IS_NOT_ADDRESS_FORMAT);
        }
        if (null == ddcs) {
            throw new DDCException(ErrorMessage.DDCID_IS_WRONG);
        }

        ArrayList<String> ddcIds = new ArrayList();
        ArrayList<String> amounts = new ArrayList();

        ddcs.forEach((key, value) -> {
            //验证accName不为空
            if (null == key || BigInteger.valueOf(0).compareTo(value) == 0) {
                throw new DDCException(ErrorMessage.AMOUNT_IS_EMPOTY);
            }

            ddcIds.add(String.valueOf(key));
            amounts.add(String.valueOf(value));
        });

        ArrayList<Object> arrayList = new ArrayList<>();
        arrayList.add(from);
        arrayList.add(to);
        arrayList.add(ddcIds.stream().collect(Collectors.joining(",")));
        arrayList.add(amounts.stream().collect(Collectors.joining(",")));
        arrayList.add(data);

        ReqJsonRpcBean reqJsonRpcBean = assembleDDC1155Transaction(sender, DDC1155Functions.SafeBatchTransferFrom, arrayList);
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
        if (Strings.isEmpty(sender)) {
            throw new DDCException(ErrorMessage.SENDER_IS_EMPTY);
        }

        if (!AddressUtils.isValidAddress(sender)) {
            throw new DDCException(ErrorMessage.SENDER_IS_NOT_ADDRESS_FORMAT);
        }

        if (null == ddcId) {
            throw new DDCException(ErrorMessage.DDCID_IS_WRONG);
        }

        ArrayList<Object> arrayList = new ArrayList<>();
        arrayList.add(ddcId);

        ReqJsonRpcBean reqJsonRpcBean = assembleDDC1155Transaction(sender, DDC1155Functions.Freeze, arrayList);
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
        if (Strings.isEmpty(sender)) {
            throw new DDCException(ErrorMessage.SENDER_IS_EMPTY);
        }

        if (!AddressUtils.isValidAddress(sender)) {
            throw new DDCException(ErrorMessage.SENDER_IS_NOT_ADDRESS_FORMAT);
        }

        if (null == ddcId) {
            throw new DDCException(ErrorMessage.DDCID_IS_WRONG);
        }

        ArrayList<Object> arrayList = new ArrayList<>();
        arrayList.add(ddcId);

        ReqJsonRpcBean reqJsonRpcBean = assembleDDC1155Transaction(sender, DDC1155Functions.UnFreeze, arrayList);
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
        if (Strings.isEmpty(sender)) {
            throw new DDCException(ErrorMessage.SENDER_IS_EMPTY);
        }

        if (!AddressUtils.isValidAddress(sender)) {
            throw new DDCException(ErrorMessage.SENDER_IS_NOT_ADDRESS_FORMAT);
        }

        if (Strings.isEmpty(owner)) {
            throw new DDCException(ErrorMessage.TO_ACCOUNT_IS_EMPTY);
        }
        if (!AddressUtils.isValidAddress(owner)) {
            throw new DDCException(ErrorMessage.ACCOUNT_IS_NOT_ADDRESS_FORMAT);
        }
        if (null == ddcId) {
            throw new DDCException(ErrorMessage.DDCID_IS_WRONG);
        }
        ArrayList<Object> arrayList = new ArrayList<>();
        arrayList.add(owner);
        arrayList.add(ddcId);

        ReqJsonRpcBean reqJsonRpcBean = assembleDDC1155Transaction(sender, DDC1155Functions.Burn, arrayList);
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
        if (Strings.isEmpty(sender)) {
            throw new DDCException(ErrorMessage.SENDER_IS_EMPTY);
        }

        if (!AddressUtils.isValidAddress(sender)) {
            throw new DDCException(ErrorMessage.SENDER_IS_NOT_ADDRESS_FORMAT);
        }

        if (Strings.isEmpty(owner)) {
            throw new DDCException(ErrorMessage.TO_ACCOUNT_IS_EMPTY);
        }
        if (!AddressUtils.isValidAddress(owner)) {
            throw new DDCException(ErrorMessage.ACCOUNT_IS_NOT_ADDRESS_FORMAT);
        }
        if (null == ddcIds) {
            throw new DDCException(ErrorMessage.DDCID_IS_WRONG);
        }
        ArrayList<Object> arrayList = new ArrayList<>();
        arrayList.add(owner);
        arrayList.add(ddcIds.stream().map(String::valueOf).collect(Collectors.joining(",")));

        ReqJsonRpcBean reqJsonRpcBean = assembleDDC1155Transaction(sender, DDC1155Functions.BurnBatch, arrayList);
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
     * @throws Exception
     */
    public BigInteger balanceOf(String owner, BigInteger ddcId) throws Exception {
        if (Strings.isEmpty(owner)) {
            throw new DDCException(ErrorMessage.TO_ACCOUNT_IS_EMPTY);
        }
        if (!AddressUtils.isValidAddress(owner)) {
            throw new DDCException(ErrorMessage.ACCOUNT_IS_NOT_ADDRESS_FORMAT);
        }
        if (null == ddcId) {
            throw new DDCException(ErrorMessage.DDCID_IS_WRONG);
        }
        ArrayList<Object> arrayList = new ArrayList<>();
        arrayList.add(owner);
        arrayList.add(ddcId);

        ReqJsonRpcBean reqJsonRpcBean = assembleDDC1155CallTransaction(DDC1155Functions.BalanceOf, arrayList);
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
     * @throws Exception
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
        params.add(owners.stream().collect(Collectors.joining(",")));
        params.add(ddcIds.stream().collect(Collectors.joining(",")));

        ReqJsonRpcBean reqJsonRpcBean = assembleDDC1155CallTransaction(DDC1155Functions.BalanceOfBatch, params);
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

        if (null == ddcId) {
            throw new DDCException(ErrorMessage.DDCID_IS_WRONG);
        }
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
     * @param sender sender
     * @param owner DDC owner
     * @param ddcId ddcId
     * @param ddcURI ddcURI
     * @return
     * @throws Exception
     */
    public String setURI(String sender, String owner, BigInteger ddcId, String ddcURI) throws Exception {

        if (Strings.isEmpty(sender)) {
            throw new DDCException(ErrorMessage.SENDER_IS_EMPTY);
        }

        if (!AddressUtils.isValidAddress(sender)) {
            throw new DDCException(ErrorMessage.SENDER_IS_NOT_ADDRESS_FORMAT);
        }

        if (Strings.isEmpty(owner)) {
            throw new DDCException(ErrorMessage.TO_ACCOUNT_IS_EMPTY);
        }

        if (!AddressUtils.isValidAddress(owner)) {
            throw new DDCException(ErrorMessage.ACCOUNT_IS_NOT_ADDRESS_FORMAT);
        }

        if (null == ddcId) {
            throw new DDCException(ErrorMessage.DDCID_IS_WRONG);
        }

        if (Strings.isEmpty(ddcURI)) {
            throw new DDCException(ErrorMessage.DDCURI_IS_EMPTY);
        }

        ArrayList<Object> arrayList = new ArrayList<>();
        arrayList.add(owner);
        arrayList.add(ddcId);
        arrayList.add(ddcURI);

        ReqJsonRpcBean reqJsonRpcBean = assembleDDC1155Transaction(sender, DDC1155Functions.SetURI, arrayList);
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


}
