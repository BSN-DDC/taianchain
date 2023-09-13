# taianchain java client library

- [taianchain java client library](#taianchain-java-client-library)
    - [合约地址信息：](#合约地址信息)
    - [1.初始化DDCSdkClient](#1.初始化DDCSdkClient)
    - [2.BSN-DDC-权限管理](#2.BSN-DDC-权限管理)
    - [3.BSN-DDC-费用管理](#3.BSN-DDC-费用管理)
    - [4.BSN-DDC-721](#4.BSN-DDC-721)
    - [5.BSN-DDC-1155](#5.BSN-DDC-1155)
    - [6.BSN-DDC-开放联盟链跨链](#6.BSN-DDC-开放联盟链跨链)
    - [7.BSN-DDC-交易查询](#7.BSN-DDC-交易查询)
    - [8.BSN-DDC-区块查询](#8.BSN-DDC-区块查询)
    - [9.BSN-DDC-数据解析](#9.BSN-DDC-数据解析)
    - [10.离线账户创建](#10.离线账户创建)
    - [测试用例](#测试用例)

### 合约地址信息

```
 权限代理合约地址 ： 0xdB208D57e68Dfb224ffEC36e46932E446d81533E
 计费代理合约地址 ： 0x9f186dDea266dB25fd76BF939de538eC60650e31
 DDC 721代理合约地址 ： 0xea485bb4015fd341D917215df98DC53e8b204FeF
 DDC 1155代理合约地址 ： 0x83B61cf8B17e5f2f15E9230e1CAFd036A800e602
 DDC 开放联盟链跨链合约地址：0x6759a28863f1cc62Ef7ECf06c0356baD78Fd4041
```

### 1.初始化DDCSdkClient

```
    static String ecPrivateKeyPem = "-----BEGIN PRIVATE KEY-----\n" +
            "MEcCAQAwEAYHKoZIzj0CAQYFK4EEAAoEMDAuAgEBBCCx4QTEw9dNxKkTHN6PNhnB\n" +
            "wtaKRC2hncFGFZ6uAxHYsaAHBgUrgCCACg==\n" +
            "-----END PRIVATE KEY-----";
    
    static String ecPublicKeyPem = "-----BEGIN PUBLIC KEY-----\n" +
            "MFYwEAYHKoZIzj0CAQYFK4EEAAoDQgAEwhEBa/HRG1COmWAoZePqRN1KKXaVpJ2Y\n" +
            "r6yu1ljcphR0EMiDdeZ7KuWWWrfBvojzSCCCUUmlN4e+vTmwK5oLwQ==\n" +
            "-----END PUBLIC KEY-----";
    static DDCSdkClient ddcSdkClient;
    static {
        // 设置网关
        ddcSdkClient = new DDCSdkClient("https://opbningxia.bsngate.com:18602/api/[project_id]/rpc/");
        Secp256K1SignEventListener signEventListener = null;
        try {
            // Set the private and public keys used to sign the message
            signEventListener = new Secp256K1SignEventListener(ecPrivateKeyPem, ecPublicKeyPem);
        } catch (Exception e) {
            e.printStackTrace();
        }
        ddcSdkClient.registerSignListener(signEventListener);
        
    }
    
    // Instances of each contract are obtained by SDK. Here we use DDC721 instance as an example
    private DDC721Service getDDC721Service() {
        return sdk.getDDC721Service();
    }
    
```

### 2.BSN-DDC-权限管理

```
    AuthorityService authorityService = ddcSdkClient.getAuthorityService();

    // 添加账户
    // sender       签名账户地址
    // account      DDC链账户地址
    // accountName  DDC账户对应的账户名称
    // accountDID   DDC账户对应的DID信息
    // leaderDID    该普通账户对应的上级账户的DID
    // 返回交易哈希
    String txHash = authorityService.addAccountByOperator(sender, account, accountName, accountDID, leaderDID);

    // 批量添加账户
    Map<String,AccountInfo> map = new HashMap<>();
    AccountInfo accountInfo = new AccountInfo();
    accountInfo.setAccountName("ttt");
    accountInfo.setAccountDID("ttt");
    map.put("0x9a5238a5c3a1027a318e344a764b2ddcb016a6cd",accountInfo);

    AccountInfo accountInfo1 = new AccountInfo();
    accountInfo1.setAccountName("ttt2");
    accountInfo1.setAccountDID("ttt2");
    map.put("0x9a5238a5c3a1027a318e344a764b2bdcb01626de",accountInfo1);

    authorityService.addBatchAccountByOperator(operatorAddress,map);

    // 查询账户
    // account DDC用户链账户地址
    // 返回DDC账户信息
    AccountInfo info = authorityService.getAccount(account);

    // 更新账户状态
    // account DDC用户链账户地址
    // state   枚举，状态 ：Frozen - 冻结状态 ； Active - 活跃状态
    // changePlatformState
    // 返回交易哈希
    authorityService.updateAccState(sender, account, 1， false);

    // 设置平台方添加链账户开关
    // 返回交易哈希
    authorityService.setSwitcherStateOfPlatform(sender, true);

    // 查询平台方添加链账户开关状态
    authorityService.switcherStateOfPlatform();

    // 对 DDC 跨平台操作授权
    // 返回交易哈希
    authorityService.crossPlatformApproval(sender, from, to, true);

    // 同步平台方 DID
    // 返回交易哈希
    List<String> dids = new ArrayList<>();
    dids.add("did:bsn:2EBNdfmKiD4qpMqUCnMzuZZqq7GA");
    authorityService.syncPlatformDID(sender, dids);
```

### 3.BSN-DDC-费用管理

```
    ChargeService chargeService = ddcSdkClient.getChargeService();

    // 充值
    // sender 签名账户地址
    // to 充值账户的地址
    // amount 充值金额
    // 返回交易哈希
    String txHash = chargeService.recharge(sender, to, amount);  

    // 批量充值
    Multimap<String, BigInteger> map = ArrayListMultimap.create();
    map.put("0x02a66ef232dac0cd4590d3af2ddb9c2cd95eccc1", new BigInteger("10"));
    map.put("0x201ea42500d8ff71cd897ca51269c0c4e5680aaa", new BigInteger("20"));
    chargeService.rechargeBatch(sender, map);

    // 链账户余额查询
    // accAddr 查询的账户地址
    // 返回账户所对应的业务费余额
    BigInteger balance = chargeService.balanceOf(accAddr);

    // 批量链账户余额查询
    List<String> accAddrs = new ArrayList<>();
    accAddrs.add("0x02CEB40D892061D457E7FA346988D0FF329935DF");
    List<BigInteger> balances = chargeService.balanceOfBatch(accAddrs);

    // DDC计费规则查询
    // ddcAddr DDC业务主逻辑合约地址
    // sig Hex格式的合约方法ID
    // 返回DDC合约业务费
    BigInteger fee = chargeService.queryFee(ddcAddr, "0x36351c7c");

    // 运营账户充值
    // amount 对运营方账户进行充值的业务费
    // 返回交易哈希
    chargeService.selfRecharge(sender, amount);

    // 设置DDC计费规则
    // ddcAddr DDC业务主逻辑合约地址
    // sig Hex格式的合约方法ID
    // amount 业务费用
    // 返回交易哈希
    chargeService.setFee(sender, ddcAddr, sig, amount);

    // 删除DDC计费规则
    // ddcAddr DDC业务主逻辑合约地址
    // sig Hex格式的合约方法ID
    // 返回交易哈希
    chargeService.delFee(sender, ddcAddr, sig);

    // 按合约删除DDC计费规则
    // ddcAddr DDC业务主逻辑合约地址
    // 返回交易哈希
    chargeService.delDDC(sender, ddcAddr);
```

### 4.BSN-DDC-721

```
    DDC721Service ddc721Service = ddcSdkClient.getDDC721Service();

    // DDC授权
    // sender 签名账户地址
    // to     授权者账户
    // ddcId  DDC唯一标识
    // 返回交易哈希
    String txHash = ddc721Service.approve(sender, to, ddcId);

    // DDC授权查询

    // ddcId DDC唯一标识
    // 返回授权的账户
    String account = ddc721Service.getApproved(ddcId);

    // 账户授权
    // operator 授权者账户
    // approved 授权标识
    // 返回交易hash
    ddc721Service.setApprovalForAll(sender,operator, true);

    // 账户授权查询
    // owner    拥有者账户
    // operator 授权者账户
    // 返回授权标识
    Boolean result = ddc721Service.isApprovedForAll(owner, operator);

    // 安全生成
    // sender  签名账户
    // to      授权者账户
    // ddcURI  DDC资源标识符
    // data    附加数据
    // 返回交易hash
    ddc721Service.safeMint(sender, to, ddcURI, data);

    // 生成
    // sender  签名账户
    // to      接收者账户
    // ddcURI  DDC资源标识符
    // 返回交易hash
    ddc721Service.mint(sender, to, ddcURI);

    // 安全转移
    // from  拥有者账户
    // to    授权者账户
    // ddcId DDC唯一标识
    // data  附加数据
    // 返回交易hash
    ddc721Service.safeTransferFrom(sender, from, to, ddcId, data);

    // 转移
    // from  拥有者账户
    // to    接收者账户
    // ddcId ddc唯一标识
    // 返回交易hash
    ddc721Service.transferFrom(sender, from, to, ddcId);

    // 冻结
    // ddcId DDC唯一标识
    // 返回交易hash
    ddc721Service.freeze(sender, ddcId);

    // 解冻
    // ddcId DDC唯一标识
    // 返回交易hash
     ddc721Service.unFreeze(sender, ddcId);

    // 销毁
    // ddcId DDC唯一标识
    // 返回交易hash
    ddc721Service.burn(sender, ddcId);

    // 查询数量
    // owner 拥有者账户
    // 返回ddc的数量
    BigInteger num = ddc721Service.balanceOf(owner);

    // 查询拥有者
    // ddcId ddc唯一标识
    // 返回拥有者账户
    String account = ddc721Service.ownerOf(ddcId);

    // 获取名称
    // 返回DDC运营方名称
    String name = ddc721Service.name();

    // 获取符号
    // 返回DDC运营方符号
    String symbol = ddc721Service.symbol();

    // 获取DDCURI
    // 返回DDC资源标识符
    String ddcURI = ddc721Service.ddcURI(ddcId);

    // 设置DDCURI
    // sender   ddc拥有者或授权者
    // ddcId    ddc唯一标识
    // ddcURI   ddc资源标识符
    ddc721Service.setURI(sender, ddcId, ddcURI)

    // 名称符号设置
    ddc721Service.setNameAndSymbol(sender, "ddc", "ddc721");

    // 最新DDCID查询
    BigInteger DDCID = ddc721Service.getLatestDDCId();

    // 元交易Nonce查询
    // from    元交易签名账户地址
    BigInteger nonce = ddc721Service.getNonce(from);

    // 元交易生成
    // to       接收者账户地址
    // ddcURI   DDC资源标识符
    // nonce    接收者nonce值
    // deadline 元交易有效期
    // sig      元交易签名信息
    String to = "0x81072375a506581CADBd90734Bd00A20CdDbE48b";
    String ddcURI = "http://ddcUrl";
    BigInteger deadline = BigInteger.valueOf(1671096761);
    BigInteger nonce = ddc721Service.getNonce(to).add(BigInteger.ONE);

    String digest = ddcSdkClient.ddc721MetaTransaction.getMintDigest(to, ddcURI, nonce, deadline);
    byte[] sign = ddcSdkClient.ddc721MetaTransaction.generateSignature(originPrivateKey, digest);
    byte[] data = Numeric.hexStringToByteArray("0x16");

    ddc721Service.metaMint(sender, to, ddcURI, nonce, deadline, sign);

    // 元交易安全生成
    // to       接收者账户地址
    // ddcURI   DDC资源标识符
    // data     附加数据
    // nonce    接收者nonce值
    // deadline 元交易有效期
    // sig      元交易签名信息
    String to = "0x81072375a506581CADBd90734Bd00A20CdDbE48b";
    String ddcURI = "http://ddcUrl";
    BigInteger deadline = BigInteger.valueOf(1671096761);
    BigInteger nonce = ddc721Service.getNonce(to).add(BigInteger.ONE);

    String digest = ddcSdkClient.ddc721MetaTransaction.getSafeMintDigest(to, ddcURI, data, nonce, deadline);
    byte[] sign = ddcSdkClient.ddc721MetaTransaction.generateSignature(originPrivateKey, digest);
    byte[] data = Numeric.hexStringToByteArray("0x16");

    ddc721Service.metaSafeMint(sender, to, ddcURI, data, nonce, deadline, sign);

    // 元交易转移
    // from     拥有者账户地址
    // to       接收者账户地址
    // ddcURI   DDC资源标识符
    // nonce    拥有者nonce值
    // deadline 元交易有效期
    // sig      元交易签名信息
    String from = "0x81072375a506581CADBd90734Bd00A20CdDbE48b";
    String to = "0x81072375a506581CADBd90734Bd00A20CdDbE48b";
    BigInteger ddcId = BigInteger.valueOf(8525);
    BigInteger deadline = BigInteger.valueOf(1671096761);
    BigInteger nonce = ddc721Service.getNonce(from).add(BigInteger.ONE);

    String digest = ddcSdkClient.ddc721MetaTransaction.getTransferFromDigest(from, to, ddcId, nonce, deadline);
    byte[] sign = ddcSdkClient.ddc721MetaTransaction.generateSignature(originPrivateKey, digest);
    log.info("TransferFrom sign: {}", Numeric.toHexString(signature));

    ddc721Service.metaTransferFrom(sender, from, to, ddcId, nonce, deadline, sign);

    // 元交易安全转移
    // from     拥有者账户地址
    // to       接收者账户地址
    // ddcURI   DDC资源标识符
    // data     附加数据
    // nonce    拥有者nonce值
    // deadline 元交易有效期
    // sig      元交易签名信息
    String from = "0x81072375a506581CADBd90734Bd00A20CdDbE48b";
    String to = "0x81072375a506581CADBd90734Bd00A20CdDbE48b";
    BigInteger ddcId = BigInteger.valueOf(8525);
    byte[] data = Numeric.hexStringToByteArray("0x16");
    BigInteger deadline = BigInteger.valueOf(1671096761);
    BigInteger nonce = ddc721Service.getNonce(from).add(BigInteger.ONE);

    String digest = ddcSdkClient.ddc721MetaTransaction.getSafeTransferFromDigest(from, to, ddcId, data, nonce, deadline);
    byte[] sign = ddcSdkClient.ddc721MetaTransaction.generateSignature(originPrivateKey, digest);

    ddc721Service.metaSafeTransferFrom(sender, from, to, ddcId, data, nonce, deadline, sign);

    // 元交易销毁
    // ddcId    DDC唯一标识符
    // nonce    拥有者nonce值
    // deadline 元交易有效期
    // sig      元交易签名信息
    String from = "0x81072375a506581CADBd90734Bd00A20CdDbE48b";
    BigInteger ddcId = BigInteger.valueOf(8526);
    BigInteger deadline = BigInteger.valueOf(1671096761);
    BigInteger nonce = ddc721Service.getNonce(from).add(BigInteger.ONE);

    String digest = ddcSdkClient.ddc721MetaTransaction.getBurnDigest(ddcId, nonce, deadline);
    byte[] sign = ddcSdkClient.ddc721MetaTransaction.generateSignature(originPrivateKey, digest);

    ddc721Service.metaBurn(sender, ddcId, nonce, deadline, sign);
```

### 5.BSN-DDC-1155

```
    DDC1155Service ddc1155Service = ddcSdkClient.getDDC1155Service();

    // 账户授权
    // sender   签名账户
    // operator 授权者账户
    // approved 授权标识
    // 返回交易哈希
    String txHash  = ddc1155Service.setApprovalForAll(sender, operator, approved);

    // 账户授权查询
    // owner    拥有者账户
    // operator 授权者账户
    // 返回授权结果（boolean）
    Boolean result = isApprovedForAll(owner, operator);

    // 安全转移
    // from   拥有者账户
    // to     接收者账户
    // ddcId  DDCID
    // amount 需要转移的DDC数量
    // data   附加数据
    // 返回交易哈希
    ddc1155Service.safeTransferFrom(sender, from, to, ddcId, amount, data);

    // 批量安全转移
    // from 拥有者账户
    // to   接收者账户
    // ddcs 拥有者DDCID集合
    // data 附加数据
    // 返回交易哈希
    String txHash  = ddc1155Service.safeBatchTransferFrom(sender, from, to, ddcs, data);

    // 冻结
    // ddcId DDC唯一标识
    // 返回交易哈希
    ddc1155Service.freeze(sender, ddcId);

    // 解冻
    // ddcId DDC唯一标识
    // 返回交易哈希
    ddc1155Service.unFreeze(sender, ddcId);

    // 销毁
    ddc1155Service.burn(sender, owner, ddcId);

    // 批量销毁
    ddc1155Service.burnBatch(sender, owner, ddcIds);

    // 查询数量
    BigInteger balance = ddc1155Service.balanceOf(owner, ddcId);

    // 批量查询数量
    Multimap<String, BigInteger> map = ArrayListMultimap.create();
    map.put("0x9dff125d6562df4d72b9bd4616c815a2b45c39ab", new BigInteger(82));
    map.put("0x9dff125d6562df4d72b9bd4616c815a2b45c39ab", new BigInteger(83));
    List<BigInteger> balances = ddc1155Service.balanceOfBatch(map);

    // 获取DDCURI
    String ddcURI = ddc1155Service.ddcURI(ddcId);

    // 设置ddcURL
    // sender 签名账户
    // ddcId  DDC唯一标识
    // ddcURL DDC资源标识符
    ddc1155Service.setURI(sender, owner，new BigInteger(""),"");

    // 最新DDCID查询
    BigInteger DDCID = ddc1155Service.getLatestDDCId();

    // 元交易Nonce查询
    BigInteger Nonce = ddc1155Service.getNonce(owner);

    // 元交易安全生成
    ddc1155Service.metaSafeMint(sender,  to,  amount,  ddcURI,  data,  nonce,  deadline,  sign);

    // 元交易安全转移
    ddc1155Service.metaSafeTransferFrom(sender, from, to, ddcId, amount, data, nonce, deadline, sign);

    // 元交易销毁
    ddc1155Service.metaBurn(sender, owner, ddcId, amount, data, nonce, deadline, sign);
```

### 6.BSN-DDC-开放联盟链跨链

```
    // 泰安链签名账户地址
    String sender = "0x057b5061c4e2ebce5482b63def1de5a21a66d1f6";
    // DDC类型
    DDCType ddcType = DDCType.TYPE_721;
    // 目标链接收者账户地址
    String to = "0x057b5061c4e2ebce5482b63def1de5a21a66d1f6";
    // DDC唯一标识
    BigInteger ddcId = BigInteger.valueOf(2987);
    // 是否锁定
    Boolean isLock = true;
    // 附加数据
    String data = "0x";
    // 目标链chainId
    BigInteger toChainID = BigInteger.valueOf(2);
    // 调用跨链方法发起跨链交易
    String txHash = ddcSdkClient.getOpbCrossChainService().crossChainTransfer(sender, ddcType, ddcId, isLock, toChainID, to, data);
```

### 7.BSN-DDC-交易查询

```
    BaseService baseService = new BaseService();

    // 查询交易回执
    // hash 交易哈希
    // 返回交易回执
     TransactionReceipt TxReceipt = baseService.getTransactionReceipt(hash);

     // 查询交易信息
     // hash 交易哈希
     // 返回交易信息
     Transaction Tx = baseService.getTransactionByHash(hash);

     // 查询交易状态
     // hash 交易哈希
     // 返回交易状态
     Boolean state = baseService.getTransByStatus(hash);

     // 查询交易数（Nonce）
     // address 账户地址
     // 返回交易数
     BigInteger nonce = baseService.getTransactionCount(address);
```

### 8.BSN-DDC-区块查询

```
    BaseService baseService = new BaseService();

    // 获取区块信息
    EthBlock.Block blockinfo = baseService.getBlockByNumber(blockNumber)
```

### 9.BSN-DDC-数据解析

```
3.1.9    BSN-DDC-数据解析
    3.1.9.1    权限数据
        3.1.9.1.1    添加账户开关设置
        3.1.9.1.2    添加账户
        3.1.9.1.3    批量添加账户
        3.1.9.1.4    更新账户状态
        3.1.9.1.5    跨平台授权
        3.1.9.1.6    同步平台方DID
    3.1.9.2    充值数据
        3.1.9.2.1    充值
        3.1.9.2.2    批量充值
        3.1.9.2.3    DDC业务费扣除
        3.1.9.2.4    设置DDC计费规则
        3.1.9.2.5    删除DDC计费规则
        3.1.9.2.6    删除DDC合约授权
    3.1.9.3    721数据
        3.1.9.3.1    生成
        3.1.9.3.2    安全生成
        3.1.9.3.3    批量生成
        3.1.9.3.4    批量安全生成
        3.1.9.3.5    转移
        3.1.9.3.6    安全转移
        3.1.9.3.7    冻结
        3.1.9.3.8    解冻
        3.1.9.3.9    销毁
        3.1.9.3.10    URI设置
        3.1.9.3.13    元交易生成
        3.1.9.3.14    元交易安全生成
        3.1.9.3.15    元交易批量生成
        3.1.9.3.16    元交易批量安全生成
        3.1.9.3.17    元交易转移
        3.1.9.3.18    元交易安全转移
        3.1.9.3.19    元交易销毁
    3.1.9.4    1155数据
        3.1.9.4.1    安全生成
        3.1.9.4.2    批量安全生成
        3.1.9.4.3    安全转移
        3.1.9.4.4    批量安全转移
        3.1.9.4.5    冻结
        3.1.9.4.6    解冻
        3.1.9.4.7    销毁
        3.1.9.4.8    批量销毁
        3.1.9.4.9    URI设置
        3.1.9.4.12    元交易安全生成
        3.1.9.4.13    元交易批量安全生成
        3.1.9.4.14    元交易安全转移
        3.1.9.4.15    元交易批量安全转移
        3.1.9.4.16    元交易销毁
        3.1.9.4.17    元交易批量销毁
```

```
    BlockEventService blockEventService = ddcSdkClient.blockEventService;

    // 获取区块事件并解析
    // 1. 根据块高获取区块信息
    // 2. 根据块中交易获取交易回执
    // 3. 遍历交易回执中的事件并解析
    // blockNumber 块高
    // 返回 ArrayList<Object>

    ArrayList<BaseEventResponse> blockEvent = blockEventService.getBlockEvent("28684");
    blockEvent.forEach(b->{
        System.out.println(b.log);
    });
```

### 10.离线账户创建

```
    // 返回包含助记词，公钥，私钥，pem格式地址的Account对象
    Account account = PemUtil.createAccount();
    System.out.println(account.getPrivateKey());
    System.out.println(account.getPublicKey());
    System.out.println(account.getAddress());
```

## 测试用例

[AuthorityServiceTest.java](src/test/java/com/reddate/taianddc/service/AuthorityServiceTest.java)

[ChargeServiceTest.java](src/test/java/com/reddate/taianddc/service/ChargeServiceTest.java)

[BaseServiceTest.java](src/test/java/com/reddate/taianddc/service/BaseServiceTest.java)

[BlockEventServiceTest.java](src/test/java/com/reddate/taianddc/service/BlockEventServiceTest.java)

[DDC721ServiceTest.java](src/test/java/com/reddate/taianddc/service/DDC721ServiceTest.java)

[DDC1155ServiceTest.java](src/test/java/com/reddate/taianddc/service/DDC1155ServiceTest.java)

[PemUtilTest.java](src/test/java/com/reddate/taianddc/util/crypto/PemUtilTest.java)

[CrossChainServiceTest.java](src/test/java/com/reddate/taianddc/service/CrossChainServiceTest.java)

[OpbCrossChainServiceTest.java](src/test/java/com/reddate/taianddc/service/OpbCrossChainServiceTest.java)
