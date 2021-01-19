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
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;
/**
 * ECKPairServiceTest
 *
 * @Description: ECKPairServiceTest
 * @author graysonzhang
 * @author yuzhichu
 * @date 2019-07-08 19:01:46
 *
 */
@Slf4j
public class PkeyByRandomServiceTest {

	private PkeyByRandomService service = new PkeyByRandomService();

	@Test
	public void testGeneratePrivateKey() throws Exception{
		PkeyInfo pkeyInfo = service.generatePrivateKey();
		Assert.assertEquals(32, pkeyInfo.getPrivateKey().length);
		Assert.assertTrue(null != pkeyInfo.getAddress() && !pkeyInfo.getAddress().isEmpty());
		Assert.assertTrue(EccTypeEnums.SECP256K1.getEccName().equals(pkeyInfo.getEccName()));
		Assert.assertNotNull(pkeyInfo.getChainCode());
	}

	@Test
	public void testCreatePrivateKey() throws Exception{
		PkeyInfo pkeyInfo = PkeyInfo.fromHexString("0xaa",EccTypeEnums.SECP256K1);
	}

}
