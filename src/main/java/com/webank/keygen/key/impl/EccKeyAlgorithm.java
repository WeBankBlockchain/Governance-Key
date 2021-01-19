package com.webank.keygen.key.impl;

import com.webank.keygen.enums.EccTypeEnums;
import com.webank.keygen.key.KeyComputeAlgorithm;
import com.webank.keygen.utils.KeyUtils;
import org.fisco.bcos.sdk.crypto.keypair.CryptoKeyPair;
import org.web3j.crypto.Keys;
import org.web3j.crypto.Sign;
import org.web3j.utils.Numeric;

import java.math.BigInteger;

/**
 * EccKeyAlgorithm
 *
 * @Description: EccKeyAlgorithm
 * @author graysonzhang
 * @date 2020-01-07 17:07:55
 *
 */
public class EccKeyAlgorithm implements KeyComputeAlgorithm {
	@Override
	public String computeAddress(byte[] privateKey) {
		CryptoKeyPair cryptoKeyPair = KeyUtils.getCryptKeyPair(privateKey, EccTypeEnums.SECP256K1);
		return cryptoKeyPair.getAddress();
	}

    @Override
    public String computePublicKey(byte[] privateKey) {
		CryptoKeyPair cryptoKeyPair = KeyUtils.getCryptKeyPair(privateKey, EccTypeEnums.SECP256K1);
		return cryptoKeyPair.getHexPublicKey();
    }
}
