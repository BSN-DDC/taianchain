package com.reddate.ddc.util.crypto;

import lombok.extern.slf4j.Slf4j;
import org.fisco.bcos.web3j.crypto.ECKeyPair;
import org.fisco.bcos.web3j.crypto.Keys;
import org.fisco.bcos.web3j.utils.Numeric;
import org.junit.jupiter.api.Test;

import java.math.BigInteger;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.interfaces.ECPrivateKey;
import java.security.interfaces.ECPublicKey;
import java.security.spec.InvalidKeySpecException;

@Slf4j
class PemUtilTest {

    @Test
    public void generatePem() throws InvalidAlgorithmParameterException, NoSuchAlgorithmException, NoSuchProviderException, InvalidKeySpecException {
        ECKeyPair keyPair = Keys.createEcKeyPair();
        String privateKey = Numeric.toHexStringNoPrefix(keyPair.getPrivateKey());
        String publicKey = Numeric.toHexStringNoPrefix(keyPair.getPublicKey());
        String address = "0x" + Keys.getAddress(keyPair.getPublicKey());

        ECPrivateKey ecPrivateKey = PemUtil.toEcPrivateKey(privateKey);
        ECPublicKey ecPublicKey = PemUtil.toEcPublicKey(publicKey);

        String ecPrivateKeyPem = PemUtil.formatToPem(ecPrivateKey.getEncoded(), "PRIVATE KEY");
        String ecPublicKeyPem = PemUtil.formatToPem(ecPublicKey.getEncoded(), "PUBLIC KEY");

        System.out.println(ecPrivateKeyPem);
        System.out.println(ecPublicKeyPem);
        System.out.println(address);
    }

}