/**
 * Copyright (C) 2018 webank, Inc. All Rights Reserved.
 */
package com.webank.keymgr.db.dao;

import com.webank.keymgr.config.SystemConfig;
import com.webank.keymgr.config.db.EncryptKeyJpaConfig;
import com.webank.keymgr.db.encryptkey.entity.EncryptKeyDO;
import com.webank.keymgr.db.encryptkey.repository.EncryptKeyInfoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;


/**
 * 
 * UserEntropyInfoDAO
 *
 * @Description: UserEntropyInfoDAO
 * @author graysonzhang
 * @data 2019-07-12 16:20:06
 *
 */
@ConditionalOnBean(value = EncryptKeyJpaConfig.class)
@Service
public class EncryptKeyInfoDAO {

	@Autowired
	private SystemConfig config;

	@Autowired
	private EncryptKeyInfoRepository encryptKeyInfoRepository;

	public Page<EncryptKeyDO> query(int currentPage, int pageLimit){
		Pageable pageable = PageRequest.of(currentPage, pageLimit, Sort.by(Sort.Order.desc("pkId")));
		Page<EncryptKeyDO> page = encryptKeyInfoRepository.findAll(pageable);
		return page;
	}
	
	public void save(String keyAddress, String userId, String keyName, String encryptKey) {
		this.save(keyAddress, userId, keyName, encryptKey, null);
	}
	
	public void save(String keyAddress, String userId, String keyName, String encryptKey, String parentAddress) {
		EncryptKeyDO encryptKeyDO = new EncryptKeyDO();
		encryptKeyDO.setUserId(userId);
		encryptKeyDO.setKeyAddress(keyAddress);
		encryptKeyDO.setEncryptKey(encryptKey);
		encryptKeyDO.setKeyName(keyName);
		encryptKeyDO.setParentAddress(parentAddress);
		encryptKeyDO.setEncType(config.getKeyEncType());
		encryptKeyDO.setEccType(config.getEccType());
		encryptKeyInfoRepository.save(encryptKeyDO);
	}
	
	public List<EncryptKeyDO> getEncryptKeyInfoByUserId(String userId){
	    return encryptKeyInfoRepository.findByUserId(userId);
	}
	
	public EncryptKeyDO getEncryptKeyInfoByUserIdAndKeyAddress(String userId, String keyAddress){
	    return encryptKeyInfoRepository.findByUserIdAndKeyAddress(userId, keyAddress);
	}
	
	public void removeEncryptKey(String userId, String keyAddress){
	    encryptKeyInfoRepository.deleteByUserIdAndKeyAddress(userId, keyAddress);
	}
	
	public void updateEncrypt(String userId, String keyAddress, String encryptKey){
	    encryptKeyInfoRepository.updateEncryptByUserIdAndKeyAddress(userId, keyAddress, encryptKey);
	}
	
	public void updateKeyName(String userId, String keyAddress, String keyName){
	    encryptKeyInfoRepository.updateKeynameByUserIdAndKeyAddress(userId, keyAddress, keyName);
	}

	public List<EncryptKeyDO> getChildKeys(String userId, String parentAddress) {
		return encryptKeyInfoRepository.findByUserIdAndParentAddress(userId, parentAddress);
	}
}
