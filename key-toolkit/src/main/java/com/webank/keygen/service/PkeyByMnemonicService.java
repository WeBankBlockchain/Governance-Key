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

import java.security.SecureRandom;
import java.util.Arrays;
import org.apache.commons.lang3.StringUtils;
import org.web3j.crypto.CipherException;
import org.web3j.crypto.ECKeyPair;
import org.web3j.crypto.MnemonicUtils;
import org.web3j.utils.Numeric;
import com.webank.keygen.enums.EccTypeEnums;
import com.webank.keygen.enums.ExceptionCodeEnums;
import com.webank.keygen.exception.KeyGenException;
import com.webank.keygen.handler.SM2KeyHandler;
import com.webank.keygen.model.PkeyInfo;
import com.webank.keygen.utils.KeyUtils;
import com.webank.keygen.utils.SecureRandomUtils;

import lombok.extern.slf4j.Slf4j;

/**
 * MnemonicService
 *
 * @Description: MnemonicService
 * @author graysonzhang
 * @date 2019-09-08 00:36:18
 *
 */
@Slf4j
public class PkeyByMnemonicService{
    
    private static final SecureRandom secureRandom = SecureRandomUtils.secureRandom();
    
    /**
     * create mnemonic by entropyStr, entropyStr can be null.
     * 
     * @param entropyStr: random  entropy
     * @return String
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

    public String createMnemonic() {
        byte[] initialEntropy = new byte[16];
        secureRandom.nextBytes(initialEntropy);
        String mnemonic = MnemonicUtils.generateMnemonic(initialEntropy);
        return mnemonic;
    }
    /**    
     * generate PkeyInfo By mnemonic 
     *
     * @param mnemonic
     * @param passphrase: password for create seed, it can be null.  
     * @return KeyStoreFileInfo       
     */
    public PkeyInfo generatePrivateKeyByMnemonic(String mnemonic, String passphrase, int eccType)
    		throws CipherException, KeyGenException {
    	byte[] seed = MnemonicUtils.generateSeed(mnemonic, passphrase);
    	byte[] chainCode = Arrays.copyOfRange(seed, 32, 64);

    	EccTypeEnums type = EccTypeEnums.getEccByType(eccType);
    	if(null == type){
    		log.error("ecc type {} error.", eccType);
    		throw new KeyGenException(ExceptionCodeEnums.ECC_TYPE_ERROR);
    	}

    	ECKeyPair ecKeyPair = null;
    	if(eccType == EccTypeEnums.SECP256K1.getEccType()){
    		ecKeyPair = ECKeyPair.create(Arrays.copyOfRange(seed, 0, 32));
    	}else if(eccType == EccTypeEnums.SM2P256V1.getEccType()){
    		ecKeyPair = SM2KeyHandler.create(Arrays.copyOfRange(seed, 0, 32));
    	}
    	else{
    	    throw new KeyGenException(ExceptionCodeEnums.ECC_TYPE_ERROR);
        }
    	return KeyUtils.createPkeyInfo(ecKeyPair.getPrivateKey(), ecKeyPair.getPublicKey(), 
    			type.getEccName(), Numeric.toHexString(chainCode));
    }
    
    /**    
     * generate private key by chainCode  
     * 
     * @param mnemonic
     * @param chainCodeStr
     * @return
     * @throws KeyGenException
     * @throws CipherException      
     * @return PkeyInfo       
     */
    public PkeyInfo generatePrivateKeyByChainCode(String mnemonic, String chainCodeStr, int eccType) throws KeyGenException, CipherException{
        byte[] chainCode = Numeric.hexStringToByteArray(chainCodeStr);
        if(chainCode == null || chainCode.length != 32){
            throw new KeyGenException(ExceptionCodeEnums.CHAIN_CODE_ERROR);
        }
        return generatePrivateKeyByMnemonic(mnemonic, chainCodeStr, eccType);
    }

      
}
