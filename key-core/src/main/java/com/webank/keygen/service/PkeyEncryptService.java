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

import java.io.IOException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.webank.keygen.encrypt.KeyStoreEncrypt;
import com.webank.keygen.encrypt.P12Encrypt;
import com.webank.keygen.encrypt.PemEncrypt;
import com.webank.keygen.enums.EccTypeEnums;
import com.webank.keygen.enums.KeyFileTypeEnums;
import com.webank.keygen.exception.KeyGenException;
import com.webank.keygen.utils.KeyStoreUtils;

/**
 * PkeyFormatService
 *
 * @Description: PkeyFormatService
 * @author graysonzhang
 * @date 2019-12-23 15:23:22
 *
 */
public class PkeyEncryptService {

    /**
     * Encrypt private key with keystore format
     * @param password
     * @param privateKey
     * @param address
     * @param destinationDirectory
     * @throws JsonGenerationException
     * @throws JsonMappingException
     * @throws KeyGenException
     * @throws IOException
     */
    public void encryptKeyStoreFormat(String password, byte[] privateKey, String address, String destinationDirectory)
            throws JsonGenerationException, JsonMappingException, KeyGenException, IOException {
        KeyStoreEncrypt.storeEncryptPrivateKeyToFile(password, privateKey, address, destinationDirectory);
    }

    /**
     * Decrypt keystore format private key
     * @param password
     * @param filePath
     * @return
     * @throws IOException
     */
    public byte[] decryptKeystoreFormat(String password, String filePath) throws IOException {
        if (!KeyStoreUtils.isValidFile(filePath, KeyFileTypeEnums.KEYSTORE_FILE))
            return null;
        return KeyStoreEncrypt.decryptPrivateKeyByFile(password, filePath);
    }

    /**
     * Encrypt private key with p12 format
     * @param password
     * @param privateKey
     * @param address
     * @param destinationDirectory
     * @throws KeyStoreException
     * @throws NoSuchAlgorithmException
     * @throws CertificateException
     * @throws NoSuchProviderException
     * @throws IOException
     * @throws KeyGenException
     */
    public void encryptP12Format(String password, byte[] privateKey, String eccTypeName, String address,
            String destinationDirectory) throws KeyStoreException, NoSuchAlgorithmException, CertificateException,
            NoSuchProviderException, IOException, KeyGenException {
        try{
            P12Encrypt.storePrivateKey(password, privateKey, eccTypeName, address,
                    destinationDirectory);
        }
        catch (Exception ex){
            throw new KeyGenException(ex.getMessage());
        }
    }

    /**
     * Decrypt p12 format private key
     * @param password
     * @param filePath
     * @return
     * @throws UnrecoverableKeyException
     * @throws NoSuchAlgorithmException
     * @throws CertificateException
     * @throws KeyStoreException
     * @throws NoSuchProviderException
     * @throws IOException
     */
    public byte[] decryptP12Format(String password, String filePath) throws UnrecoverableKeyException,
            NoSuchAlgorithmException, CertificateException, KeyStoreException, NoSuchProviderException, IOException {
        if (!KeyStoreUtils.isValidFile(filePath, KeyFileTypeEnums.P12_FILE))
            return null;
        return P12Encrypt.decryptPrivateKeyByFile(password, filePath);
    }

    /**
     * Encrypt private key with pem format
     * @param privateKey
     * @param address
     * @param destinationDirectory
     * @throws IOException
     * @throws KeyGenException
     */
    public void encryptPEMFormat(byte[] privateKey, int eccType, String address, String destinationDirectory)
            throws Exception {
        PemEncrypt.storePrivateKey(privateKey, EccTypeEnums.getEccByType(eccType), address, destinationDirectory);
    }

    /**
     * Decrypt pem format private key
     * @param filePath
     * @return
     * @throws IOException
     */
    public byte[] decryptPEMFormat(String filePath) throws Exception {
        if (!KeyStoreUtils.isValidFile(filePath, KeyFileTypeEnums.PEM_FILE))
            return null;
        return PemEncrypt.decryptPrivateKeyByFile(filePath);
    }

}
