package com.reddate.ddc.service;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.reddate.ddc.config.ConfigCache;
import com.reddate.ddc.dto.ddc.DDC1155TransferSingleEventBean;
import com.reddate.ddc.dto.ddc.DDC721TransferEventBean;
import com.reddate.ddc.dto.taianchain.TransactionRecepitBean;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertNotNull;


@Slf4j
class DDC1155ServiceTest extends BaseServiceTest {

    String abi = ConfigCache.get().getDdc1155ABI();
    String bin = ConfigCache.get().getDdc1155BIN();

    @Test
    void mint() throws Exception {
        String tx = getDDC1155Service().safeMint(consumerAddress, "0xb0031Aa7725A6828BcCE4F0b90cFE451C31c1e63", new BigInteger("100"), "", "test additional data".getBytes());
        log.info(tx);
        assertNotNull(tx);

        while (true) {
            TransactionRecepitBean transactionRecepitBean = getDDC1155Service().getTransactionRecepit(tx);
            if (transactionRecepitBean == null) {
                Thread.sleep(200 * 2);
                continue;
            }
            BlockEventService blockEventService = new BlockEventService();
            ArrayList result = blockEventService.getBlockEvent(transactionRecepitBean.getBlockNumber());
            result.forEach(t -> {
                if (t instanceof DDC1155TransferSingleEventBean) {
                    log.info("{}:DDCID {}", t.getClass(), ((DDC1155TransferSingleEventBean) t).getDdcId());
                }
            });
            break;
        }
    }

    @Test
    void mintBatch() throws Exception {
        Multimap<BigInteger, String> map = ArrayListMultimap.create();
        map.put(new BigInteger("1"), "0xb0031Aa7725A6828BcCE4F0b90cFE451C31c1e63");
        map.put(new BigInteger("1"), "0xb0031Aa7725A6828BcCE4F0b90cFE451C31c1e2");
        String tx = getDDC1155Service().safeMintBatch(consumerAddress, "0xb0031Aa7725A6828BcCE4F0b90cFE451C31c1e63", map, "test additional data".getBytes());
        log.info(tx);
        assertNotNull(tx);
        log.info(analyzeRecepit(tx, abi, bin));
    }

    @Test
    void setApprovalForAll() throws Exception {
        String tx = getDDC1155Service().setApprovalForAll(consumerAddress, "0x179319b482320c74be043bf0fb3f00411ca12f8d", true);
        log.info(tx);
        assertNotNull(tx);
        log.info(analyzeRecepit(tx, abi, bin));
    }

    @Test
    void safeTransferFrom() throws Exception {
        byte[] data = new byte[1];
        data[0] = 1;
        String tx = getDDC1155Service().safeTransferFrom(consumerAddress, "0xb0031Aa7725A6828BcCE4F0b90cFE451C31c1e63", "0x5c5101afe03b416b9735f40ddc3ba7b0c354a5a0", new BigInteger("18"), new BigInteger("1"), data);
        log.info(tx);
        assertNotNull(tx);
        log.info(analyzeRecepit(tx, abi, bin));
    }


    @Test
    void safeBatchTransferFrom() throws Exception {
        byte[] data = new byte[1];
        data[0] = 1;

        Map<BigInteger, BigInteger> ddcInfos = new HashMap<>();
        ddcInfos.put(new BigInteger("14"), new BigInteger("1"));
        String tx = getDDC1155Service().safeBatchTransferFrom(consumerAddress, "0xb0031Aa7725A6828BcCE4F0b90cFE451C31c1e63", "0x5c5101afe03b416b9735f40ddc3ba7b0c354a5a0", ddcInfos, data);
        log.info(tx);
        assertNotNull(tx);
        log.info(analyzeRecepit(tx, abi, bin));
    }

    @Test
    void freeze() throws Exception {
        String tx = getDDC1155Service().freeze(consumerAddress, new BigInteger("18"));
        log.info(tx);
        assertNotNull(tx);
        log.info(analyzeRecepit(tx, abi, bin));
        assertNotNull(tx);
    }


    @Test
    void unFreeze() throws Exception {
        String tx = getDDC1155Service().unFreeze(consumerAddress, new BigInteger("18"));
        log.info(tx);
        assertNotNull(tx);
        log.info(analyzeRecepit(tx, abi, bin));
    }

    @Test
    void burn() throws Exception {
        String tx = getDDC1155Service().burn(consumerAddress, "0xb0031Aa7725A6828BcCE4F0b90cFE451C31c1e63", new BigInteger("21"));
        log.info(tx);
        assertNotNull(tx);
        log.info(analyzeRecepit(tx, abi, bin));
    }

    @Test
    void burnBatch() throws Exception {
        ArrayList<BigInteger> arrayList = new ArrayList();
        arrayList.add(new BigInteger("23"));

        String tx = getDDC1155Service().burnBatch(consumerAddress, "0xb0031Aa7725A6828BcCE4F0b90cFE451C31c1e63", arrayList);
        log.info(tx);
        assertNotNull(tx);
        log.info(analyzeRecepit(tx, abi, bin));
    }

    @Test
    void balanceOf() throws Exception {
        BigInteger bigInteger = getDDC1155Service().balanceOf("0xf87e284379405b47f0be5317e3c7fcb436985843", new BigInteger("1"));
        log.info(bigInteger.toString());
        assertNotNull(bigInteger);
    }

    @Test
    void balanceOfBatch() throws Exception {
        Multimap<String, BigInteger> map = ArrayListMultimap.create();
        for (int i = 0; i < 100; i++) {
            map.put("0x5c5101aFe03B416b9735F40dDc3ba7B0c354A5A0", new BigInteger(String.valueOf(i)));
        }

        List<BigInteger> bigIntegerList = getDDC1155Service().balanceOfBatch(map);
        assertNotNull(bigIntegerList);
        bigIntegerList.forEach(t -> {
            log.info(String.valueOf(t));
        });
    }

    @Test
    void ddcURI() throws Exception {
        for (int i = 80; i < 81; i++) {
            String result = getDDC1155Service().ddcURI(new BigInteger(String.valueOf(i)));
            assertNotNull(result);
            log.info("URI: {}", result);
        }
    }

    @Test
    void setURI() throws Exception {
        String tx = getDDC1155Service().setURI(consumerAddress, consumerAddress, new BigInteger("80"),"test76");
        log.info(tx);
        assertNotNull(tx);
        log.info(analyzeRecepit(tx,abi,bin));
    }
}