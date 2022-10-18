package com.reddate.taianddc.service;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.reddate.taianddc.config.ConfigCache;
import com.reddate.taianddc.constant.AuthorityFunctions;
import com.reddate.taianddc.constant.ErrorMessage;
import com.reddate.taianddc.dto.ddc.AccountInfo;
import com.reddate.taianddc.dto.ddc.AccountRole;
import com.reddate.taianddc.dto.ddc.AccountState;
import com.reddate.taianddc.dto.taianchain.ReqCallRpcBean;
import com.reddate.taianddc.dto.taianchain.ReqJsonRpcBean;
import com.reddate.taianddc.dto.taianchain.RespCallRpcBean;
import com.reddate.taianddc.dto.taianchain.RespJsonRpcBean;
import com.reddate.taianddc.exception.DDCException;
import com.reddate.taianddc.listener.SignEventListener;
import com.reddate.taianddc.util.AddressUtils;
import com.reddate.taianddc.util.AnalyzeChainInfoUtils;
import com.reddate.taianddc.util.HexUtils;

import lombok.extern.slf4j.Slf4j;
import org.fisco.bcos.web3j.abi.datatypes.*;
import org.fisco.bcos.web3j.abi.datatypes.generated.Bytes4;
import org.fisco.bcos.web3j.crypto.gm.sm2.util.encoders.Hex;
import org.fisco.bcos.web3j.tx.txdecode.InputAndOutputResult;
import org.fisco.bcos.web3j.tx.txdecode.ResultEntity;
import org.fisco.bcos.web3j.utils.Strings;

import java.math.BigInteger;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
public class AuthorityService extends BaseService {

    public AuthorityService(SignEventListener signEventListener) {
        super.signEventListener = signEventListener;
    }

    /**
     * 平台方添加账户
     * @param sender 调用者
     * @param account 账户地址
     * @param accountName 账户名称
     * @param accountDID 账户DID
     * @return 交易哈希
     * @throws Exception Exception
     */
    public String addAccountByPlatform(String sender,String account,String accountName, String accountDID) throws Exception {
        if (Strings.isEmpty(sender)) {
            throw new DDCException(ErrorMessage.SENDER_IS_EMPTY);
        }

        if (!AddressUtils.isValidAddress(sender)) {
            throw new DDCException(ErrorMessage.SENDER_IS_NOT_ADDRESS_FORMAT);
        }

        checkAccountAddress(account);

        if (Strings.isEmpty(accountName)) {
            throw new DDCException(ErrorMessage.ACCOUNT_NAME_IS_EMPTY);
        }

        ArrayList<Object> arrayList = new ArrayList<>();
        arrayList.add(new Address(account));
        arrayList.add(new Utf8String(accountName));
        arrayList.add(new Utf8String(accountDID));

        ReqJsonRpcBean reqJsonRpcBean = assembleAuthorityTransaction(sender, AuthorityFunctions.ADD_ACCOUNT_BY_PLATFORM, arrayList);
        RespJsonRpcBean respJsonRpcBean = restTemplateUtil.sendPost(ConfigCache.get().getOpbGatewayAddress(), reqJsonRpcBean, RespJsonRpcBean.class);
        resultCheck(respJsonRpcBean);
        return (String) respJsonRpcBean.getResult();
    }


