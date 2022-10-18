package com.reddate.taianddc.service;

import com.reddate.taianddc.config.ConfigCache;
import com.reddate.taianddc.dto.ddc.AccountInfo;
import com.reddate.taianddc.dto.ddc.AccountRole;
import com.reddate.taianddc.dto.ddc.AccountState;
import lombok.extern.slf4j.Slf4j;
import org.fisco.bcos.web3j.protocol.exceptions.TransactionException;
import org.fisco.bcos.web3j.tx.txdecode.BaseException;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@Slf4j
public class AuthorityServiceTest extends BaseServiceTest{

    String abi = ConfigCache.get().getAuthorityLogicABI();
    String bin = ConfigCache.get().getAuthorityLogicBIN();

    @Test
    void analyzeTransaction() throws BaseException, TransactionException, InterruptedException {
        String tx = "0x8c96fce2af5df286ec0a3ee8b9eec5bc989f7f014aee44fc0c7e84b19b46a3b4";
        String result = analyzeRecepit(tx,abi,bin);
        System.out.println(result);
    }

    @Test
    public void addAccountByOperator() throws Exception {
        AuthorityService authorityService = getAuthorityService();

        String account = "0x9a5238a5c3a1027a318e344a764b2ddcb01626ef";
        String accountName = "ttt";
        String accountDID = "ttt";
        String leaderDID = "";

        String txHash = authorityService.addAccountByOperator(operatorAddress,account, accountName, accountDID, leaderDID);
        assertNotNull(txHash);
        log.info(analyzeRecepit(txHash,abi,bin));
    }

    @Test
    void addBatchAccountByOperator() throws Exception {
        AuthorityService authorityService = getAuthorityService();

        Map<String,AccountInfo> map = new HashMap<>();
        AccountInfo accountInfo = new AccountInfo();
        accountInfo.setAccountName("ttt");
        accountInfo.setAccountDID("ttt");
        map.put("0x9a5238a5c3a1027a318e344a764b2ddcb016a6cd",accountInfo);

        AccountInfo accountInfo1 = new AccountInfo();
        accountInfo1.setAccountName("ttt2");
        accountInfo1.setAccountDID("ttt2");
        map.put("0x9a5238a5c3a1027a318e344a764b2bdcb01626de",accountInfo1);

        String txHash = authorityService.addBatchAccountByOperator(operatorAddress,map);
        assertNotNull(txHash);
        log.info(analyzeRecepit(txHash,abi,bin));
    }

    @Test
    void crossPlatformApproval() throws Exception {
        AuthorityService authorityService = getAuthorityService();
        String from = "0xb0031Aa7725A6828BcCE4F0b90cFE451C31c1e64";
        String to = "0x10031Aa7725A6828BcCE4F0b90cFE451C31c1e61";

        String txHash = authorityService.crossPlatformApproval(operatorAddress,platformAddress, consumerAddress, true);
        assertNotNull(txHash);
        log.info(analyzeRecepit(txHash,abi,bin));
    }

    @Test
    public void getAccount() throws Exception {
        AuthorityService authorityService = getAuthorityService();
        //普通用户
//        AccountInfo accountInfo = authorityService.getAccount(consumerAddress);
        //普通用户
//        AccountInfo accountInfo = authorityService.getAccount(testConsumerAddress);
        //普通用户
        AccountInfo accountInfo = authorityService.getAccount(operatorAddress);
        //平台方
//        AccountInfo accountInfo = authorityService.getAccount(platformAddress);
        //测试 平台方1
//        AccountInfo accountInfo = authorityService.getAccount(testPlatformAddress1);
        //测试 平台方2
//        AccountInfo accountInfo = authorityService.getAccount(testPlatformAddress2);
        //运营方
//        AccountInfo accountInfo = authorityService.getAccount(operatorAddress);
        System.out.println(accountInfo);
        assertNotNull(accountInfo);
    }

