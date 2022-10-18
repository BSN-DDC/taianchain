package com.reddate.taianddc.util.crypto;

import org.bouncycastle.jcajce.provider.asymmetric.util.EC5Util;
import org.bouncycastle.jce.ECNamedCurveTable;
import org.bouncycastle.jce.spec.ECNamedCurveParameterSpec;
import org.bouncycastle.jce.spec.ECNamedCurveSpec;
import org.bouncycastle.util.encoders.Base64;
import org.fisco.bcos.web3j.crypto.ECKeyPair;
import org.fisco.bcos.web3j.crypto.Keys;
import org.fisco.bcos.web3j.utils.Account;
import org.fisco.bcos.web3j.utils.Numeric;

import java.math.BigInteger;
import java.security.*;
import java.security.interfaces.ECPrivateKey;
import java.security.interfaces.ECPublicKey;
import java.security.spec.*;

public class PemUtil {

    static {
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
    }

    private static final String BEGIN_EC_PRIVATE_KEY = "-----BEGIN PRIVATE KEY-----";
    private static final String END_EC_PRIVATE_KEY = "-----END PRIVATE KEY-----";
    private static final String BEGIN_EC_PUBLIC_KEY = "-----BEGIN PUBLIC KEY-----";
    private static final String END_EC_PUBLIC_KEY = "-----END PUBLIC KEY-----";
    private static final java.util.Base64.Decoder decoder = java.util.Base64.getDecoder();
    private final java.util.Base64.Encoder encoder = java.util.Base64.getEncoder();

    /**
     * @Description 将从密钥对中读取的私钥或公钥进行转换
     * @Param data 待转换的私钥或公钥证书字节数组
     * @Param type 标识私钥、公钥或公钥证书
     * @Return 返回转换后的字符串
     **/
    public static String formatToPem(byte[] data, String type) {
        String base64Str = Base64.toBase64String(data);
        StringBuilder strBilder = new StringBuilder();
        strBilder.append(String.format("-----BEGIN %s-----\n", type));
        int length = base64Str.length();
        for (int index = 1; index <= length; index++) {
            strBilder.append(base64Str.charAt(index - 1));
            if (index % 64 == 0) {
                strBilder.append('\n');
            }
            if (index == length && index % 64 != 0) {
                strBilder.append('\n');
            }
        }
        strBilder.append(String.format("-----END %s-----\n", type));
        return strBilder.toString();
    }

    /**
     * @Description
     * @Param
     * @Return
     **/
    public static String assembleKey(String pemValue, String keyType) {
        pemValue = pemValue.replace(String.format("-----BEGIN %s KEY-----", keyType.toUpperCase()), "").
                replace(String.format("-----END %s KEY-----", keyType.toUpperCase()), "").
                replace("\n", "");
        StringBuilder strBilder = new StringBuilder();
        strBilder.append(String.format("-----BEGIN %s KEY-----\n", keyType.toUpperCase()));
        int length = pemValue.length();
        for (int index = 1; index <= length; index++) {
            strBilder.append(pemValue.charAt(index - 1));
            if (index % 64 == 0) {
                strBilder.append('\n');
            }
            if (index == length && index % 64 != 0) {
                strBilder.append('\n');
            }
        }
        strBilder.append(String.format("-----END %s KEY-----\n", keyType.toUpperCase()));
        return strBilder.toString();
    }

    /**
     * @Description
     * @Param
     * @Return
     **/
    public static String assembleChainCert(String pemValue) {
        pemValue = pemValue.replace("-----BEGIN CERTIFICATE-----", "").
                replace("-----END CERTIFICATE-----", "").replace("\n", "");
        StringBuilder strBilder = new StringBuilder();
        strBilder.append("-----BEGIN CERTIFICATE-----\n");
        int length = pemValue.length();
        for (int index = 1; index <= length; index++) {
            strBilder.append(pemValue.charAt(index - 1));
            if (index % 64 == 0) {
                strBilder.append('\n');
            }
            if (index == length && index % 64 != 0) {
                strBilder.append('\n');
            }
        }
        strBilder.append("-----END CERTIFICATE-----\n");
        return strBilder.toString();
    }