    /**
     * 平台方批量添加账户
     * @param sender 调用者
     * @param accounts 账户信息列表
     * @return 交易哈希
     * @throws Exception Exception
     */
    public String addBatchAccountByPlatform(String sender, Map<String,AccountInfo> accounts) throws Exception {
        checkSenderAddress(sender);

        if (accounts.size() == 0) {
            throw new DDCException(ErrorMessage.ACCOUNT_IS_EMPTY);
        }

        ArrayList<Object> accountList = new ArrayList<>();
        ArrayList<Object> accountNameList = new ArrayList<>();
        ArrayList<Object> accountDIDList = new ArrayList<>();

        accounts.forEach( (account,accountInfo) -> {
            checkAccountAddress(account);

            String accountName = accountInfo.getAccountName();
            String accountDID = accountInfo.getAccountDID();

            if (Strings.isEmpty(accountInfo.getAccountName())) {
                throw new DDCException(ErrorMessage.ACCOUNT_NAME_IS_EMPTY);
            }

            accountList.add(account);
            accountNameList.add(accountName);
            accountDIDList.add(accountDID);
        });

        ArrayList<Object> arrayList = new ArrayList<>();
        arrayList.add(accountList.stream().map(String::valueOf).collect(Collectors.joining(",")));
        arrayList.add(accountNameList.stream().map(String::valueOf).collect(Collectors.joining(",")));
        arrayList.add(accountDIDList.stream().map(String::valueOf).collect(Collectors.joining(",")));

        ReqJsonRpcBean reqJsonRpcBean = assembleAuthorityTransaction(sender, AuthorityFunctions.ADD_BATCH_ACCOUNT_BY_PLATFORM, arrayList);
        RespJsonRpcBean respJsonRpcBean = restTemplateUtil.sendPost(ConfigCache.get().getOpbGatewayAddress(), reqJsonRpcBean, RespJsonRpcBean.class);
        resultCheck(respJsonRpcBean);
        return (String) respJsonRpcBean.getResult();
    }

    /**
     * 平台方添加链账户开关设置
     * @param sender 调用者
     * @param isOpen 开关标识
     * @return 交易哈希
     * @throws Exception Exception
     */
    public String setSwitcherStateOfPlatform(String sender,boolean isOpen) throws Exception {
        checkSenderAddress(sender);

        ArrayList<Object> arrayList = new ArrayList<>();
        arrayList.add(isOpen);

        ReqJsonRpcBean reqJsonRpcBean = assembleAuthorityTransaction(sender, AuthorityFunctions.SET_SWITCHER_STATE_OF_PLATFORM, arrayList);
        RespJsonRpcBean respJsonRpcBean = restTemplateUtil.sendPost(ConfigCache.get().getOpbGatewayAddress(), reqJsonRpcBean, RespJsonRpcBean.class);
        resultCheck(respJsonRpcBean);
        return (String) respJsonRpcBean.getResult();
    }

    /**
     * 平台方添加链账户开关查询
     * @return 开关状态
     * @throws Exception Exception
     */
    public boolean switcherStateOfPlatform() throws Exception {
        ArrayList<Object> arrayList = new ArrayList<>();

        // 发送请求账户信息请求
        ReqJsonRpcBean reqJsonRpcBean = assembleAuthorityCallTransaction(AuthorityFunctions.SWITCHER_STATE_OF_PLATFORM, arrayList);
        RespJsonRpcBean respJsonRpcBean = restTemplateUtil.sendPost(ConfigCache.get().getOpbGatewayAddress(), reqJsonRpcBean, RespJsonRpcBean.class);
        resultCheck(respJsonRpcBean);

        String encodeParams = new ObjectMapper().writeValueAsString(reqJsonRpcBean.getParams().get(1));
        ReqCallRpcBean reqCallRpcBean = JSONObject.parseObject(encodeParams, ReqCallRpcBean.class);
        String encodedFunction = reqCallRpcBean.getData();

        String jsonResult = new ObjectMapper().writeValueAsString(respJsonRpcBean.getResult());
        RespCallRpcBean respCallRpcBean = JSONObject.parseObject(jsonResult, RespCallRpcBean.class);
        callResultCheck(respCallRpcBean);

        // 解析交易返回信息
        InputAndOutputResult inputAndOutputResult = AnalyzeChainInfoUtils.analyzeTransactionOutput(ConfigCache.get().getAuthorityLogicABI(), ConfigCache.get().getAuthorityLogicBIN(), encodedFunction, respCallRpcBean.getOutput());
        return Boolean.parseBoolean(inputAndOutputResult.getResult().get(0).getData().toString());

    }

