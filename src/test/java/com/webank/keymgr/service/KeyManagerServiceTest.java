/*
 * Copyright 2014-2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.webank.keymgr.service;

import com.webank.keygen.key.KeyComputeAlgorithm;
import com.webank.keymgr.BaseTest;
import com.webank.keymgr.model.EncryptKeyInfo;
import com.webank.keymgr.model.vo.PkeyInfoVO;
import com.webank.keymgr.persistence.KeyPersistenceService;
import com.webank.keymgr.utils.JacksonUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.web3j.utils.Numeric;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.SecureRandom;
import java.util.List;

/**
 * DBKeyManagerServiceTest
 *
 * @Description: DBKeyManagerServiceTest
 * @author graysonzhang
 * @author aaronchu
 * @data 2019-07-13 13:45:42
 *
 */
@Slf4j
public class KeyManagerServiceTest extends BaseTest {
    
    @Autowired
    private KeysManagerService keyManagerService;
    
    @Autowired
    private KeyComputeAlgorithm addressHandler;
    
    private static SecureRandom random = new SecureRandom();
    
	private String tmpDir;
	
	@Before
	public void init() throws Exception{
		this.tmpDir = Paths.get(System.getProperty("user.dir"), "tmpTest").toString();
		Path path = Paths.get(this.tmpDir);
		if(!Files.exists(path)) {
			Files.createDirectory(Paths.get(this.tmpDir));
		}
	}
	
