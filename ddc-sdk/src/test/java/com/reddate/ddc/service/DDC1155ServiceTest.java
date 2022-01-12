package com.reddate.ddc.service;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.reddate.ddc.DDCSdkClient;
import com.reddate.ddc.config.ConfigCache;
import com.reddate.ddc.dto.taianchain.TransactionRecepitBean;
import com.reddate.ddc.listener.Secp256K1SignEventListener;
import com.reddate.ddc.listener.SignEvent;
import com.reddate.ddc.listener.SignEventListener;
import com.reddate.ddc.util.AnalyzeChainInfoUtils;
import lombok.extern.slf4j.Slf4j;
import org.fisco.bcos.web3j.crypto.gm.sm2.util.encoders.Hex;
import org.fisco.bcos.web3j.protocol.exceptions.TransactionException;
import org.fisco.bcos.web3j.tx.txdecode.BaseException;
import org.fisco.bcos.web3j.tx.txdecode.InputAndOutputResult;
import org.junit.jupiter.api.Test;

import static org.junit.Assert.assertNotNull;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
class DDC1155ServiceTest {

    String privateKey = "-----BEGIN PRIVATE KEY-----\n" +
            "MIGEAgEAMBAGByqGSM49AgAABSuBBAAKBG0wawIBAQQgEWL4mAyD0V4cKcZ+RXS+\n" +
            "Y0b/Wt3WYOuHNynQQwCaGPGhRANCAAQojPfT83xRrijQNk6CXq1/w61/ZU5GC6CE\n" +
            "BTq8PEeUyqngCJCN0gkfRU1IEmusAsIGJb3ff2cQRvYTBqcismv5\n" +
            "-----END PRIVATE KEY-----\n";


    static String publicKey = "-----BEGIN PUBLIC KEY-----\n" +
            "MFYwEAYHKoZIzj0CAQYFK4EEAAoDQgAEjRHf7EbOKvUwRJW/kn4N6Vmf++n/gBu0\n" +
            "WEBUzovj+TAxwvgB26tCfoqk9X2gTdjwwKh6o/hvtx66EDB9GlzgTA==\n" +
            "-----END PUBLIC KEY-----\n";

    static {
        DDCSdkClient sdk = new DDCSdkClient();
        sdk.init();
    }

    private DDC1155Service getDDC1155Service() {
        DDC1155Service ddc1155Service = null;
        try {
            ddc1155Service = new DDC1155Service(
                    new Secp256K1SignEventListener(privateKey, publicKey));

        } catch (Exception e) {
            e.printStackTrace();
        }
        return ddc1155Service;
    }

    private String analyzeRecepit(String tx) throws InterruptedException, BaseException, TransactionException {
        TransactionRecepitBean transactionRecepitBean = null;
        for (int i = 0; i < 20; i++) {
            log.info("times: " + i);
            transactionRecepitBean = getDDC1155Service().getTransactionRecepit(tx);
            if (transactionRecepitBean != null) {
                break;
            }
            Thread.sleep(500);
        }
        log.info(transactionRecepitBean.toString());
        if (transactionRecepitBean.getStatus().equals("0x0")) {
            InputAndOutputResult inputAndOutputResult = AnalyzeChainInfoUtils.analyzeTransactionOutput(ConfigCache.get().getDdc1155ABI(), ConfigCache.get().getDdc1155BIN(), transactionRecepitBean.getInput(), transactionRecepitBean.getOutput());
            return inputAndOutputResult.toString();
        } else {
            return new String(Hex.decode(transactionRecepitBean.getOutput().substring(2).getBytes(StandardCharsets.UTF_8)));
        }
    }

    @Test
    void mint() throws Exception {
        String tx = getDDC1155Service().mint("0xb0031Aa7725A6828BcCE4F0b90cFE451C31c1e63", new BigInteger("100"), "Token1");
        log.info(tx);
        assertNotNull(tx);
        log.info(analyzeRecepit(tx));
    }

