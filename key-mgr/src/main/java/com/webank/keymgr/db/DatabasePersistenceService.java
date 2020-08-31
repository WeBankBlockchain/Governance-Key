package com.webank.keymgr.db;

import com.webank.keymgr.config.db.EncryptKeyJpaConfig;
import com.webank.keymgr.config.SystemConfig;
import com.webank.keymgr.db.dao.EncryptKeyInfoDAO;
import com.webank.keymgr.db.dao.KeyPwdInfoDAO;
import com.webank.keymgr.db.encryptkey.entity.EncryptKeyDO;
import com.webank.keymgr.db.keypwd.entity.KeyPwdInfo;
import com.webank.keymgr.exception.KeyMgrException;
import com.webank.keymgr.model.EncryptKeyInfo;
import com.webank.keymgr.persistence.KeyPersistenceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.data.domain.Page;

import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author aaronchu
 * @Description
 * @data 2020/06/22
 */
@Slf4j
@ConditionalOnBean(value = EncryptKeyJpaConfig.class)
public class DatabasePersistenceService implements KeyPersistenceService {
    @Autowired
    private EncryptKeyInfoDAO encryptKeyInfoDAO;
    @Autowired(required = false)
    private KeyPwdInfoDAO keyPwdInfoDAO;
    @Autowired
    private SystemConfig systemConfig;

    @Override
    @Transactional
    public void save(String userId, String keyAddress, String keyName, String encryptKey, String parentAddress, String password)
    throws KeyMgrException
    {
        encryptKeyInfoDAO.save(keyAddress, userId, keyName, encryptKey, parentAddress);
        if (this.systemConfig.isStorePwd()) {
            keyPwdInfoDAO.save(keyAddress, userId, password);
        }
    }

    @Override
    public EncryptKeyInfo getEncryptKeyInfoByUserIdAndKeyAddress(String userId, String address) {
        EncryptKeyDO encryptKeyDO = encryptKeyInfoDAO.getEncryptKeyInfoByUserIdAndKeyAddress(userId, address);
        return convert(encryptKeyDO);
    }

    @Override
    public List<EncryptKeyInfo> getEncryptKeyInfoByUserId(String userId) {
        List<EncryptKeyDO> datas = encryptKeyInfoDAO.getEncryptKeyInfoByUserId(userId);
        return datas.stream().map(this::convert).collect(Collectors.toList());
    }

    @Override
    public boolean updateKeyName(String userId, String address, String newKeyName) {
        encryptKeyInfoDAO.updateKeyName(userId, address, newKeyName);
        return true;
    }

    @Override
    public boolean updateEncrypt(String userId, String keyAddress, String newEncryptKey, String newPassword) {
        encryptKeyInfoDAO.updateEncrypt(userId, keyAddress, newEncryptKey);
        if (systemConfig.isStorePwd()) {
            keyPwdInfoDAO.modifyKeyPwdByKeyAddress(userId, keyAddress, newPassword);
        }
        return true;
    }

    @Override
    @Transactional
    public boolean removeEncryptKey(String userId, String keyAddress) {
        encryptKeyInfoDAO.removeEncryptKey(userId, keyAddress);
        if(systemConfig.isStorePwd()){
            KeyPwdInfo keyPwdInfo = keyPwdInfoDAO.getUserKeyPwdInfoByUserIdAndKeyAddress(userId, keyAddress);
            if (keyPwdInfo == null) {
                log.info("key {} does not exist", keyAddress);
                return false;
            }
            keyPwdInfoDAO.deleteKeyPwdByUserIdAndKeyAddress(userId, keyAddress);
        }
        return true;
    }

    @Override
    public List<EncryptKeyInfo> getChildKeys(String userId, String parentAddress) {
        List<EncryptKeyDO> datas = encryptKeyInfoDAO.getChildKeys(userId, parentAddress);
        return datas.stream().map(this::convert).collect(Collectors.toList());
    }

    @Override
    public Page<EncryptKeyInfo> query(int currentPage, int pageLimit) {
        Page<EncryptKeyDO> doList =  this.encryptKeyInfoDAO.query(currentPage, pageLimit);
        Page<EncryptKeyInfo> result = doList.map(this::convert);
        return result;
    }

    private EncryptKeyInfo convert(EncryptKeyDO encryptKeyDO){
        if(encryptKeyDO == null) return null;
        EncryptKeyInfo encryptKeyInfo = new EncryptKeyInfo();
        encryptKeyInfo.setUserId(encryptKeyDO.getUserId());
        encryptKeyInfo.setKeyAddress(encryptKeyDO.getKeyAddress());
        encryptKeyInfo.setEncryptKey(encryptKeyDO.getEncryptKey());
        encryptKeyInfo.setKeyName(encryptKeyDO.getKeyName());
        encryptKeyInfo.setParentAddress(encryptKeyDO.getParentAddress());
        encryptKeyInfo.setEncType(encryptKeyDO.getEncType());
        encryptKeyInfo.setEccType(encryptKeyDO.getEccType());
        return encryptKeyInfo;
    }
}
