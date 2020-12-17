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

import com.webank.keygen.encrypt.KeyStoreEncrypt;
import com.webank.keygen.encrypt.P12Encrypt;
import com.webank.keygen.encrypt.PemEncrypt;
import com.webank.keygen.enums.EccTypeEnums;
import org.junit.Assert;
import org.junit.Test;

import java.security.SecureRandom;

/**
 * @Description FormatDecryptTest
 * @author yuzhichu
 * @date 2019-12-26 
 */
public class FormatDecryptTest {

	private SecureRandom random = new SecureRandom();

	
	@Test
	public void testDecryptKeystore() throws Exception{
		byte[] pkey = getPrivateKey();
		String encrypted = KeyStoreEncrypt.encryptPrivateKey("123", pkey, "address");
		byte[] recovered = KeyStoreEncrypt.decryptPrivateKey("123", encrypted);
		Assert.assertArrayEquals(pkey, recovered);
	}
	
	@Test
	public void testDecryptPEM() throws Exception {
		byte[] pkey = getPrivateKey();
		String encrypted = PemEncrypt.encryptPrivateKey(pkey, EccTypeEnums.SM2P256V1);
		byte[] recovered = PemEncrypt.decryptPrivateKey(encrypted);
		Assert.assertArrayEquals(pkey, recovered);
	}
	
	@Test
	public void testDecryptP12() throws Exception {
		byte[] pkey = getPrivateKey();
		String encrypted = P12Encrypt.encryptPrivateKey("123", pkey, EccTypeEnums.SM2P256V1.getEccName());
		byte[] recovered = P12Encrypt.decryptPrivateKey("123", encrypted);
		Assert.assertArrayEquals(pkey, recovered);
		
		try {
			P12Encrypt.decryptPrivateKey("456", encrypted);
			Assert.assertTrue(false);
		}
		catch (Exception e) {
			Assert.assertTrue(true);
		}
	}
	
	private byte[] getPrivateKey()  throws Exception{
		byte[] bytes = new byte[32];
		this.random.nextBytes(bytes);
		return bytes;
	}
}