	@After
	public void destroy() {
		try {
			deleteDirectory(new File(this.tmpDir));
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
    @Test
    public void testCreatePrivateKey() throws Exception{
        String userId = "1000";
        String password = "123456";
        String keyName = "MyKey";
        PkeyInfoVO vo = keyManagerService.createPrivateKey(userId, password, keyName);
        log.info(JacksonUtils.toJson(vo));
    }
    
    @Test
    public void testImportPrivateKey() throws Exception{
    	String userId = "1001";
    	String password = "123456";
    	String privatekey = generatePkey();
    	String keyName = "aaa";
    	keyManagerService.importPrivateKey(userId, password, privatekey, keyName);
    }


    
    @Test
    public void testDelete() throws Exception{
    	String userId = "1001";
    	String password = "123456";
    	String privatekey = generatePkey();
    	String keyName = "MyKey";
    	keyManagerService.importPrivateKey(userId, password, privatekey, keyName);
    	String addr = addressHandler.computeAddress(Numeric.hexStringToByteArray(privatekey));

    	keyManagerService.deleteUserKey(userId, addr);
    }

    @Test
    public void testExportPrivateKeyAndImport() throws Exception{
    	//import first
    	String userId = "1001";
    	String password = "123456";
    	String privatekey = generatePkey();
    	String keyName = "MyKey";
    	keyManagerService.importPrivateKey(userId, password, privatekey, keyName);
    	
    	String addr = addressHandler.computeAddress(Numeric.hexStringToByteArray(privatekey));
    	
    	//export
    	String path = keyManagerService.exportPrivateKeyFile(userId, addr, this.tmpDir);
    	log.info("exported to temp path", path);
    	//delete
    	keyManagerService.deleteUserKey(userId, addr);
    	
    	//import again from file
    	keyManagerService.importPrivateKeyFile(userId, "123456", path);
    }

	@Test
	public void demo() throws Exception{
		//import first
		String userId = "1001";
		String password = "123456";
		String privatekey = generatePkey();
		String keyName = "MyKey";
		keyManagerService.importPrivateKey(userId, password, privatekey, keyName);

		String addr = addressHandler.computeAddress(Numeric.hexStringToByteArray(privatekey));

		//export
		//String path = keyManagerService.exportPrivateKeyFile(userId, addr, "C:\\Users\\aaronchu\\Desktop");
		//log.info("exported to temp path", path);

		//import again from file
		keyManagerService.importPrivateKeyFile(userId, "123456", "C:\\Users\\aaronchu\\Desktop\\UTC--2020-12-25T09-10-03.997000000Z--0x7bbaca2c41dc3409f379c694499a0b0f2f668d1b.json");
	}

	@Test
	public void testExportPrivateKey() throws Exception{
		//import first
		String userId = "1001";
		String password = "123456";
		String privatekey = generatePkey();
		String keyName = "MyKey";
		keyManagerService.importPrivateKey(userId, password, privatekey, keyName);

		String addr = addressHandler.computeAddress(Numeric.hexStringToByteArray(privatekey));

		//export
		String path = keyManagerService.exportPrivateKeyFile(userId, addr, this.tmpDir);
		log.info("exported to temp path", path);
	}
    
    @Test
    public void testUpdateKeyName() throws Exception{
    	String userId = "1001";
    	String password = "123456";
    	String privatekey = generatePkey();
    	String keyName = "MyKey";
    	keyManagerService.importPrivateKey(userId, password, privatekey, keyName);
    	String addr = this.addressHandler.computeAddress(Numeric.hexStringToByteArray(privatekey));
    	keyManagerService.updateKeyName(userId, addr, "newKey");

		keyManagerService.updateKeyName("nonExist", addr, "newKey");
	}
    
    @Test
    public void testUpdatePassword() throws Exception{
     	String userId = "1001";
    	String password = "123456";
    	String privatekey = generatePkey();
    	log.info("private key {}",privatekey);
    	String keyName = "MyKey";
    	keyManagerService.importPrivateKey(userId, password, privatekey, keyName);
    	String addr = this.addressHandler.computeAddress(Numeric.hexStringToByteArray(privatekey));
    	keyManagerService.updateKeyPassword(userId, addr, password, "654321");
    }
    
    @Test
    public void testDecryptP12() throws Exception{
    	String enc = "0x3082070c020103308206c506092a864886f70d010701a08206b6048206b2308206ae3081e306092a864886f70d010701a081d50481d23081cf3081cc060b2a864886f70d010c0a0102a07f307d3029060a2a864886f70d010c0103301b041406526d96aa07c2caa02ef695be3d0c45c917fe35020300c8000450f5d643341ccaf73c75f98561e6a8638281ad5f5daa730a940250d578af9904d97a8ca77a936a1ce01355f6fa8a8d38bf101058f76accf25a8873afdbcdc7c237bec093196e950ae627b7cfd96e4f32d9313c301506092a864886f70d01091431081e06006b00650079302306092a864886f70d010915311604141c7c957c6af8d3453d209c414edc1bb634418810308205c406092a864886f70d010706a08205b5308205b1020100308205aa06092a864886f70d0107013029060a2a864886f70d010c0106301b04147081618902ae793be19aa30bfd774adc0895cc4a020300c800808205702c1eab655dd1190eca8eacdd4cde15fb64af92f5ce46e6ca363929203e93d0a38d00206d888ccd08503392830ffc3d1376d37103b50c2332d834adf49a4d6de9c2e6f03287a65d89533eaa644ebbed0397202a07475072315fdcdb9906597f4d8912f683bd8a5e690596cd215c277a330e53fbcb67c3dc3f84bfb6acb4110a7228822838016b8f7ca301bfd72d73a3fb77022da227c74659ab050ad18b104e3303592424fc27665e7b95d8edfe167b7ce688d3e1e84f96a3713e895a072ad6a0a595452443664bbcb11dc37fa09cee2704abc113432f07940b69f6a9c38eeefd3516be6d784f5393fbf45e8cf207aa0ebc33d94f5c80cc0c6da1d20fb0964a0e4cb7cc73eab131345f60f5ecedea5f4fe14d00b860ce0efa4f10a1830c217dd808142c2e8306ded4835c2b15c2a8ebf18101a3efe3aa1bfc3f432dd4a6bc19039dfd61bdafef10deb0dda18b58e17975ae00a0c8d31961b2ed70966bdc846365100a6c1140add70b74f687855b68ccddfa7037a1bf7fce3ffcaf45b70db5f4b378f79de88c04e909b9b49ddd9715a534e241e63bbf77893683c525933c4582129f8bf333f8d8072c9875abf95922c2079ee572187906678690d213d1f70aa7e36b59e3fdfae52f99a9f688d014c6941fa833be5b2c57d83cbec20c6525b08c6f7603acdcae6e24fb9d26edccf39ae39dae75771c0137f365e3ace9ecf205037555accc9b2c68534e2e996e1e94f9349dc9a60626f4935e2521f5c40872a2d9ad6d9a5c2adc269aabaabe95823fd9019b11ddfee7302e67ccc141510dc66eef27808f5d11ceee148bed87b5addf5a0f7acd08bae389ff9d24fc8992a0b02968735193cc4cbc9b386d0145ab339bfc2634cb355db80284db5d7e9769446d5c6c0c6a158d71be0c82f5715fc10beabe209c5fb4b619ebe64af90a06261fe67f14bcd161fdd2b939efb83cfe6c27bf831f54190bc468ba390792e8f91dcd0aec0cf34faa97f458ebedc8007dbe0b2084de8eea8764660b2925e57a8049a20d2a0a432b2f89b49c1b270c72256ef27ddec5f3c5bc13b1fc76c9354e3e35322a66cc790a1a19b87d747d491f632454fd98bf335309c8a7ce39b99e3f84f0b4dc42a7cf3e03bab50e751c3b07357a0db2040516c228b33f4efbc0046a1c6b620fc533f46f6d64af4426b77c417c78272e4b4812845438a90cfa40aa11cf60d307f146e27c24e764829eae9c4c9e863dbb9aa87ce400b45530256a7e7e203359d323661ba2b361bdd3b254872627ec931862ea32d1cab85a44a269d5dba66b80f86d1fa85777406e6b51a08e334c3d08c4c008ae9afd98fe01973d45d1a1c2b17f646dafb46c3176cdcd25fa8e5e00efd930589c2ccdfe1f6fe47d663adb1549352fa47407c7317466baccbc644f0c9570f500e88154144925d7ecbea9850c18f853f9e6470728187d5d763236188b0c7b35921ebe01aba16d456db47099be9fccae2fbbfdedbdb536a9514af3940d0f53ea84fe32b068b65f57b6c4b52aa7c056acaccb172260268eb190b92db125ff5a64c656b516632a8de95c604639f24d0e2fea14d007f3cf226c2608adeddcdfd36de4a8c3cb9dc046af08ddad95aedecd6caa1b89259116df34232adf8a927ba8eb38968bcdc1937e62976936faba646e2ab941b82dc1338ef7a2ba53e2ac4ccbee566a8cd85550518cb0b1a34f51ee5d04c8c5ad4c2e6d27da94bfbc209f6f05fe3bdac0775b19032aee0125d56615507d500d878bdef6f3a0c98527e92aa47567c6841775288a6e7e583e63368a48a77e74f74e5299e456a63cf6cc34ac20c851f8bd76ed59fb924e1535f76e8e96d9df9ba909a1fdf56970f191124a120f9d2ba02ee91970a50e7eaf6194ff02827d8c02d0686a071d06da4f6f415b3a75c8d05b84b943d248e750f813c24313b7ec49ff7aa9d8ca89910599753694c22703ce6887303e3021300906052b0e03021a05000414eb02652a1aa4ba867754680de1471182af1c7cd0041445b246796f1c5b66a6a560dc2bf4e2ba07e815790203019000";
    	String pk = keyManagerService.decryptPrivateKey("password", enc);
    	log.info("recovered {}", pk);
    }
    
    @Test
    public void testDecryptKeystore() throws Exception{
    	String enc = "{\"address\":\"0x68e1d0ad5ebc77feeaaac712e55cd274a2b1d33a\",\"id\":\"c5f82b87-b25a-42b7-baa1-51871ffae0eb\",\"version\":3,\"crypto\":{\"cipher\":\"aes-128-ctr\",\"ciphertext\":\"0ca68b721be5d1556cbd9be2b3b043e7e1ddf1458d89a43339a24b71d2f2bb3c\",\"cipherparams\":{\"iv\":\"7fa497df863424072f7964d480095bfd\"},\"kdf\":\"scrypt\",\"kdfparams\":{\"dklen\":32,\"n\":262144,\"p\":1,\"r\":8,\"salt\":\"6e039e411821bde52b17481a8759ff66548ba97f4d2e094262d06f93a9f6844c\"},\"mac\":\"04e8d244a4ed9c5206952ba17cb9a49560a2ce141c52a60dac15fbca1f3544db\"}}";
    	String pk = keyManagerService.decryptPrivateKey("password", enc);
    	log.info("recovered {}", pk);
    }
    
    @Test
    public void testGetEncryptKeyList() throws Exception{
		String userId = "1200";

		keyManagerService.createPrivateKey(userId, "123456", "key1");
		keyManagerService.createPrivateKey(userId, "123456", "key2");
		keyManagerService.createPrivateKey(userId, "123456", "key2");

    	List<EncryptKeyInfo> keys = keyManagerService.getEncryptPrivateKeyList(userId);
    	log.info("Key Number: {}",keys.size());
    	log.info("keyStrings {}", JacksonUtils.toJson(keys));

		keys = keyManagerService.getEncryptPrivateKeyList("nonExist");
		log.info("Key Number: {}",keys.size());
		log.info("keyStrings {}", JacksonUtils.toJson(keys));
    }
    
    @Test
    public void testGetEncryptPrivateKeyByUserIdAndAddress() throws Exception{
    	String userId = "1200";
    	String address = "0418c386dddbfca7740947168dc27047e038baac";
    	EncryptKeyInfo key = keyManagerService.getEncryptPrivateKeyByUserIdAndAddress(userId, address);
    	log.info("keyStrings {}", JacksonUtils.toJson(key));

		key = keyManagerService.getEncryptPrivateKeyByUserIdAndAddress("non-exists", address);
		log.info("keyStrings {}", JacksonUtils.toJson(key));
    }
    
    @Test
    public void testCreateChildKey() throws Exception{
    	String userId = "1200";
    	String parentKeyString = generatePkey();
    	String parentKeyAddress = addressHandler.computeAddress(Numeric.hexStringToByteArray(parentKeyString));
    	//import parent key first
    	keyManagerService.importPrivateKey("1200", "parentPwd",parentKeyString , "parentKey");
    	
    	//Derive private key
      	String chaincode1 = "32C4AE2C1F1981195F9904466A39C9948FE30BBFF2660BE1715A4589334C74C7";
    	keyManagerService.createPrivateKeyByParent(userId, parentKeyString,chaincode1, "pwd1");
    	String chaincode2 = "AAA4AE2C1F1981195F9904466A39C9948FE30BBFF2660BE1715A458933412345";
    	keyManagerService.createPrivateKeyByParent(userId, parentKeyString,chaincode2, "pwd2");
    	//Query child keys
    	List<EncryptKeyInfo> childs = keyManagerService.queryChildKeys(userId, parentKeyAddress);
    	log.info("Child keys {}", JacksonUtils.toJson(childs));
    }
    
    private static String generatePkey() {
    	byte[] pkey = new byte[32];
    	random.nextBytes(pkey);
    	//Make sure less than order
    	pkey[0] = (byte) 0xee;
    	return Numeric.toHexString(pkey);
    }
    
	static boolean deleteDirectory(File directoryToBeDeleted) {
	    File[] allContents = directoryToBeDeleted.listFiles();
	    if (allContents != null) {
	        for (File file : allContents) {
	            deleteDirectory(file);
	        }
	    }
	    return directoryToBeDeleted.delete();
	}

	@Autowired
	private KeyPersistenceService keyPersistenceService;

	@Test
	public void test() throws Exception{
		this.keyManagerService.createPrivateKey("111","111","111");
		this.keyManagerService.createPrivateKey("222","222","222");
		this.keyManagerService.createPrivateKey("333","333","333");
		Page<EncryptKeyInfo> page = keyManagerService.query(0, 2);
		System.out.println(page.get().findFirst().get().getKeyName());


	}
}


















