key-mgr组件介绍

# 组件介绍

key-mgr用于私钥托管，适合B2B2C场景。

支持文件存储和数据库存储。文件存储模式下，私钥会被存储到所属用户文件内；数据库存储模式，支持单库存储和双库存储两种模式。单库模式下，服务端仅保存加密后的私钥，加密密码由用户自行保管。双库模式下，服务端既持有加密过后的私钥，也持有加密密码，二者由不同的数据库保存，提高了安全性，适用于用户信任平台的场景。

就私钥托管格式而言，支持以下格式的存储：
*   keystore
*   p12

就曲线类型而言，支持经典曲线和国密曲线。曲线对于地址计算、私钥加密等都是必须的信息。
*   secp256k1，ECC经典曲线
*   sm2p256v1，国密曲线

# 部署教程

目前支持从源码进行部署。

### 1. 获取源码

通过git下载源码：

```
git clone https://gitee.com/aaronchu/key-center
```

进入目录：
```
cd key-center
cd key-mgr
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

key-mgr编译之后在根目录下会生成dist文件夹，文件夹中包含key-mgr.jar。可以将key-mgr.jar导入到自己的项目中，例如拷贝到libs目录下，然后进行依赖配置。gradle推荐依赖配置如下，然后再对自己的项目进行编译。

```
repositories {
    mavenCentral()
    mavenLocal()
    maven {
        url "http://maven.aliyun.com/nexus/content/groups/public/"
    }
}

dependencies {
    compile 'org.springframework.boot:spring-boot-starter'
    compile 'org.springframework.boot:spring-boot-starter-data-jpa'

    testCompile('org.springframework.boot:spring-boot-starter-test') {
        exclude group: 'org.junit.vintage', module: 'junit-vintage-engine'
        //exclude group: 'junit', module: 'junit'
    }
    compile 'org.springframework.boot:spring-boot-starter-jta-atomikos'
    compile ('org.projectlombok:lombok:1.18.8')
    compile ('org.projectlombok:lombok:1.18.8')
    annotationProcessor 'org.projectlombok:lombok:1.18.8'
    compile "org.apache.commons:commons-lang3:3.6"
    compile "commons-io:commons-io:2.6"

    compile "com.fasterxml.jackson.core:jackson-core:2.9.6"
    compile "com.fasterxml.jackson.core:jackson-databind:2.9.6"
    compile "com.fasterxml.jackson.core:jackson-annotations:2.9.6"

    compile 'com.lhalcyon:bip32:1.0.0'
    //compile 'io.github.novacrypto:BIP44:0.0.3'

    compile group: 'org.bouncycastle', name: 'bcprov-jdk15on', version: '1.60'
    compile group: 'org.bouncycastle', name: 'bcpkix-jdk15on', version: '1.60'
    compile 'org.web3j:core:3.4.0'
    compile 'com.lambdaworks:scrypt:1.4.0'
    compile 'commons-codec:commons-codec:1.9'

    compile 'mysql:mysql-connector-java'
    compile fileTree(dir:'libs',include:['*.jar'])
}

```


# 使用详解

key-mgr使用了SpringBoot自动装配功能，所以只要您按照上文添加了SpringBoot依赖，就可以自动装配所需的Bean。

### 1. 配置

请参考下面的模板，配置application.properties。
```
## 托管方式：file-文件托管方式，db-数据库托管方式
system.mgrStyle=db
## 加密格式，支持p12或keystore
system.keyEncType=p12
## 可以用secp256k1 or sm2p256v1。前者为ECC经典曲线；后者为国密曲线。
system.eccType=sm2p256v1

# 以下配置为system.mgrType=file时的配置
system.dataFileDir=~/myKeys

# 以下配置为system.mgrType=db时的配置
## true-双库，false-单库
system.storePwd=true  

## 加密后的私钥存储url
spring.datasource.encryptkeydata.url=jdbc:mysql://[ip]:[port]/pkey_mgr?autoReconnect=true&characterEncoding=utf8&useSSL=false&serverTimezone=GMT%2b8
spring.datasource.encryptkeydata.username=
spring.datasource.encryptkeydata.password=

## 若采用双库,需要配置该url用于存储私钥加密密码
spring.datasource.keypwd.url=jdbc:mysql://[ip]:[port]/pkey_mgr_pwd?autoReconnect=true&characterEncoding=utf8&useSSL=false&serverTimezone=GMT%2b8
spring.datasource.keypwd.username=
spring.datasource.keypwd.password=

