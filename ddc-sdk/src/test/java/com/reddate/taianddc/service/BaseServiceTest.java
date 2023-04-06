package com.reddate.taianddc.service;

import com.reddate.taianddc.DDCSdkClient;
import com.reddate.taianddc.config.ConfigCache;
import com.reddate.taianddc.dto.taianchain.TransactionInfoBean;
import com.reddate.taianddc.dto.taianchain.TransactionRecepitBean;
import com.reddate.taianddc.listener.Secp256K1SignEventListener;
import com.reddate.taianddc.util.AnalyzeChainInfoUtils;
import com.reddate.taianddc.util.crypto.Secp256K1Handle;
import lombok.extern.slf4j.Slf4j;
import org.fisco.bcos.web3j.crypto.gm.sm2.util.encoders.Hex;
import org.fisco.bcos.web3j.protocol.exceptions.TransactionException;
import org.fisco.bcos.web3j.tx.txdecode.BaseException;
import org.fisco.bcos.web3j.tx.txdecode.InputAndOutputResult;
import org.junit.jupiter.api.Test;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.PublicKey;
import java.security.spec.RSAPublicKeySpec;
import java.util.Base64;

@Slf4j
class BaseServiceTest {


    //0xb0031Aa7725A6828BcCE4F0b90cFE451C31c1e63
    static String consumerAddress = "0x836595c71b84be3e6fb4e840acdcb5ce70aee893";
    static String consumer = "-----BEGIN PRIVATE KEY-----\n" +
            "MEcCAQAwEAYHKoZIzj0CAQYFK4EEAAoEMDAuAgEBBCDdkCHeZC135LVxVMnL7teT\n" +
            "ofG7zuJ5dx0IxZ4H9HlvrqAHBgUrgQQACg==\n" +
            "-----END PRIVATE KEY-----";

    static String testConsumerAddress = "0x60aefbce113f95efaaab94b34a2c8780d07d3f86";
    static String testConsumer = "-----BEGIN PRIVATE KEY-----\n" +
            "MEcCAQAwEAYHKoZIzj0CAQYFK4EEAAoEMDAuAgEBBCCJ6g53QmHGlrYgKPdgKyH3\n" +
            "Fqz+/uUAlqP8ijnIp4iEbqAHBgUrgQQACg==\n" +
            "-----END PRIVATE KEY-----";

    static String testConsumerAddress2 = "0x04990919113fc98a11640fd1e371f0cdb8755a4d";
    static String testConsumer2 = "-----BEGIN PRIVATE KEY-----\n" +
            "MEcCAQAwEAYHKoZIzj0CAQYFK4EEAAoEMDAuAgEBBCB8YBtGzD4LmXw15RrB/CSo\n" +
            "6s914Vlms/9zUikISt10NKAHBgUrgQQACg==\n" +
            "-----END PRIVATE KEY-----";

    static String platformAddress = "0x5c5101afe03b416b9735f40ddc3ba7b0c354a5a0";
    static String platform = "-----BEGIN PRIVATE KEY-----\n" +
            "MEcCAQAwEAYHKoZIzj0CAQYFK4EEAAoEMDAuAgEBBCBN1l/N5JjChM2fKUduLpvA\n" +
            "H90b2293jZImJFsul5CKqaAHBgUrgQQACg==\n" +
            "-----END PRIVATE KEY-----";

    static String testPlatformAddress1 = "0x9a5238a5c3a1027a318e344a764b2ddcb01626eb";
    static String testPlatform1 = "-----BEGIN PRIVATE KEY-----\n" +
            "MEcCAQAwEAYHKoZIzj0CAQYFK4EEAAoEMDAuAgEBBCDD+eEgRS30HCCewavUbz1J\n" +
            "rWDfp/XNh8I5KOHZO80R5qAHBgUrgQQACg==\n" +
            "-----END PRIVATE KEY-----";

    static String testPlatformAddress2 = "0xb5c70b0b7bbd94e2356ee8c1f8b1960bddb1bcde";
    static String testPlatform2 = "-----BEGIN PRIVATE KEY-----\n" +
            "MEcCAQAwEAYHKoZIzj0CAQYFK4EEAAoEMDAuAgEBBCA0f6W2QoLZraXUyfUo7/mB\n" +
            "kklbdTgKgjzq6csgHhYZZqAHBgUrgQQACg==\n" +
            "-----END PRIVATE KEY-----";

    static String operatorAddress = "0x81072375a506581cadbd90734bd00a20cddbe48b";
    static String operator = "-----BEGIN PRIVATE KEY-----\n" +
            "MIGEAgEAMBAGByqGSM49AgEGBSuBBAAKBG0wawIBAQQgseEExMPXTcSpExzejzYZ\n" +
            "wcLWikQtoZ3BRhWergMR2LGhRANCAATCEQFr8dEbUI6ZYChl4+pE3UopdpWknZiv\n" +
            "rK7WWNymFHQQyIN15nsq5ZZat8G+iPNLtCdRSaU3h769ObArmgvB\n" +
            "-----END PRIVATE KEY-----\n";


