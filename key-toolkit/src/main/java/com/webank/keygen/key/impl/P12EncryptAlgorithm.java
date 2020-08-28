package com.webank.keygen.key.impl;

import com.webank.keygen.encrypt.P12Encrypt;
import com.webank.keygen.model.DecryptResult;
import com.webank.keygen.key.KeyEncryptAlgorithm;

public class P12EncryptAlgorithm implements KeyEncryptAlgorithm{
	@Override
	public String encrypt(String password, byte[] privateKey, String address, String eccType) throws Exception {
		return P12Encrypt.encryptPrivateKey(password, privateKey, eccType);
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
    	return P12Encrypt.storePrivateKey(encryptKey, address, destinationDirectory);
	}

	@Override
	public String getName() {
		return "p12";
	}
}
