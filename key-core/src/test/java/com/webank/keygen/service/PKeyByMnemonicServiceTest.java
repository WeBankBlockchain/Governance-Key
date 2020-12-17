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
import com.webank.keygen.exception.KeyGenException;
import com.webank.keygen.model.PkeyInfo;
import com.webank.keygen.utils.JacksonUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;

/**
 * PKeyByMnemonicServiceTest
 *
 * @Description: PKeyByMnemonicServiceTest
 * @author graysonzhang
 * @author yuzhichu
 * @date 2019-09-09 17:35:39
 *
 */
@Slf4j
public class PKeyByMnemonicServiceTest extends BaseTest {

	private PkeyByMnemonicService service = new PkeyByMnemonicService();
		    
	@Test
	public void testCreateMnemonic(){
	    String mnemonicStr = service.createMnemonic(null);
	    System.out.println( mnemonicStr);
	}

	@Test
	public void testCreateMnemonicDefault(){
		String mnemonicStr = service.createMnemonic();
		log.info("mnemonic str : {}", mnemonicStr);
	}

	@Test
	public void testCreateMnemonicByEntropy() {
		String mnemonicStr = service.createMnemonic("0x8a58ab78052b1c02ee5c4957252a415c");
		Assert.assertEquals("medal shed task apart range accident ride matrix fire citizen motion ridge", mnemonicStr);
	}
	
	@Test
	public void testGeneratePrivateKeyByMnemonicAbnormally() throws Exception{
		String mnemonicStr = "medal shed task apart range accident ride matrix fire citizen motion ridge";
		try {
			service.generatePrivateKeyByMnemonic(mnemonicStr, null, -1);
			Assert.assertTrue(false);
		}
		catch (KeyGenException e) {
			Assert.assertTrue(true);
		}

	}
		    
	@Test
	public void testGeneratePrivateKeyByMnemonic() throws Exception{
	    String mnemonicStr = "alpha segment cube animal wash ozone dream search uphold tennis fury abuse";
	    PkeyInfo pkeyInfo1 = service.generatePrivateKeyByMnemonic(mnemonicStr, "123456", EccTypeEnums.SM2P256V1.getEccType());
	    log.info("pkey info : {}", JacksonUtils.toJson(pkeyInfo1));
	    
	    PkeyInfo pkeyInfo2 = service.generatePrivateKeyByMnemonic(mnemonicStr, "123456", EccTypeEnums.SECP256K1.getEccType());
	    log.info("pkey info : {}", JacksonUtils.toJson(pkeyInfo2));
	}
	
	@Test
	public void testGeneratePrivateKeyByChainCode() throws Exception{
		String mnemonicStr = "alpha segment cube animal wash ozone dream search uphold tennis fury abuse";
		try {
			@SuppressWarnings("unused")
			PkeyInfo pkeyInfo = service.generatePrivateKeyByChainCode(mnemonicStr, "0x0a1b", EccTypeEnums.SECP256K1.getEccType());
			Assert.assertTrue(false);
		}
		catch (KeyGenException e) {
			Assert.assertTrue(true);
		}
		
		String chaincodeString  = "0x8902265ac00b3e99baef1ffaffe0e9b23ccd71fb482bc32df25b2877c32c33f5";

		@SuppressWarnings("unused")
		PkeyInfo pkeyInfo = service.generatePrivateKeyByChainCode(mnemonicStr, chaincodeString, EccTypeEnums.SECP256K1.getEccType());
	}
}












