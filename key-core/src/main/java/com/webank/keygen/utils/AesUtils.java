/**
 * Copyright 2014-2019  the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package com.webank.keygen.utils;

import com.webank.keygen.enums.ExceptionCodeEnums;
import com.webank.keygen.exception.KeyGenException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

@Slf4j
public class AesUtils{

    /**
     * Encrypt by aes.
     *
     * @param content info
     * @param key key
     * @throws KeyGenException
     */
    public static String aesEncrypt(String content, String key) throws KeyGenException {
        if (StringUtils.isBlank(key) || key.length() != 16) {
            log.error("aesEncrypt. error key:{}", key);
            throw new KeyGenException(ExceptionCodeEnums.AES_KEY_ERROR);
        }

        try {
            byte[] keyBytes = key.getBytes("UTF-8");
            SecretKeySpec skeySpec = new SecretKeySpec(keyBytes, "AES");
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
            byte[] encrypted = cipher.doFinal(content.getBytes("utf-8"));
            return Base64.getEncoder().encodeToString(encrypted);
        } catch (Exception ex) {
            log.warn("fail aesEncrypt", ex);
            return null;
        }
    }


    /**
     * Decrypt by aes.
     *
     * @param content info
     * @param key key
     * @throws KeyGenException
     */
    public static String aesDecrypt(String content, String key) throws KeyGenException {
        
        if (StringUtils.isBlank(key) || key.length() != 16) {
            log.error("aesEncrypt. error key:{}", key);
            throw new KeyGenException(ExceptionCodeEnums.AES_KEY_ERROR);
        }
        
        try {
            byte[] keyBytes = key.getBytes("UTF-8");
            SecretKeySpec skeySpec = new SecretKeySpec(keyBytes, "AES");
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, skeySpec);
            
            byte[] encrypted1 = Base64.getDecoder().decode(content);
            byte[] original = cipher.doFinal(encrypted1);

            return new String(original, "UTF-8");

        } catch (Exception ex) {
            log.warn("fail aesDecrypt", ex);
            return null;
        }
    }

}
