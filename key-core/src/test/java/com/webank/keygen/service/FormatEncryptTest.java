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
import com.webank.keygen.key.KeyEncryptAlgorithm;
import com.webank.keygen.key.impl.KeystoreEncryptAlgorithm;
import com.webank.keygen.key.impl.P12EncryptAlgorithm;
import com.webank.keygen.key.impl.PemEncryptAlgorithm;
import com.webank.keygen.model.DecryptResult;
import com.webank.keygen.model.PkeyInfo;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;

/**
 * @Description Example
 * @author yuzhichu
 * @date 2019-12-23 
 */
@Slf4j
public class FormatEncryptTest extends BaseTest{

	private PkeyByRandomService keyGenerationService = new PkeyByRandomService();

	
	@Test
	public void testKeyStoreFormat() throws Exception {
		PkeyInfo pkey = keyGenerationService.generatePrivateKey();
		String password = "123456";
		KeyEncryptAlgorithm algorithm = new KeystoreEncryptAlgorithm();
		String encryptKey = algorithm.encrypt(password, pkey.getPrivateKey(), pkey.getAddress(), pkey.getEccName());

		DecryptResult decryptResult = algorithm.decryptFully(password, encryptKey);
		Assert.assertArrayEquals(pkey.getPrivateKey(), decryptResult.getPrivateKey());
		Assert.assertTrue(pkey.getEccName().equals(decryptResult.getEccType()));
	}
	
	@Test
	public void testPEMFormat() throws Exception {
		PkeyInfo pkey = keyGenerationService.generatePrivateKey();
		KeyEncryptAlgorithm algorithm = new PemEncryptAlgorithm();
		String encryptKey = algorithm.encrypt(null, pkey.getPrivateKey(), pkey.getAddress(), pkey.getEccName());

		DecryptResult decryptResult = algorithm.decryptFully(null, encryptKey);
		Assert.assertArrayEquals(pkey.getPrivateKey(), decryptResult.getPrivateKey());
		Assert.assertTrue(pkey.getEccName().equals(decryptResult.getEccType()));
	}
	
	@Test
	public void testP12Format() throws Exception {
		PkeyInfo pkey = keyGenerationService.generatePrivateKey();
		KeyEncryptAlgorithm algorithm = new P12EncryptAlgorithm();
		String encryptKey = algorithm.encrypt(null, pkey.getPrivateKey(), pkey.getAddress(), pkey.getEccName());

		DecryptResult decryptResult = algorithm.decryptFully(null, encryptKey);
		Assert.assertArrayEquals(pkey.getPrivateKey(), decryptResult.getPrivateKey());
		Assert.assertTrue(pkey.getEccName().equals(decryptResult.getEccType()));
	}
}












