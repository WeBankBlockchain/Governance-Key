package com.webank.keygen.key.impl;

import com.webank.keygen.handler.SM2KeyHandler;
import com.webank.keygen.key.KeyComputeAlgorithm;
import org.web3j.crypto.Keys;
import org.web3j.utils.Numeric;

/**
 * Sm2KeyAlgorithm
 *
 * @Description: Sm2KeyAlgorithm
 * @author graysonzhang
 * @date 2020-01-07 17:07:49
 *
 */
public class Sm2KeyAlgorithm implements KeyComputeAlgorithm {
	@Override
	public String computeAddress(byte[] privateKey) {
		byte[] pubkey = SM2KeyHandler.SM2PrivateKeyToPublicKey(privateKey);
		String address = Keys.getAddress(Numeric.toBigInt(pubkey));
        if(!address.contains("0x") && !address.contains("0X")) return "0x" + address;
        return address;
	}

    @Override
    public String computePublicKey(byte[] privateKey) {
        byte[] pubkey = SM2KeyHandler.SM2PrivateKeyToPublicKey(privateKey);
        return Numeric.toHexString(pubkey);
    }

}
