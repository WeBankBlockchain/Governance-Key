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
package com.webank.keymgr.service;


import com.webank.keymgr.handler.KeyHandler;
import com.webank.keymgr.model.EncryptKeyInfo;
import com.webank.keymgr.model.vo.PkeyInfoVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.web3j.utils.Numeric;

import java.util.List;

/**
 * KeysManagerService
 *
 * @Description: KeysManagerService
 * @author graysonzhang
 * @data 2019-07-10 14:52:32
 *
 */
@Service
public class KeysManagerService {
    private KeyHandler keyHandler;

    @Autowired
    public KeysManagerService(KeyHandler keyHandler){
        this.keyHandler = keyHandler;
    }
    /**
     * createPrivateKey
     * @param userId
     * @param password
     * @param keyName
     * @return
     * @throws Exception
     */
    public PkeyInfoVO createPrivateKey(String userId, String password, String keyName) throws Exception{
        return keyHandler.createPrivateKey(userId, password, keyName);
    }

    public Page<EncryptKeyInfo> query(int page, int limit) throws Exception{
        return keyHandler.query(page, limit);
    }

    /**
     * importPrivateKeyFile
     * @param userId
     * @param password
     * @param filePath
     * @return
     * @throws Exception
     */
    public PkeyInfoVO importPrivateKeyFile(String userId, String password, String filePath) throws Exception{
        return keyHandler.importPrivateKeyFile(userId, password, filePath);
    }
    
    /**
     * importPrivateKey
     * @param userId
     * @param password
     * @param privateKey
     * @param keyName
     * @return
     * @throws Exception
     */
    public PkeyInfoVO importPrivateKey(String userId, String password, String privateKey, String keyName) throws Exception{
        return keyHandler.importPrivateKey(userId, password, privateKey, keyName);
    }
    
    /**
     * createPrivateKeyWithParent
     * @param userId
     * @param chaincode
     * @param parentKey
     * @return
     * @throws Exception
     */
    public byte[] createPrivateKeyByParent(String userId, String parentKey,String chaincode, String password) 
    throws Exception{
    	  return keyHandler.createPrivateKeyByParent(userId, Numeric.hexStringToByteArray(parentKey),chaincode, password);
    }
    
    
    /**
     * queryChildKeys
     * @param userId
     * @param parentAddress
     * @return
     * @throws Exception
     */
    public List<EncryptKeyInfo> queryChildKeys(String userId, String parentAddress) throws Exception{
    	return keyHandler.queryChildKeys(userId, parentAddress);
    }


    /**
     * exportPrivateKeyFile
     * @param userId
     * @param keyAddress
     * @param destinationDirectory
     * @return the file path
     * @throws Exception
     */
    public String exportPrivateKeyFile(String userId, String keyAddress, String destinationDirectory) throws Exception{
        return keyHandler.exportPrivateKeyFile(userId, keyAddress, destinationDirectory);
    }
    
    /**
     * getEncryptPrivateKeyList
     * @param userId
     * @return
     */
    public List<EncryptKeyInfo> getEncryptPrivateKeyList(String userId) throws Exception{
        return keyHandler.getEncryptPrivateKeyList(userId);
    }
    
    public EncryptKeyInfo getEncryptPrivateKeyByUserIdAndAddress(String userId, String address) throws Exception{
        return keyHandler.getEncryptPrivateKeyByUserIdAndAddress(userId, address);
    }
    
   
    /**
     * decryptPrivateKey
     * @param password
     * @param encryptPrivateKey
     * @return
     * @throws Exception
     */
    public String decryptPrivateKey(String password, String encryptPrivateKey) throws Exception{
        return keyHandler.decryptPrivateKey(password, encryptPrivateKey);
    }
    
    /**
     * updateKeyName
     * @param userId
     * @param address
     * @param newKeyName
     * @return
     * @throws Exception
     */
    public boolean updateKeyName(String userId, String address, String newKeyName) throws Exception{
        return keyHandler.updateKeyName(userId, address, newKeyName);
    }
    
    /**
     * updateKeyPassword
     * @param userId
     * @param keyAddress
     * @param oldPwd
     * @param newPwd
     * @return
     * @throws Exception
     */
    public boolean updateKeyPassword(String userId, String keyAddress, String oldPwd, String newPwd) throws Exception{
        return keyHandler.updateKeyPassword(userId, keyAddress, oldPwd, newPwd);
    }
    
    /**
     * deleteUserKey
     * @param userId
     * @param keyAddress
     * @return
     */
    public boolean deleteUserKey(String userId, String keyAddress) throws Exception{
        return keyHandler.deleteUserKey(userId, keyAddress);
    }

}








