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


import com.webank.keygen.enums.EccTypeEnums;
import com.webank.keygen.enums.KeyFileTypeEnums;
import com.webank.keygen.model.DecryptResult;
import com.webank.keygen.model.ECCPrivateKey;
import com.webank.keygen.utils.CertUtils;
import com.webank.keygen.utils.FileOperationUtils;
import com.webank.keygen.utils.KeyUtils;
import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.jcajce.provider.asymmetric.ec.BCECPrivateKey;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.fisco.bcos.sdk.crypto.keypair.CryptoKeyPair;
import org.web3j.utils.Numeric;

import java.io.*;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;

/**
 * P12Format
 *
 * @Description: P12Format
 * @author graysonzhang
 * @author yuzhichu
 * @date 2019-12-23 15:01:26
 *
 */
@Slf4j
public class P12Encrypt{
	static {
		if (Security.getProvider(BouncyCastleProvider.PROVIDER_NAME) == null) {
			Security.addProvider(new BouncyCastleProvider());
		}
	}
	//KeyStore force that a key should be accompanied with a certificate so we put a dummy one
	private static Certificate dummyCert;
	
	static {
		try {
			dummyCert = CertUtils.generateDummyCertificate();
		} catch (Exception e) {
			log.error("Error generating dummy cert, so p12 cannot be used",e);
		}
	}

	//Keep consistant with web3sdk
	private static final String NAME = "key";
	/**
	 * Encrypt private key using p12 format
	 * @param password Password
	 * @param destinationDirectory
	 * @throws KeyStoreException 
	 * @throws CertificateException 
	 * @throws NoSuchAlgorithmException 
	 * @throws NoSuchProviderException 
	 */
    public static String storePrivateKey(String password, byte[] privateKey, EccTypeEnums eccTypeEnums, String destinationDirectory) throws Exception{
		CryptoKeyPair cryptoKeyPair = KeyUtils.getCryptKeyPair(privateKey, eccTypeEnums);
		String encryptKey = encryptPrivateKey(password, privateKey, eccTypeEnums);
		String fileName = cryptoKeyPair.getAddress()+KeyFileTypeEnums.P12_FILE.getKeyFilePostfix();
		return storePrivateKey(encryptKey, fileName, destinationDirectory);
    }

	public static String storePrivateKey(String encryptKey,
										 String fileName, String destinationDirectory){
		String filePath = FileOperationUtils.generateFilePath(
				fileName,
				destinationDirectory);
		FileOperationUtils.writeBinary(filePath, encryptKey);
		return filePath;
	}
   
    /**
     * Encrypt private key
     * @param password
     * @param privateKey
     * @return Encrypted data
     * @throws Exception
     */
    public static String encryptPrivateKey(String password, byte[] privateKey, EccTypeEnums eccTypeEnums) throws Exception{
    	if(privateKey == null || privateKey.length != 32) {
    		throw new IllegalArgumentException("privateKey");
    	}
    	char[] passCharArray = password == null?new char[0]:password.toCharArray();
    	ByteArrayOutputStream os = new ByteArrayOutputStream();
    	Certificate[] certs = new Certificate[] {dummyCert};
    	KeyStore ks = KeyStore.getInstance("PKCS12", "BC");
    	ks.load(null);
    	ks.setKeyEntry(NAME, new ECCPrivateKey(privateKey,eccTypeEnums.getEccName()), passCharArray, certs);
    	ks.store(os, passCharArray);
    	os.close();
    	return Numeric.toHexString(os.toByteArray());
    }

	public static DecryptResult decryptFully(String password, String encryptPrivateKey) throws Exception{
		char[] passCharArray = password == null?new char[0]:password.toCharArray();
		KeyStore ks = KeyStore.getInstance("PKCS12", "BC");
		ks.load(new ByteArrayInputStream(Numeric.hexStringToByteArray(encryptPrivateKey)), passCharArray);
		BCECPrivateKey k = (BCECPrivateKey)ks.getKey(NAME, passCharArray);
		byte[] rawkey = Numeric.toBytesPadded(k.getD(), 32);
		String curve = KeyUtils.getCurve(k);
		return new DecryptResult(rawkey, curve);
    }
	/**
     * Extract plain private key from encrypted key
     * @param password Password
     * @param encryptPrivateKey Encrypted private key
     * @return raw private key bytes
     * @throws IOException 
     * @throws CertificateException 
     * @throws NoSuchAlgorithmException 
     * @throws NoSuchProviderException 
     * @throws KeyStoreException 
     * @throws UnrecoverableKeyException 
     */
    public static byte[] decryptPrivateKey(String password, String encryptPrivateKey) throws UnrecoverableKeyException, KeyStoreException, NoSuchProviderException, NoSuchAlgorithmException, CertificateException, IOException{
    	byte[] bytes = Numeric.hexStringToByteArray(encryptPrivateKey);
    	ByteArrayInputStream in = new ByteArrayInputStream(bytes);
    	return decryptPrivateKey(password, in);
    } 

    /**
     * Decrypt p12 file
     * @param password
     * @param filePath
     * @return
     * @throws NoSuchAlgorithmException
     * @throws CertificateException
     * @throws IOException
     * @throws KeyStoreException
     * @throws NoSuchProviderException
     * @throws UnrecoverableKeyException
     */
    public static byte[] decryptPrivateKeyByFile(String password, String filePath) 
    		throws NoSuchAlgorithmException, CertificateException, IOException, KeyStoreException, 
    		NoSuchProviderException, UnrecoverableKeyException{
    	FileInputStream fIn = null;
    	try {
			fIn = new FileInputStream(filePath);
			return decryptPrivateKey(password, fIn);
    	}
    	finally {
			if(fIn != null) {
				try {
					fIn.close(); 
				}
				catch (Exception e) {
				}
			}
		}
    } 
    
    private static byte[] decryptPrivateKey(String password, InputStream in) throws KeyStoreException, NoSuchProviderException, UnrecoverableKeyException, NoSuchAlgorithmException, CertificateException, IOException {
       	char[] passCharArray = password == null?new char[0]:password.toCharArray();
        KeyStore ks = KeyStore.getInstance("PKCS12", "BC");
        ks.load(in, passCharArray);
        BCECPrivateKey k = (BCECPrivateKey)ks.getKey(NAME, passCharArray);
        return Numeric.toBytesPadded(k.getD(), 32);
    }
}





