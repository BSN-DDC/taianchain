package com.reddate.ddc.service;

import com.reddate.ddc.config.ConfigCache;
import com.reddate.ddc.dto.ddc.DDC721TransferEventBean;
import com.reddate.ddc.dto.taianchain.TransactionRecepitBean;
import lombok.extern.slf4j.Slf4j;
import org.fisco.bcos.web3j.protocol.exceptions.TransactionException;
import org.fisco.bcos.web3j.tx.txdecode.BaseException;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.math.BigInteger;
import java.util.ArrayList;

import static org.junit.Assert.assertNotNull;

@Slf4j
class DDC721ServiceTest extends BaseServiceTest {

    String abi = ConfigCache.get().getDdc721ABI();
    String bin = ConfigCache.get().getDdc721BIN();

    @Test
    void mint() throws Exception {
        String tx = getDDC721Service().mint(consumerAddress, "0xb0031Aa7725A6828BcCE4F0b90cFE451C31c1e63", "0xb0031Aa7725A6828BcCE4F0b90cFE451C31c1e63");
        log.info(tx);
        assertNotNull(tx);
        while (true) {
            TransactionRecepitBean transactionRecepitBean = getDDC721Service().getTransactionRecepit(tx);
            if (transactionRecepitBean == null) {
                Thread.sleep(200 * 2);
                continue;
            }
            BlockEventService blockEventService = new BlockEventService();
            ArrayList result = blockEventService.getBlockEvent(transactionRecepitBean.getBlockNumber());
            result.forEach(t -> {
                if (t instanceof DDC721TransferEventBean) {
                    log.info("{}:DDCID {}", t.getClass(), ((DDC721TransferEventBean) t).getDdcId());
                }
            });
            break;
        }

    }

    @Test
    void safeMint() throws Exception {
        for (int i = 0; i < 1; i++) {
            String tx = getDDC721Service().safeMint(consumerAddress, "0xb0031Aa7725A6828BcCE4F0b90cFE451C31c1e63", "0xb0031Aa7725A6828BcCE4F0b90cFE451C31c1e63", "test additional data".getBytes());
            log.info(tx);
            log.info(analyzeRecepit(tx, abi, bin));
            assertNotNull(tx);
        }

    }

    @Test
    void getDDCInfoByBlockNumber() throws BaseException, InterruptedException, IOException, InvocationTargetException, IllegalAccessException, NoSuchMethodException, InstantiationException {
        BlockEventService blockEventService = new BlockEventService();
        String blockNumber = "760261";
        ArrayList result = new ArrayList();
        result.addAll(blockEventService.getBlockEvent(blockNumber));

        result.forEach(t -> {
            if (t instanceof DDC721TransferEventBean) {
                log.info("{}:DDCID {}", t.getClass(), ((DDC721TransferEventBean) t).getDdcId());
            }
        });
    }

    @Test
    void approve() throws Exception {
        String tx = getDDC721Service().approve(consumerAddress, "0x179319b482320c74be043bf0fb3f00411ca12f8d", new BigInteger("150"));
        log.info(tx);
        log.info(analyzeRecepit(tx, abi, bin));
        assertNotNull(tx);
    }

    @Test
    void getApproved() throws Exception {
        String tx = getDDC721Service().getApproved(new BigInteger("1"));
        log.info(tx);
        // assertNotNull(tx);
    }

    @Test
    void setApprovalForAll() throws Exception {
        String tx = getDDC721Service().setApprovalForAll(consumerAddress, "0x179319b482320c74be043bf0fb3f00411ca12f8d", true);
        log.info(tx);
        assertNotNull(tx);
    }

    @Test
    void isApprovedForAll() throws Exception {
        Boolean tx = getDDC721Service().isApprovedForAll("0xf87e284379405b47f0be5317e3c7fcb436985843", "0x81072375a506581cadbd90734bd00a20cddbe48b");
        log.info(String.valueOf(tx));
        assertNotNull(tx);
    }

    @Test
    void safeTransferFrom() throws Exception {
        byte[] data = new byte[1];
        data[0] = 1;
        String tx = getDDC721Service().safeTransferFrom(consumerAddress, "0xb0031aa7725a6828bcce4f0b90cfe451c31c1e63", "0x5c5101afe03b416b9735f40ddc3ba7b0c354a5a0", new BigInteger("150"), data);
        log.info(tx);
        log.info(analyzeRecepit(tx, abi, bin));
        assertNotNull(tx);
    }

    @Test
    void transferFrom() throws Exception {
        String tx = getDDC721Service().transferFrom(consumerAddress, "0xb0031Aa7725A6828BcCE4F0b90cFE451C31c1e63", "0x5c5101afe03b416b9735f40ddc3ba7b0c354a5a0", new BigInteger("1"));
        log.info(tx);
        assertNotNull(tx);
        log.info(analyzeRecepit(tx, abi, bin));
    }

    @Test
    void freeze() throws Exception {
        String tx = getDDC721Service().freeze(consumerAddress, new BigInteger("132"));
        log.info(tx);
        log.info(analyzeRecepit(tx, abi, bin));
        assertNotNull(tx);
    }


    @Test
    void unFreeze() throws Exception {
        String tx = getDDC721Service().unFreeze(consumerAddress, new BigInteger("132"));
        log.info(tx);
        log.info(analyzeRecepit(tx, abi, bin));
        assertNotNull(tx);
    }

    @Test
    void burn() throws Exception {
        String tx = getDDC721Service().burn(consumerAddress, new BigInteger("134"));
        log.info(tx);
        log.info(analyzeRecepit(tx, abi, bin));
        assertNotNull(tx);
    }

    @Test
    void balanceOf() throws Exception {
        BigInteger bigInteger = getDDC721Service().balanceOf("0xf87e284379405b47f0be5317e3c7fcb436985843");
        log.info(bigInteger.toString());
        assertNotNull(bigInteger);
    }

    @Test
    void ownerOf() {
        String account = null;
        try {
            account = getDDC721Service().ownerOf(new BigInteger("1"));
            log.info(account);
        } catch (Exception e) {
            e.printStackTrace();
            log.info("null");
        }

        // assertNotNull(account);
    }

    @Test
    void name() throws Exception {
        String name = getDDC721Service().name();
        log.info(name);
        assertNotNull(name);
    }

    @Test
    void symbol() throws Exception {
        String symbol = getDDC721Service().symbol();
        log.info(symbol);
        assertNotNull(symbol);
    }

    @Test
    void ddcURI() throws Exception {
        String ddcURI = getDDC721Service().ddcURI(new BigInteger("1"));
        log.info(ddcURI);
        // assertNotNull(ddcURI);
    }

    @Test
    void setDDCURI() throws Exception {
        String tx = getDDC721Service().setURI(consumerAddress, new BigInteger(""), "");
        log.info(tx);
        assertNotNull(tx);
        log.info(analyzeRecepit(tx, abi, bin));
    }
}