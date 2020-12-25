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
package com.webank.keymgr.db.dao;


import com.webank.keymgr.config.db.KeyPwdJpaConfig;
import com.webank.keymgr.db.keypwd.entity.KeyPwdInfo;
import com.webank.keymgr.db.keypwd.repository.KeyPwdInfoRepository;
import com.webank.keymgr.enums.MgrExceptionCodeEnums;
import com.webank.keymgr.exception.KeyMgrException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Service;


/**
 * KeyPwdInfoDAO
 *
 * @Description: KeyPwdInfoDAO
 * @author graysonzhang
 * @date 2020-01-02 16:30:33
 *
 */
@Service
@Slf4j
@ConditionalOnBean(KeyPwdJpaConfig.class)
public class KeyPwdInfoDAO {
    
    @Autowired
    private KeyPwdInfoRepository keyPwdInfoRepository;
    
    public void save(String keyAddress, String userId, String keyPwd) throws KeyMgrException {
        if (keyPwdInfoRepository.findByUserIdAndKeyAddress(userId, keyAddress) != null) {
            log.info("privake key has exists");
            throw new KeyMgrException(MgrExceptionCodeEnums.PKEY_MGR_ACCOUNT_EXIST);
        }
        KeyPwdInfo userKeyPwdInfo = new KeyPwdInfo();
        userKeyPwdInfo.setKeyAddress(keyAddress);
        userKeyPwdInfo.setKeyPwd(keyPwd);
        userKeyPwdInfo.setUserId(userId);
        keyPwdInfoRepository.save(userKeyPwdInfo);
    }
    
    public KeyPwdInfo getUserKeyPwdInfoByUserIdAndKeyAddress(String userId, String keyAddress){
        return keyPwdInfoRepository.findByUserIdAndKeyAddress(userId, keyAddress);
    }
    
    public void modifyKeyPwdByKeyAddress(String userId, String keyAddress, String newPwd){
        keyPwdInfoRepository.updateByUserIdAndKeyAddress(userId, keyAddress, newPwd);
    }
    
    public int deleteKeyPwdByUserIdAndKeyAddress(String userId, String keyAddress){
        return keyPwdInfoRepository.deleteByUserIdAndKeyAddress(userId, keyAddress);
    }
}