    /**
     * 运营方批量添加账户
     * @param sender 调用者
     * @param accounts 账户信息列表
     * @return 交易哈希
     */
    public String  addBatchAccountByOperator(String sender,Map<String, AccountInfo> accounts) throws Exception {
        checkSenderAddress(sender);

        if (accounts.size() == 0) {
            throw new DDCException(ErrorMessage.ACCOUNT_IS_EMPTY);
        }

        ArrayList<Object> accountList = new ArrayList<>();
        ArrayList<Object> accountNameList = new ArrayList<>();
        ArrayList<Object> accountDIDList = new ArrayList<>();
        ArrayList<Object> leaderDIDList = new ArrayList<>();

        accounts.forEach( (account,accountInfo) -> {
            checkAccountAddress(account);

            String accountName = accountInfo.getAccountName();
            String accountDID = accountInfo.getAccountDID();
            String leaderDID = accountInfo.getLeaderDID();

            if (Strings.isEmpty(accountInfo.getAccountName())) {
                throw new DDCException(ErrorMessage.ACCOUNT_NAME_IS_EMPTY);
            }

            accountList.add(account);
            accountNameList.add(accountName);
            accountDIDList.add(accountDID);
            leaderDIDList.add(Strings.isEmpty(leaderDID) ? "" : leaderDID);

        });

        ArrayList<Object> arrayList = new ArrayList<>();
        arrayList.add(accountList.stream().map(String::valueOf).collect(Collectors.joining(",")));
        arrayList.add(accountNameList.stream().map(String::valueOf).collect(Collectors.joining(",")));
        arrayList.add(accountDIDList.stream().map(String::valueOf).collect(Collectors.joining(",")));
        arrayList.add(leaderDIDList.stream().map(String::valueOf).collect(Collectors.joining(",")));

        ReqJsonRpcBean reqJsonRpcBean = assembleAuthorityTransaction(sender, AuthorityFunctions.ADD_BATCH_ACCOUNT_BY_OPERATOR, arrayList);
        RespJsonRpcBean respJsonRpcBean = restTemplateUtil.sendPost(ConfigCache.get().getOpbGatewayAddress(), reqJsonRpcBean, RespJsonRpcBean.class);
        resultCheck(respJsonRpcBean);
        return (String) respJsonRpcBean.getResult();
    }

    /**
     * 运营方可以通过调用该方法直接对平台方或平台方的终端用户进行创建。
     *
     * @param sender    调用者地址
     * @param account   DDC链账户地址
     * @param accName   DDC账户对应的账户名称
     * @param accDID    DDC账户对应的DID信息
     * @param leaderDID 该普通账户对应的上级账户的DID
     * @return 返回交易哈希
     * @throws Exception
     */
    public String addAccountByOperator(String sender, String account, String accName, String accDID, String leaderDID) throws Exception {
        checkSenderAddress(sender);

        checkAccountAddress(account);

        if (Strings.isEmpty(accName)) {
            throw new DDCException(ErrorMessage.ACCOUNT_NAME_IS_EMPTY);
        }

        if (Strings.isEmpty(leaderDID)) {
            log.info("{} ,will add platform account", ErrorMessage.ACCOUNT_LEADER_DID_IS_EMPTY.getMessage());
            leaderDID = "";
        }

        ArrayList<Object> arrayList = new ArrayList<>();
        arrayList.add(new Address(account));
        arrayList.add(new Utf8String(accName));
        arrayList.add(new Utf8String(accDID));
        arrayList.add(new Utf8String(leaderDID));


        ReqJsonRpcBean reqJsonRpcBean = assembleAuthorityTransaction(sender, AuthorityFunctions.ADD_ACCOUNT_BY_OPERATOR, arrayList);
        RespJsonRpcBean respJsonRpcBean = restTemplateUtil.sendPost(ConfigCache.get().getOpbGatewayAddress(), reqJsonRpcBean, RespJsonRpcBean.class);
        resultCheck(respJsonRpcBean);
        return (String) respJsonRpcBean.getResult();
    }

