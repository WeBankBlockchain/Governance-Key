package com.webank.keygen.key.impl;

import com.webank.keygen.encrypt.KeyStoreEncrypt;
import com.webank.keygen.enums.EccTypeEnums;
import com.webank.keygen.enums.KeyFileTypeEnums;
import com.webank.keygen.key.KeyEncryptAlgorithm;
import com.webank.keygen.model.DecryptResult;

/**
 * Wrapper for keystore encrypt
 */
public class KeystoreEncryptAlgorithm implements KeyEncryptAlgorithm{


	@Override
	public String encrypt(String password, byte[] privateKey, String address, String eccType) throws Exception {
		return KeyStoreEncrypt.encryptPrivateKey(password, privateKey, EccTypeEnums.getEccByName(eccType));
	}

	@Override
	public DecryptResult decryptFully(String password, String encryptPrivateKey) throws Exception {
		return KeyStoreEncrypt.decryptWithEccType(password, encryptPrivateKey);
	}

	@Override
	public byte[] decrypt(String password, String encryptPrivateKey) throws Exception {
		return KeyStoreEncrypt.decryptPrivateKey(password, encryptPrivateKey);
	}

	@Override
	public byte[] decryptFile(String password, String filePath) throws Exception {
		return KeyStoreEncrypt.decryptPrivateKeyByFile(password, filePath);
	}

	@Override
	public String exportKey(String encryptKey, String address, String destinationDirectory) throws Exception {
		String fileName = address + KeyFileTypeEnums.KEYSTORE_FILE.getKeyFilePostfix();
		return KeyStoreEncrypt.storeEncryptPrivateKeyToFile(encryptKey, fileName, destinationDirectory);
	}

	@Override
	public String getName() {
		return "keystore";
	}
}
