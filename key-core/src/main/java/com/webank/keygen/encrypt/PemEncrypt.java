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
package com.webank.keygen.encrypt;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.StringReader;
import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.Security;
import java.security.spec.PKCS8EncodedKeySpec;

import com.webank.keygen.enums.KeyFileTypeEnums;
import com.webank.keygen.handler.ECKeyHandler;
import com.webank.keygen.handler.SM2KeyHandler;
import com.webank.keygen.model.DecryptResult;
import com.webank.keygen.utils.FileOperationUtils;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.DERBitString;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.asn1.sec.ECPrivateKey;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x9.X962Parameters;
import org.bouncycastle.asn1.x9.X9ObjectIdentifiers;
import org.bouncycastle.jcajce.provider.asymmetric.ec.BCECPrivateKey;
import org.bouncycastle.jcajce.provider.asymmetric.util.ECUtil;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.util.BigIntegers;
import org.bouncycastle.util.io.pem.PemObject;
import org.bouncycastle.util.io.pem.PemReader;
import org.bouncycastle.util.io.pem.PemWriter;
import org.web3j.utils.Numeric;
import com.webank.keygen.enums.EccTypeEnums;
import com.webank.keygen.utils.KeyUtils;


/**
 * PemFormat
 *
 * @Description: PemFormat
 * @author graysonzhang
 * @author yuzhichu
 * @date 2019-12-23 15:01:13
 *
 */
public class PemEncrypt{
	static {
		if (Security.getProvider(BouncyCastleProvider.PROVIDER_NAME) == null) {
			Security.addProvider(new BouncyCastleProvider());
		}
	}
	
	/**
	 * Convert plain private key to PEM format and save to disk
	 */
    public static String storePrivateKey(byte[] privateKey, EccTypeEnums eccTypeEnums,
    		String address, String destinationDirectory) throws Exception{
    	if(privateKey == null || privateKey.length != 32) {
    		throw new IllegalArgumentException("privateKey");
    	}
    	String encryptPkey = encryptPrivateKey(privateKey, eccTypeEnums);
		return storePrivateKey(encryptPkey, address, destinationDirectory);
    }

	public static String storePrivateKey(String encryptKey,
										 String address, String destinationDirectory) throws Exception{
		address = address.startsWith("0x")?address:"0x"+address;
		String filePath = FileOperationUtils.generateFilePath(
				address+ KeyFileTypeEnums.PEM_FILE.getKeyFilePostfix(),
				destinationDirectory);
		FileOperationUtils.writeFile(filePath, encryptKey);
		return filePath;
	}
    /**
     * Encrypt private key
     * @param privateKey
     * @param eccTypeEnums
     * @return Encrypted data
     * @throws IOException
     */
    public static String encryptPrivateKey(byte[] privateKey, EccTypeEnums eccTypeEnums) 
    		throws Exception {
       	BigInteger key = Numeric.toBigInt(privateKey);
       	BigInteger pubKey = null;
       	if(eccTypeEnums == EccTypeEnums.SECP256K1){
			pubKey = ECKeyHandler.create(privateKey).getPublicKey();
		}
       	else if (eccTypeEnums == EccTypeEnums.SM2P256V1){
			pubKey = SM2KeyHandler.create(privateKey).getPublicKey();
		}

        //1. Encapsulate curve meta info and private key bytes in PKCS#8 format
    	ASN1ObjectIdentifier curveOid = ECUtil.getNamedCurveOid(eccTypeEnums.getEccName());
    	X962Parameters params = new X962Parameters(curveOid);
    	ECPrivateKey keyStructure = new ECPrivateKey(256, key,
				new DERBitString(KeyUtils.get65BytePubKey(BigIntegers.asUnsignedByteArray(pubKey))),
				null);
    	PrivateKeyInfo privateKeyInfo = new PrivateKeyInfo(
    				new AlgorithmIdentifier(X9ObjectIdentifiers.id_ecPublicKey, params),
    				keyStructure);
    		
    	//2. Serialize the private key data to output stream
    	ByteArrayOutputStream bos = new ByteArrayOutputStream();
        PemWriter w = new PemWriter (new OutputStreamWriter(bos));
    	try {
    	    ASN1Object o = (ASN1Object) privateKeyInfo.toASN1Primitive();
    	    w.writeObject (new PemObject ("PRIVATE KEY", o.getEncoded("DER")));
    	    w.flush();
    	    //3. Gather result and return.
    	    return new String(bos.toByteArray());
    	}
    	finally {
    		try {
    			if(w != null){
    			    w.close();
    			}
    		}
    		catch (Exception e) {
			}
		}
    }

    public static DecryptResult decryptFully(String encryptedKey) throws Exception{
		PemReader pemReader = new PemReader(new StringReader(encryptedKey));
		PemObject pemObject =pemReader.readPemObject();
		try {
			PKCS8EncodedKeySpec encodedKeySpec = new PKCS8EncodedKeySpec(pemObject.getContent());
			KeyFactory keyFacotry = KeyFactory.getInstance("EC", BouncyCastleProvider.PROVIDER_NAME);
			PrivateKey privateKey =  keyFacotry.generatePrivate(encodedKeySpec);
			BCECPrivateKey k = (BCECPrivateKey)privateKey;
			byte[] rawkey = Numeric.toBytesPadded(k.getD(), 32);
			String curve = KeyUtils.getCurve(k);
			return new DecryptResult(rawkey, curve);
		}
		finally {
			try {
				if(pemReader != null){
					pemReader.close();
				}
			}
			catch (Exception e) {
			}
		}
	}
    /**
     * Decrypt encrypt key
     * @param encryptPrivateKey
     * @return
     * @throws IOException
     */
	public static byte[] decryptPrivateKey(String encryptPrivateKey) throws Exception{
		return decryptPrivateKey(new StringReader(encryptPrivateKey));
    }   
	
	/**
	 * Decrypt encrypt key from pem file
	 * @param filePath
	 * @return
	 * @throws IOException
	 */
	public static byte[] decryptPrivateKeyByFile(String filePath) 
			throws Exception{
	    FileInputStream fIn = null;
	    try {
	    	fIn = new FileInputStream(filePath);
	    	return decryptPrivateKey(new InputStreamReader(fIn));
	    }
	    finally {
    		try {
    			if(fIn != null) {
    				fIn.close();
    			}
    		}
    		catch (Exception e) {
			}
		}
    }
	
	private static byte[] decryptPrivateKey(Reader reader) throws Exception {
		PemReader pemReader = new PemReader(reader);
		PemObject pemObject =pemReader.readPemObject();
	    try {
			PKCS8EncodedKeySpec encodedKeySpec = new PKCS8EncodedKeySpec(pemObject.getContent());
			KeyFactory keyFacotry = KeyFactory.getInstance("EC", BouncyCastleProvider.PROVIDER_NAME);

			PrivateKey privateKey =  keyFacotry.generatePrivate(encodedKeySpec);
			BCECPrivateKey bcecPrivateKey = (BCECPrivateKey)privateKey;
		    return Numeric.toBytesPadded(bcecPrivateKey.getD(),32);
	    }
	    finally {
    		try {
    			if(pemReader != null){
					pemReader.close();
    			}
    		}
    		catch (Exception e) {
			}
		}
	}
}













