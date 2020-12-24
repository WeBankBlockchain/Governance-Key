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
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;
import org.web3j.utils.Numeric;

/**
 * @Description SM2PKeyByRandomServiceTest
 * @author yuzhichu
 * @date 2019-12-16 
 */
@Slf4j
public class PKeySM2ByRandomServiceTest{

	private PkeySM2ByRandomService service = new PkeySM2ByRandomService();

	@Test
	public void testGeneratePrivateKey() throws Exception{
		PkeyInfo pkeyInfo = service.generatePrivateKey();
		Assert.assertEquals(32, pkeyInfo.getPrivateKey().length);
		Assert.assertTrue(null != pkeyInfo.getAddress() && !pkeyInfo.getAddress().isEmpty());
		Assert.assertTrue(EccTypeEnums.SM2P256V1.getEccName().equals(pkeyInfo.getEccName()));
		Assert.assertNull(pkeyInfo.getChainCode());
	}

}