    @Test
    void mintBatch() throws Exception {
        Multimap<BigInteger, String> map = HashMultimap.create();
        map.put(new BigInteger("100"),"0xb0031Aa7725A6828BcCE4F0b90cFE451C31c1e63");
        map.put(new BigInteger("100"),"0xb0031Aa7725A6828BcCE4F0b90cFE451C31c1e63");
        String tx = getDDC1155Service().mintBatch("0xb0031Aa7725A6828BcCE4F0b90cFE451C31c1e63",map);
        log.info(tx);
        assertNotNull(tx);
        log.info(analyzeRecepit(tx));
    }

    @Test
    void setApprovalForAll() throws Exception {
        String tx = getDDC1155Service().setApprovalForAll("0x179319b482320c74be043bf0fb3f00411ca12f8d", true);
        log.info(tx);
        assertNotNull(tx);
        log.info(analyzeRecepit(tx));
    }

    @Test
    void safeTransferFrom() throws Exception {
        byte[] data = new byte[1];
        data[0] = 1;
        String tx = getDDC1155Service().safeTransferFrom("0xb0031Aa7725A6828BcCE4F0b90cFE451C31c1e63", "0x179319b482320c74be043bf0fb3f00411ca12f8d", new BigInteger("18"), new BigInteger("1"), data);
        log.info(tx);
        assertNotNull(tx);
        log.info(analyzeRecepit(tx));
    }


    @Test
    void safeBatchTransferFrom() throws Exception {
        byte[] data = new byte[1];
        data[0] = 1;

        Map<BigInteger,BigInteger> ddcInfos = new HashMap<>();
        ddcInfos.put(new BigInteger("23"),new BigInteger("23"));
        String tx = getDDC1155Service().safeBatchTransferFrom("0xb0031Aa7725A6828BcCE4F0b90cFE451C31c1e63", "0x179319b482320c74be043bf0fb3f00411ca12f8d", ddcInfos, data);
        log.info(tx);
        assertNotNull(tx);
        log.info(analyzeRecepit(tx));
    }

    @Test
    void freeze() throws Exception {
        String tx = getDDC1155Service().freeze(new BigInteger("18"));
        log.info(tx);
        assertNotNull(tx);
        log.info(analyzeRecepit(tx));
        assertNotNull(tx);
    }


    @Test
    void unFreeze() throws Exception {
        String tx = getDDC1155Service().unFreeze(new BigInteger("18"));
        log.info(tx);
        assertNotNull(tx);
        log.info(analyzeRecepit(tx));
        assertNotNull(tx);
    }

    @Test
    void burn() throws Exception {
        String tx = getDDC1155Service().burn("0x179319b482320c74be043bf0fb3f00411ca12f8d", new BigInteger("18"));
        log.info(tx);
        assertNotNull(tx);
        log.info(analyzeRecepit(tx));
        assertNotNull(tx);
    }

    @Test
    void burnBatch() throws Exception {
        ArrayList<BigInteger> arrayList = new ArrayList();
        arrayList.add(new BigInteger("23"));

        String tx = getDDC1155Service().burnBatch("0xb0031Aa7725A6828BcCE4F0b90cFE451C31c1e63", arrayList);
        log.info(tx);
        assertNotNull(tx);
        log.info(analyzeRecepit(tx));
    }

    @Test
    void balanceOf() throws Exception {
        BigInteger bigInteger = getDDC1155Service().balanceOf("0xf87e284379405b47f0be5317e3c7fcb436985843", new BigInteger("1"));
        log.info(bigInteger.toString());
        assertNotNull(bigInteger);
    }

    @Test
    void balanceOfBatch() throws Exception {
        Multimap<String, BigInteger> map = HashMultimap.create();
        for (int i = 0; i < 100; i++) {
            map.put("0x5c5101aFe03B416b9735F40dDc3ba7B0c354A5A0",new BigInteger(String.valueOf(i)));
        }

        List<BigInteger> bigIntegerList = getDDC1155Service().balanceOfBatch(map);
        assertNotNull(bigIntegerList);
        bigIntegerList.forEach(t -> {
            log.info(String.valueOf(t));
        });
    }

    @Test
    void ddcURI() throws Exception {
        for (int i = 1; i < 100; i++) {
            String result = getDDC1155Service().ddcURI(new BigInteger(String.valueOf(i)));
            assertNotNull(result);
            log.info("URL: {}",result);
        }
    }

}