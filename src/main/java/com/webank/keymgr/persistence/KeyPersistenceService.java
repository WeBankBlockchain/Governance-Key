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
package com.webank.keymgr.persistence;

import com.webank.keymgr.exception.KeyMgrException;
import com.webank.keymgr.model.EncryptKeyInfo;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * Abstract interface of store data
 *
 * @Description: KeyStorageService
 * @author graysonzhang
 * @date 2020-06-18 14:17:00
 *
 */
public interface KeyPersistenceService {


    /**
     * Save a encrypted private key
     * @param userId
     * @param keyAddress
     * @param keyName
     * @param encryptKey
     * @param parentAddress
     * @param password
     */
    void save(String userId, String keyAddress, String keyName, String encryptKey, String parentAddress, String password)
            throws KeyMgrException;

    EncryptKeyInfo getEncryptKeyInfoByUserIdAndKeyAddress(String userId, String address) throws KeyMgrException;

    List<EncryptKeyInfo> getEncryptKeyInfoByUserId(String userId) throws KeyMgrException;

    boolean updateKeyName(String userId, String address, String newKeyName) throws KeyMgrException;

    boolean updateEncrypt(String userId, String keyAddress, String newEncryptKey, String newPassword) throws KeyMgrException;

    boolean removeEncryptKey(String userId, String keyAddress) throws KeyMgrException;

    List<EncryptKeyInfo> getChildKeys(String userId, String parentAddress) throws KeyMgrException;

    Page<EncryptKeyInfo> query(int currentPage, int pageLimit);
}
