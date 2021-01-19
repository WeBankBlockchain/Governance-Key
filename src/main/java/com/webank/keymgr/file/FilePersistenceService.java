package com.webank.keymgr.file;

import com.webank.keymgr.config.SystemConfig;
import com.webank.keymgr.enums.MgrExceptionCodeEnums;
import com.webank.keymgr.exception.KeyMgrException;
import com.webank.keymgr.file.dao.FileKeyDAO;
import com.webank.keymgr.file.entity.SingleKey;
import com.webank.keymgr.file.entity.UserKeys;
import com.webank.keymgr.model.EncryptKeyInfo;
import com.webank.keymgr.persistence.KeyPersistenceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author aaronchu
 * @Description
 * @data 2020/06/22
 */
@Slf4j
public class FilePersistenceService implements KeyPersistenceService {

    @Autowired
    private FileKeyDAO fileKeyDAO;

    @Autowired
    private SystemConfig config;

    @Override
    public void save(String userId, String keyAddress, String keyName, String encryptKey, String parentAddress, String password) throws KeyMgrException {
        SingleKey singleKey = new SingleKey(userId, keyAddress, encryptKey, keyName, parentAddress, config.getKeyEncType(), config.getEccType());
        UserKeys userKeys = readUserKeys(userId);
        userKeys.addKey(singleKey);
        this.createOrReplaceUserKeys(userKeys);
    }

    @Override
    public EncryptKeyInfo getEncryptKeyInfoByUserIdAndKeyAddress(String userId, String address) throws KeyMgrException {
        UserKeys userKeys = readUserKeys(userId);
        SingleKey singleKey = userKeys.getKeys().get(address);
        if(singleKey == null) return null;
        EncryptKeyInfo result = convert(userId, address, singleKey);
        return result;
    }

    @Override
    public List<EncryptKeyInfo> getEncryptKeyInfoByUserId(String userId) throws KeyMgrException {
        UserKeys userKeys = readUserKeys(userId);
        List<EncryptKeyInfo> result = new ArrayList<>();
        for(Map.Entry<String, SingleKey> entry: userKeys.getKeys().entrySet()){
            EncryptKeyInfo encryptKey = convert(userId, entry.getKey(), entry.getValue());
            result.add(encryptKey);
        }
        return result;
    }

    @Override
    public boolean updateKeyName(String userId, String keyAddress, String newKeyName) throws KeyMgrException {
        UserKeys userKeys = readUserKeys(userId);
        SingleKey singleKey = userKeys.getKeys().get(keyAddress);
        if(singleKey == null) {
            log.error("key address not found for {}", keyAddress);
            return false;
        }
        singleKey.setKeyName(newKeyName);
        userKeys.getKeys().put(keyAddress, singleKey);
        this.createOrReplaceUserKeys(userKeys);
        return true;
    }

    @Override
    public boolean updateEncrypt(String userId, String keyAddress, String newEncryptKey, String newPassword) throws KeyMgrException {
        UserKeys userKeys = readUserKeys(userId);
        SingleKey singleKey = userKeys.getKeys().get(keyAddress);
        if(singleKey == null) {
            log.error("key address not found for {}", keyAddress);
            return false;
        }
        singleKey.setEncryptKey(newEncryptKey);
        userKeys.getKeys().put(keyAddress, singleKey);
        this.createOrReplaceUserKeys(userKeys);
        return true;
    }

    @Override
    public boolean removeEncryptKey(String userId, String keyAddress) throws KeyMgrException {
        UserKeys userKeys = readUserKeys(userId);
        SingleKey old = userKeys.getKeys().remove(keyAddress);
        if(old == null) return false;
        if (!userKeys.getKeys().isEmpty()) {
            this.createOrReplaceUserKeys(userKeys);
            return true;
        }
        //No keys are stored for that user, so delete the file
        try {
            this.fileKeyDAO.removeUser(userId);
            return true;
        } catch (IOException ex) {
            log.warn("Failed to remove file ", ex);
            return false;
        }
    }

    @Override
    public List<EncryptKeyInfo> getChildKeys(String userId, String parentAddress) throws KeyMgrException {
        if(parentAddress == null){
            throw new IllegalArgumentException("parentAddress is null");
        }
        UserKeys userKeys = readUserKeys(userId);
        List<EncryptKeyInfo> result = new ArrayList<>();
        for(Map.Entry<String, SingleKey> entry: userKeys.getKeys().entrySet()){
            String address = entry.getKey();
            SingleKey singleKey = entry.getValue();
            if(!Objects.equals(parentAddress, singleKey.getParentAddress())) continue;
            result.add(convert(userId, address, singleKey));
        }
        return result;
    }

    @Override
    public Page<EncryptKeyInfo> query(int currentPage, int pageLimit) {
        return null;
    }

    private EncryptKeyInfo convert(String userId, String address, SingleKey singleKey){
        if(singleKey == null) return null;
        EncryptKeyInfo encryptKey = new EncryptKeyInfo();
        encryptKey.setUserId(userId);
        encryptKey.setKeyAddress(address);
        encryptKey.setEncryptKey(singleKey.getEncryptKey());
        encryptKey.setKeyName(singleKey.getKeyName());
        encryptKey.setParentAddress(singleKey.getParentAddress());
        encryptKey.setEncType(singleKey.getEncType());
        encryptKey.setEccType(singleKey.getEccType());
        return encryptKey;
    }

    private UserKeys readUserKeys(String userId) throws KeyMgrException {
        UserKeys userKeys = fileKeyDAO.loadUserKeys(userId);
        if(userKeys == null){
            throw new KeyMgrException(MgrExceptionCodeEnums.PKEY_MGR_FILE_PARSE_ERROR);
        }
        return userKeys;
    }

    private void createOrReplaceUserKeys(UserKeys userKeys) throws KeyMgrException {
        try{
            fileKeyDAO.saveUserKeys(userKeys);
        }
        catch (Exception ex){
            log.error("Exception on persistenting to file",ex);
            throw new KeyMgrException(MgrExceptionCodeEnums.PKEY_MGR_CREATE_ERROR);
        }
    }
}
