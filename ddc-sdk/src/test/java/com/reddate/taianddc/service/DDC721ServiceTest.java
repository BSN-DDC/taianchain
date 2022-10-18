package com.reddate.taianddc.service;

import com.reddate.taianddc.config.ConfigCache;
import com.reddate.taianddc.dto.ddc.DDC721TransferEventBean;
import com.reddate.taianddc.dto.taianchain.TransactionRecepitBean;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.buf.HexUtils;
import org.fisco.bcos.web3j.abi.datatypes.DynamicBytes;
import org.fisco.bcos.web3j.abi.datatypes.StaticArray;
import org.fisco.bcos.web3j.abi.datatypes.Utf8String;
import org.fisco.bcos.web3j.crypto.gm.sm2.util.Strings;
import org.fisco.bcos.web3j.crypto.gm.sm2.util.encoders.Hex;
import org.fisco.bcos.web3j.protocol.exceptions.TransactionException;
import org.fisco.bcos.web3j.tx.txdecode.BaseException;
import org.fisco.bcos.web3j.tx.txdecode.ContractTypeUtil;
import org.junit.jupiter.api.Test;
import org.web3j.utils.Numeric;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@Slf4j
class DDC721ServiceTest extends BaseServiceTest {

    String abi = ConfigCache.get().getDdc721ABI();
    String bin = ConfigCache.get().getDdc721BIN();

    @Test
    void hexPrivateKey() {
        String pri = "7864185170587681948426534767989407184294173122271333180861463159995933268209";
        BigInteger bigInteger = new BigInteger(pri);
        System.out.println(bigInteger.toString(16));
    }


    @Test
    void analyzeTransaction() throws BaseException, TransactionException, InterruptedException {
        String tx = "0xfc9e8d62725a8937e78bab7584f6299b2b7309e3abb97a661bc09ef324f053bb";
        String result = analyzeRecepit(tx, abi, bin);
        System.out.println(result);
    }

