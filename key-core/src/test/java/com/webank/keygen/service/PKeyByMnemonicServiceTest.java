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
import com.webank.keysign.utils.Numeric;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;

import java.util.Objects;

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
public class PKeyByMnemonicServiceTest {

	private PkeyByMnemonicService service = new PkeyByMnemonicService();
		    
	@Test
	public void testCreateMnemonic(){
	    String mnemonicStr = service.createMnemonic(null);
	    String[] words = mnemonicStr.split(" ");
		Assert.assertEquals(12, words.length);
	}

	@Test
	public void testCreateMnemonicDefault(){
		String mnemonicStr = service.createMnemonic(null);
		String[] words = mnemonicStr.split(" ");
		Assert.assertEquals(12, words.length);
	}

	@Test
	public void testCreateMnemonicByEntropy128Bits() {
		String mnemonicStr = service.createMnemonic("0x8a58ab78052b1c02ee5c4957252a415c");
		Assert.assertEquals("medal shed task apart range accident ride matrix fire citizen motion ridge", mnemonicStr);
	}

	@Test
	public void testCreateMnemonicByEntropy256Bits() {
		String mnemonicStr = service.createMnemonic("0x8a58ab78052b1c02ee5c4957252a415c8a58ab78052b1c02ee5c4957252a415c");
		String[] words = mnemonicStr.split(" ");
		Assert.assertEquals(24, words.length);
	}
		    
	@Test
	public void testGeneratePrivateKeyByMnemonic() throws Exception {

		String mnemonicStr = "alpha segment cube animal wash ozone dream search uphold tennis fury abuse";
		PkeyInfo pkeyInfo1 = service.generatePrivateKeyByMnemonic(mnemonicStr, "123456", EccTypeEnums.SM2P256V1);
		Assert.assertTrue(Objects.equals("59cac30eff70215cc1fd7567f63589efd5025421a925deeebb09f4d5a33b4dde", Numeric.toHexString(pkeyInfo1.getPrivateKey())));
		Assert.assertTrue(Objects.equals("0xd833d9616afee92a27fef402965769a81756b7cb", pkeyInfo1.getAddress()));
		Assert.assertTrue(Objects.equals("2751afb3607a68f39d80cf2535c676a355d57cae812657aad10be39f2ebd113b", Numeric.toHexString(pkeyInfo1.getChainCode())));
		Assert.assertTrue(Objects.equals("sm2p256v1", pkeyInfo1.getEccName()));

		PkeyInfo pkeyInfo2 = service.generatePrivateKeyByMnemonic(mnemonicStr, "123456", EccTypeEnums.SECP256K1);
		Assert.assertTrue(Objects.equals("59cac30eff70215cc1fd7567f63589efd5025421a925deeebb09f4d5a33b4dde", Numeric.toHexString(pkeyInfo2.getPrivateKey())));
		Assert.assertTrue(Objects.equals("0x4bf93852e6b29003d21aa280cdfeca6f3d2e60d7", pkeyInfo2.getAddress()));
		Assert.assertTrue(Objects.equals("2751afb3607a68f39d80cf2535c676a355d57cae812657aad10be39f2ebd113b", Numeric.toHexString(pkeyInfo2.getChainCode())));
		Assert.assertTrue(Objects.equals("secp256k1", pkeyInfo2.getEccName()));

	}
}