    public static ECPrivateKey toEcPrivateKey(String privateKey) throws NoSuchAlgorithmException, InvalidKeySpecException {
        ECKeyPair ecKeyPair  = ECKeyPair.create(Numeric.hexStringToByteArray(privateKey));
        ECNamedCurveParameterSpec params  = ECNamedCurveTable.getParameterSpec("secp256k1");
        ECNamedCurveSpec curveSpec  = new ECNamedCurveSpec("secp256k1", params.getCurve(), params.getG(), params.getN());
        ECPrivateKeySpec keySpec = new ECPrivateKeySpec(ecKeyPair.getPrivateKey(),curveSpec);
        KeyFactory keyFactory = KeyFactory.getInstance("ECDSA");
        return (ECPrivateKey) keyFactory.generatePrivate(keySpec);
    }

    public static ECPublicKey toEcPublicKey(String publicKey) throws NoSuchAlgorithmException, InvalidKeySpecException {
        ECNamedCurveParameterSpec params = ECNamedCurveTable.getParameterSpec("secp256k1");
        ECNamedCurveSpec curveSpec = new ECNamedCurveSpec("secp256k1", params.getCurve(), params.getG(), params.getN());

        //This is the part how to generate ECPoint manually from public key string.
        String pubKeyX = publicKey.substring(0, publicKey.length() / 2);
        String pubKeyY = publicKey.substring(publicKey.length() / 2);
        ECPoint ecPoint = new ECPoint(new BigInteger(pubKeyX, 16), new BigInteger(pubKeyY, 16));

        ECParameterSpec params2 = EC5Util.convertSpec(curveSpec.getCurve(), params);

        ECPublicKeySpec keySpec = new ECPublicKeySpec(ecPoint, params2);
        KeyFactory factory = KeyFactory.getInstance("ECDSA");
        return (ECPublicKey) factory.generatePublic(keySpec);
    }

    /**
     * 创建账户
     *
     * @throws InvalidAlgorithmParameterException
     * @throws NoSuchAlgorithmException
     * @throws NoSuchProviderException
     * @throws InvalidKeySpecException
     */
    public static Account createAccount() throws InvalidAlgorithmParameterException, NoSuchAlgorithmException, NoSuchProviderException, InvalidKeySpecException {
        ECKeyPair keyPair = Keys.createEcKeyPair();
        String privateKey = Numeric.toHexStringNoPrefix(keyPair.getPrivateKey());
        String publicKey = Numeric.toHexStringNoPrefix(keyPair.getPublicKey());
        String address = "0x" + Keys.getAddress(keyPair.getPublicKey());

        ECPrivateKey ecPrivateKey = PemUtil.toEcPrivateKey(privateKey);
        ECPublicKey ecPublicKey = PemUtil.toEcPublicKey(publicKey);

        String ecPrivateKeyPem = PemUtil.formatToPem(ecPrivateKey.getEncoded(), "PRIVATE KEY");
        String ecPublicKeyPem = PemUtil.formatToPem(ecPublicKey.getEncoded(), "PUBLIC KEY");

        Account account = new Account();
        account.setPrivateKey(ecPrivateKeyPem);
        account.setPublicKey(ecPublicKeyPem);
        account.setAddress(address);
        return account;

    }

    public static PrivateKey loadPrivateKey(String priKey) throws Exception {
        String privateKeyPEM = priKey.replaceAll(BEGIN_EC_PRIVATE_KEY, "")
                .replaceAll(END_EC_PRIVATE_KEY, "").replace("\r", "").replace("\n", "");
        byte[] asBytes = decoder.decode(privateKeyPEM.replace("\r\n", ""));
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(asBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("ECDSA", "BC");
        return keyFactory.generatePrivate(spec);
    }
}
