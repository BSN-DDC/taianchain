package com.reddate.taianddc.util.crypto;

import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.jcajce.provider.asymmetric.ec.BCECPrivateKey;
import org.fisco.bcos.web3j.crypto.ECKeyPair;
import org.fisco.bcos.web3j.utils.Account;
import org.fisco.bcos.web3j.utils.Numeric;
import org.fisco.bcos.web3j.utils.Strings;
import org.junit.jupiter.api.Test;

import java.math.BigInteger;
import java.security.*;
import java.security.interfaces.ECPrivateKey;
import java.security.interfaces.ECPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;

@Slf4j
class PemUtilTest {

    @Test
    public void generatePem() throws InvalidAlgorithmParameterException, NoSuchAlgorithmException, NoSuchProviderException, InvalidKeySpecException {
        Account account = PemUtil.createAccount();
        System.out.println(account.getPrivateKey());
        System.out.println(account.getPublicKey());
        System.out.println(account.getAddress());
    }

    static {
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
    }

    @Test
    public void testHex2Pem() throws Exception {
        String privateKey = "b1e104c4c3d74dc4a9131cde8f3619c1c2d68a442da19dc146159eae0311d8b1";
//        String privateKey = "b7ec288f608cae4b5a8e04c9ca4fb0d4d5beab6590c70b4b55238378f5c29485";

        ECKeyPair ecKeyPair = ECKeyPair.create(new BigInteger(privateKey,16));
        ECPrivateKey ecPrivateKey = PemUtil.toEcPrivateKey(Numeric.toHexStringNoPrefix(ecKeyPair.getPrivateKey()));
        ECPublicKey ecPublicKey = PemUtil.toEcPublicKey(Numeric.toHexStringNoPrefix(ecKeyPair.getPublicKey()));
        String ecPrivateKeyPem = PemUtil.formatToPem(ecPrivateKey.getEncoded(), "PRIVATE KEY");
        String ecPublicKeyPem = PemUtil.formatToPem(ecPublicKey.getEncoded(), "PUBLIC KEY");
        System.out.println(ecPrivateKeyPem);
        System.out.println(ecPublicKeyPem);

        Secp256K1Handle secp256K1Handle = new Secp256K1Handle(ecPrivateKeyPem,ecPublicKeyPem);
        System.out.println(secp256K1Handle.getAddress());


    }

    @Test
    public void testPem2Hex() throws Exception {
        String privateKeyPem = "-----BEGIN PRIVATE KEY-----\n" +
                "MEcCAQAwEAYHKoZIzj0CAQYFK4EEAAoEMDAuAgEBBCDJW8WWMQQvgUpGJ8jhed+p\n" +
                "W/nfOqjn5cuPdnoxgTfKkaAHBgUrgQQACg==\n" +
                "-----END PRIVATE KEY-----";

        PrivateKey privateKey = PemUtil.loadPrivateKey(privateKeyPem);

        BCECPrivateKey bcECPrivateKey = (BCECPrivateKey) privateKey;
        BigInteger privateKeyValue = bcECPrivateKey.getD();
        System.out.println(Numeric.toHexStringWithPrefix(privateKeyValue));
    }

}