    @Test
    void mint() throws Exception {
        String tx = getDDC721Service().mint(platformAddress, platformAddress, "");
        log.info(tx);
        assertNotNull(tx);
        while (true) {
            TransactionRecepitBean transactionRecepitBean = getDDC721Service().getTransactionReceipt(tx);
            if (transactionRecepitBean == null) {
                Thread.sleep(200 * 2);
                continue;
            }
            log.info(analyzeRecepit(tx, abi, bin));
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
    void mintBatch() throws Exception {
        List<String> ddcURIs = new ArrayList<>();
        ddcURIs.add("2");
        ddcURIs.add("2");

        String txHash = getDDC721Service().mintBatch(platformAddress, platformAddress, ddcURIs);
        assertNotNull(txHash);
        log.info(txHash);
        while (true) {
            TransactionRecepitBean transactionRecepitBean = getDDC721Service().getTransactionReceipt(txHash);
            if (transactionRecepitBean == null) {
                Thread.sleep(200 * 2);
                continue;
            }
            log.info(analyzeRecepit(txHash, abi, bin));
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
    void safeMintBatch() throws Exception {
        List<String> ddcURIs = new ArrayList<>();
        ddcURIs.add("1");
        ddcURIs.add("1");
        ddcURIs.add("1");
        String data = "123";
        String txHash = getDDC721Service().safeMintBatch(platformAddress, platformAddress, ddcURIs, data.getBytes());
        assertNotNull(txHash);
        log.info(txHash);
        while (true) {
            TransactionRecepitBean transactionRecepitBean = getDDC721Service().getTransactionReceipt(txHash);
            if (transactionRecepitBean == null) {
                Thread.sleep(200 * 2);
                continue;
            }
            log.info(analyzeRecepit(txHash, abi, bin));
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
            String txHash = getDDC721Service().safeMint(platformAddress, platformAddress, "2", "我test additional data".getBytes());
            assertNotNull(txHash);
            log.info(txHash);
            while (true) {
                TransactionRecepitBean transactionRecepitBean = getDDC721Service().getTransactionReceipt(txHash);
                if (transactionRecepitBean == null) {
                    Thread.sleep(200 * 2);
                    continue;
                }
                log.info(analyzeRecepit(txHash, abi, bin));
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
        String tx = getDDC721Service().approve(platformAddress, consumerAddress, new BigInteger("32"));
        log.info(tx);
        log.info(analyzeRecepit(tx, abi, bin));
        assertNotNull(tx);
    }

    @Test
    void getApproved() throws Exception {
        String tx = getDDC721Service().getApproved(new BigInteger("33"));
        log.info(tx);
        // assertNotNull(tx);
    }

    @Test
    void setApprovalForAll() throws Exception {
        String tx = getDDC721Service().setApprovalForAll(platformAddress, consumerAddress, false);
        log.info(tx);
        assertNotNull(tx);
        log.info(analyzeRecepit(tx, abi, bin));
    }

    @Test
    void isApprovedForAll() throws Exception {
        Boolean tx = getDDC721Service().isApprovedForAll(platformAddress, consumerAddress);
        log.info(String.valueOf(tx));
        assertNotNull(tx);
    }

    @Test
    void safeTransferFrom() throws Exception {
        byte[] data = new byte[1];
        data[0] = 1;
        String tx = getDDC721Service().safeTransferFrom(platformAddress, platformAddress, consumerAddress, new BigInteger("55"), data);
        log.info(tx);
        log.info(analyzeRecepit(tx, abi, bin));
        assertNotNull(tx);
    }

    @Test
    void transferFrom() throws Exception {
        String tx = getDDC721Service().transferFrom(platformAddress, platformAddress, consumerAddress, new BigInteger("34"));
        log.info(tx);
        assertNotNull(tx);
        log.info(analyzeRecepit(tx, abi, bin));
    }

    @Test
    void freeze() throws Exception {
        String tx = getDDC721Service().freeze(operatorAddress, new BigInteger("12"));
        log.info(tx);
        log.info(analyzeRecepit(tx, abi, bin));
        assertNotNull(tx);
    }


    @Test
    void unFreeze() throws Exception {
        String tx = getDDC721Service().unFreeze(operatorAddress, new BigInteger("12"));
        log.info(tx);
        log.info(analyzeRecepit(tx, abi, bin));
        assertNotNull(tx);
    }

    @Test
    void burn() throws Exception {
        String tx = getDDC721Service().burn(consumerAddress, new BigInteger("19"));
        log.info(tx);
        log.info(analyzeRecepit(tx, abi, bin));
        assertNotNull(tx);
    }

    @Test
    void balanceOf() throws Exception {
        BigInteger bigInteger = getDDC721Service().balanceOf(consumerAddress);
        log.info(bigInteger.toString());
        assertNotNull(bigInteger);
    }

    @Test
    void ownerOf() {
        String account = null;
        try {
            account = getDDC721Service().ownerOf(new BigInteger("55"));
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
        String ddcURI = getDDC721Service().ddcURI(new BigInteger("13"));
        log.info(ddcURI);
        // assertNotNull(ddcURI);
    }

    @Test
    void setDDCURI() throws Exception {
        String tx = getDDC721Service().setURI(testPlatformAddress1, new BigInteger("20"), "test14SetURI");
        log.info(tx);
        assertNotNull(tx);
        log.info(analyzeRecepit(tx, abi, bin));
    }

    @Test
    void getLatestDDCId() throws Exception {
        BigInteger ddcId = getDDC721Service().getLatestDDCId();
        assertNotNull(ddcId);
        log.info("getLatestDDCId : {}", ddcId);
    }

    @Test
    void getNonce() throws Exception {
        BigInteger nonce = getDDC721Service().getNonce(platformAddress);
//        BigInteger nonce = getDDC721Service().getNonce(consumerAddress);
        log.info(nonce.toString());
    }

    @Test
    void metaMint() throws Exception {
        String to = platformAddress;
        String ddcURI = "1";
        BigInteger nonce = BigInteger.valueOf(2);
        BigInteger deadline = new BigInteger("1694851747000");

        String digest = ddcSdkClient.getDDC721MetaTransaction().getMintDigest(to, ddcURI, nonce, deadline);
//      byte[] signature = ddcSdkClient.getDDC721MetaTransaction().generateSignature(originPrivateKey, digest);
        byte[] signature = ddcSdkClient.getDDC721MetaTransaction().generateSignatureByPem(platform, digest);
        String txHash = getDDC721Service().metaMint(platformAddress, to, ddcURI, nonce, deadline, signature);

        log.info(txHash);
        assertNotNull(txHash);
        log.info(analyzeRecepit(txHash, abi, bin));
    }

    @Test
    void metaSafeMint() throws Exception {
        String to = platformAddress;
        String ddcURI = "1";
        byte[] data = new byte[]{0x11};
        BigInteger nonce = BigInteger.valueOf(35);
        BigInteger deadline = new BigInteger("1663231633000");

        String digest = ddcSdkClient.getDDC721MetaTransaction().getSafeMintDigest(to, ddcURI, data, nonce, deadline);
        //byte[] signature = ddcSdkClient.getDDC721MetaTransaction().generateSignature(originPrivateKey, digest);
        byte[] signature = ddcSdkClient.getDDC721MetaTransaction().generateSignatureByPem(platform, digest);
        log.info("MetaMintBatch sign: {}", Numeric.toHexString(signature));

        String txHash = getDDC721Service().metaSafeMint(platformAddress, platformAddress, ddcURI, data, nonce, deadline, signature);
        log.info(txHash);
        assertNotNull(txHash);
        log.info(analyzeRecepit(txHash, abi, bin));
    }

    @Test
    void metaMintBatch() throws Exception {
        String to = platformAddress;
        List<String> ddcURIs = new ArrayList<>();
        ddcURIs.add("1");
        ddcURIs.add("1");
        BigInteger nonce = BigInteger.valueOf(37);
        BigInteger deadline = new BigInteger("1663231633000");
        String digest = ddcSdkClient.getDDC721MetaTransaction().getMintBatchDigest(to, ddcURIs, nonce, deadline);
        //byte[] signature = ddcSdkClient.getDDC721MetaTransaction().generateSignature(originPrivateKey, digest);
        byte[] signature = ddcSdkClient.getDDC721MetaTransaction().generateSignatureByPem(platform, digest);
        log.info("MetaMintBatch sign: {}", Numeric.toHexString(signature));

        String txHash = getDDC721Service().metaMintBatch(platformAddress, platformAddress, ddcURIs, nonce, deadline, signature);
        log.info(txHash);
        assertNotNull(txHash);
        log.info(analyzeRecepit(txHash, abi, bin));
    }

    @Test
    void metaSafeMintBatch() throws Exception {
        String to = "0x81072375a506581CADBd90734Bd00A20CdDbE48b";
        List<String> ddcURIs = new ArrayList<>();
        ddcURIs.add("1");
        ddcURIs.add("1");

        byte[] data = new byte[]{0x11};
        BigInteger nonce = BigInteger.valueOf(2);
        BigInteger deadline = new BigInteger("1671505027000");
        String digest = ddcSdkClient.getDDC721MetaTransaction().getSafeMintBatchDigest(to, ddcURIs, nonce, deadline);
        //byte[] signature = ddcSdkClient.getDDC721MetaTransaction().generateSignature(originPrivateKey, digest);
        byte[] signature = ddcSdkClient.getDDC721MetaTransaction().generateSignatureByPem(platform, digest);
        log.info("MetaMintBatch sign: {}", Numeric.toHexString(signature));

        String txHash = getDDC721Service().metaSafeMintBatch(platformAddress, platformAddress, ddcURIs, data, nonce, deadline, signature);
        log.info(txHash);
        assertNotNull(txHash);
        log.info(analyzeRecepit(txHash, abi, bin));
    }

    @Test
    void metaTransferFrom() throws Exception {
        String from = "0x81072375a506581CADBd90734Bd00A20CdDbE48b";
        String to = "0x81072375a506581CADBd90734Bd00A20CdDbE48b";
        BigInteger ddcId = BigInteger.valueOf(39);
        BigInteger nonce = BigInteger.valueOf(6);
        BigInteger deadline = new BigInteger("1671505027000");
        String digest = ddcSdkClient.getDDC721MetaTransaction().getTransferFromDigest(from, to, ddcId, nonce, deadline);
        //byte[] signature = ddcSdkClient.getDDC721MetaTransaction().generateSignature(originPrivateKey, digest);
        byte[] signature = ddcSdkClient.getDDC721MetaTransaction().generateSignatureByPem(platform, digest);
        log.info("TransferFrom sign: {}", Numeric.toHexString(signature));

        String txHash = getDDC721Service().metaTransferFrom(platformAddress, platformAddress, consumerAddress, ddcId, nonce, deadline, signature);
        log.info(txHash);
        assertNotNull(txHash);
        log.info(analyzeRecepit(txHash, abi, bin));
    }

    @Test
    void metaSafeTransferFrom() throws Exception {
        String from = "0x81072375a506581CADBd90734Bd00A20CdDbE48b";
        String to = "0x81072375a506581CADBd90734Bd00A20CdDbE48b";
        BigInteger ddcId = BigInteger.valueOf(39);
        byte[] data = new byte[]{0x1};
        BigInteger nonce = BigInteger.valueOf(6);
        BigInteger deadline = new BigInteger("1671505027000");
        String digest = ddcSdkClient.getDDC721MetaTransaction().getSafeTransferFromDigest(from, to, ddcId, data, nonce, deadline);
        //byte[] signature = ddcSdkClient.getDDC721MetaTransaction().generateSignature(originPrivateKey, digest);
        byte[] signature = ddcSdkClient.getDDC721MetaTransaction().generateSignatureByPem(platform, digest);
        log.info("SafeTransferFrom sign: {}", Numeric.toHexString(signature));

        String txHash = getDDC721Service().metaSafeTransferFrom(platformAddress, platformAddress, consumerAddress, ddcId, data, nonce, deadline, signature);
        log.info(txHash);
        assertNotNull(txHash);
        log.info(analyzeRecepit(txHash, abi, bin));
    }

    @Test
    void metaBurn() throws Exception {
        BigInteger ddcId = BigInteger.valueOf(39);
        BigInteger nonce = BigInteger.valueOf(6);
        BigInteger deadline = new BigInteger("1671505027000");
        String digest = ddcSdkClient.getDDC721MetaTransaction().getBurnDigest(ddcId, nonce, deadline);
        //byte[] signature = ddcSdkClient.getDDC721MetaTransaction().generateSignature(originPrivateKey, digest);
        byte[] signature = ddcSdkClient.getDDC721MetaTransaction().generateSignatureByPem(platform, digest);
        log.info("Burn sign: {}", Numeric.toHexString(signature));
        String txHash = getDDC721Service().metaBurn(platformAddress, ddcId, nonce, deadline, signature);
        log.info(txHash);
        assertNotNull(txHash);
        log.info(analyzeRecepit(txHash, abi, bin));
    }

    @Test
    void setNameAndSymbol() throws Exception {
        String name = "kk";
        String symbol = "kk";
        String tx = getDDC721Service().setNameAndSymbol(operatorAddress, name, symbol);
        assertNotNull(tx);
        log.info(analyzeRecepit(tx, abi, bin));
    }

}