## spring jpa config
spring.jpa.properties.hibernate.hbm2ddl.auto=update
spring.jpa.properties.hibernate.show_sql=true
spring.jpa.database-platform=org.hibernate.dialect.MySQL5InnoDBDialect
```

### 2.建表

如果在上述配置中指定了**spring.jpa.properties.hibernate.hbm2ddl.auto=update**，则jpa会帮助用户自动建立数据表。

如果不希望自动建立数据表，请先关闭jpa建表开关：
```
spring.jpa.properties.hibernate.hbm2ddl.auto=validate
```
然后按下面方式手动建表。

1） 在encryptkeydata数据源运行下述建表语句：

```
//私钥管理
CREATE TABLE `encrypt_keys_info` (
   `pk_id` bigint(20) NOT NULL AUTO_INCREMENT,
   `encrypt_key` longtext,
   `user_id` varchar(255) DEFAULT NULL,
   `key_address` varchar(255) DEFAULT NULL,
   `key_name` varchar(255) DEFAULT NULL,
   `parent_address` varchar(255) DEFAULT NULL,
   PRIMARY KEY (`pk_id`),
   KEY `user_id` (`user_id`),
   KEY `key_address` (`key_address`)
 ) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8
 

```

2） 请在keypwd数据源运行下述建表语句：

```
 CREATE TABLE `key_pwds_info` (
   `pk_id` bigint(20) NOT NULL AUTO_INCREMENT,
    `user_id` varchar(255) DEFAULT NULL,
   `key_address` varchar(255) DEFAULT NULL,
   `key_pwd` varchar(255) DEFAULT NULL,
   PRIMARY KEY (`pk_id`),
   KEY `user_id` (`user_id`),
   KEY `key_address` (`key_address`)
 ) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8
```

### 3.接口使用

KeysManagerService类是整个pkey-mgr模块的入口，覆盖私钥管理的全生命周期，包含如下功能：

*   createPrivateKey：生成私钥，并进行托管存储
*   importPrivateKeyFile：导入私钥密文文件，并进行托管存储
*   importPrivateKey：导入私钥原文，并进行托管存储
*   createPrivateKeyByParent: 根据父私钥和chaincode，生成子私钥，并进行托管存储
*   queryChildKeys：查询父私钥的所有直接下级子私钥
*   exportPrivateKeyFile: 从库中读取私钥密文，并导出到密文文件
*   decryptPrivateKey：解密私钥密文，得到明文
*   getEncryptPrivateKeyList：获取某用户的所有私钥密文列表
*   getEncryptPrivateKeyByUserIdAndAddress：根据用户id、私钥地址获取私钥密文
*   updateKeyName：更新私钥名称
*   updateKeyPassword：重置私钥密码
*   deleteUserKey：删除某用户的某一私钥

建议您通过Spring自动注入KeysManagerService服务，示例如下：

```
@SpringBootTest
@RunWith(SpringRunner.class)
public class Example {

    @Autowired
    private KeysManagerService keysManagerService;

}
```

#### createPrivateKey

该方法用于随机生成私钥，并进行托管存储。

```
@Test
public void demo() throws Exception {
    String userId = "1000";
    String password = "123456";
    String keyName = "MyKey";
    keysManagerService.createPrivateKey(userId, password, keyName);
}

