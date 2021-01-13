package com.webank.keygen.key.impl;

import com.webank.keygen.encrypt.P12Encrypt;
import com.webank.keygen.enums.EccTypeEnums;
import com.webank.keygen.enums.KeyFileTypeEnums;
import com.webank.keygen.key.KeyEncryptAlgorithm;
import com.webank.keygen.model.DecryptResult;

public class P12EncryptAlgorithm implements KeyEncryptAlgorithm{
	@Override
	public String encrypt(String password, byte[] privateKey, String address, String eccType) throws Exception {
		return P12Encrypt.encryptPrivateKey(password, privateKey, EccTypeEnums.getEccByName(eccType));
	}

	@Override
	public DecryptResult decryptFully(String password, String encryptPrivateKey) throws Exception {
		return P12Encrypt.decryptFully(password, encryptPrivateKey);
	}

	@Override
	public byte[] decrypt(String password, String encryptPrivateKey) throws Exception {
		return P12Encrypt.decryptPrivateKey(password, encryptPrivateKey);
	}

	@Override
	public byte[] decryptFile(String password, String filePath) throws Exception {
		return P12Encrypt.decryptPrivateKeyByFile(password, filePath);
	}

	@Override
	public String exportKey(String encryptKey, String address, String destinationDirectory)
			throws Exception {
		String fileName = address+ KeyFileTypeEnums.P12_FILE.getKeyFilePostfix();
    	return P12Encrypt.storePrivateKey(encryptKey, fileName, destinationDirectory);
	}

	@Override
	public String getName() {
		return "p12";
	}
}
