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

import com.webank.keygen.enums.EccTypeEnums;
import com.webank.keygen.model.PkeyInfo;
import com.webank.keygen.utils.KeyUtils;
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
		PkeyInfo pkeyInfo2 = service.generatePrivateKeyByMnemonic(mnemonicStr, "123456", EccTypeEnums.SECP256K1);

		String pkeyInfo1Str = Numeric.toHexString(pkeyInfo1.getPrivateKey());
		String address1 = pkeyInfo1.getAddress();
		String cc1 = Numeric.toHexString(pkeyInfo1.getChainCode());

		Assert.assertTrue(Objects.equals("ce46107d965618bccc7e6de53b889ac893dfb38a3dacdf571feba67ff5d334b9", pkeyInfo1Str));
		Assert.assertTrue(KeyUtils.isAddressEquals("8cc5d505d02a7f9436934e1d4f16818d100f1373", address1));
		Assert.assertTrue(Objects.equals("2579ab059f514514a04b0f941bd8c09b53b62cdbfa8cfdfa688869ce45fbd240", cc1));
		Assert.assertTrue(Objects.equals("sm2p256v1", pkeyInfo1.getEccName()));

		String pkeyInfo2Str = Numeric.toHexString(pkeyInfo2.getPrivateKey());
		String address2 = pkeyInfo2.getAddress();
		String cc2 = Numeric.toHexString(pkeyInfo2.getChainCode());

		Assert.assertTrue(Objects.equals("ce46107d965618bccc7e6de53b889ac893dfb38a3dacdf571feba67ff5d334b9", pkeyInfo2Str));
		Assert.assertTrue(KeyUtils.isAddressEquals("6f4da6efc551de988ce3f65bd9d21e9471d6f0ee",address2));
		Assert.assertTrue(Objects.equals("2579ab059f514514a04b0f941bd8c09b53b62cdbfa8cfdfa688869ce45fbd240", cc2));
		Assert.assertTrue(Objects.equals("secp256k1", pkeyInfo2.getEccName()));
	}

	@Test
	public void testGeneratePrivateKeyByMnemonic2() throws Exception {
		String mnemonicStr = "common feel corn harvest dirt rapid potato verify enough sausage eye limb";
		PkeyInfo pkeyInfo1 = service.generatePrivateKeyByMnemonic(mnemonicStr, "123456", EccTypeEnums.SM2P256V1);
		PkeyInfo pkeyInfo2 = service.generatePrivateKeyByMnemonic(mnemonicStr, "123456", EccTypeEnums.SECP256K1);

		String pkeyInfo1Str = Numeric.toHexString(pkeyInfo1.getPrivateKey());
		String address1 = pkeyInfo1.getAddress();
		String cc1 = Numeric.toHexString(pkeyInfo1.getChainCode());

		Assert.assertTrue(Objects.equals("17370d3bb51ff24ef149e410dd9c07ba57bedeb509adbd4b21e07fbaa72a4301", pkeyInfo1Str));
		Assert.assertTrue(KeyUtils.isAddressEquals("493286567e6b18601fca2acd1d1af3b695fc3994", address1));
		Assert.assertTrue(Objects.equals("1f254a38fa868219824f68136727ee1fb2df638c31feabdba20de870ba484457", cc1));
		Assert.assertTrue(Objects.equals("sm2p256v1", pkeyInfo1.getEccName()));

		String pkeyInfo2Str = Numeric.toHexString(pkeyInfo2.getPrivateKey());
		String address2 = pkeyInfo2.getAddress();
		String cc2 = Numeric.toHexString(pkeyInfo2.getChainCode());

		Assert.assertTrue(Objects.deepEquals("17370d3bb51ff24ef149e410dd9c07ba57bedeb509adbd4b21e07fbaa72a4301", pkeyInfo2Str));
		Assert.assertTrue(KeyUtils.isAddressEquals("f80e95fb536ad12518737bc3dc1a7582ffe66768",address2));
		Assert.assertTrue(Objects.deepEquals("1f254a38fa868219824f68136727ee1fb2df638c31feabdba20de870ba484457", cc2));
		Assert.assertTrue(Objects.deepEquals("secp256k1", pkeyInfo2.getEccName()));
	}
}












