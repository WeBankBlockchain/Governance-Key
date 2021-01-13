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

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.webank.keygen.enums.EccTypeEnums;
import com.webank.keygen.enums.ExceptionCodeEnums;
import com.webank.keygen.enums.KeyFileTypeEnums;
import com.webank.keygen.exception.KeyGenException;
import com.webank.keygen.key.impl.EccKeyAlgorithm;
import com.webank.keygen.key.impl.Sm2KeyAlgorithm;
import com.webank.keygen.model.DecryptResult;
import com.webank.keygen.utils.FileOperationUtils;
import com.webank.keygen.utils.JacksonUtils;
import com.webank.keygen.utils.KeyUtils;
import com.webank.keygen.wallet.KeystoreWalletAdaptor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.fisco.bcos.sdk.crypto.keypair.CryptoKeyPair;
import org.web3j.crypto.*;
import org.web3j.utils.Numeric;

import java.io.File;
import java.io.IOException;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

/**
 * KeyStoreFormat
 *
 * @Description: KeyStoreFormat
 * @author graysonzhang
 * @date 2019-12-23 15:01:02
 *
 */
@Slf4j
public class KeyStoreEncrypt {    
    
    /**    
     * encrypt private key to keystore file  
     * 
     * @param password
     * @param privateKey
     * @throws KeyGenException
     * @return String       
     * @throws IOException 
     * @throws JsonMappingException 
     * @throws JsonGenerationException 
     */
    public static String storeEncryptPrivateKeyToFile(String password, byte[] privateKey, EccTypeEnums eccTypeEnums, String destinationDirectory) throws KeyGenException, JsonGenerationException, JsonMappingException, IOException{
        
        try {
            CryptoKeyPair cryptoKeyPair = KeyUtils.getCryptKeyPair(privateKey, eccTypeEnums);
            //Encrypt key
            String encryptKey = encryptPrivateKey(password, privateKey, eccTypeEnums);
            //filename
            String filename = buildDefaultFilename(cryptoKeyPair.getAddress());
            //Store private key
            return storeEncryptPrivateKeyToFile(encryptKey, filename, destinationDirectory);
        } catch (CipherException e) {
            log.info("encrypt private key error", e);
            return null;
        }
    }

    /**
     *
     * @param encryptKey
     * @param fileName file name
     * @param destinationDirectory
     * @throws KeyGenException
     */
    public static String storeEncryptPrivateKeyToFile(String encryptKey, String fileName, String destinationDirectory) throws KeyGenException {
        //Generate file path
        String filePath = FileOperationUtils.generateFilePath(fileName, destinationDirectory);
        //Write to path
        FileOperationUtils.writeFile(filePath, encryptKey);
        return filePath;
    }
    
    /**
     * Encrypt private key
     * @param password
     * @param privateKey
     * @return Encrypted data
     * @throws KeyGenException
     * @throws CipherException
     */
    public static String encryptPrivateKey(String password, byte[] privateKey, EccTypeEnums eccTypeEnums) throws CipherException{
        CryptoKeyPair cryptoKeyPair = KeyUtils.getCryptKeyPair(privateKey, eccTypeEnums);
        WalletFile walletFile = KeystoreWalletAdaptor.createStandard(password, cryptoKeyPair);
        return JacksonUtils.toJson(walletFile);
    }

    /**    
     * decrypt keystore to private key
     * 
     * @param password
     * @param encryptPrivateKey  
     * @return byte[]       
     * @throws IOException 
     * @throws JsonMappingException 
     * @throws JsonParseException 
     */
    public static byte[] decryptPrivateKey(String password, String encryptPrivateKey) throws CipherException{
        WalletFile walletFile = JacksonUtils.strToObject(encryptPrivateKey, WalletFile.class);
        CryptoKeyPair cryptoKeyPair = KeystoreWalletAdaptor.decrypt(password, walletFile);
        return Numeric.hexStringToByteArray(cryptoKeyPair.getHexPrivateKey());
    }

    public static DecryptResult decryptWithEccType(String password, String encryptPrivateKey) throws Exception {
        WalletFile walletFile = JacksonUtils.strToObject(encryptPrivateKey, WalletFile.class);
        CryptoKeyPair cryptoKeyPair = KeystoreWalletAdaptor.decrypt(password, walletFile);
        return new DecryptResult(
                Numeric.hexStringToByteArray(cryptoKeyPair.getHexPrivateKey()),
                KeyUtils.getEccType(cryptoKeyPair));
    }

    /**
     * Decrypt key from file
     * @param password
     * @param filePath
     * @return
     * @throws IOException
     */
    public static byte[] decryptPrivateKeyByFile(String password, String filePath) throws IOException,CipherException {
    	File file = FileOperationUtils.getFile(filePath);
        String encryptPrivateKey = FileUtils.readFileToString(file, "UTF-8");
        return decryptPrivateKey(password, encryptPrivateKey);
    }

    private static String buildDefaultFilename(String address){
        DateTimeFormatter format = DateTimeFormatter.ofPattern("'UTC--'yyyy-MM-dd'T'HH-mm-ss.nVV'--'");
        ZonedDateTime now = ZonedDateTime.now(ZoneOffset.UTC);
        String fileName = now.format(format) + address + KeyFileTypeEnums.KEYSTORE_FILE.getKeyFilePostfix();
        return fileName;
    }
}
