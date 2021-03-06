package com.reddate.ddc.service;

import com.reddate.ddc.config.ConfigCache;
import com.reddate.ddc.dto.ddc.AccountInfo;
import com.reddate.ddc.dto.ddc.AccountRole;
import com.reddate.ddc.dto.ddc.AccountState;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@Slf4j
public class AuthorityServiceTest extends BaseServiceTest{

    String abi = ConfigCache.get().getAuthorityLogicABI();
    String bin = ConfigCache.get().getAuthorityLogicBIN();

    @Test
    public void addPlatform() throws Exception {
        AuthorityService authorityService = getAuthorityService();

        String account = "0x5c5101afe03b416b9735f40ddc3ba7b0c354a5a0";
        String accountName = "test platform";
        String accountDID = "did:bsn:47YheHZbT7h1kJ55Q9vZs7brqDg8";

        String txHash = authorityService.addAccountByOperator(consumerAddress,account, accountName, accountDID, null);
        assertNotNull(txHash);
        log.info(analyzeRecepit(txHash,abi,bin));
    }

    @Test
    public void addConsumer() throws Exception {
        AuthorityService authorityService = getAuthorityService();

        String account = "0xb0031Aa7725A6828BcCE4F0b90cFE451C31c1e64";
//		String account = Keys.getAddress(new BigInteger("12056570804324336128027346291352825124257919156276624237554265998690345715407156125193589947291805854067057250100482437816040545905436874737813956607649510"));
        String accountName = "test user account1";
        String accountDID = "did:bsn:47YheHZbT7h1kJ55Q9vZs7brq122";
        String leaderDID = "did:bsn:47YheHZbT7h1kJ55Q9vZs7brqDg8";

        String txHash = authorityService.addAccountByOperator(consumerAddress,account, accountName, accountDID, leaderDID);
        assertNotNull(txHash);
        log.info(analyzeRecepit(txHash,abi,bin));
    }

    @Test
    void crossPlatformApproval() throws Exception {
        AuthorityService authorityService = getAuthorityService();
        String from = "0xb0031Aa7725A6828BcCE4F0b90cFE451C31c1e64";
        String to = "0x10031Aa7725A6828BcCE4F0b90cFE451C31c1e61";

        String txHash = authorityService.crossPlatformApproval(consumerAddress,from, to, true);
        assertNotNull(txHash);
        log.info(analyzeRecepit(txHash,abi,bin));
    }

    @Test
    public void getAccount() throws Exception {
        AuthorityService authorityService = getAuthorityService();

        //AccountInfo accountInfo = authorityService.getAccount("0x179319b482320c74bE043bf0fb3F00411ca12F8d");
//		AccountInfo accountInfo = authorityService.getAccount("4bab66900062c2b13604324f572fafed28234f0a");
        AccountInfo accountInfo = authorityService.getAccount("0x81072375a506581CADBd90734Bd00A20CdDbE48b");
//		AccountInfo accountInfo = authorityService.getAccount("0x522bc3e4e29276A13f7b7BE9D404961826a82b11");
//		AccountInfo accountInfo = authorityService.getAccount("4bab66900062c2b13604324f572fafed28234f0a");
        // AccountInfo accountInfo = authorityService.getAccount("39db18cb303bce407bded5b0c082c3f193321374");
        System.out.println(accountInfo);
        assertNotNull(accountInfo);
    }


    @Test
    public void updateAccState() throws Exception {
        AuthorityService authorityService = getAuthorityService();

        // String account = Keys.getAddress(new BigInteger("10411698110993959739535609003328767528005678182467896878050524806097812542225230327763618090295889890389743624855091682652783845527766539103610648004292062"));
        String account = "0xb0031Aa7725A6828BcCE4F0b90cFE451C31c1e63";

        String txHash = authorityService.updateAccState(consumerAddress,account, AccountState.Active, false);
        assertNotNull(txHash);
        log.info(analyzeRecepit(txHash,abi,bin));
    }

    @Test
    public void getFunctions() throws Exception {
        AuthorityService authorityService = getAuthorityService();
        List<String> sigList = authorityService.getFunctions(ConfigCache.get().getDdc721Address(), AccountRole.Consumer);
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

}
