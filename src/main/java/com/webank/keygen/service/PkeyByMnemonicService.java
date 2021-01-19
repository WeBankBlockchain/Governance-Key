/*
 * Copyright 2014-2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.webank.keygen.service;

import com.webank.keygen.crypto.EccOperations;
import com.webank.keygen.enums.EccTypeEnums;
import com.webank.keygen.exception.KeyGenException;
import com.webank.keygen.hd.bip32.MasterKeyGenerator;
import com.webank.keygen.mnemonic.SeedGenerator;
import com.webank.keygen.model.PkeyInfo;
import com.webank.keygen.utils.KeyUtils;
import com.webank.keygen.utils.SecureRandomUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.fisco.bcos.sdk.crypto.keypair.CryptoKeyPair;
import org.web3j.crypto.ECKeyPair;
import org.web3j.crypto.MnemonicUtils;
import org.web3j.utils.Numeric;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Arrays;

/**
 * MnemonicService.
 *
 * @Description: MnemonicService
 * @author graysonzhang
 * @date 2019-09-08 00:36:18
 *
 */
@Slf4j
public class PkeyByMnemonicService{
    
    private static final SecureRandom secureRandom = SecureRandomUtils.secureRandom();

    private SeedGenerator seedGenerator = new SeedGenerator();
    private MasterKeyGenerator keyGenerator = new MasterKeyGenerator();
    
    /**
     * create mnemonic by entropyStr, entropyStr can be null. If entropy is null, a 128 bit random entropy will be used.
     * 
     * @param entropyStr: random  entropy
     * @return mnemonic. mnemonic length is determined by entrophy length
     */
    public String createMnemonic(String entropyStr) {
        byte[] initialEntropy;
        if (StringUtils.isEmpty(entropyStr)) {
            initialEntropy = new byte[16];
            secureRandom.nextBytes(initialEntropy);
        }else{
            initialEntropy = Numeric.hexStringToByteArray(entropyStr);
        }
        String mnemonic = MnemonicUtils.generateMnemonic(initialEntropy);
        return mnemonic;
    }

    /**
     * create mnemonic. A 128 bit random entropy will be used.
     *
     * @return mnemonic. mnemonic length is 12
     */
    public String createMnemonic() {
        byte[] initialEntropy = new byte[16];
        secureRandom.nextBytes(initialEntropy);
        String mnemonic = MnemonicUtils.generateMnemonic(initialEntropy);
        return mnemonic;
    }
    /**    
     * generate PkeyInfo by mnemonic
     *
     * @param mnemonic
     * @param passphrase: password for create seed, it can be null.  
     * @return PkeyInfo
     */
    public PkeyInfo generatePrivateKeyByMnemonic(String mnemonic, String passphrase, EccTypeEnums eccType)
    		throws KeyGenException {
    	byte[] seed = seedGenerator.generateSeed(mnemonic, passphrase);
        byte[] pkeyWithChainCode = keyGenerator.toMasterKey(seed);

        byte[] pkey = Arrays.copyOfRange(pkeyWithChainCode, 0, 32);
    	byte[] chainCode = Arrays.copyOfRange(pkeyWithChainCode, 32, 64);

        EccOperations eccOperations = new EccOperations(eccType);
        eccOperations.verifyPrivateKey(new BigInteger(1, pkey));

    	CryptoKeyPair ecKeyPair = new EccOperations(eccType).getKeyPair(pkey);

    	return PkeyInfo.fromCryptoKeypair(ecKeyPair, chainCode);
    }
}
