# 组件介绍
## 功能介绍
key-toolkit用于私钥生成、私钥格式转换、私钥分片保管、签名验签、加密解密。支持轻量级jar包接入。

就私钥生成方式而言，支持如下方式：
*   ECC随机生成私钥，既支持经典的Sec曲线，也支持国密标准sm2曲线
*   助记词生成私钥，遵循BIP39协议

就私钥加解密而言，支持如下方式：
*   KeyStore私钥存储格式
*   PEM存储格式
*   P12存储格式   

就私钥分片保管而言，支持Shamir算法。

就签名验签、加解密，支持经典的曲线secp256k1，也支持国密标准曲线sm2p256v1。

## 基本概念
### 私钥、公钥、
在非对称加密领域，对数据的加解密、签名都依赖于密钥对。在密钥对中，公开的密钥叫公钥，只有自己知道的叫私钥。
非对称加密有许多体系，最著名的是RSA、DH、ECDSA。其中ECDSA是区块链领域采纳的密钥体系，也称为椭圆曲线体系，该体系中，私钥是一个某范围内的整数，公钥则是曲线上的一个点。

### 曲线
在椭圆曲线体系中，密钥生成依赖于曲线参数，一条曲线中合法的密钥对，在另一条曲线的环境中，就是非法密钥。最经典的曲线之一就是secp256k1，为多种主流区块链采纳；另一种曲线是国密曲线sm2p256v1，该曲线满足我国的密码学标准。

### 地址
在区块链中，无论采用UTXO模型还是账户模型，均需要用地址来标记一个使用者。地址由公钥确定性的生成。

### 助记词
私钥是一个很大的数字，例如0xfe2186d66e3070fa77bc431e0c9a28f02fdaf93f51724293842c246b923d0f8f，这样的数字是不利于记忆和口头传输的，所以BIP-39提出了基于助记词的私钥方案，在该方案中，会生成一串随机的单词构成的字符串，这个字符串就是助记词，助记词可以确定性的生成私钥。

### 分片还原
私钥可以分解成n块，分别由不同的人持有，这被称为分片；如果凑齐t块，就可以还原出完整的私钥，这被称为还原。整个模式被称为(n,t)门限的秘密还原，基于一元多次方程的shamir分片还原是最经典的一种方式。



# 部署说明

目前支持从源码进行部署。

### 1. 获取源码

通过git下载源码：

```
git clone https://github.com/WeBankBlockchain/key-center.git
```

进入目录：
```
cd key-center
cd key-toolkit
```

### 2. 编译源码

方式一：如果服务器已安装Gradle
```
gradle build -x test
```

方式二：如果服务器未安装Gradle，使用gradlew编译
```
chmod +x ./gradlew && ./gradlew build -x test
```

### 3. 导入jar包

key-toolkit编译之后在根目录下会生成dist文件夹，文件夹中包含key-toolkit.jar。可以将其导入到自己的项目中，例如libs目录下。然后进行依赖配置。gradle依赖配置如下，然后再对自己的项目进行编译。

```
repositories {
    mavenCentral()
    mavenLocal()
    maven {
        url "http://maven.aliyun.com/nexus/content/groups/public/"
    }
}

dependencies {
    compile "org.apache.commons:commons-lang3:3.6"
    compile 'com.lhalcyon:bip32:1.0.0'
    compile group: 'org.bouncycastle', name: 'bcprov-jdk15on', version: '1.60'
    compile group: 'org.bouncycastle', name: 'bcpkix-jdk15on', version: '1.60'
    compile 'org.web3j:core:3.4.0'
    compile "commons-io:commons-io:2.6"
    compile 'com.lambdaworks:scrypt:1.4.0'
    compile 'commons-codec:commons-codec:1.9'
    testCompile group: 'junit', name: 'junit', version: '4.12'
    compile fileTree(dir:'libs',include:['*.jar'])
}

```
### 4. 接口使用

