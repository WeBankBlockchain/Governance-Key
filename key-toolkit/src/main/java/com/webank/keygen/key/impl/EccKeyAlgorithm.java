package com.webank.keygen.key.impl;

import com.webank.keygen.key.KeyComputeAlgorithm;
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
		BigInteger pubkey = Sign.publicKeyFromPrivate(Numeric.toBigInt(privateKey));
		String address = Keys.getAddress(pubkey);
	    if(!address.contains("0x") && !address.contains("0X")) return "0x" + address;
	    return address;
	}

    @Override
    public String computePublicKey(byte[] privateKey) {
        BigInteger pubkey = Sign.publicKeyFromPrivate(Numeric.toBigInt(privateKey));
        return Numeric.toHexStringWithPrefix(pubkey);
    }
}
