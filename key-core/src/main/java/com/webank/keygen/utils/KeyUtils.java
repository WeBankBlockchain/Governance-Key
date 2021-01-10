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

import com.webank.keygen.model.PkeyInfo;
import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.jcajce.provider.asymmetric.ec.BCECPrivateKey;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.web3j.crypto.Keys;
import org.web3j.utils.Numeric;

import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PublicKey;
import java.security.interfaces.ECPrivateKey;
import java.security.spec.ECParameterSpec;
import java.security.spec.ECPoint;
import java.security.spec.ECPublicKeySpec;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;
import java.util.Collections;

/**
 * @Description KeyUtils
 * @author yuzhichu
 * @author wesleywang
 * @date 2019-12-23 
 */
@Slf4j
public class KeyUtils {

	public static byte[] ensure64bytesPubkey(byte[] pubkeBytes){
		if(pubkeBytes.length == 65){
			return Arrays.copyOfRange(pubkeBytes, 1, pubkeBytes.length);
		}
		return pubkeBytes;
	}

	public static PkeyInfo createPkeyInfo(BigInteger privateKey, BigInteger publicKey, String eccName) {
		return createPkeyInfo(privateKey, publicKey, eccName, null);
	}
	
	public static PkeyInfo createPkeyInfo(BigInteger privateKey, BigInteger publicKey, String eccName, byte[] chainCode) {
		PkeyInfo pkey = new PkeyInfo();
		//Ensure bytes are 32 byte-len
		byte[] bytes = Numeric.toBytesPadded(privateKey, 32);
		pkey.setPrivateKey(bytes);
		pkey.setAddress("0x"+Keys.getAddress(publicKey));//public key to address is curve-non-relevant
		pkey.setEccName(eccName);
		pkey.setChainCode(chainCode);
		return pkey;
	}

	public static byte[] get65BytePubKey(byte[] pubKey){
		if(pubKey.length != 64) throw new RuntimeException("pubKey length not 64");
		byte[] bytes = new byte[65];
		bytes[0] = 0x04;
		System.arraycopy(pubKey,0,bytes,1,pubKey.length);
		return bytes;
	}


	public static PublicKey getPublicKey(ECPrivateKey privateKey)
			throws InvalidKeySpecException, NoSuchAlgorithmException, NoSuchProviderException {
		ECParameterSpec params = privateKey.getParams();
		org.bouncycastle.jce.spec.ECParameterSpec bcSpec =
				org.bouncycastle.jcajce.provider.asymmetric.util.EC5Util.convertSpec(params, false);
		org.bouncycastle.math.ec.ECPoint q = bcSpec.getG().multiply(privateKey.getS());
		org.bouncycastle.math.ec.ECPoint bcW = bcSpec.getCurve().decodePoint(q.getEncoded(false));
		ECPoint w =
				new ECPoint(
						bcW.getAffineXCoord().toBigInteger(), bcW.getAffineYCoord().toBigInteger());
		ECPublicKeySpec keySpec = new ECPublicKeySpec(w, tryFindNamedCurveSpec(params));
		return KeyFactory.getInstance("EC", BouncyCastleProvider.PROVIDER_NAME)
				.generatePublic(keySpec);
	}

	public static String getCurve(BCECPrivateKey privateKey){
		//Like SECP256K1Curve
		String curveClass = privateKey.getParameters().getCurve().getClass().getSimpleName();
		String curve = curveClass.substring(0, curveClass.length() - 5).toLowerCase();
		return curve;
	}

	@SuppressWarnings("unchecked")
	private static ECParameterSpec tryFindNamedCurveSpec(ECParameterSpec params) {
		org.bouncycastle.jce.spec.ECParameterSpec bcSpec =
				org.bouncycastle.jcajce.provider.asymmetric.util.EC5Util.convertSpec(params, false);
		for (Object name : Collections.list(org.bouncycastle.jce.ECNamedCurveTable.getNames())) {
			org.bouncycastle.jce.spec.ECNamedCurveParameterSpec bcNamedSpec =
					org.bouncycastle.jce.ECNamedCurveTable.getParameterSpec((String) name);
			if (bcNamedSpec.getN().equals(bcSpec.getN())
					&& bcNamedSpec.getH().equals(bcSpec.getH())
					&& bcNamedSpec.getCurve().equals(bcSpec.getCurve())
					&& bcNamedSpec.getG().equals(bcSpec.getG())) {
				return new org.bouncycastle.jce.spec.ECNamedCurveSpec(
						bcNamedSpec.getName(),
						bcNamedSpec.getCurve(),
						bcNamedSpec.getG(),
						bcNamedSpec.getN(),
						bcNamedSpec.getH(),
						bcNamedSpec.getSeed());
			}
		}
		return params;
	}
}
















