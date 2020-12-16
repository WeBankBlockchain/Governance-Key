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

import org.junit.Test;
import org.web3j.utils.Numeric;

import com.webank.keygen.BaseTest;
import com.webank.keygen.exception.KeyGenException;
import com.webank.keygen.model.PkeyInfo;

import lombok.extern.slf4j.Slf4j;

/**
 * @Description SM2PKeyByRandomServiceTest
 * @author yuzhichu
 * @date 2019-12-16 
 */
@Slf4j
public class PKeySM2ByRandomServiceTest extends BaseTest{

	private PkeySM2ByRandomService service = new PkeySM2ByRandomService();
			
	@Test
	public void example() throws Exception{
	    PkeyInfo ecKeyPair = service.generatePrivateKey();
	    log.info(Numeric.toHexString(ecKeyPair.getPrivateKey()));
	}   
	
	@Test
	public void testGeneratePrivateKeyByChainCode() throws KeyGenException {
		String privateKey = "2c8fa96c22238e071743ee7c5b9a2b331f474f4e42d720aa3b48a507d6c5c967";
		String chainCode = "32C4AE2C1F1981195F9904466A39C9948FE30BBFF2660BE1715A4589334C74C7";
		PkeyInfo newPrivateKey = service.generatePrivateKeyByChainCode(Numeric.hexStringToByteArray(privateKey), chainCode);
		log.info(Numeric.toHexString(newPrivateKey.getPrivateKey()));
	}
}