key-toolkit中包含若干类服务接口，如下，接口使用可以直接通过new对象然后调用。

- PkeyByRandomService：随机数通过secp256k1椭圆曲线生成私钥
- PkeySM2ByRandomService: 随机数通过国密sm2椭圆曲线生成私钥
- PkeyByMnemonicService：助记词生成种子后，通过椭圆曲线生成私钥
- PkeyEncryptService：私钥的加解密，支持Keystore、PEM、P12三种形式
- PkeyShardingService：私钥的分片和还原
- ECCSignService: 通过secp256k1曲线进行签名或验签
- SM2SignService：国密的签名和验签
- ECCEncryptService: 通过secp256k1曲线的加密解密
- SM2EncryptService: 国密曲线的加密解密

#### 4.1 PkeyByRandomService使用

PkeyByRandomService提供两个接口：generatePrivateKey、generatePrivateKeyByChainCode。
- generatePrivateKey：随机生成256位的私钥
- generatePrivateKeyByChainCode：根据私钥和用户自己的熵值（256位）派生下一级的私钥

```java

private PkeyByRandomService service = new PkeyByRandomService();
    
@Test
public void testGeneratePrivateKey() throws Exception{
    PkeyInfo pkeyInfo = service.generatePrivateKey();
    log.info(Numeric.toHexString(pkeyInfo.getPrivateKey()));
}
    
@Test
public void testGeneratePrivateKeyByChainCode() throws PkeyGenException{
	String privateKey = "2c8fa96c22238e071743ee7c5b9a2b331f474f4e42d720aa3b48a507d6c5c967";
	String chainCode = "32C4AE2C1F1981195F9904466A39C9948FE30BBFF2660BE1715A4589334C74C7";
	PkeyInfo pkeyInfo = service.generatePrivateKeyByChainCode(Numeric.hexStringToByteArray(privateKey), chainCode);	   
    log.info(Numeric.toHexString(pkeyInfo.getPrivateKey()));
}
```

#### 4.2 PkeySM2ByRandomService使用

PkeySM2ByRandomService提供接口：generatePrivateKey，用于通过随熵生成国密SM2曲线的密钥

```java
private PkeySM2ByRandomService service = new PkeySM2ByRandomService();
		
@Test
public void example() throws Exception{
    PkeyInfo pkeyInfo = service.generatePrivateKey();
    log.info(Numeric.toHexString(pkeyInfo.getPrivateKey()));
}   
```

#### 4.3 PkeyByMnemonicService使用

PKeyByMnemonicService提供三个接口：createMnemonic、generatePrivateKeyByMnemonic、generatePrivateKeyByChainCode。
- createMnemonic：生成助记词
- generatePrivateKeyByMnemonic：根据助记词生成256位的私钥
- generatePrivateKeyByChainCode：根据助记词和chain code生成下级私钥及chain code

```java
private PkeyByMnemonicService service = new PkeyByMnemonicService();
	    
@Test
public void testCreateMnemonic(){
    String mnemonicStr = service.createMnemonic(null);
    log.info("mnemonic str : {}", mnemonicStr);
}
	    
@Test
public void testGeneratePrivateKeyByMnemonic() throws CipherException, PkeyGenException{
    String mnemonicStr = "alpha segment cube animal wash ozone dream search uphold tennis fury abuse";
    PkeyInfo pkeyInfo = service.generatePrivateKeyByMnemonic(mnemonicStr, null, EccTypeEnums.SM2P256V1.getEccType();
    log.info("pkey info : {}", JacksonUtils.toJson(pkeyInfo));
}

@Test
public void testGeneratePrivateKeyByChainCode() throws Exception{
	String mnemonicStr = "alpha segment cube animal wash ozone dream search uphold tennis fury abuse";
	String chaincodeString  = "0x8902265ac00b3e99baef1ffaffe0e9b23ccd71fb482bc32df25b2877c32c33f5";
	PkeyInfo pkeyInfo = service.generatePrivateKeyByChainCode(mnemonicStr, chaincodeString, EccTypeEnums.SECP256K1.getEccType());
    log.info(Numeric.toHexString(pkeyInfo.getPrivateKey()));
}	   
```

