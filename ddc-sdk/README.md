## ddc-sdk-taian

### 要求

**Java 1.8 或 更高**


### 配置文件
请参考：src/main/resource/sdk-config.yml

```yaml
restTemplate:
  ## HTTP超时时间
  conTimeout: 60
  readTimeout: 60
  ## 网关地址
  opbGatewayAddress: https://opbtest.bsngate.com:18602/api/2ad7f9e442e2401e8e885b30277724c3/rpc

contract:
  ## DDC721合约地址
  ddc721Addr: "0xbf615Db72C3eF57724d3b3483da65cDAD938744f"
  ## DDC1155合约地址
  ddc1155Addr:  "0x38a0F1D843F68E782F1B8d879a51d078371b8a9b"
  ## 权限逻辑合约地址
  authorityLogicAddr: "0x95eB7D1169A314E49B34B0A67fE5d6a939F7C8F4"
  ## 计费逻辑合约地址
  chargeLogicAddr: "0xdBAD63EbBA20696e6EC3413e5D7315DcaDE463Ca"
```

### 调用示例

1. 创建链账户
```
    // 生成链账户
    public void generatePem() throws InvalidAlgorithmParameterException, NoSuchAlgorithmException, NoSuchProviderException, InvalidKeySpecException {
        ECKeyPair keyPair = Keys.createEcKeyPair();
        String privateKey = Numeric.toHexStringNoPrefix(keyPair.getPrivateKey());
        String publicKey = Numeric.toHexStringNoPrefix(keyPair.getPublicKey());
        String address = "0x" + Keys.getAddress(keyPair.getPublicKey());

        ECPrivateKey ecPrivateKey = PemUtil.toEcPrivateKey(privateKey);
        ECPublicKey ecPublicKey = PemUtil.toEcPublicKey(publicKey);

        String ecPrivateKeyPem = PemUtil.formatToPem(ecPrivateKey.getEncoded(), "PRIVATE KEY");
        String ecPublicKeyPem = PemUtil.formatToPem(ecPublicKey.getEncoded(), "PUBLIC KEY");

        log.info("ecPrivateKeyPem: \n{}",ecPrivateKeyPem);
        log.info("ecPublicKeyPem: \n{}",ecPublicKeyPem);
        log.info("address: \n{}",address);
    }

```

2. 初始化SDK实例
```
    // 初始化SDK配置信息
    static DDCSdkClient sdk = new DDCSdkClient();
    static {
        sdk.init();
        try {
            //初始化签名方式，此处以本地签名为例
            sdk.registerSignListener(new Secp256K1SignEventListener(ecPrivateKeyPem, ecPublicKeyPem));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    // 每个合约的实例通过sdk获取，此处以获取721实例为例
    private DDC721Service getDDC721Service() {
        return sdk.getDDC721Service();
    }

```

3. 调用合约方法进行DDC的发行、流转
```
    // 通过合约的实例进行该合约内方法的调用，此处以发行、流转一个DDC721为例
    void mint() throws Exception {
        for (int i = 0; i < 1; i++) {
            String tx = getDDC721Service().mint("0xb0031Aa7725A6828BcCE4F0b90cFE451C31c1e63","0xb0031Aa7725A6828BcCE4F0b90cFE451C31c1e63");
            log.info(tx);
        }
    }
    
    void safeTransferFrom() throws Exception {
        byte[] data = new byte[1];
        data[0] = 1;
        String tx = getDDC721Service().safeTransferFrom("0xb0031aa7725a6828bcce4f0b90cfe451c31c1e63","0x179319b482320c74be043bf0fb3f00411ca12f8d",new BigInteger("150"),data);
        log.info(tx);
        assertNotNull(tx);
    }
```