    @Test
    public void updateAccState() throws Exception {
        AuthorityService authorityService = getAuthorityService();

        // String account = Keys.getAddress(new BigInteger("10411698110993959739535609003328767528005678182467896878050524806097812542225230327763618090295889890389743624855091682652783845527766539103610648004292062"));
        String account = "0x60aefbce113f95efaaab94b34a2c8780d07d3f86";

        String txHash = authorityService.updateAccState(operatorAddress,testPlatformAddress1, AccountState.Active, false);
        assertNotNull(txHash);
        log.info(analyzeRecepit(txHash,abi,bin));
    }

    @Test
    public void getFunctions() throws Exception {
        AuthorityService authorityService = getAuthorityService();
        List<String> sigList = authorityService.getFunctions(ConfigCache.get().getDdc721Address(), AccountRole.Operator);
        assertNotNull(sigList);
        for (int i = 0; i < sigList.size(); i++) {
            System.out.println(sigList.get(i));
        }

    }

    @Test
    public void delFunction() throws Exception {
        AuthorityService authorityService = getAuthorityService();
        String txHash = authorityService.delFunction(consumerAddress,ConfigCache.get().getDdc721Address(), AccountRole.PlatformManager, "0x4e1273f4");
        assertNotNull(txHash);
        log.info(analyzeRecepit(txHash,abi,bin));
    }

