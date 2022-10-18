package com.reddate.taianddc.service;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.reddate.taianddc.config.ConfigCache;
import com.reddate.taianddc.dto.ddc.DDC1155TransferBatchEventBean;
import com.reddate.taianddc.dto.ddc.DDC1155TransferSingleEventBean;
import com.reddate.taianddc.dto.taianchain.TransactionRecepitBean;
import lombok.extern.slf4j.Slf4j;
import org.fisco.bcos.web3j.protocol.exceptions.TransactionException;
import org.fisco.bcos.web3j.tx.txdecode.BaseException;
import org.junit.jupiter.api.Test;
import org.web3j.utils.Numeric;

import java.math.BigInteger;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertNotNull;


@Slf4j
class DDC1155ServiceTest extends BaseServiceTest {

    String abi = ConfigCache.get().getDdc1155ABI();
    String bin = ConfigCache.get().getDdc1155BIN();

    @Test
    void analyzeTransaction() throws BaseException, TransactionException, InterruptedException {
        String tx = "0x50dda17c5f14547e244d5576ef7bb705210aaa76fe557f8ff89ecac88dc1672b";
        String result = analyzeRecepit(tx, abi, bin);
        System.out.println(result);
    }

    @Test
    void safeMint() throws Exception {
        String tx = getDDC1155Service().safeMint(platformAddress, platformAddress, new BigInteger("3"), "", "test additional data".getBytes());
        log.info(tx);
        assertNotNull(tx);

        while (true) {
            TransactionRecepitBean transactionRecepitBean = getDDC1155Service().getTransactionReceipt(tx);
            if (transactionRecepitBean == null) {
                Thread.sleep(200 * 2);
                continue;
            }
            log.info(analyzeRecepit(tx, abi, bin));
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
    void safeMintBatch() throws Exception {
        Multimap<BigInteger, String> map = ArrayListMultimap.create();
        map.put(new BigInteger("2"), "0xb0031Aa7725A6828BcCE4F0b90cFE451C31c1e63");
        map.put(new BigInteger("2"), "0xb0031Aa7725A6828BcCE4F0b90cFE451C31c1e2");
        String tx = getDDC1155Service().safeMintBatch(platformAddress, platformAddress, map, "test additional data".getBytes());
        log.info(tx);
        assertNotNull(tx);
        log.info(analyzeRecepit(tx, abi, bin));
        while (true) {
            TransactionRecepitBean transactionRecepitBean = getDDC1155Service().getTransactionReceipt(tx);
            if (transactionRecepitBean == null) {
                Thread.sleep(200 * 2);
                continue;
            }
            log.info(analyzeRecepit(tx, abi, bin));
            BlockEventService blockEventService = new BlockEventService();
            ArrayList result = blockEventService.getBlockEvent(transactionRecepitBean.getBlockNumber());
            result.forEach(t -> {
                if (t instanceof DDC1155TransferBatchEventBean) {
                    log.info("{}:DDCIDs {}", t.getClass(), ((DDC1155TransferBatchEventBean) t).getDdcIds());
                }
            });
            break;
        }
    }

    @Test
    void setApprovalForAll() throws Exception {
        String tx = getDDC1155Service().setApprovalForAll(platformAddress, consumerAddress, true);
        log.info(tx);
        assertNotNull(tx);
        log.info(analyzeRecepit(tx, abi, bin));
    }

    @Test
    void isApprovedForAll() throws Exception {
        String tx = getDDC1155Service().isApprovedForAll(platformAddress, consumerAddress);
        assertNotNull(tx);
        log.info(tx);
    }

    @Test
    void safeTransferFrom() throws Exception {
        byte[] data = new byte[1];
        data[0] = 1;
        String tx = getDDC1155Service().safeTransferFrom(platformAddress, platformAddress, consumerAddress, new BigInteger("33"), new BigInteger("1"), data);
        log.info(tx);
        assertNotNull(tx);
        log.info(analyzeRecepit(tx, abi, bin));
    }

    @Test
    void safeBatchTransferFrom() throws Exception {
        byte[] data = new byte[1];
        data[0] = 1;

        Map<BigInteger, BigInteger> ddcInfos = new HashMap<>();
        ddcInfos.put(new BigInteger("36"), new BigInteger("1"));
        ddcInfos.put(new BigInteger("37"), new BigInteger("1"));
        String tx = getDDC1155Service().safeBatchTransferFrom(platformAddress, platformAddress, consumerAddress, ddcInfos, data);
        log.info(tx);
        assertNotNull(tx);
        log.info(analyzeRecepit(tx, abi, bin));
    }

    @Test
    void freeze() throws Exception {
        String tx = getDDC1155Service().freeze(operatorAddress, new BigInteger("4"));
        log.info(tx);
        assertNotNull(tx);
        log.info(analyzeRecepit(tx, abi, bin));
        assertNotNull(tx);
    }


    @Test
    void unFreeze() throws Exception {
        String tx = getDDC1155Service().unFreeze(operatorAddress, new BigInteger("4"));
        log.info(tx);
        assertNotNull(tx);
        log.info(analyzeRecepit(tx, abi, bin));
    }

    @Test
    void burn() throws Exception {
        String tx = getDDC1155Service().burn(testPlatformAddress2, testPlatformAddress2, new BigInteger("5"));
        log.info(tx);
        assertNotNull(tx);
        log.info(analyzeRecepit(tx, abi, bin));
    }

    @Test
    void burnBatch() throws Exception {
        ArrayList<BigInteger> arrayList = new ArrayList();
        arrayList.add(new BigInteger("4"));
        arrayList.add(new BigInteger("3"));

        String tx = getDDC1155Service().burnBatch(testPlatformAddress2, testPlatformAddress2, arrayList);
        log.info(tx);
        assertNotNull(tx);
        log.info(analyzeRecepit(tx, abi, bin));
    }

    @Test
    void balanceOf() throws Exception {
        BigInteger bigInteger = getDDC1155Service().balanceOf(platformAddress, new BigInteger("33"));
        log.info(bigInteger.toString());
        assertNotNull(bigInteger);
    }

    @Test
    void balanceOfBatch() throws Exception {
        Multimap<String, BigInteger> map = ArrayListMultimap.create();
        for (int i = 36; i <= 37; i++) {
            map.put(platformAddress, new BigInteger(String.valueOf(i)));
        }

        List<BigInteger> bigIntegerList = getDDC1155Service().balanceOfBatch(map);
        assertNotNull(bigIntegerList);
        bigIntegerList.forEach(t -> {
            log.info(String.valueOf(t));
        });
    }

    @Test
    void ddcURI() throws Exception {
        for (int i = 1; i < 2; i++) {
            String result = getDDC1155Service().ddcURI(new BigInteger(String.valueOf(i)));
            assertNotNull(result);
            log.info("URI: {}", result);
        }
    }

    @Test
    void setURI() throws Exception {
        String tx = getDDC1155Service().setURI(testPlatformAddress1, testPlatformAddress1, new BigInteger("6"), "test66");
        log.info(tx);
        assertNotNull(tx);
        log.info(analyzeRecepit(tx, abi, bin));
    }

    @Test
    void getLatestDDCId() throws Exception {
        BigInteger ddcId = getDDC1155Service().getLatestDDCId();
        assertNotNull(ddcId);
        log.info("getLatestDDCId : {}", ddcId);
    }

    @Test
    void getNonce() throws Exception {
        BigInteger nonce = getDDC1155Service().getNonce(platformAddress);
//        BigInteger nonce = getDDC1155Service().getNonce(consumerAddress);
        log.info(nonce.toString());
    }

    @Test
    void metaSafeMint() throws Exception {
        String to = "0x81072375a506581CADBd90734Bd00A20CdDbE48b";
        BigInteger amount = BigInteger.valueOf(10);
        String ddcURI = "1";
        byte[] data = new byte[]{0x1};
        BigInteger nonce = BigInteger.valueOf(1);
        BigInteger deadline = new BigInteger("1671505027000");
        String digest = ddcSdkClient.getDDC1155MetaTransaction().getSafeMintDigest(to, amount, ddcURI, data, nonce, deadline);
//        byte[] signature = ddcSdkClient.getDDC1155MetaTransaction().generateSignature(originPrivateKey, digest);
        byte[] signature = ddcSdkClient.getDDC1155MetaTransaction().generateSignatureByPem(platform, digest);
        log.info("MetaSafeMint sign: {}", Numeric.toHexString(signature));

        String txHash = getDDC1155Service().metaSafeMint(platformAddress, platformAddress, amount, ddcURI, data, nonce, deadline, signature);
        log.info(txHash);
        assertNotNull(txHash);
        log.info(analyzeRecepit(txHash, abi, bin));
    }

    @Test
    void metaSafeMintBatch() throws Exception {
        String to = "0x81072375a506581CADBd90734Bd00A20CdDbE48b";
        List<BigInteger> amounts = Arrays.asList(BigInteger.valueOf(1), BigInteger.valueOf(1));
        List<String> ddcURIs = Arrays.asList("http://ddcUrl", "http://ddcUrl");
        Multimap<BigInteger, String> ddcs = ArrayListMultimap.create();
        ddcs.put(BigInteger.valueOf(10), "Banana");
        ddcs.put(BigInteger.valueOf(10), "Banana");
        byte[] data = new byte[]{0x11};
        BigInteger nonce = BigInteger.valueOf(10);
        BigInteger deadline = new BigInteger("1671505027000");
        String digest = ddcSdkClient.getDDC1155MetaTransaction().getSafeMintBatchDigest(to, amounts, ddcURIs, data, nonce, deadline);
//        byte[] signature = ddcSdkClient.getDDC1155MetaTransaction().generateSignature(originPrivateKey, digest);
        byte[] signature = ddcSdkClient.getDDC1155MetaTransaction().generateSignatureByPem(platform, digest);
        log.info("MetaMintBatch signature: {}", Numeric.toHexString(signature));

        String txHash = getDDC1155Service().metaSafeMintBatch(platformAddress, platformAddress, ddcs, data, nonce, deadline, signature);
        assertNotNull(txHash);
        log.info(analyzeRecepit(txHash, abi, bin));
    }

    @Test
    void metaSafeTransferFrom() throws Exception {
        String from = "0x81072375a506581CADBd90734Bd00A20CdDbE48b";
        String to = "0x81072375a506581CADBd90734Bd00A20CdDbE48b";
        BigInteger ddcId = BigInteger.valueOf(10);
        BigInteger amount = BigInteger.valueOf(1);
        byte[] data = {0x1};
        BigInteger nonce = BigInteger.valueOf(1);
        BigInteger deadline = new BigInteger("1671505027000");
        String digest = ddcSdkClient.getDDC1155MetaTransaction().getSafeTransferFromDigest(from, to, ddcId, amount, data, nonce, deadline);
//        byte[] signature = ddcSdkClient.getDDC1155MetaTransaction().generateSignature(originPrivateKey, digest);
        byte[] signature = ddcSdkClient.getDDC1155MetaTransaction().generateSignatureByPem(platform, digest);
        log.info("SafeTransferFrom sign: {}", Numeric.toHexString(signature));

        String txHash = getDDC1155Service().metaSafeTransferFrom(platformAddress, platformAddress, consumerAddress, ddcId, amount, data, nonce, deadline, signature);
        assertNotNull(txHash);
        log.info(analyzeRecepit(txHash, abi, bin));
    }

    @Test
    void metaSafeBatchTransferFrom() throws Exception {
        String from = "0x81072375a506581CADBd90734Bd00A20CdDbE48b";
        String to = "0x81072375a506581CADBd90734Bd00A20CdDbE48b";
        List<BigInteger> amounts = Arrays.asList(BigInteger.valueOf(1), BigInteger.valueOf(1));
        List<BigInteger> ddcIds = Arrays.asList(BigInteger.valueOf(839), BigInteger.valueOf(840));
        Map<BigInteger, BigInteger> ddcs = new HashMap<>();
        ddcs.put(BigInteger.valueOf(10), BigInteger.valueOf(1));
        ddcs.put(BigInteger.valueOf(11), BigInteger.valueOf(1));
        byte[] data = new byte[]{0x1};
        BigInteger nonce = BigInteger.valueOf(12);
        BigInteger deadline = new BigInteger("1671505027000");
        String digest = ddcSdkClient.getDDC1155MetaTransaction().getSafeBatchTransferFromDigest(from, to, ddcIds, amounts, data, nonce, deadline);
//        byte[] signature = ddcSdkClient.getDDC1155MetaTransaction().generateSignature(originPrivateKey, digest);
        byte[] signature = ddcSdkClient.getDDC1155MetaTransaction().generateSignatureByPem(platform, digest);
        log.info("SafeTransferFrom sign: {}", Numeric.toHexString(signature));

        String txHash = getDDC1155Service().metaSafeBatchTransferFrom(platformAddress, platformAddress, consumerAddress, ddcs, data, nonce, deadline, signature);
        assertNotNull(txHash);
        log.info(analyzeRecepit(txHash, abi, bin));
    }

    @Test
    void metaBurn() throws Exception {
        String owner = "0x81072375a506581CADBd90734Bd00A20CdDbE48b";
        BigInteger ddcId = BigInteger.valueOf(11);
        BigInteger nonce = BigInteger.valueOf(3);
        BigInteger deadline = new BigInteger("1671505027000");
        String digest = ddcSdkClient.getDDC1155MetaTransaction().getBurnDigest(owner, ddcId, nonce, deadline);
//        byte[] signature = ddcSdkClient.getDDC1155MetaTransaction().generateSignature(originPrivateKey, digest);
        byte[] signature = ddcSdkClient.getDDC1155MetaTransaction().generateSignatureByPem(platform, digest);
        log.info("Burn sign: {}", Numeric.toHexString(signature));

        String txHash = getDDC1155Service().metaBurn(platformAddress, platformAddress, ddcId, nonce, deadline, signature);
        assertNotNull(txHash);
        log.info(analyzeRecepit(txHash, abi, bin));
    }

    @Test
    void metaBurnBatch() throws Exception {
        String owner = "0x81072375a506581CADBd90734Bd00A20CdDbE48b";
        List<BigInteger> ddcIds = new ArrayList<>();
        ddcIds.add(BigInteger.valueOf(14));
        ddcIds.add(BigInteger.valueOf(15));
        BigInteger nonce = BigInteger.valueOf(14);
        BigInteger deadline = new BigInteger("1671505027000");
        String digest = ddcSdkClient.getDDC1155MetaTransaction().getBurnBatchDigest(owner, ddcIds, nonce, deadline);
//        byte[] signature = ddcSdkClient.getDDC1155MetaTransaction().generateSignature(originPrivateKey, digest);
        byte[] signature = ddcSdkClient.getDDC1155MetaTransaction().generateSignatureByPem(platform, digest);
        log.info("BurnBatch sign: {}", Numeric.toHexString(signature));

        String txHash = getDDC1155Service().metaBurnBatch(platformAddress, platformAddress, ddcIds, nonce, deadline, signature);
        assertNotNull(txHash);
        log.info(analyzeRecepit(txHash, abi, bin));
    }

    @Test
    void syncDDCOwners() throws Exception {
        List<BigInteger> ddcIds = new ArrayList<>();
        List<List<String>> owners = new ArrayList<>();
        List<String> ownerList1 = new ArrayList<>();
        List<String> ownerList2 = new ArrayList<>();

        ddcIds.add(new BigInteger("33"));
        ddcIds.add(new BigInteger("34"));
        ownerList1.add(platformAddress);
//        ownerList1.add(consumerAddress);
        ownerList2.add(platformAddress);
//        ownerList2.add(consumerAddress);
        owners.add(ownerList1);
        owners.add(ownerList2);

        String txHash = getDDC1155Service().syncDDCOwners(operatorAddress, ddcIds, owners);
        assertNotNull(txHash);
        log.info(analyzeRecepit(txHash, abi, bin));
    }

}