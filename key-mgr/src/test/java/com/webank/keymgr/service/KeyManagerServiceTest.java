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
    	String enc = "0x30820708020103308206c106092a864886f70d010701a08206b2048206ae308206aa3081df06092a864886f70d010701a081d10481ce3081cb3081c8060b2a864886f70d010c0a0102a07f307d3029060a2a864886f70d010c0103301b0414f7fc26b5a85f36c54bbdf4e6fe8c49c4b9c8250d020300c8000450aab786ff7c10158b90ff0eaf34a7b113d817b26f94abee97bd38992c4e1377ef599ced3a8157b028faa3323c98198e3863cad95b2b70bc1e1daa1e94284286190e1aeac4a4e6309ad8bcf24ffb14f7963138301106092a864886f70d01091431041e020031302306092a864886f70d01091531160414f8b3d8a7c476befeed72ddb7af19cb0d8f59d0bd308205c406092a864886f70d010706a08205b5308205b1020100308205aa06092a864886f70d0107013029060a2a864886f70d010c0106301b0414607b51b0881ead37d5c50feea3e1d9a178503bcd020300c800808205702913d6c9d00ba8c1c8637d248bafbc984d12265e9302074db996e84caefa21a3bd259271a013e03d4cb1b707fde1b41a90340a87769ba0f1a7814c6ff08860d42084fca8bd64d66ccfa983fc414f8f22794517614358ed8a020a57163607f790fc8331f23df6617785185646fb88992574e351cb4c756c9f8151fa2e00beb344712891a36d54bab05da59b08b2a31e6ba1a751826f51deabcc23bb329d2cc3fc290fddb30c33e1e65ffb2f3647d04c2549ce95abf1ee28b2a68e5e24210f7cb5f8d369e4a27d297ff975d245df74b09827870c7727c5b53ea17d8eb207d499ce0eb747b409e03c59f2a1ceceb73f4049ef4932d1c3b6ba90001ad272ead36f8c87dfd66f8ba391153241eee93ec7b567085d6a97dc3f0d6ecfb839d34877917e3dcfbe9fcdb76af3942646c305e90caef36f978d2375dd33d9b7144679b09fddb11f5195251736fd1246130c5fec160fc49701c73fe9732ea42ff98c6c1eb3794cfe60457c464467a3cb30457d6522467dbb6154e377b0456a301e1cc2d4eff0820303a4e95191cca73e01801c4f5b5cf041f7d3d9b27c98e5b8f74ebc9ee93e86b467b6b7cfe7777a931f4d0edf8994e1b7d829cde91b052077a426f5f872e734a0c2f71658c092a524c48ceadee076506009555119f4a6d0e7021d83571252d7766ce3ad01b53df20ab5855edddd686878f9a911a195eddb02e2f5683ddf942ba0546c32fa4a571c414dfefe7a378a0a9e02a0617c42faaad0462f2683782f3acb0611370b2d83bdc4e24c8433874a8d6cd1ecf206d72fc1d75c8ab87be00342e46ee9cbe844ca2be83776333c4840f7d092c4206a2b55ca5a50d0859b9485c4ce3dcf20ad2cf0dfa13ba4326b95b1a4f494270819eae4be1e78fa4a2b12619944ba2fe68510a6ba18f5f7074ad903fc278304c3caf374dd657b5169482f7089b1cb337a92cd43cea4dd178136b5af98d2162db0be2f17400e91e446ce6c161a09b0baceb243043ac973782bb0c864a8684ab284d0d707b96cea18f62f786c61c3a12f09b39b785a4b9db3f86781cc84089061efb89722ac8865e4c35eb68e637136bdd5a321fe58884a7f964d782582c62e0e2c08e38ff65eb48394c4b1733c48598a94f0c027f28a62542bd5c0357a45f0695ef99d073f0f0a28b0f6407fb7b407c90963fb318d0e2974edc6f5065c3aa649d753fa72668c6fc04f5212f498cd5b5b7cc0ab0ee65e6b08202ebe4cf854a6f0b24f75a40cfa0656f7e3820cf29e5641720561520e8448e72a192bbd97295af14467d39c3e24a9ccf37317a3c6e84836f3e9362d2fa53692ac20db266245f3927305ad2066eacb9e0afdb51723e22d86c8ce3fd3686d2ee51d6b84ba6eb3a253cbd4a46f2ccef6dcef85cb8f42538fb9f4eea0163807f1f4f27884f09f2279a0a2f8c3d6c78746e5a48f4db8ff5601159472254d28960f87dbbe20b43cd26897a5e80bfda70b958b05cd017d2e71a5058c5db3262dec5414584b3866d5708f0c52075ee962b694d187752964c53807c26c1f0e398da9332460c1a74b890e7653a4a590e1dfe13f22e0d00970e2eb79aee4e5a81cc6fd953874c4841f48adbd94110cf1c0c57fd0fbf19d94f4782448195c30986f4c70d1671d96ebabab9da14dad6837160003d828c49285e52ca37490b91b71a9b632a6351b3440a38c713993aa1acc95be6dca0a07bd282a64817936e8ee5f59908cc25dcc5d2b281518efd78fba0e3d38e126282da92b790276ff5ef483801a8ab8c86ea3ae0598b80489ff583bf9e3287363b44fc2b8cc464a7cc7dd892883b93f873fb8cd145c8b7009ebe0d9d0a880fddc7396bb527350090edcf5cfa210fddf0e2ec3bac8a884e3e145a1f0e16f85bc129064722851e3b8a9f7c58bf0c97a4b15cadd02548d52deab859e77323d81e0a2d1f25b81414cadde1ec55eea6a21026cde1d708d6e303e3021300906052b0e03021a05000414e1665635c1f39e467a83748c3290b95b126a0ab10414d5b66a57b7b04eb9ea4efede7d48640b902f42020203019000";
    	String pk = keyManagerService.decryptPrivateKey("654321", enc);
    	log.info("recovered {}", pk);
    }
    
    @Test
    public void testDecryptKeystore() throws Exception{
    	String enc = "{\"address\":\"40b81d043e7b45c409cc389f2ee9e94335707d6d\",\"id\":\"15107545-f9bd-4472-abe1-a0010f960e79\",\"version\":3,\"crypto\":{\"cipher\":\"aes-128-ctr\",\"ciphertext\":\"d879a0dd1108d82da46ff89875f240764cffc0047796f2673a663a8a68b16dd0\",\"cipherparams\":{\"iv\":\"510e8b0d769735e1bde8e8f720787687\"},\"kdf\":\"scrypt\",\"kdfparams\":{\"dklen\":32,\"n\":262144,\"p\":1,\"r\":8,\"salt\":\"a20c9408f7be3394e8170d6b75a9c3ee8bb8b37c0b292876e02cc1c8bd037037\"},\"mac\":\"b82851f08c0bf2c5fee344f5deb4b430cf3de08eaf1ce5eacde325623b0ee19d\"}}";
    	String pk = keyManagerService.decryptPrivateKey("123456", enc);
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


















