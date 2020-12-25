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
package com.webank.keygen.handler;

import com.webank.keygen.constants.SM2Constants;
import com.webank.keygen.exception.KeyGenException;
import com.webank.keygen.utils.KeyUtils;
import com.webank.wedpr.crypto.CryptoResult;
import com.webank.wedpr.crypto.NativeInterface;
import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.math.ec.ECPoint;
import org.web3j.crypto.ECKeyPair;
import org.web3j.utils.Numeric;

import java.math.BigInteger;
import java.security.KeyPairGenerator;
import java.security.SecureRandom;
import java.security.Security;
import java.security.spec.ECGenParameterSpec;
import java.util.Arrays;

/**
 * @Description SM2KeyHandler
 * @author yuzhichu
 * @date 2019-12-18 
 */
@Slf4j
public class SM2KeyHandler {

	private static SecureRandom random;
	private static KeyPairGenerator keyPairGenerator;
	private static ECGenParameterSpec spec;
	
	static {
	    if (Security.getProvider(BouncyCastleProvider.PROVIDER_NAME) == null) {
	    	Security.addProvider(new BouncyCastleProvider());
	    }
		random = new SecureRandom();
		try {
			spec = new ECGenParameterSpec("sm2p256v1");
			keyPairGenerator = KeyPairGenerator.getInstance("ECDSA", "BC");
 	        keyPairGenerator.initialize(spec, random);
		} catch (Exception e) {
			log.error("Error initlizing SM2KeyHandler ", e);
			throw new RuntimeException(e);
		} 	
	}
	
	
    public static ECKeyPair generateSM2KeyPair() throws KeyGenException {
		CryptoResult result = NativeInterface.sm2keyPair();
		if(result.getWedprErrorMessage() != null){
			log.error("Failed to generate sm2 keypair: {}",result.getWedprErrorMessage());
			throw new KeyGenException(result.getWedprErrorMessage());
		}
		BigInteger privateVal = new BigInteger(result.getPrivteKey(), 16);
		byte[] pubkeyBytes = KeyUtils.ensure64bytesPubkey(Numeric.hexStringToByteArray(result.getPublicKey()));

		BigInteger publicVal = new BigInteger(1, pubkeyBytes);
		return new ECKeyPair(privateVal, publicVal);
    }
    
    public static ECKeyPair create(byte[] privKeyBytes) {
		//The pubkey will be 64 byte long
    	BigInteger privKeyBigInt = Numeric.toBigInt(privKeyBytes);
    	byte[] pubKeyBytes = SM2PrivateKeyToPublicKey(privKeyBytes);
    	BigInteger pubKeyBigInt = Numeric.toBigInt(pubKeyBytes);
    	ECKeyPair ecKeyPair = new ECKeyPair(privKeyBigInt, pubKeyBigInt);
    	return ecKeyPair;
    }
    
	/**
	 * Return 64 bytes encoded public key
	 * @param privateKey
	 * @return
	 */
	public static byte[] SM2PrivateKeyToPublicKey(byte[] privateKey) {
		BigInteger d = Numeric.toBigInt(privateKey);
		ECPoint g = SM2Constants.GPOINT;
		ECPoint q = g.multiply(d);
		byte[] data = q.getEncoded(false);
		return Arrays.copyOfRange(data, 1, data.length);
	}
}














