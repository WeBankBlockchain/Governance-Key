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
package com.webank.keygen.service;


import com.webank.keygen.BaseTest;
import com.webank.keygen.enums.EccTypeEnums;
import com.webank.keygen.key.KeyEncryptAlgorithm;
import com.webank.keygen.key.impl.KeystoreEncryptAlgorithm;
import com.webank.keygen.key.impl.P12EncryptAlgorithm;
import com.webank.keygen.key.impl.PemEncryptAlgorithm;
import com.webank.keygen.model.PkeyInfo;
import lombok.extern.slf4j.Slf4j;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * @Description Example
 * @author yuzhichu
 * @date 2019-12-23 
 */
@Slf4j
public class FormatEncryptTest extends BaseTest{

	private PkeyByRandomService keyGenerationService = new PkeyByRandomService();

	    
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
	public void testKeyStoreFormat() throws Exception{
	   PkeyInfo pkey = keyGenerationService.generatePrivateKey();
	   String password = "123456";
	   KeyEncryptAlgorithm algorithm = new KeystoreEncryptAlgorithm();
	   String encryptKey = algorithm.encrypt(password, pkey.getPrivateKey(), pkey.getAddress(), EccTypeEnums.SECP256K1.getEccName());
	   log.info("keystore encrypt key\n {}", encryptKey);
	   algorithm.exportKey(encryptKey, pkey.getAddress(), this.tmpDir);
	}
	
	@Test
	public void testPEMFormat() throws Exception {
		PkeyInfo pkey = keyGenerationService.generatePrivateKey();
		KeyEncryptAlgorithm algorithm = new PemEncryptAlgorithm();
		String encryptKey = algorithm.encrypt(null, pkey.getPrivateKey(), pkey.getAddress(), EccTypeEnums.SECP256K1.getEccName());
		log.info("pem encrypt key \n{}", encryptKey);
		algorithm.exportKey(encryptKey, pkey.getAddress(), this.tmpDir);
	}
	
	@Test
	public void testP12Format() throws Exception {
		PkeyInfo pkey = keyGenerationService.generatePrivateKey();
		String password = "123456";
		KeyEncryptAlgorithm algorithm = new P12EncryptAlgorithm();
		String encryptKey = algorithm.encrypt(password, pkey.getPrivateKey(), pkey.getAddress(), EccTypeEnums.SECP256K1.getEccName());
		log.info("p12 encrypt key {}", encryptKey);
		algorithm.exportKey(encryptKey, pkey.getAddress(), this.tmpDir);
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
}












