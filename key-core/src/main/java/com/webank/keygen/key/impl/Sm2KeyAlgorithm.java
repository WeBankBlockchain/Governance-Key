package com.webank.keygen.key.impl;

import com.webank.keygen.enums.EccTypeEnums;
import com.webank.keygen.handler.SM2KeyHandler;
import com.webank.keygen.key.KeyComputeAlgorithm;
import com.webank.keygen.utils.KeyUtils;
import org.fisco.bcos.sdk.crypto.keypair.CryptoKeyPair;
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
        CryptoKeyPair cryptoKeyPair = KeyUtils.getCryptKeyPair(privateKey, EccTypeEnums.SM2P256V1);
        return cryptoKeyPair.getAddress();
    }

    @Override
    public String computePublicKey(byte[] privateKey) {
        CryptoKeyPair cryptoKeyPair = KeyUtils.getCryptKeyPair(privateKey, EccTypeEnums.SM2P256V1);
        return cryptoKeyPair.getHexPublicKey();
    }
}