    static String publicKey = "-----BEGIN PUBLIC KEY-----\n" +
            "MFYwEAYHKoZIzj0CAQYFK4EEAAoDQgAEjRHf7EbOKvUwRJW/kn4N6Vmf++n/gBu0\n" +
            "WEBUzovj+TAxwvgB26tCfoqk9X2gTdjwwKh6o/hvtx66EDB9GlzgTA==\n" +
            "-----END PUBLIC KEY-----";

    // 0x81072375a506581CADBd90734Bd00A20CdDbE48b
    public static String originPrivateKey = "0xb1e104c4c3d74dc4a9131cde8f3619c1c2d68a442da19dc146159eae0311d8b1";
    public static String ecPrivateKeyPem = "-----BEGIN PRIVATE KEY-----\n" +
            "MEcCAQAwEAYHKoZIzj0CAQYFK4EEAAoEMDAuAgEBBCCx4QTEw9dNxKkTHN6PNhnB\n" +
            "wtaKRC2hncFGFZ6uAxHYsaAHBgUrgCCACg==\n" +
            "-----END PRIVATE KEY-----";

    public static String ecPublicKeyPem = "-----BEGIN PUBLIC KEY-----\n" +
            "MFYwEAYHKoZIzj0CAQYFK4EEAAoDQgAEwhEBa/HRG1COmWAoZePqRN1KKXaVpJ2Y\n" +
            "r6yu1ljcphR0EMiDdeZ7KuWWWrfBvojzSCCCUUmlN4e+vTmwK5oLwQ==\n" +
            "-----END PUBLIC KEY-----";

    BaseService baseService = new BaseService();
    static DDCSdkClient ddcSdkClient;
    static {

        ddcSdkClient = new DDCSdkClient("https://opbningxia.bsngate.com:18602/api/[project_id]/rpc/");
        Secp256K1SignEventListener signEventListener = null;
        try {
            // 设置签名使用的公私钥
            signEventListener = new Secp256K1SignEventListener(platform, publicKey);
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

//    public CrossChainService getCrossChainService() {
//        return ddcSdkClient.getCrossChainService();
//    }

    public DDC1155Service getDDC1155Service() {
        return ddcSdkClient.getDDC1155Service();
    }

    public OpbCrossChainService getOpbCrossChainService() {
        return ddcSdkClient.getOpbCrossChainService();
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
            transactionRecepitBean = getAuthorityService().getTransactionReceipt(tx);
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
            return parseErrorMsg(transactionRecepitBean.getOutput());
        }
    }

    /**
     * 0x08c379a0                                                         // Error(string) 的函数选择器
     * 0x0000000000000000000000000000000000000000000000000000000000000020 // 数据的偏移量（32）
     * 0x000000000000000000000000000000000000000000000000000000000000001a // 字符串长度（26）
     * 0x4e6f7420656e6f7567682045746865722070726f76696465642e000000000000 // 字符串数据（ ASCII 编码，26字节）
     * @return
     */
    public static final String errorId = "0x08c379a0";
    public String parseErrorMsg(String output) {
        StringBuilder outputStringBuilder = new StringBuilder(output);
        if (!output.startsWith(errorId)) {
            return "";
        }
        outputStringBuilder.delete(0,errorId.length());
        int offset = Integer.parseInt(outputStringBuilder.substring(0,64),16);
        outputStringBuilder.delete(0,64);
        int length = Integer.parseInt(outputStringBuilder.substring(0,offset * 2),16);
        outputStringBuilder.delete(0,offset * 2);
        return new String(Hex.decode(outputStringBuilder.substring(0,length * 2).getBytes(StandardCharsets.UTF_8)));
    }


    @Test
    void getBlockNumber() {
        System.out.println(baseService.getBlockNumber());
    }

    @Test
    void getTransactionReceipt() throws InterruptedException, BaseException, TransactionException {
        TransactionRecepitBean transactionRecepitBean = baseService.getTransactionReceipt("0x379436fe1fd26a4b6eb949bf0ec9dc1d97f1c703ab328231fa09edab03bf722c");
        log.info(transactionRecepitBean.toString());
        InputAndOutputResult inputAndOutputResult = AnalyzeChainInfoUtils.analyzeTransactionOutput(ConfigCache.get().getDdc1155ABI(), ConfigCache.get().getDdc1155BIN(), transactionRecepitBean.getInput(), transactionRecepitBean.getOutput());
        log.info(inputAndOutputResult.toString());
    }

    @Test
    void getTransactionByHash(){
        String hash = "0x379436fe1fd26a4b6eb949bf0ec9dc1d97f1c703ab328231fa09edab03bf722c";
        TransactionInfoBean transaction = baseService.getTransactionByHash(hash);
        log.info(transaction.toString());
    }

}