#### 4.4 PkeyEncryptService使用

PkeyEncryptService提供6个接口：encryptKeyStoreFormat、decryptKeystoreFormat、encryptP12Format、decryptP12Format、encryptPEMFormat、decryptPEMFormat。这些接口分为三组，分别对应Keystore、P12、PEM格式。
- encryptKeyStoreFormat：将私钥加密为Keystore格式文件
- decryptKeystoreFormat：解密Keystore格式文件，得到私钥
- encryptP12Format: 将私钥加密为p12格式文件，该文件可被openssl等工具访问。
- decryptP12Format：解密p12格式文件，得到私钥
- encryptPEMFormat：将私钥加密为pem格式文件，该文件可被openssl等工具访问。
- decryptPEMFormat：解密pem格式文件，得到私钥

```java

private PkeyByRandomService keyGenerationService = new PkeyByRandomService();

private PkeyEncryptService service = new PkeyEncryptService();

@Test
public void testKeyStoreEncrypt() throws Exception{
        //用keystore加密示例
    PkeyInfo pkey = keyGenerationService.generatePrivateKey();
    String password = "123456";
    String destinationDirectory = "~";//示例输出目录，加密后的私钥会存放在该目录下
    service.encryptKeyStoreFormat(password, pkey.getPrivateKey(), pkey.getAddress(), destinationDirectory);
}

@Test
public void testKeyStoreDecrypt() throws Exception{
    //解密keystore示例
    String password = "123456";
    String file = "~/UTC--2020-03-04T07-41-13.789000000Z--0x3acce050fd8160c6b749b222b732c946ed993c19.json";
    byte[] privateKey = service.decryptKeystoreFormat(password, file);
    log.info("private key : {}", Numeric.toHexString(privateKey));
}

@Test
public void testPEMEncrypt() throws Exception{
    //用pem加密示例
    PkeyInfo pkey = keyGenerationService.generatePrivateKey();
    String destinationDirectory = "~";//示例输出目录，加密后的私钥会存放在该目录下
    service.encryptPEMFormat(pkey.getPrivateKey(), EccTypeEnums.SECP256K1.getEccType(), pkey.getAddress(), destinationDirectory);
}

 @Test
public void testPEMDecrypt() throws Exception{
    //解密pem示例
    String file = "~/UTC--2020-03-04T07-41-13.789000000Z--0x3acce050fd8160c6b749b222b732c946ed993c19.pem";
    byte[] privateKey = service.decryptPEMFormat(file);
    log.info("private key : {}", Numeric.toHexString(privateKey));
}

@Test
public void testP12Encrypt() throws Exception{
    //用p12加密示例
    PkeyInfo pkey = keyGenerationService.generatePrivateKey();
    String password = "123456";
    String destinationDirectory = "~";//示例输出目录，加密后的私钥会存放在该目录下
    service.encryptP12Format(password, pkey.getPrivateKey(), EccTypeEnums.SECP256K1.getEccName(), pkey.getAddress(), destinationDirectory);
}

@Test
public void testP12Decrypt() throws Exception{
    //解密p12示例
    String password = "123456";
    String file = "~/UTC--2020-03-04T07-41-13.789000000Z--0x3acce050fd8160c6b749b222b732c946ed993c19.p12";//
    byte[] privateKey = service.decryptP12Format(password, file);
    log.info("private key : {}", Numeric.toHexString(privateKey));
}
```

#### 4.5 PkeyShardingService使用

PkeyShardingService提供两个接口：shardingPKey、recoverPKey。
- shardingPKey：根据输入将私钥分成多分
- recoverPKey：根据私钥分片还原私钥

