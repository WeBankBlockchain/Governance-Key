package com.webank.keygen.key;


import com.webank.keygen.model.DecryptResult;

public interface KeyEncryptAlgorithm {
	
	String encrypt(String password, byte[] privateKey, String address, String eccName) throws Exception;

	DecryptResult decryptFully(String password, String encryptPrivateKey) throws Exception;

	byte[] decrypt(String password, String encryptPrivateKey) throws Exception;
	
	byte[] decryptFile(String password, String filePath) throws Exception;
	
	String exportKey(String encryptKey, String address, String destinationDirectory) throws Exception;

	String getName();
}
