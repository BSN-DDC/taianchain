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

import static org.junit.Assert.assertNotNull;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;

@Slf4j
class DDC721ServiceTest {

    //0x81072375a506581CADBd90734Bd00A20CdDbE48b
    String privateKey = "-----BEGIN PRIVATE KEY-----\n" +
            "MIGEAgEAMBAGByqGSM49AgEGBSuBBAAKBG0wawIBAQQgseEExMPXTcSpExzejzYZ\n" +
            "wcLWikQtoZ3BRhWergMR2LGhRANCAATCEQFr8dEbUI6ZYChl4+pE3UopdpWknZiv\n" +
            "rK7WWNymFHQQyIN15nsq5ZZat8G+iPNLtCdRSaU3h769ObArmg11\n" +
            "-----END PRIVATE KEY-----";

    String publicKey = "-----BEGIN PUBLIC KEY-----\n" +
            "MFYwEAYHKoZIzj0CAQYFK4EEAAoDQgAEjRHf7EbOKvUwRJW/kn4N6Vmf++n/gBu0\n" +
            "WEBUzovj+TAxwvgB26tCfoqk9X2gTdjwwKh6o/hvtx66EDB9GlzgTA==\n" +
            "-----END PUBLIC KEY-----";

    String address = "0x81072375a506581CADBd90734Bd00A20CdDbE48b";

    static {
        DDCSdkClient sdk = new DDCSdkClient();
        sdk.init();
    }

    private DDC721Service getDDC721Service() {
        DDC721Service ddc721Service = null;
        try {
            ddc721Service = new DDC721Service(
                    new Secp256K1SignEventListener(privateKey, publicKey));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ddc721Service;
    }

    private String analyzeRecepit(String tx) throws InterruptedException, BaseException, TransactionException {
        TransactionRecepitBean transactionRecepitBean = null;
        for (int i = 0; i < 20; i++) {
            log.info("times: " + i);
            transactionRecepitBean = getDDC721Service().getTransactionRecepit(tx);
            if (transactionRecepitBean != null) {
                break;
            }
            Thread.sleep(500);
        }
        log.info(transactionRecepitBean.toString());
        if (transactionRecepitBean.getStatus().equals("0x0")) {
            InputAndOutputResult inputAndOutputResult = AnalyzeChainInfoUtils.analyzeTransactionOutput(ConfigCache.get().getDdc721ABI(), ConfigCache.get().getDdc721BIN(), transactionRecepitBean.getInput(), transactionRecepitBean.getOutput());
            return inputAndOutputResult.toString();
        } else {
            return new String(Hex.decode(transactionRecepitBean.getOutput().substring(2).getBytes(StandardCharsets.UTF_8)));
        }
    }

    @Test
    void mint() throws Exception {
        String tx = getDDC721Service().mint(address, "0xb0031Aa7725A6828BcCE4F0b90cFE451C31c1e63", "0xb0031Aa7725A6828BcCE4F0b90cFE451C31c1e63");
        log.info(tx);
        log.info(analyzeRecepit(tx));
        assertNotNull(tx);
    }

    @Test
    void safeMint() throws Exception {
        for (int i = 0; i < 1; i++) {
            String tx = getDDC721Service().safeMint(address, "0xb0031Aa7725A6828BcCE4F0b90cFE451C31c1e63", "0xb0031Aa7725A6828BcCE4F0b90cFE451C31c1e63", "test additional data".getBytes());
            log.info(tx);
            log.info(analyzeRecepit(tx));
            assertNotNull(tx);
        }

    }

    @Test
    void approve() throws Exception {
        String tx = getDDC721Service().approve(address, "0x179319b482320c74be043bf0fb3f00411ca12f8d", new BigInteger("150"));
        log.info(tx);
        log.info(analyzeRecepit(tx));
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
        String tx = getDDC721Service().setApprovalForAll(address, "0x179319b482320c74be043bf0fb3f00411ca12f8d", true);
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
        String tx = getDDC721Service().safeTransferFrom(address, "0xb0031aa7725a6828bcce4f0b90cfe451c31c1e63", "0x5c5101afe03b416b9735f40ddc3ba7b0c354a5a0", new BigInteger("150"), data);
        log.info(tx);
        log.info(analyzeRecepit(tx));
        assertNotNull(tx);
    }

    @Test
    void transferFrom() throws Exception {
        String tx = getDDC721Service().transferFrom(address, "0xb0031Aa7725A6828BcCE4F0b90cFE451C31c1e63", "0x5c5101afe03b416b9735f40ddc3ba7b0c354a5a0", new BigInteger("1"));
        log.info(tx);
        assertNotNull(tx);
        analyzeRecepit(tx);
    }

    @Test
    void freeze() throws Exception {
        String tx = getDDC721Service().freeze(address, new BigInteger("132"));
        log.info(tx);
        log.info(analyzeRecepit(tx));
        assertNotNull(tx);
    }


    @Test
    void unFreeze() throws Exception {
        String tx = getDDC721Service().unFreeze(address, new BigInteger("132"));
        log.info(tx);
        log.info(analyzeRecepit(tx));
        assertNotNull(tx);
    }

    @Test
    void burn() throws Exception {
        String tx = getDDC721Service().burn(address, new BigInteger("134"));
        log.info(tx);
        log.info(analyzeRecepit(tx));
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
        log.info(analyzeRecepit(name));
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
}