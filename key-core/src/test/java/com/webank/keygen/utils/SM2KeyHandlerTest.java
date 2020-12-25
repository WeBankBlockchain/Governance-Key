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
package com.webank.keygen.utils;

import com.webank.keygen.handler.SM2KeyHandler;
import org.junit.Assert;
import org.junit.Test;
import org.web3j.crypto.ECKeyPair;
import org.web3j.utils.Numeric;

import java.math.BigInteger;

/**
 * @Description SM2KeyHandlerTest
 * @author yuzhichu
 * @date 2019-12-26 
 */
public class SM2KeyHandlerTest {


	@Test
	public void testPublicKeyFromPrivatekKey() throws Exception{

		ECKeyPair ecKeyPair = SM2KeyHandler.generateSM2KeyPair();
		byte[] pkeyBytes = Numeric.toBytesPadded(ecKeyPair.getPrivateKey(),32);
		BigInteger pubExpected = ecKeyPair.getPublicKey();
		BigInteger pubActual = SM2KeyHandler.create(pkeyBytes).getPublicKey();
		Assert.assertEquals(pubExpected, pubActual);

	}
}
