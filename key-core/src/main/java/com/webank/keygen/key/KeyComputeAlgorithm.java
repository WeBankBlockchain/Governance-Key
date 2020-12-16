package com.webank.keygen.key;


public interface KeyComputeAlgorithm {

	String computeAddress(byte[] privateKey);
	String computePublicKey(byte[] privateKey);
	
}
