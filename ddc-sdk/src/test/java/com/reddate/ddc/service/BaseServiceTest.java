package com.reddate.ddc.service;

import com.reddate.ddc.DDCSdkClient;
import com.reddate.ddc.config.ConfigCache;
import com.reddate.ddc.dto.taianchain.TransactionRecepitBean;
import com.reddate.ddc.listener.Secp256K1SignEventListener;
import com.reddate.ddc.util.AnalyzeChainInfoUtils;
import lombok.extern.slf4j.Slf4j;
import org.fisco.bcos.web3j.crypto.gm.sm2.util.encoders.Hex;
import org.fisco.bcos.web3j.protocol.exceptions.TransactionException;
import org.fisco.bcos.web3j.tx.txdecode.BaseException;
import org.fisco.bcos.web3j.tx.txdecode.InputAndOutputResult;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;

@Slf4j
class BaseServiceTest {


    //0xb0031Aa7725A6828BcCE4F0b90cFE451C31c1e63
    static String consumerAddress = "0xb0031Aa7725A6828BcCE4F0b90cFE451C31c1e63";
    static String consumer = "-----BEGIN PRIVATE KEY-----\n" +
            "MIGEAgEAMBAGByqGSM49AgEGBSuBBAAKBG0wawIBAQQgEWL4mAyD0V4cKcZ+RXS+\n" +
            "Y0b/Wt3WYOuHNynQQwCaGPGhRANCAAQojPfT83xRrijQNk6CXq1/w61/ZU5GC6CE\n" +
            "BTq8PEeUyqngCJCN0gkfRU1IEmusAsIGJb3ff2cQRvYTBqcismv1\n" +
            "-----END PRIVATE KEY-----\n";


    static String publicKey = "-----BEGIN PUBLIC KEY-----\n" +
            "MFYwEAYHKoZIzj0CAQYFK4EEAAoDQgAEjRHf7EbOKvUwRJW/kn4N6Vmf++n/gBu0\n" +
            "WEBUzovj+TAxwvgB26tCfoqk9X2gTdjwwKh6o/hvtx66EDB9GlzgTA==\n" +
            "-----END PUBLIC KEY-----";


    BaseService baseService = new BaseService();
    static DDCSdkClient ddcSdkClient;
    static {

        ddcSdkClient = new DDCSdkClient("https://opbningxia.bsngate.com:18602/api/projectId/rpc");

        Secp256K1SignEventListener signEventListener = null;
        try {
            // 设置签名使用的公私钥
            signEventListener = new Secp256K1SignEventListener(consumer, publicKey);
        } catch (Exception e) {
            e.printStackTrace();
        }
        ddcSdkClient.registerSignListener(signEventListener);
    }

    // region 获取SDK实例
    public AuthorityService getAuthorityService() {
        return ddcSdkClient.getAuthorityService();
    }

    public ChargeService getChargeService() {
        return ddcSdkClient.getChargeService();
    }

    public DDC721Service getDDC721Service() {
        return ddcSdkClient.getDDC721Service();
    }

    public DDC1155Service getDDC1155Service() {
        return ddcSdkClient.getDDC1155Service();
    }
    //endregion

    /**
     * 解析交易回执
     * @param tx
     * @param abi
     * @param bin
     * @return
     * @throws InterruptedException
     * @throws BaseException
     * @throws TransactionException
     */
    public String analyzeRecepit(String tx,String abi,String bin) throws InterruptedException, BaseException, TransactionException {
        TransactionRecepitBean transactionRecepitBean = null;
        for (int i = 0; i < 20; i++) {
            log.info("times: " + i);
            transactionRecepitBean = getAuthorityService().getTransactionRecepit(tx);
            if (transactionRecepitBean != null) {
                break;
            }
            Thread.sleep(500);
        }
        log.info(transactionRecepitBean.toString());
        if (transactionRecepitBean.getStatus().equals("0x0")) {
            InputAndOutputResult inputAndOutputResult = AnalyzeChainInfoUtils.analyzeTransactionOutput(abi, bin, transactionRecepitBean.getInput(), transactionRecepitBean.getOutput());
            return inputAndOutputResult.toString();
        } else {
            return new String(Hex.decode(transactionRecepitBean.getOutput().substring(2).getBytes(StandardCharsets.UTF_8)));
        }
    }


    @Test
    void getBlockNumber() {
        System.out.println(baseService.getBlockNumber());
    }

    @Test
    void getTransactionRecepit() throws InterruptedException, BaseException, TransactionException {
        TransactionRecepitBean transactionRecepitBean = baseService.getTransactionRecepit("0x318e2d077722ffc78bfc2efa050ca412425316ba7e0504b47de204687a0b1e2b");
        log.info(transactionRecepitBean.toString());
        InputAndOutputResult inputAndOutputResult = AnalyzeChainInfoUtils.analyzeTransactionOutput(ConfigCache.get().getAuthorityLogicABI(), ConfigCache.get().getAuthorityLogicBIN(), transactionRecepitBean.getInput(), transactionRecepitBean.getOutput());
        log.info(inputAndOutputResult.toString());
    }

}