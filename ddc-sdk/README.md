本SDK中包含平台方可调用的如下方法：
```

3.2.1BSN-DDC-权限管理
    3.2.1.1添加下级账户
    3.2.1.3查询账户
    3.2.1.4更新账户状态

3.2.2BSN-DDC-费用管理
    3.2.2.1充值
    3.2.2.2链账户余额查询
    3.2.2.3DDC计费规则查询

3.2.3BSN-DDC-721
    3.2.3.1生成
    3.2.3.2DDC授权
    3.2.3.3DDC授权查询
    3.2.3.4账户授权
    3.2.3.5账户授权查询
    3.2.3.6安全转移
    3.2.3.7转移
    3.2.3.10销毁
    3.2.3.11查询数量
    3.2.3.12查询拥有者
    3.2.3.13获取名称
    3.2.3.14获取符号
    3.2.3.15获取DDCURI

3.2.4BSN-DDC-1155
    3.2.4.1生成
    3.2.4.2批量生成
    3.2.4.3账户授权
    3.2.4.4账户授权查询
    3.2.4.5安全转移
    3.2.4.6批量安全转移
    3.2.4.9销毁
    3.2.4.10批量销毁
    3.2.4.11查询数量
    3.2.4.12批量查询数量
    3.2.4.13获取DDCURI

3.2.5BSN-DDC-交易查询
    3.2.5.1查询交易信息
    3.2.5.2查询交易回执
    3.2.5.3查询交易状态

3.2.6BSN-DDC-区块查询
    3.2.6.1获取区块信息

3.2.7BSN-DDC-签名事件

3.2.8BSN-DDC-数据解析

    3.2.8.1权限数据
        3.2.8.1.1添加账户
        3.2.8.1.2更新账户状态

    3.2.8.2充值数据
        3.2.8.2.1充值
        3.2.8.2.2DDC业务费扣除
        3.2.8.2.3设置DDC计费规则
        3.2.8.2.4删除DDC计费规则
        3.2.8.2.5按合约删除DDC计费规则

    3.2.8.3BSN-DDC-721数据
        3.2.8.3.1生成
        3.2.8.3.2转移/安全转移
        3.2.8.3.3冻结
        3.2.8.3.4解冻
        3.2.8.3.5销毁

    3.2.8.4BSN-DDC-1155数据
        3.2.8.4.1生成
        3.2.8.4.2批量生成
        3.2.8.4.3安全转移
        3.2.8.4.4批量安全转移
        3.2.8.4.5冻结
        3.2.8.4.6解冻
        3.2.8.4.7销毁
        3.2.8.4.8批量销毁

```

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
