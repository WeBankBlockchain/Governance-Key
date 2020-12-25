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
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
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
    public static String storeEncryptPrivateKeyToFile(String password, byte[] privateKey, String address, String destinationDirectory) throws KeyGenException, JsonGenerationException, JsonMappingException, IOException{
        
        try {
            //Encrypt key
            String encryptKey = encryptPrivateKey(password, privateKey, address);
            //Store private key
            return storeEncryptPrivateKeyToFile(encryptKey, address, destinationDirectory);
        } catch (CipherException e) {
            log.info("encrypt private key error", e);
            return null;
        }
    }

    /**
     *
     * @param encryptKey
     * @param address
     * @param destinationDirectory
     * @throws KeyGenException
     */
    public static String storeEncryptPrivateKeyToFile(String encryptKey, String address, String destinationDirectory) throws KeyGenException {
        //Generate file path
        address = address.startsWith("0x")?address:"0x"+address;
        DateTimeFormatter format = DateTimeFormatter.ofPattern("'UTC--'yyyy-MM-dd'T'HH-mm-ss.nVV'--'");
        ZonedDateTime now = ZonedDateTime.now(ZoneOffset.UTC);
        String fileName = now.format(format) + address + KeyFileTypeEnums.KEYSTORE_FILE.getKeyFilePostfix();
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
    public static String encryptPrivateKey(String password, byte[] privateKey, String address) throws KeyGenException, CipherException{
        String privateStr = Numeric.toHexString(privateKey);
        if (StringUtils.isBlank(privateStr) || !WalletUtils.isValidPrivateKey(privateStr)) {
            log.error("private key format is not right ");
            throw new KeyGenException(ExceptionCodeEnums.PRIVATEKEY_FORMAT_ERROR);
        }
        //ECKeyPair is bind to secp256k1, so need to set the address to support gm
        ECKeyPair ecKeyPair = ECKeyPair.create(Numeric.toBigInt(privateKey));
        WalletFile file = Wallet.createStandard(password, ecKeyPair);
        file.setAddress(address);
        return JacksonUtils.toJson(file);
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
    public static byte[] decryptPrivateKey(String password, String encryptPrivateKey) throws JsonParseException, JsonMappingException, IOException{
        
        WalletFile walletFile = JacksonUtils.strToObject(encryptPrivateKey, WalletFile.class);
        ECKeyPair ecKeyPair = null;
        try {
            ecKeyPair = Wallet.decrypt(password, walletFile);
            return Numeric.toBytesPadded(ecKeyPair.getPrivateKey(), 32);
        } catch (CipherException e) {
            log.info("decrypt private key error", e);
            return null;
        }
    }

    public static DecryptResult decryptFully(String password, String encryptPrivateKey) throws Exception {
        byte[] rawKey = decryptPrivateKey(password, encryptPrivateKey);
        String eccType = resolveEcc(rawKey, encryptPrivateKey);
        return new DecryptResult(rawKey, eccType);
    }

    private static EccKeyAlgorithm eccKeyAlgorithm = new EccKeyAlgorithm();
    private static Sm2KeyAlgorithm sm2KeyAlgorithm = new Sm2KeyAlgorithm();
    private static String resolveEcc(byte[] rawKey, String encryptPrivateKey){
        Map encryptedObj = JacksonUtils.fromJson(encryptPrivateKey, Map.class);
        String addrInKey = encryptedObj.get("address").toString();
        String addr = eccKeyAlgorithm.computeAddress(rawKey);
        String eccType = null;
        if (addrInKey.equals(addr)) {
            eccType = EccTypeEnums.SECP256K1.getEccName();
        }
        addr = sm2KeyAlgorithm.computeAddress(rawKey);
        if (addrInKey.equals(addr)) {
            eccType = EccTypeEnums.SM2P256V1.getEccName();
        }
        return eccType;
    }
    
    /**
     * Decrypt key from file
     * @param password
     * @param filePath
     * @return
     * @throws IOException
     */
    public static byte[] decryptPrivateKeyByFile(String password, String filePath) throws IOException {
    	File file = FileOperationUtils.getFile(filePath);
        String encryptPrivateKey = FileUtils.readFileToString(file, "UTF-8");
        return decryptPrivateKey(password, encryptPrivateKey);
    }
}