```java
@
private PkeyShardingService service = new PkeyShardingService();
    
@Test
public void testSharding(){
    String testStr = "this is a test string";
    List<String> list = service.shardingPKey(testStr, 5, 3);
    for (String str : list) {
        log.info(str);
    }  
     
   List<String> newList = new ArrayList<>();
    newList.add(list.get(0));
    newList.add(list.get(1));
    newList.add(list.get(4));
    log.info(service.recoverPKey(newList));
}
```

#### 4.6 ECCSignService

签名和验签示例
```
    String utf8Msg = "HelloEccSign";
    String eccPrivateKey = "28018238ac7eec853401dfc3f31133330e78ac27a2f53481270083abb1a126f9";
    String eccPublicKey = "0460fc2bce5795ee2ac34d1f584f603b4e2920a95d8d3db5f5c664244a99fd76405831ffaf932f64eae3ec67bc8ff7bfed9039f29bf39ce6583d55ca449b64319e";

    ECCSignService eccSignService = new ECCSignService();
    String eccSignature = eccSignService.sign(utf8Msg, eccPrivateKey);
    System.out.println("ecc signature:"+eccSignature);

    boolean eccVerifyResult = eccSignService.verify(utf8Msg, eccSignature, eccPublicKey);
    System.out.println("ecc verify result:"+eccVerifyResult);

```


#### 4.7 SM2SignService

签名和验签示例
```
    String gmMsg = "HelloGM";
    String gmPrivateKey = "73c8a8054b5e42b0d089e24f16c665bc82a132082d258c5efb54c49a3b7273f9";
    String gmPublicKey = "0451c895673d372267a565c4a7711102108138132b21f22ed556df08fb4c8cfdcaf17dcb605f8a6394f8684aa1916df60929532faf808c36c133ce52356d0f45f3";

    SM2SignService sm2SignService = new SM2SignService();
    String gmSignature = sm2SignService.sign(gmMsg, gmPrivateKey);
    System.out.println("gm signature:"+gmSignature);

    boolean gmVerifyResult = sm2SignService.verify(gmMsg, gmSignature, gmPublicKey);
    System.out.println("gm verify result:"+gmVerifyResult);
```

#### 4.8 ECCEncryptService

加解密示例

```
    String utf8Msg = "HelloEccSign";
    String eccPrivateKey = "28018238ac7eec853401dfc3f31133330e78ac27a2f53481270083abb1a126f9";
    String eccPublicKey = "0460fc2bce5795ee2ac34d1f584f603b4e2920a95d8d3db5f5c664244a99fd76405831ffaf932f64eae3ec67bc8ff7bfed9039f29bf39ce6583d55ca449b64319e";

    ECCEncryptService eccEncryptService = new ECCEncryptService();
    String eccCipherText = eccEncryptService.encrypt(eccMsg, eccPublicKey);
    System.out.println("ecc encryption cipher:"+eccCipherText);

    String eccPlainText = eccEncryptService.decrypt(eccCipherText, eccPrivateKey);
    System.out.println("ecc decryption result:"+eccPlainText);
```

#### 4.9 SM2EncryptService

加解密示例

```
    String gmMsg = "HelloGM";
    String gmPrivateKey = "73c8a8054b5e42b0d089e24f16c665bc82a132082d258c5efb54c49a3b7273f9";
    String gmPublicKey = "0451c895673d372267a565c4a7711102108138132b21f22ed556df08fb4c8cfdcaf17dcb605f8a6394f8684aa1916df60929532faf808c36c133ce52356d0f45f3";

    SM2EncryptService gmEncryptService = new SM2EncryptService();
    String gmCipherText = gmEncryptService.encrypt(gmMsg, gmPublicKey);
    System.out.println("gm encryption cipher:"+gmCipherText);

    String gmPlainText = gmEncryptService.decrypt(gmCipherText, gmPrivateKey);
    System.out.println("gm decryption result:"+gmPlainText);
```