    /**
     * 运营方或平台方通过该方法进行DDC账户信息的查询，上级角色可进行下级角色账户的操作。
     *
     * @param account DDC用户链账户地址
     * @return 返回DDC账户信息
     * @throws Exception Exception
     */
    public AccountInfo getAccount(String account) throws Exception {

        checkAccountAddress(account);

        ArrayList<Object> arrayList = new ArrayList<>();
        arrayList.add(account);

        // 发送请求账户信息请求
        ReqJsonRpcBean reqJsonRpcBean = assembleAuthorityCallTransaction(AuthorityFunctions.GET_ACCOUNT, arrayList);
        RespJsonRpcBean respJsonRpcBean = restTemplateUtil.sendPost(ConfigCache.get().getOpbGatewayAddress(), reqJsonRpcBean, RespJsonRpcBean.class);
        resultCheck(respJsonRpcBean);

        String encodeParams = new ObjectMapper().writeValueAsString(reqJsonRpcBean.getParams().get(1));
        ReqCallRpcBean reqCallRpcBean = JSONObject.parseObject(encodeParams, ReqCallRpcBean.class);
        String encodedFunction = reqCallRpcBean.getData();

        String jsonResult = new ObjectMapper().writeValueAsString(respJsonRpcBean.getResult());
        RespCallRpcBean respCallRpcBean = JSONObject.parseObject(jsonResult, RespCallRpcBean.class);
        callResultCheck(respCallRpcBean);

        // 解析交易返回信息
        InputAndOutputResult inputAndOutputResult = AnalyzeChainInfoUtils.analyzeTransactionOutput(ConfigCache.get().getAuthorityLogicABI(), ConfigCache.get().getAuthorityLogicBIN(), encodedFunction, respCallRpcBean.getOutput());

        AccountInfo accountInfo = new AccountInfo();
        accountInfo.setAccountDID(String.valueOf(inputAndOutputResult.getResult().get(0).getData()));
        accountInfo.setAccountName(String.valueOf(inputAndOutputResult.getResult().get(1).getData()));
        accountInfo.setLeaderDID(String.valueOf(inputAndOutputResult.getResult().get(3).getData()));
        accountInfo.setField(String.valueOf(inputAndOutputResult.getResult().get(6).getData()));
        String accountRole = String.valueOf(inputAndOutputResult.getResult().get(2).getData());
        if (accountRole != null && !accountRole.trim().isEmpty()) {
            accountInfo.setAccountRole(AccountRole.getByVal(Integer.parseInt(accountRole)));
        }
        String platformState = String.valueOf(inputAndOutputResult.getResult().get(4).getData());
        if (platformState != null && !platformState.trim().isEmpty()) {
            accountInfo.setPlatformState(AccountState.getByVal(Integer.parseInt(platformState)));
        }

        String operatorState = String.valueOf(inputAndOutputResult.getResult().get(5).getData());
        if (operatorState != null && !operatorState.trim().isEmpty()) {
            accountInfo.setOperatorState(AccountState.getByVal(Integer.parseInt(operatorState)));
        }

        return accountInfo;
    }

    /**
     * 跨平台授权链账户转移DDC
     *
     * @param sender 调用者
     * @param from 授权者
     * @param to 接收者
     * @param approved 授权标识
     * @return 交易哈希
     * @throws Exception Exception
     */
    public String crossPlatformApproval(String sender, String from, String to, boolean approved) throws Exception {
        checkSenderAddress(sender);

        checkFromAddress(from);

        checkToAddress(to);

        ArrayList<Object> arrayList = new ArrayList<>();
        arrayList.add(new Address(from));
        arrayList.add(new Address(to));
        arrayList.add(approved);

        ReqJsonRpcBean reqJsonRpcBean = assembleAuthorityTransaction(sender, AuthorityFunctions.CROSS_PLATFORM_APPROVAL, arrayList);
        RespJsonRpcBean respJsonRpcBean = restTemplateUtil.sendPost(ConfigCache.get().getOpbGatewayAddress(), reqJsonRpcBean, RespJsonRpcBean.class);
        resultCheck(respJsonRpcBean);
        return (String) respJsonRpcBean.getResult();
    }


