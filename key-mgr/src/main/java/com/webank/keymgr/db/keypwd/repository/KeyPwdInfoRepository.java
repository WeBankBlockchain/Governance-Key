/**
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
package com.webank.keymgr.db.keypwd.repository;


import com.webank.keymgr.config.db.KeyPwdJpaConfig;
import com.webank.keymgr.db.keypwd.entity.KeyPwdInfo;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;


/**
 * AccountInfoRepository
 *
 * @Description: AccountInfoRepository
 * @author graysonzhang
 * @data 2018-12-13 19:32:21
 *
 */
@Repository
@ConditionalOnBean(KeyPwdJpaConfig.class)
public interface KeyPwdInfoRepository extends JpaRepository<KeyPwdInfo, Long>, JpaSpecificationExecutor<KeyPwdInfo> {
       
    public KeyPwdInfo findByUserIdAndKeyAddress(String userId, String keyAddress);
    
    @Modifying
    @Query(value = "update key_pwds_info set key_pwd = ?3 where key_address = ?2 and user_id = ?1", nativeQuery = true)
    public void updateByUserIdAndKeyAddress(String userId, String keyAddress, String newAccPwd);
    
    public void deleteByUserIdAndKeyAddress(String userId, String keyAddress);
    
}
