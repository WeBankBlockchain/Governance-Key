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


import static java.nio.charset.StandardCharsets.UTF_8;
import static org.web3j.crypto.Hash.sha256;

import org.bouncycastle.crypto.digests.SHA512Digest;
import org.bouncycastle.crypto.generators.PKCS5S2ParametersGenerator;
import org.bouncycastle.crypto.params.KeyParameter;
import org.web3j.crypto.ECKeyPair;
import com.webank.keygen.enums.EccTypeEnums;
import com.webank.keygen.exception.KeyGenException;
import com.webank.keygen.face.PrivateKeyCreator;
import com.webank.keygen.handler.SM2KeyHandler;
import com.webank.keygen.model.PkeyInfo;
import com.webank.keygen.utils.KeyUtils;

import lombok.extern.slf4j.Slf4j;

/**
 * @Description SM2PkeyByRandomService
 * @author yuzhichu
 * @author graysonzhang
 * @date 2019-12-16 
 */
@Slf4j
public class PkeySM2ByRandomService implements PrivateKeyCreator{
	
    private static final int SEED_ITERATIONS = 2048;
    private static final int SEED_KEY_SIZE = 512;
    
    @Override
    public PkeyInfo generatePrivateKey() throws KeyGenException {
        
        ECKeyPair ecKeyPair = SM2KeyHandler.generateSM2KeyPair();
        return KeyUtils.createPkeyInfo(ecKeyPair.getPrivateKey(), ecKeyPair.getPublicKey(), EccTypeEnums.SM2P256V1.getEccName());
    }
    
    @Override
    public PkeyInfo generatePrivateKeyByChainCode(byte[] privateKey, String chainCode){
        PKCS5S2ParametersGenerator gen = new PKCS5S2ParametersGenerator(new SHA512Digest());
        gen.init(privateKey, chainCode.getBytes(UTF_8), SEED_ITERATIONS);

        byte[] seed = ((KeyParameter) gen.generateDerivedParameters(SEED_KEY_SIZE)).getKey();
        ECKeyPair ecKeyPair = SM2KeyHandler.create((sha256(seed)));
        return KeyUtils.createPkeyInfo(ecKeyPair.getPrivateKey(), ecKeyPair.getPublicKey(), EccTypeEnums.SM2P256V1.getEccName());
    }
}
 