    /**
     * 运营方或平台方通过该方法进行DDC账户信息状态的更改。
     *
     * @param sender  调用者地址
     * @param account DDC用户链账户地址
     * @param state   状态 ：Frozen - 冻结状态 ； Active - 活跃状态
     * @return 返回交易哈希
     * @throws Exception Exception
     */
    public String updateAccState(String sender, String account, AccountState state, boolean changePlatformState) throws Exception {
        checkSenderAddress(sender);

        checkAccountAddress(account);

        if (state == null) {
            throw new DDCException(ErrorMessage.ACCOUNT_STATUS_IS_EMPTY);
        }

        ArrayList<Object> arrayList = new ArrayList<>();
        arrayList.add(new Address(account));
        // arrayList.add(new Int(new BigInteger(state.getStatus().toString())));
        arrayList.add(state.getStatus());
        arrayList.add(changePlatformState);

        ReqJsonRpcBean reqJsonRpcBean = assembleAuthorityTransaction(sender, AuthorityFunctions.UPDATE_ACCOUNT_STATE, arrayList);
        RespJsonRpcBean respJsonRpcBean = restTemplateUtil.sendPost(ConfigCache.get().getOpbGatewayAddress(), reqJsonRpcBean, RespJsonRpcBean.class);
        resultCheck(respJsonRpcBean);
        return (String) respJsonRpcBean.getResult();
    }

    /**
     * 同步平台方DID
     * @param sender 调用者
     * @param dids 平台方did集合
     * @return 交易哈希
     */
    public String syncPlatformDID(String sender,List<String> dids) throws Exception {
        checkSenderAddress(sender);

        if (dids.size() == 0) {
            throw new DDCException(ErrorMessage.DID_IS_EMPTY);
        }

        for (int i = 0; i < dids.size(); i++) {
            if (Strings.isEmpty(dids.get(i))) {
                throw new DDCException(ErrorMessage.DID_IS_EMPTY);
            }
        }

        ArrayList<Object> arrayList = new ArrayList<>();
        arrayList.add(dids.stream().map(String::valueOf).collect(Collectors.joining(",")));

        ReqJsonRpcBean reqJsonRpcBean = assembleAuthorityTransaction(sender, AuthorityFunctions.SYNC_PLATFORM_DID, arrayList);
        RespJsonRpcBean respJsonRpcBean = restTemplateUtil.sendPost(ConfigCache.get().getOpbGatewayAddress(), reqJsonRpcBean, RespJsonRpcBean.class);
        resultCheck(respJsonRpcBean);
        return (String) respJsonRpcBean.getResult();
    }

    /**
     * 启用批量设置
     * @param sender 调用者
     * @param isOpen 开关标识
     * @return 交易哈希
     */
    public String setSwitcherStateOfBatch(String sender,Boolean isOpen) throws Exception {
        checkSenderAddress(sender);

        ArrayList<Object> arrayList = new ArrayList<>();
        arrayList.add(isOpen);

        ReqJsonRpcBean reqJsonRpcBean = assembleAuthorityTransaction(sender, AuthorityFunctions.SET_SWITCHER_STATE_OF_BATCH, arrayList);
        RespJsonRpcBean respJsonRpcBean = restTemplateUtil.sendPost(ConfigCache.get().getOpbGatewayAddress(), reqJsonRpcBean, RespJsonRpcBean.class);
        resultCheck(respJsonRpcBean);
        return (String) respJsonRpcBean.getResult();
    }

    /**
     * 查询合约中角色具有调用权限的方法列表
     *
     * @param ddcAddr 合约地址
     * @param role 角色
     * @return
     * @throws Exception
     */
    public ArrayList<String> getFunctions(String ddcAddr, AccountRole role) throws Exception {

        checkDDCAddress(ddcAddr);

        if (role == null) {
            throw new DDCException(ErrorMessage.ROLE_IS_EMPTY);
        }

        ArrayList<Object> arrayList = new ArrayList<>();
        arrayList.add(role.getRole());
        arrayList.add(new Address(ddcAddr));

        ReqJsonRpcBean reqJsonRpcBean = assembleAuthorityCallTransaction(AuthorityFunctions.GET_FUNCTIONS, arrayList);
        RespJsonRpcBean respJsonRpcBean = restTemplateUtil.sendPost(ConfigCache.get().getOpbGatewayAddress(), reqJsonRpcBean, RespJsonRpcBean.class);
        resultCheck(respJsonRpcBean);

        String encodeParams = new ObjectMapper().writeValueAsString(reqJsonRpcBean.getParams().get(1));
        ReqCallRpcBean reqCallRpcBean = JSONObject.parseObject(encodeParams, ReqCallRpcBean.class);
        String encodedFunction = reqCallRpcBean.getData();
        String jsonResult = new ObjectMapper().writeValueAsString(respJsonRpcBean.getResult());
        RespCallRpcBean respCallRpcBean = JSONObject.parseObject(jsonResult, RespCallRpcBean.class);
        callResultCheck(respCallRpcBean);

        InputAndOutputResult inputAndOutputResult = AnalyzeChainInfoUtils.analyzeTransactionOutput(ConfigCache.get().getAuthorityLogicABI(), ConfigCache.get().getAuthorityLogicBIN(), encodedFunction, respCallRpcBean.getOutput());

        ArrayList<String> list = new ArrayList<>();
        List<ResultEntity> functionList = inputAndOutputResult.getResult();
        if (functionList != null) {
            for (ResultEntity resultEntity : functionList) {

                ArrayList<Bytes4> dataList = (ArrayList<Bytes4>) resultEntity.getTypeObject().getValue();
                dataList.forEach(data -> {
                    list.add("0x" + Hex.toHexString(data.getValue()));
                });

            }
        }
        return list;
    }