```

执行过后，可在encryptkeydata数据源的encrypt_keys_info表查看存储的私钥；如果配置了双库（system.storePwd=true），可通过keypwd数据源的key_pwds_info表查看密码存储结果。

**参数说明**：

- userId: 用户id

- password：私钥加密密码

- keyName：私钥名称

#### importPrivateKeyFile

该方法用于从p12或keystore文件中提取私钥，并进行托管存储。按哪种格式提取取决于applicaiton.properties中配置的是p12还是keystore，故要注意确保文件格式和配置相符。

```
@Test
public void demo() throws Exception {
    String userId = "1000";
    String password = "123456";
    String path = "~/keys/enryptFile.p12";
    keysManagerService.importPrivateKeyFile(userId, password, path);
}
```
执行过后，可在encryptkeydata数据源的encrypt_keys_info表查看存储的私钥；如果配置了双库（system.storePwd=true），可通过keypwd数据源的key_pwds_info表查看密码存储结果。

**参数说明**：

- userId: 用户id

- password：该密码负责解密密文文件；也负责在托管存储时给私钥加密。

- filePath：密文文件目录。需要和application.properties中的system.keyEncType保持一致。

#### importPrivateKey

该方法用于直接导入私钥原文，并对其托管存储。

```
@Test
public void demo() throws Exception {
    String userId = "1000";
    String password = "123456";
    String privateKey = "0xeec37c95fb78c49132513882bb293fd6ecdef194e85efcc654af0d9c291995ec";
    String keyName = "MyKey";
    keysManagerService.importPrivateKey(userId, password, privateKey, keyName);
}
```
执行过后，可在encryptkeydata数据源的encrypt_keys_info表查看存储的私钥；如果配置了双库（system.storePwd=true），可通过keypwd数据源的key_pwds_info表查看密码存储结果。

**参数说明**：

- userId: 用户id

- password：该密码负责解密密文文件；也负责在托管存储时给私钥加密。

- privateKey: 16进制的私钥原文

- keyName: 私钥名称

#### createPrivateKeyByParent

该方法通过父私钥原文和一个chaincode来确定性地派生子私钥。派生过后的子私钥也会被导入托管。

```
@Test
public void demo() throws Exception {
    String userId = "1200";
    String parentKey = "eec37c95fb78c49132513882bb293fd6ecdef194e85efcc654af0d9c291995ec";
    //首先导入父私钥
    keysManagerService.importPrivateKey(userId, "123456",parentKey , "parentKey");
    //派生子私钥并导入
    String chaincode1 = "123";
    String password1 = "pwd1";
    keysManagerService.createPrivateKeyByParent(userId, parentKey,chaincode1, password1);
    String chaincode2 = "xyz";
    String password2 = "pwd2";
    keysManagerService.createPrivateKeyByParent(userId, parentKey,chaincode2, password2);
}
```

执行过后，可在encryptkeydata数据源的encrypt_keys_info表查看存储的子私钥；如果配置了双库（system.storePwd=true），可通过keypwd数据源的key_pwds_info表查看密码存储结果。

**参数说明**：

- userId: 用户id

- parentKey：父私钥原文。需为16进制私钥原文。

- chaincode: 任意字符串。比如可以传入一串UUID。

- password: 子私钥名称


#### queryChildKeys

该方法用于查询一个父私钥所关联的下级子私钥。

```
@Test
public void demo() throws Exception {
    String userId = "1200";
    String parentAddress = "9349e27ae2202202bd0487d4aa08705a14c52710";
    //Query child keys
    List<EncryptKeyInfo> childs = keysManagerService.queryChildKeys(userId, parentAddress);
    log.info("Child keys {}", JacksonUtils.toJson(childs));
}
```


**参数说明**：

- userId: 用户id

- parentAddress：父私钥的地址


#### exportPrivateKeyFile

该方法根据用户id、私钥地址从库中读取私钥，以所配置的格式导出到目标目录。

```
@Test
public void demo() throws Exception {
    String userId = "1200";
    String keyAddress = "9349e27ae2202202bd0487d4aa08705a14c52710";
    String destinationDirectory = "C:\\Users\\unnamed\\Desktop\\keys";
    keysManagerService.exportPrivateKeyFile(userId, keyAddress, destinationDirectory);
}
```

执行过后，将在destinationDirectory指定的目录内，得到一个加密的p12或keystore文件（取决于system.keyEncType配置）

**参数说明**：

- userId: 用户id

- keyAddress: 私钥地址

- destinationDirectory：输出目录


#### decryptPrivateKey

该方法将一个密文解密为原文。密文格式取决于system.keyEncType配置。

```
@Test
public void demo() throws Exception {
    String encryptPrivateKey = "0x30820708020103308206c106092a864886f70d010701a08206b2048206ae308206aa3081df06092a864886f70d010701a081d10481ce3081cb3081c8060b2a864886f70d010c0a0102a07f307d3029060a2a864886f70d010c0103301b0414f7fc26b5a85f36c54bbdf4e6fe8c49c4b9c8250d020300c8000450aab786ff7c10158b90ff0eaf34a7b113d817b26f94abee97bd38992c4e1377ef599ced3a8157b028faa3323c98198e3863cad95b2b70bc1e1daa1e94284286190e1aeac4a4e6309ad8bcf24ffb14f7963138301106092a864886f70d01091431041e020031302306092a864886f70d01091531160414f8b3d8a7c476befeed72ddb7af19cb0d8f59d0bd308205c406092a864886f70d010706a08205b5308205b1020100308205aa06092a864886f70d0107013029060a2a864886f70d010c0106301b0414607b51b0881ead37d5c50feea3e1d9a178503bcd020300c800808205702913d6c9d00ba8c1c8637d248bafbc984d12265e9302074db996e84caefa21a3bd259271a013e03d4cb1b707fde1b41a90340a87769ba0f1a7814c6ff08860d42084fca8bd64d66ccfa983fc414f8f22794517614358ed8a020a57163607f790fc8331f23df6617785185646fb88992574e351cb4c756c9f8151fa2e00beb344712891a36d54bab05da59b08b2a31e6ba1a751826f51deabcc23bb329d2cc3fc290fddb30c33e1e65ffb2f3647d04c2549ce95abf1ee28b2a68e5e24210f7cb5f8d369e4a27d297ff975d245df74b09827870c7727c5b53ea17d8eb207d499ce0eb747b409e03c59f2a1ceceb73f4049ef4932d1c3b6ba90001ad272ead36f8c87dfd66f8ba391153241eee93ec7b567085d6a97dc3f0d6ecfb839d34877917e3dcfbe9fcdb76af3942646c305e90caef36f978d2375dd33d9b7144679b09fddb11f5195251736fd1246130c5fec160fc49701c73fe9732ea42ff98c6c1eb3794cfe60457c464467a3cb30457d6522467dbb6154e377b0456a301e1cc2d4eff0820303a4e95191cca73e01801c4f5b5cf041f7d3d9b27c98e5b8f74ebc9ee93e86b467b6b7cfe7777a931f4d0edf8994e1b7d829cde91b052077a426f5f872e734a0c2f71658c092a524c48ceadee076506009555119f4a6d0e7021d83571252d7766ce3ad01b53df20ab5855edddd686878f9a911a195eddb02e2f5683ddf942ba0546c32fa4a571c414dfefe7a378a0a9e02a0617c42faaad0462f2683782f3acb0611370b2d83bdc4e24c8433874a8d6cd1ecf206d72fc1d75c8ab87be00342e46ee9cbe844ca2be83776333c4840f7d092c4206a2b55ca5a50d0859b9485c4ce3dcf20ad2cf0dfa13ba4326b95b1a4f494270819eae4be1e78fa4a2b12619944ba2fe68510a6ba18f5f7074ad903fc278304c3caf374dd657b5169482f7089b1cb337a92cd43cea4dd178136b5af98d2162db0be2f17400e91e446ce6c161a09b0baceb243043ac973782bb0c864a8684ab284d0d707b96cea18f62f786c61c3a12f09b39b785a4b9db3f86781cc84089061efb89722ac8865e4c35eb68e637136bdd5a321fe58884a7f964d782582c62e0e2c08e38ff65eb48394c4b1733c48598a94f0c027f28a62542bd5c0357a45f0695ef99d073f0f0a28b0f6407fb7b407c90963fb318d0e2974edc6f5065c3aa649d753fa72668c6fc04f5212f498cd5b5b7cc0ab0ee65e6b08202ebe4cf854a6f0b24f75a40cfa0656f7e3820cf29e5641720561520e8448e72a192bbd97295af14467d39c3e24a9ccf37317a3c6e84836f3e9362d2fa53692ac20db266245f3927305ad2066eacb9e0afdb51723e22d86c8ce3fd3686d2ee51d6b84ba6eb3a253cbd4a46f2ccef6dcef85cb8f42538fb9f4eea0163807f1f4f27884f09f2279a0a2f8c3d6c78746e5a48f4db8ff5601159472254d28960f87dbbe20b43cd26897a5e80bfda70b958b05cd017d2e71a5058c5db3262dec5414584b3866d5708f0c52075ee962b694d187752964c53807c26c1f0e398da9332460c1a74b890e7653a4a590e1dfe13f22e0d00970e2eb79aee4e5a81cc6fd953874c4841f48adbd94110cf1c0c57fd0fbf19d94f4782448195c30986f4c70d1671d96ebabab9da14dad6837160003d828c49285e52ca37490b91b71a9b632a6351b3440a38c713993aa1acc95be6dca0a07bd282a64817936e8ee5f59908cc25dcc5d2b281518efd78fba0e3d38e126282da92b790276ff5ef483801a8ab8c86ea3ae0598b80489ff583bf9e3287363b44fc2b8cc464a7cc7dd892883b93f873fb8cd145c8b7009ebe0d9d0a880fddc7396bb527350090edcf5cfa210fddf0e2ec3bac8a884e3e145a1f0e16f85bc129064722851e3b8a9f7c58bf0c97a4b15cadd02548d52deab859e77323d81e0a2d1f25b81414cadde1ec55eea6a21026cde1d708d6e303e3021300906052b0e03021a05000414e1665635c1f39e467a83748c3290b95b126a0ab10414d5b66a57b7b04eb9ea4efede7d48640b902f42020203019000";
    String pk = keysManagerService.decryptPrivateKey("654321", encryptPrivateKey);
    log.info("recovered {}", pk);
}
```

**参数说明**：

- password: 解密密码

- encryptPrivateKey: 密文数据。可从encryptkeydata数据源的encrypt_keys_info表的encrypt_key字段获取。

#### getEncryptPrivateKeyList

该方法读取某一用户的所有私钥密文。

```
@Test
public void demo() throws Exception {
    String userId = "1200";
    List<EncryptKeyInfo> keys = keyManagerService.getEncryptPrivateKeyList(userId);
    log.info("keyStrings {}", JacksonUtils.toJson(keys));
}
```

**参数说明**：

- userId: 用户id

#### getEncryptPrivateKeyByUserIdAndAddress

该方法读取某一用户下某地址对应的私钥密文。

```
@Test
public void demo() throws Exception {
    String userId = "1000";
    String address = "e4cbf2581d2fc8541613079440cfbdbf2595b127";
    EncryptKeyInfo key = keyManagerService.getEncryptPrivateKeyByUserIdAndAddress(userId, address);
    log.info("keyStrings {}", JacksonUtils.toJson(key));
}
```

**参数说明**：

- userId: 用户id

- address：私钥地址

#### updateKeyName

updateKeyName更新库中的私钥名称。

```
@Test
public void demo() throws Exception {
    String userId = "1200";
    String address = "9349e27ae2202202bd0487d4aa08705a14c52710";
    String newKeyName = "newKeyName";
    keysManagerService.updateKeyName(userId, address, newKeyName);
}
```

执行过后，可在encryptkeydata数据源的encrypt_keys_info表的key_name字段查看新的私钥名。

**参数说明**：

- userId: 用户Id

- address: 私钥地址

- newKeyName：新的名称


#### updateKeyPassword

更新私钥密码。

```
@Test
public void demo() throws Exception {
    String userId = "1200";
    String address = "9349e27ae2202202bd0487d4aa08705a14c52710";
    String oldPwd = "123456";
    String newPwd = "654321";
    keysManagerService.updateKeyPassword(userId, address, oldPwd, newPwd);
}
```

执行过后，可在encryptkeydata数据源的encrypt_keys_info表查看修改过的密文；如果配置了双库（system.storePwd=true），可通过keypwd数据源的key_pwds_info表的key_pwd字段看到新的密码。

**参数说明**：

- userId: 用户Id

- address: 私钥地址

- oldPwd：旧密码

- newPwd：新密码



#### deleteUserKey示例

根据用户Id和私钥地址来删除私钥。

```
@Test
public void demo() throws Exception {
    String userId = "1200";
    String keyAddress = "9349e27ae2202202bd0487d4aa08705a14c52710";
    keysManagerService.deleteUserKey(userId, keyAddress);
}
```

执行过后，可在encryptkeydata数据源的encrypt_keys_info表查看到私钥已删除；如果配置了双库（system.storePwd=true），可通过keypwd数据源的key_pwds_info表看到密码已删除。

**参数说明**：

- userId: 用户Id

- keyAddress: 私钥地址


# 常见问题

### Illegal key size

在P12模式下，加密时有可能报错Illegal key size:

```
java.io.IOException: exception encrypting data - java.security.InvalidKeyException: Illegal key size
	at org.bouncycastle.jcajce.provider.keystore.pkcs12.PKCS12KeyStoreSpi.wrapKey(Unknown Source)
	at org.bouncycastle.jcajce.provider.keystore.pkcs12.PKCS12KeyStoreSpi.doStore(Unknown Source)
	at org.bouncycastle.jcajce.provider.keystore.pkcs12.PKCS12KeyStoreSpi.engineStore(Unknown Source)
    ...
```

这是因为jce默认情况下对密码强度做了限制，需要在JRE中安装一组特殊的协议，请参考：

https://stackoverflow.com/questions/6481627/java-security-illegal-key-size-or-default-parameters

或

https://blog.csdn.net/hfhwfw/article/details/68557238










