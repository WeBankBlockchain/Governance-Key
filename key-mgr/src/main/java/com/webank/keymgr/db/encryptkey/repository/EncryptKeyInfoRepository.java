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
package com.webank.keymgr.db.encryptkey.repository;

import com.webank.keymgr.config.db.EncryptKeyJpaConfig;
import com.webank.keymgr.db.encryptkey.entity.EncryptKeyDO;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * EncryptKeyInfoRepository
 *
 * @Description: EncryptKeyInfoRepository
 * @author graysonzhang
 * @date 2020-01-02 16:06:02
 *
 */
@ConditionalOnBean(value = EncryptKeyJpaConfig.class)
@Repository
public interface EncryptKeyInfoRepository
        extends JpaRepository<EncryptKeyDO, Long>, JpaSpecificationExecutor<EncryptKeyDO> {

    public List<EncryptKeyDO> findByUserId(String userId);

    public EncryptKeyDO findByUserIdAndKeyAddress(String userId, String keyAddress);
    
    public List<EncryptKeyDO> findByUserIdAndParentAddress(String userId, String parentAddress);
    
    public void deleteByUserIdAndKeyAddress(String userId, String keyAddress);

    @Transactional
    @Modifying
    @Query(value = "update encrypt_keys_info set encrypt_key = ?3 where user_id = ?1 and key_address = ?2", nativeQuery = true)
    public void updateEncryptByUserIdAndKeyAddress(String userId, String keyAddress, String encryptKey);

    @Transactional
    @Modifying
    @Query(value = "update encrypt_keys_info set key_name = ?3 where user_id = ?1 and key_address = ?2", nativeQuery = true)
    public void updateKeynameByUserIdAndKeyAddress(String userId, String keyAddress, String keyName);
}
