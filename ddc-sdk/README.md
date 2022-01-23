## ddc-sdk-taian

### 要求

**Java 1.8 或 更高**


### 配置说明
配置信息硬编码到com.reddate.ddc.config.ConfigCache文件中，如需更换相关配置请修改该文件下的信息

### 调用示例

1. 创建链账户
```
    // 生成链账户
    public void generatePem() throws InvalidAlgorithmParameterException, NoSuchAlgorithmException, NoSuchProviderException, InvalidKeySpecException {
        Account account = PemUtil.createAccount();
        System.out.println(account.getPrivateKey());
        System.out.println(account.getPublicKey());
        System.out.println(account.getAddress());
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
    //通过合约的实例进行该合约内方法的调用，此处以发行、查看ddcId、流转一个DDC721为例
    //发行DDC
    void mint() throws Exception {
        String tx = getDDC721Service().mint(address, "0xb0031Aa7725A6828BcCE4F0b90cFE451C31c1e63", "0xb0031Aa7725A6828BcCE4F0b90cFE451C31c1e63");
        log.info(tx);
    }
    
    //查看721 DDCID 信息
    void getDDCInfoByBlockNumber() throws BaseException, InterruptedException, IOException, InvocationTargetException, IllegalAccessException, NoSuchMethodException, InstantiationException {
        BlockEventService blockEventService = new BlockEventService();
        String blockNumber = "760261";
        ArrayList result = new ArrayList();
        result.addAll(blockEventService.getBlockEvent(blockNumber));

        result.forEach( t -> {
            if (t instanceof DDC721TransferEventBean) {
                log.info("{}:DDCID {}",t.getClass(),((DDC721TransferEventBean) t).getDdcId());
            }
        });
    }
    
    //流转DDC
     void transferFrom() throws Exception {
        String tx = getDDC721Service().transferFrom(address, "0xb0031Aa7725A6828BcCE4F0b90cFE451C31c1e63", "0x5c5101afe03b416b9735f40ddc3ba7b0c354a5a0", new BigInteger("1"));
        log.info(tx);
    }
    
   
    
```