    /**
     * 删除角色对方法的调用权限
     *
     * @param sender  调用者地址
     * @param ddcAddr 合约地址
     * @param role    角色
     * @param sig     签名
     * @return
     * @throws Exception
     */
    public String delFunction(String sender, String ddcAddr, AccountRole role, String sig) throws Exception {
        checkSenderAddress(sender);

        checkDDCAddress(ddcAddr);

        if (role == null) {
            throw new DDCException(ErrorMessage.ROLE_IS_EMPTY);
        }

        checkSig(sig);

        ArrayList<Object> arrayList = new ArrayList<>();
        arrayList.add(role.getRole());
        arrayList.add(new Address(ddcAddr));
        arrayList.add(sig);

        ReqJsonRpcBean reqJsonRpcBean = assembleAuthorityTransaction(sender, AuthorityFunctions.DEL_FUNCTION, arrayList);
        RespJsonRpcBean respJsonRpcBean = restTemplateUtil.sendPost(ConfigCache.get().getOpbGatewayAddress(), reqJsonRpcBean, RespJsonRpcBean.class);
        resultCheck(respJsonRpcBean);

        return (String) respJsonRpcBean.getResult();
    }

    /**
     * 添加角色对方法的调用权限
     *
     * @param sender  调用者地址
     * @param ddcAddr 合约地址
     * @param role    角色
     * @param sig     签名
     * @return
     * @throws Exception
     */
    public String addFunction(String sender, String ddcAddr, AccountRole role, String sig) throws Exception {
        checkSenderAddress(sender);

        checkDDCAddress(ddcAddr);

        if (role == null) {
            throw new DDCException(ErrorMessage.ROLE_IS_EMPTY);
        }

        checkSig(sig);

        ArrayList<Object> arrayList = new ArrayList<>();
        arrayList.add(role.getRole());
        arrayList.add(new Address(ddcAddr));
        arrayList.add(sig);

        ReqJsonRpcBean reqJsonRpcBean = assembleAuthorityTransaction(sender, AuthorityFunctions.ADD_FUNCTION, arrayList);
        RespJsonRpcBean respJsonRpcBean = restTemplateUtil.sendPost(ConfigCache.get().getOpbGatewayAddress(), reqJsonRpcBean, RespJsonRpcBean.class);
        resultCheck(respJsonRpcBean);

        return (String) respJsonRpcBean.getResult();
    }


    private ReqJsonRpcBean assembleAuthorityTransaction(String sender, String functionName, ArrayList<Object> params) throws Exception {
        return assembleTransaction(sender, getBlockNumber(), ConfigCache.get().getAuthorityLogicABI(), ConfigCache.get().getAuthorityLogicAddress(), functionName, params);
    }

    private ReqJsonRpcBean assembleAuthorityCallTransaction(String functionName, ArrayList<Object> params) throws Exception {
        return assembleTransaction(OneAddress, new BigInteger("0"), ConfigCache.get().getAuthorityLogicABI(), ConfigCache.get().getAuthorityLogicAddress(), functionName, params);
    }

}
