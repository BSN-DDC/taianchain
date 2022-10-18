package com.reddate.taianddc.service;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Multimap;
import com.reddate.taianddc.config.ConfigCache;
import com.reddate.taianddc.constant.ChargeFunctions;
import com.reddate.taianddc.constant.ErrorMessage;
import com.reddate.taianddc.dto.taianchain.ReqCallRpcBean;
import com.reddate.taianddc.dto.taianchain.ReqJsonRpcBean;
import com.reddate.taianddc.dto.taianchain.RespCallRpcBean;
import com.reddate.taianddc.dto.taianchain.RespJsonRpcBean;
import com.reddate.taianddc.exception.DDCException;
import com.reddate.taianddc.listener.SignEventListener;
import com.reddate.taianddc.util.AddressUtils;
import com.reddate.taianddc.util.AnalyzeChainInfoUtils;
import com.reddate.taianddc.util.HexUtils;
import org.fisco.bcos.web3j.abi.datatypes.Address;
import org.fisco.bcos.web3j.abi.datatypes.generated.Bytes4;
import org.fisco.bcos.web3j.abi.datatypes.generated.Uint256;
import org.fisco.bcos.web3j.crypto.gm.sm2.util.encoders.Hex;
import org.fisco.bcos.web3j.tx.txdecode.InputAndOutputResult;
import org.fisco.bcos.web3j.tx.txdecode.ResultEntity;
import org.fisco.bcos.web3j.utils.Strings;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ChargeService extends BaseService {

    public ChargeService(SignEventListener signEventListener) {
        super.signEventListener = signEventListener;
    }

    /**
     * 运营方、平台方调用该接口为所属同一方的同一级别账户或者下级账户充值；
     *
     * @param sender 调用者地址
     * @param to 充值账户的地址
     * @param amount 充值金额
     * @return 返回交易哈希
     * @throws Exception Exception
     */
    public String recharge(String sender, String to, BigInteger amount) throws Exception {
        checkSenderAddress(sender);
        checkToAddress(to);

        if (amount == null || amount.compareTo(BigInteger.valueOf(0L)) <= 0) {
            throw new DDCException(ErrorMessage.AMOUNT_IS_EMPTY);
        }

        ArrayList<Object> arrayList = new ArrayList<>();
        arrayList.add(new Address(to));
        arrayList.add(amount);

        ReqJsonRpcBean reqJsonRpcBean = assembleChargeTransaction(sender, ChargeFunctions.RECHARGE, arrayList);
        RespJsonRpcBean respJsonRpcBean = restTemplateUtil.sendPost(ConfigCache.get().getOpbGatewayAddress(), reqJsonRpcBean, RespJsonRpcBean.class);
        resultCheck(respJsonRpcBean);
        return (String) respJsonRpcBean.getResult();
    }

    /**
     * 批量充值 运营方、平台方调用该接口为所属同一方的同一级别账户或者下级账户充值；
     *
     * @param sender 调用者地址
     * @param accounts 充值账户的地址/充值金额
     * @return 返回交易哈希
     * @throws Exception Exception
     */
    public String rechargeBatch(String sender, Multimap<String,BigInteger> accounts) throws Exception {
        checkSenderAddress(sender);

        if (accounts.isEmpty()) {
            throw new DDCException(ErrorMessage.TO_ACCOUNT_IS_EMPTY);
        }

        ArrayList<Address> toList = new ArrayList<>();
        ArrayList<BigInteger> amountList = new ArrayList<>();
        accounts.forEach( (to, amount) -> {
            checkToAddress(to);

            if (amount == null || amount.compareTo(BigInteger.valueOf(0L)) <= 0) {
                throw new DDCException(ErrorMessage.AMOUNT_IS_EMPTY);
            }

            toList.add(new Address(to));
            amountList.add(amount);
        });

        ArrayList<Object> arrayList = new ArrayList<>();
        arrayList.add(toList.stream().map(String::valueOf).collect(Collectors.joining(",")));
        arrayList.add(amountList.stream().map(String::valueOf).collect(Collectors.joining(",")));

        ReqJsonRpcBean reqJsonRpcBean = assembleChargeTransaction(sender, ChargeFunctions.RECHARGE_BATCH, arrayList);
        RespJsonRpcBean respJsonRpcBean = restTemplateUtil.sendPost(ConfigCache.get().getOpbGatewayAddress(), reqJsonRpcBean, RespJsonRpcBean.class);
        resultCheck(respJsonRpcBean);
        return (String) respJsonRpcBean.getResult();
    }

    /**
     * 查询指定账户的余额。
     *
     * @param accAddr 查询的账户地址
     * @return 返回账户所对应的业务费余额
     * @throws Exception Exception
     */
    public BigInteger balanceOf(String accAddr) throws Exception {
        checkDDCAddress(accAddr);

        ArrayList<Object> arrayList = new ArrayList<>();
        arrayList.add(new Address(accAddr));

        ReqJsonRpcBean reqJsonRpcBean = assembleChargeCallTransaction(ChargeFunctions.BALANCE_OF, arrayList);
        RespJsonRpcBean respJsonRpcBean = restTemplateUtil.sendPost(ConfigCache.get().getOpbGatewayAddress(), reqJsonRpcBean, RespJsonRpcBean.class);
        resultCheck(respJsonRpcBean);

        String jsonResult = new ObjectMapper().writeValueAsString(respJsonRpcBean.getResult());
        RespCallRpcBean respCallRpcBean = JSONObject.parseObject(jsonResult, RespCallRpcBean.class);
        callResultCheck(respCallRpcBean);

        return new BigInteger(respCallRpcBean.getOutput().replace("0x", ""), 16);
    }

    /**
     * 批量链账户余额查询
     *
     * @param accAddrs 查询的账户地址
     * @return 返回账户所对应的业务费余额
     * @throws Exception Exception
     */
    public List<BigInteger> balanceOfBatch(List<String> accAddrs) throws Exception {

        if (accAddrs.size() == 0) {
            throw new DDCException(ErrorMessage.ACC_ADDR_IS_EMPTY);
        }

        for (int i = 0; i < accAddrs.size(); i++) {
            String accAddr = accAddrs.get(i);
            checkDDCAddress(accAddr);
        }

        ArrayList<Object> arrayList = new ArrayList<>();
        arrayList.add(accAddrs.stream().map(String::valueOf).collect(Collectors.joining(",")));

        ReqJsonRpcBean reqJsonRpcBean = assembleChargeCallTransaction(ChargeFunctions.BALANCE_OF_BATCH, arrayList);
        RespJsonRpcBean respJsonRpcBean = restTemplateUtil.sendPost(ConfigCache.get().getOpbGatewayAddress(), reqJsonRpcBean, RespJsonRpcBean.class);
        resultCheck(respJsonRpcBean);

        String encodeParams = new ObjectMapper().writeValueAsString(reqJsonRpcBean.getParams().get(1));
        ReqCallRpcBean reqCallRpcBean = JSONObject.parseObject(encodeParams, ReqCallRpcBean.class);
        String encodedFunction = reqCallRpcBean.getData();

        String jsonResult = new ObjectMapper().writeValueAsString(respJsonRpcBean.getResult());
        RespCallRpcBean respCallRpcBean = JSONObject.parseObject(jsonResult, RespCallRpcBean.class);
        callResultCheck(respCallRpcBean);

        InputAndOutputResult inputAndOutputResult = AnalyzeChainInfoUtils.analyzeTransactionOutput(ConfigCache.get().getChargeLogicABI(), ConfigCache.get().getChargeLogicBIN(), encodedFunction, respCallRpcBean.getOutput());

        List<ResultEntity> functionList = inputAndOutputResult.getResult();

        List<BigInteger> balanceBatchList = new ArrayList<>();
        if (functionList != null) {
            for (ResultEntity resultEntity : functionList) {

                ArrayList<Uint256> dataList = (ArrayList<Uint256>) resultEntity.getTypeObject().getValue();
                dataList.forEach(data -> {
                    balanceBatchList.add(data.getValue());
                });
            }
        }

        return balanceBatchList;
    }

    /**
     * 查询指定的DDC业务主逻辑合约的方法所对应的调用业务费用。
     *
     * @param ddcAddr DDC业务主逻辑合约地址
     * @param sig     Hex格式的合约方法ID
     * @return 返回DDC合约业务费
     * @throws Exception Exception
     */
    public BigInteger queryFee(String ddcAddr, String sig) throws Exception {
        checkDDCAddress(ddcAddr);
        checkSig(sig);

        ArrayList<Object> arrayList = new ArrayList<>();
        arrayList.add(new Address(ddcAddr));
        arrayList.add(sig);

        ReqJsonRpcBean reqJsonRpcBean = assembleChargeCallTransaction(ChargeFunctions.QUERY_FEE, arrayList);
        RespJsonRpcBean respJsonRpcBean = restTemplateUtil.sendPost(ConfigCache.get().getOpbGatewayAddress(), reqJsonRpcBean, RespJsonRpcBean.class);
        resultCheck(respJsonRpcBean);

        String jsonResult = new ObjectMapper().writeValueAsString(respJsonRpcBean.getResult());
        RespCallRpcBean respCallRpcBean = JSONObject.parseObject(jsonResult, RespCallRpcBean.class);
        callResultCheck(respCallRpcBean);

        return new BigInteger(respCallRpcBean.getOutput().replace("0x", ""), 16);

    }

    /**
     * 运营方调用为自己的账户增加业务费。
     *
     * @param sender 调用者地址
     * @param amount 对运营方账户进行充值的业务费
     * @return 返回交易哈希
     * @throws Exception Exception
     */
    public String selfRecharge(String sender, BigInteger amount) throws Exception {
        checkSenderAddress(sender);

        if (amount == null || amount.compareTo(BigInteger.valueOf(0L)) <= 0) {
            throw new DDCException(ErrorMessage.AMOUNT_IS_EMPTY);
        }

        ArrayList<Object> arrayList = new ArrayList<>();
        arrayList.add(amount);

        ReqJsonRpcBean reqJsonRpcBean = assembleChargeTransaction(sender, ChargeFunctions.SELF_RECHARGE, arrayList);
        RespJsonRpcBean respJsonRpcBean = restTemplateUtil.sendPost(ConfigCache.get().getOpbGatewayAddress(), reqJsonRpcBean, RespJsonRpcBean.class);
        resultCheck(respJsonRpcBean);
        return (String) respJsonRpcBean.getResult();
    }

    /**
     * 运营方调用接口设置指定的DDC主合约的方法调用费用。
     *
     * @param sender  调用者地址
     * @param ddcAddr DDC业务主逻辑合约地址
     * @param sig     Hex格式的合约方法ID
     * @param amount  业务费用
     * @return 返回交易哈希
     * @throws Exception Exception
     */
    public String setFee(String sender, String ddcAddr, String sig, BigInteger amount) throws Exception {
        checkSenderAddress(sender);
        checkDDCAddress(ddcAddr);
        checkSig(sig);

        if (amount == null) {
            throw new DDCException(ErrorMessage.AMOUNT_IS_EMPTY);
        }

        if (amount.compareTo(BigInteger.valueOf(0L)) < 0) {
            throw new DDCException(ErrorMessage.AMOUNT_LT_ZERO);
        }

        ArrayList<Object> arrayList = new ArrayList<>();
        arrayList.add(ddcAddr);
        arrayList.add(sig);
        arrayList.add(amount);

        ReqJsonRpcBean reqJsonRpcBean = assembleChargeTransaction(sender, ChargeFunctions.SET_FEE, arrayList);
        RespJsonRpcBean respJsonRpcBean = restTemplateUtil.sendPost(ConfigCache.get().getOpbGatewayAddress(), reqJsonRpcBean, RespJsonRpcBean.class);
        resultCheck(respJsonRpcBean);
        return (String) respJsonRpcBean.getResult();
    }

    /**
     * 运营方调用接口删除指定的DDC主合约的方法调用费用。
     *
     * @param sender  调用者地址
     * @param ddcAddr DDC业务主逻辑合约地址
     * @param sig     Hex格式的合约方法ID
     * @return 返回交易哈希
     * @throws Exception Exception
     */
    public String delFee(String sender, String ddcAddr, String sig) throws Exception {
        checkSenderAddress(sender);
        checkDDCAddress(ddcAddr);
        checkSig(sig);

        ArrayList<Object> arrayList = new ArrayList<>();
        arrayList.add(ddcAddr);
        arrayList.add(sig);

        ReqJsonRpcBean reqJsonRpcBean = assembleChargeTransaction(sender, ChargeFunctions.DEL_FEE, arrayList);
        RespJsonRpcBean respJsonRpcBean = restTemplateUtil.sendPost(ConfigCache.get().getOpbGatewayAddress(), reqJsonRpcBean, RespJsonRpcBean.class);
        resultCheck(respJsonRpcBean);
        return (String) respJsonRpcBean.getResult();
    }

    /**
     * 运营方调用该接口删除指定的DDC业务主逻辑合约授权。
     *
     * @param sender  调用者地址
     * @param ddcAddr DDC业务主逻辑合约地址
     * @return 返回交易哈希
     * @throws Exception Exception
     */
    public String delDDC(String sender, String ddcAddr) throws Exception {
        checkSenderAddress(sender);
        checkDDCAddress(ddcAddr);

        ArrayList<Object> arrayList = new ArrayList<>();
        arrayList.add(new Address(ddcAddr));

        ReqJsonRpcBean reqJsonRpcBean = assembleChargeTransaction(sender, ChargeFunctions.DEL_DDC, arrayList);
        RespJsonRpcBean respJsonRpcBean = restTemplateUtil.sendPost(ConfigCache.get().getOpbGatewayAddress(), reqJsonRpcBean, RespJsonRpcBean.class);
        resultCheck(respJsonRpcBean);
        return (String) respJsonRpcBean.getResult();
    }

    /**
     * 启用批量设置
     *
     * @param sender  调用者地址
     * @param isOpen 开关标识
     * @return 返回交易哈希
     * @throws Exception Exception
     */
    public String setSwitcherStateOfBatch(String sender,Boolean isOpen) throws Exception {
        checkSenderAddress(sender);

        ArrayList<Object> arrayList = new ArrayList<>();
        arrayList.add(isOpen);

        ReqJsonRpcBean reqJsonRpcBean = assembleChargeTransaction(sender, ChargeFunctions.SET_SWITCHER_STATE_OF_BATCH, arrayList);
        RespJsonRpcBean respJsonRpcBean = restTemplateUtil.sendPost(ConfigCache.get().getOpbGatewayAddress(), reqJsonRpcBean, RespJsonRpcBean.class);
        resultCheck(respJsonRpcBean);
        return (String) respJsonRpcBean.getResult();
    }

    private ReqJsonRpcBean assembleChargeTransaction(String sender, String functionName, ArrayList<Object> params) throws Exception {
        return assembleTransaction(sender, getBlockNumber(), ConfigCache.get().getChargeLogicABI(), ConfigCache.get().getChargeLogicAddress(), functionName, params);
    }

    private ReqJsonRpcBean assembleChargeCallTransaction(String functionName, ArrayList<Object> params) throws Exception {
        return assembleTransaction(OneAddress, new BigInteger("0"), ConfigCache.get().getChargeLogicABI(), ConfigCache.get().getChargeLogicAddress(), functionName, params);
    }
}
