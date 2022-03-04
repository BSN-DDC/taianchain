## ddc-sdk-taian

### Requirements


**Java 1.8 or higher version**

**Already created a project in the BSN-DDC Network. Gateway URL:https://opbningxia.bsngate.com:18602/api/[project_id]/rpc**

**Already created a chain account in the BSN-DDC Network**

### Configuration description
Configuration information is hard-coded into the com.reddate.ddc.config.ConfigCache file, if you need to replace the relevant configuration please modify the information in this file

### Call example

1. Create a chain account
```
    // Generate a chain account
    public void generatePem() throws InvalidAlgorithmParameterException, NoSuchAlgorithmException, NoSuchProviderException, InvalidKeySpecException {
        Account account = PemUtil.createAccount();
        System.out.println(account.getPrivateKey());
        System.out.println(account.getPublicKey());
        System.out.println(account.getAddress());
    }

```

2. Initialize the SDK instance
```
    // Initialize the SDK configuration information
    static DDCSdkClient ddcSdkClient;
    static {
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

3. Call the functions in the contract to mint and transfer DDCs
```
    //Call the functions in the contract by the instances of the contract. Here we mint AND transfer a 721 DDC, and query its DDC ID
    //Mint a DDC and query its DDC ID
    void mint() throws Exception {
        String tx = getDDC721Service().mint(consumerAddress, "0xb0031Aa7725A6828BcCE4F0b90cFE451C31c1e63", "0xb0031Aa7725A6828BcCE4F0b90cFE451C31c1e63");
        log.info(tx);
        assertNotNull(tx);
        while (true) {
            TransactionRecepitBean transactionRecepitBean = getDDC721Service().getTransactionRecepit(tx);
            if (transactionRecepitBean == null) {
                Thread.sleep(200 * 2);
                continue;
            }
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
    
    //Transfer the DDC
     void transferFrom() throws Exception {
        String tx = getDDC721Service().transferFrom(address, "0xb0031Aa7725A6828BcCE4F0b90cFE451C31c1e63", "0x5c5101afe03b416b9735f40ddc3ba7b0c354a5a0", new BigInteger("1"));
        log.info(tx);
    }
    
   
    
```