    @Test
    public void add721Function() {
        AuthorityService authorityService = getAuthorityService();

        ArrayList<String> sigList = new ArrayList<>();
        // 721 0x5a7ac17A046003E3048aB749E60EB71988393104
        String mint = "0xd0def521";
        String safeMint = "0xf6dda936";
        String approve = "0x095ea7b3";
        String setApprovalForAll = "0xa22cb465";
        String supportsInterface = "0x01ffc9a7";
        String safeTransferFrom = "0xb88d4fde";
        String transferFrom = "0x23b872dd";
        String burn = "0x42966c68";
        String freeeze = "0xd7a78db8";
        String unFreeze = "0xd302b0dc";
        String setURI   = "0x862440e2";

        sigList.add(mint);
        sigList.add(safeMint);
        sigList.add(freeeze);
        sigList.add(unFreeze);
        sigList.add(approve);
        sigList.add(setApprovalForAll);
        sigList.add(safeTransferFrom);
        sigList.add(transferFrom);
        sigList.add(burn);
        sigList.add(setURI);

        sigList.forEach(sig -> {
            String txHash = null;
            try {
                txHash = authorityService.addFunction(consumerAddress,ConfigCache.get().getDdc721Address(), AccountRole.Operator, sig);
                assertNotNull(txHash);
                log.info(analyzeRecepit(txHash,abi,bin));

            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        sigList.forEach(sig -> {
            String txHash = null;
            try {
                txHash = authorityService.addFunction(consumerAddress,ConfigCache.get().getDdc721Address(), AccountRole.PlatformManager, sig);
                assertNotNull(txHash);
                log.info(analyzeRecepit(txHash,abi,bin));

            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        sigList.forEach(sig -> {
            String txHash = null;
            try {
                txHash = authorityService.addFunction(consumerAddress,ConfigCache.get().getDdc721Address(), AccountRole.Consumer, sig);
                assertNotNull(txHash);
                log.info(analyzeRecepit(txHash,abi,bin));

            } catch (Exception e) {
                e.printStackTrace();
            }
        });

    }

    @Test
    public void add1155Function() {
        AuthorityService authorityService = getAuthorityService();

        ArrayList<String> sigList = new ArrayList<>();

        // 1155 0x727CdAD1C0324E8f236fa5A22F7f13174FF5F9C3
        String safeMint              = "0xb55bc617";
        String safeMintBatch         = "0x63570355";
        String setApprovalForAll     = "0xa22cb465";
        String isApprovedForAll      = "0xe985e9c5";
        String safeTransferFrom      = "0xf242432a";
        String safeBatchTransferFrom = "0x2eb2c2d6";
        String freeze                = "0xd7a78db8";
        String unFreeze              = "0xd302b0dc";
        String burn                  = "0x9dc29fac";
        String burnBatch             = "0xb2dc5dc3";
        String balanceOf             = "0x00fdd58e";
        String balanceOfBatch        = "0x4e1273f4";
        String ddcURI                = "0x293ec97c";
        String setURI                = "0x685e8247";

        sigList.add(safeMint);
        sigList.add(safeMintBatch);
        sigList.add(setApprovalForAll);
        sigList.add(isApprovedForAll);
        sigList.add(safeTransferFrom);
        sigList.add(safeBatchTransferFrom);
        sigList.add(freeze);
        sigList.add(unFreeze);
        sigList.add(burn);
        sigList.add(burnBatch);
        sigList.add(balanceOf);
        sigList.add(balanceOfBatch);
        sigList.add(ddcURI);
        sigList.add(setURI);

        sigList.forEach( sig -> {
            String txHash = null;
            try {
                txHash = authorityService.addFunction(consumerAddress,ConfigCache.get().getDdc1155Address(), AccountRole.Operator, sig);
                assertNotNull(txHash);
                log.info(analyzeRecepit(txHash,abi,bin));
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        sigList.forEach( sig -> {
            String txHash = null;
            try {
                txHash = authorityService.addFunction(consumerAddress,ConfigCache.get().getDdc1155Address(), AccountRole.PlatformManager, sig);
                assertNotNull(txHash);
                log.info(analyzeRecepit(txHash,abi,bin));
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        sigList.forEach( sig -> {
            String txHash = null;
            try {
                txHash = authorityService.addFunction(consumerAddress,ConfigCache.get().getDdc1155Address(), AccountRole.Consumer, sig);
                assertNotNull(txHash);
                log.info(analyzeRecepit(txHash,abi,bin));
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

    }

    @Test
    void addAccountByPlatform() throws Exception {
        AuthorityService authorityService = getAuthorityService();

        String account = "0x847f92c871dbc19273480b1f3fa3dcb14d314a1d";
        String accountName = "xx3";
        String accountDID = "xx3";
        String txHash = authorityService.addAccountByPlatform(platformAddress,account,accountName,accountDID);
        assertNotNull(txHash);
        log.info(analyzeRecepit(txHash,abi,bin));
    }

    @Test
    void addBatchAccountByPlatform() throws Exception {

        Map<String,AccountInfo> map = new HashMap<>();
        map.put("0x00f368a33cdfaa06148b065b748300e7f1e73d97",new AccountInfo("addBatchAccountByPlatform1","addBatchAccountByPlatform1",null,null,null,null,null));
        map.put("0xdf9f2963a9ebda5451c698599f4972b812907b05",new AccountInfo("addBatchAccountByPlatform2","addBatchAccountByPlatform2",null,null,null,null,null));

        String txHash = getAuthorityService().addBatchAccountByPlatform(platformAddress,map);
        assertNotNull(txHash);
        log.info(analyzeRecepit(txHash,abi,bin));
    }

    @Test
    void setSwitcherStateOfPlatform() throws Exception {
        String txHash = getAuthorityService().setSwitcherStateOfPlatform(operatorAddress,true);
        assertNotNull(txHash);
        log.info(analyzeRecepit(txHash,abi,bin));
    }

    @Test
    void switcherStateOfPlatform() throws Exception {
        boolean switchState = getAuthorityService().switcherStateOfPlatform();
        assertNotNull(switchState);
        log.info("switchStateOfPlatform {}",switchState);
    }

    @Test
    void syncPlatformDID() throws Exception {
        List<String> dids = new ArrayList<>();
        dids.add("testPlatformAddress2");
        String txHash = getAuthorityService().syncPlatformDID(operatorAddress,dids);
        assertNotNull(txHash);
        log.info(analyzeRecepit(txHash,abi,bin));
    }

    @Test
    void setSwitcherStateOfBatch() throws Exception {
        String txHash = getAuthorityService().setSwitcherStateOfBatch(operatorAddress,false);
        assertNotNull(txHash);
        log.info(analyzeRecepit(txHash,abi,bin));
    }

}
