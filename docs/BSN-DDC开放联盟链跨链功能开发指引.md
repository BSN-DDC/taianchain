## BSN-DDC 泰安链跨链功能开发指引

> 本文介绍泰安链官方DDC 跨链功能详细步骤，跨链功能目前支持文昌链，武汉链，泰安链之间的互跨。如果您没有此需求可忽略此文。



### 参数信息

#### 链ID

- 文昌链 chainId：`2`
- 泰安链 chainId：`3`
- 武汉链 chainId：`4`



### 功能说明

#### 发起跨链交易


##### 1.初始化DDCSdkClient并调用跨链方法
​        为了从泰安链将DDC信息发送到其他开放联盟链，首先您需要初始化DDCSdkClient，调用 `crossChainTransfer` 方法发起跨链，参考示例代码：

``` java
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
// 调用SDK方法发起跨链交易
String txHash = ddcSdkClient.getOpbCrossChainService().crossChainTransfer(sender, ddcType, ddcId, isLock, toChainID, to, data);
```

**请注意：**

- crossChainTransfer方法参数中的接受者账户地址一定要是目标链存在的账户，否则会交易失败。

- 类型为1155的ddc发起跨链时，需要该DDC的拥有者账户拥有全部数量时才可以发起跨链。

  

#### 确认跨链交易

​        发起跨链交易后，您需要通知目标链接收者账户去访问[BSN-DDC开放联盟链跨链网关OpenAPI](BSN-DDC%E5%BC%80%E6%94%BE%E8%81%94%E7%9B%9F%E9%93%BE%E8%B7%A8%E9%93%BE%E7%BD%91%E5%85%B3OpenAPI.md) 去确认交易，才能在目标链执行跨链交易。因此目标链接收者账户需要进行以下步骤：

##### 1.获取跨链中心化服务访问权限

​        首先需要目标链链账户对自己的钱包地址进行签名，签名算法可参考[BSN-DDC开放联盟链跨链网关OpenAPI](BSN-DDC%E5%BC%80%E6%94%BE%E8%81%94%E7%9B%9F%E9%93%BE%E8%B7%A8%E9%93%BE%E7%BD%91%E5%85%B3OpenAPI.md) 的签名算法说明。签名后，请参见[BSN-DDC开放联盟链跨链网关OpenAPI](BSN-DDC%E5%BC%80%E6%94%BE%E8%81%94%E7%9B%9F%E9%93%BE%E8%B7%A8%E9%93%BE%E7%BD%91%E5%85%B3OpenAPI.md)的**【获取Token】**接口，得到访问Token。

##### 2.查询需要签名确认的跨链交易

​        获取到Token后，继续参见[BSN-DDC开放联盟链跨链网关OpenAPI](BSN-DDC%E5%BC%80%E6%94%BE%E8%81%94%E7%9B%9F%E9%93%BE%E8%B7%A8%E9%93%BE%E7%BD%91%E5%85%B3OpenAPI.md)的【**查询需要签名确认的跨链交易**】接口，得到需要签名确认的跨链交易的起始链交易hash，以便确认跨链交易使用。

##### 3.确认跨链交易

​        跨链交易只有目标链账户确认后才能继续进行，为了证明是目标链接收者账户亲自确认交易，目标链账户需要使用第1步获取到的签名和第2步获取到的起始链交易hash，结合目标链接收者账户地址，参见[BSN-DDC开放联盟链跨链网关OpenAPI](BSN-DDC%E5%BC%80%E6%94%BE%E8%81%94%E7%9B%9F%E9%93%BE%E8%B7%A8%E9%93%BE%E7%BD%91%E5%85%B3OpenAPI.md)的【**确认跨链交易**】接口，进行确认。跨链交易确认完成后，BSN-DDC跨链网关会自动向目标链发起跨链交易。