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
package com.webank.keymgr.handler;

import com.webank.keygen.face.PrivateKeyCreator;
import com.webank.keygen.key.KeyComputeAlgorithm;
import com.webank.keygen.key.KeyEncryptAlgorithm;
import com.webank.keygen.model.PkeyInfo;
import com.webank.keymgr.config.SystemConfig;
import com.webank.keymgr.enums.MgrExceptionCodeEnums;
import com.webank.keymgr.exception.KeyMgrException;
import com.webank.keymgr.model.EncryptKeyInfo;
import com.webank.keymgr.model.vo.PkeyInfoVO;
import com.webank.keymgr.persistence.KeyPersistenceService;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.web3j.utils.Numeric;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * KeyHandler
 *
 * @Description: KeyHandler
 * @author graysonzhang
 * @author yuzhichu
 * @date 2020-01-02 19:41:41
 *
 */
@Service
@Slf4j
public class KeyHandler {

    @Autowired
    private PrivateKeyCreator privateKeyCreator;
    @Autowired
    private SystemConfig systemConfig;
    @Autowired
    private KeyComputeAlgorithm addressAlgorithm;
    @Autowired
    private KeyEncryptAlgorithm keyEncryptAlgorithm;
    @Autowired
    private KeyPersistenceService keyPersistenceService;

    @Transactional
    public PkeyInfoVO createPrivateKey(String userId, String password, String keyName) throws Exception {
        PkeyInfo pkeyInfo = privateKeyCreator.generatePrivateKey();
        byte[] privateKey = pkeyInfo.getPrivateKey();
        return this.storePrivatekey(privateKey, password, userId, keyName, null);
    }
    
    /**
     * Extract private key from file and save to database
     * @param userId
     * @param password
     * @param filePath
     * @return
     */
    @Transactional
    public PkeyInfoVO importPrivateKeyFile(String userId, String password, String filePath) throws Exception{
    	//Decrypt private key
    	byte[] privateKey = this.keyEncryptAlgorithm.decryptFile(password, filePath);
        //Persistence
        return this.storePrivatekey(privateKey, password, userId, null, null);
	}
        
    @Transactional
    public PkeyInfoVO importPrivateKey(String userId, String password, String privateKey, String keyName) throws Exception {
    	return this.storePrivatekey(Numeric.hexStringToByteArray(privateKey), password, userId, keyName, null);
    }
    
    public String exportPrivateKeyFile(String userId, String address, String destinationDirectory) throws Exception
    {
        EncryptKeyInfo key = this.keyPersistenceService.getEncryptKeyInfoByUserIdAndKeyAddress(userId, address);
    	if(key == null) return null;
    	return keyEncryptAlgorithm.exportKey(key.getEncryptKey(), address, destinationDirectory);
    }	
    
    public List<EncryptKeyInfo> getEncryptPrivateKeyList(String userId) throws Exception{
    	List<EncryptKeyInfo> keys = keyPersistenceService.getEncryptKeyInfoByUserId(userId);
    	if(keys == null || keys.isEmpty()) return Collections.emptyList();
    	return keys.stream().collect(Collectors.toList());
    }

	public EncryptKeyInfo getEncryptPrivateKeyByUserIdAndAddress(String userId, String address) throws Exception{
        EncryptKeyInfo key = keyPersistenceService.getEncryptKeyInfoByUserIdAndKeyAddress(userId, address);
		return key;
	}
	
    public String decryptPrivateKey(String password, String encryptPrivateKey) throws Exception{
    	return Numeric.toHexString(keyEncryptAlgorithm.decrypt(password, encryptPrivateKey));
    }

    @Transactional
    public boolean updateKeyName(String userId, String address, String newKeyName) throws Exception {
        keyPersistenceService.updateKeyName(userId, address, newKeyName);
    	return true;
    }

    @Transactional
    public boolean updateKeyPassword(String userId, String keyAddress, String oldPwd, String newPwd)
            throws Exception {
        EncryptKeyInfo encryptKey = keyPersistenceService.getEncryptKeyInfoByUserIdAndKeyAddress(userId, keyAddress);
        if (encryptKey == null) {
            log.info("key {} does not exist", keyAddress);
            return false;
        }
        byte[] privateKey = null;
        String newEncryptKey = null;
        privateKey = keyEncryptAlgorithm.decrypt(oldPwd, encryptKey.getEncryptKey());
        if(privateKey == null){
            log.error("Decrypt failed ");
            return false;
        }
        newEncryptKey = keyEncryptAlgorithm.encrypt(newPwd, privateKey, keyAddress, this.systemConfig.getEccType());
        keyPersistenceService.updateEncrypt(userId, keyAddress, newEncryptKey, newPwd);
        return true;
    }

    @Transactional
    public boolean deleteUserKey(String userId, String keyAddress) throws Exception{
        return keyPersistenceService.removeEncryptKey(userId, keyAddress);
    }
   
    
    private PkeyInfoVO storePrivatekey(byte[] privateKey, String password, String userId
    		, String keyName, String parentAddress) throws Exception {
    	//Compute private key address
    	String address = this.addressAlgorithm.computeAddress(privateKey);
    	//Encrypt private key
        String encryptKey = this.keyEncryptAlgorithm.encrypt(password, privateKey, address
        		, this.systemConfig.getEccType());
        //Assign a default key name
        if(Strings.isBlank(keyName)) {
        	keyName = address;
        }
        //Persistence
        this.keyPersistenceService.save(userId, address, keyName, encryptKey, parentAddress, password);
        //Return
        PkeyInfoVO vo = new PkeyInfoVO();
        vo.setKeyAddress(address);
        vo.setKeyName(keyName);
        return vo;
    }
    
	public List<EncryptKeyInfo> queryChildKeys(String userId, String parentAddress) throws Exception{
        EncryptKeyInfo parent = keyPersistenceService.getEncryptKeyInfoByUserIdAndKeyAddress(userId, parentAddress);
		if(parent == null) {
			log.error("Parent key {} {} is not imported, please import parent key first", userId, parentAddress);
			return Collections.emptyList();
		}
		List<EncryptKeyInfo> childKeys = keyPersistenceService.getChildKeys(userId, parentAddress);
		return childKeys;
	}

	
	public byte[] createPrivateKeyByParent(String userId, byte[] parentKey, String chainCode, String password) 
			throws Exception {
    	//Check parent key
		if(parentKey.length != 32) throw new KeyMgrException(MgrExceptionCodeEnums.PKEY_MGR_KEY_FORMAT_ERROR);
		String parentAddress = this.addressAlgorithm.computeAddress(parentKey);
        EncryptKeyInfo parent = keyPersistenceService.getEncryptKeyInfoByUserIdAndKeyAddress(userId, parentAddress);
		if(parent == null) {
			log.error("Parent key {} {} is not imported, please import parent key first", userId, parentAddress);
			throw new KeyMgrException(MgrExceptionCodeEnums.PKEY_MGR_PARENT_NOT_EXIST);
		}
		//Derive child key
		PkeyInfo derivedKey = this.privateKeyCreator.generatePrivateKeyByChainCode(parentKey, chainCode);
		//Store the child key and relation
		this.storePrivatekey(derivedKey.getPrivateKey(), password, userId, null, parentAddress);
		return derivedKey.getPrivateKey();
	}

    public Page<EncryptKeyInfo> query(int page, int limit) {
        return this.keyPersistenceService.query(page, limit);
    }
}










