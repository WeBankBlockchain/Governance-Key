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

import com.webank.keygen.enums.EccTypeEnums;
import com.webank.keygen.exception.KeyGenException;
import com.webank.keygen.face.PrivateKeyCreator;
import com.webank.keygen.handler.ECKeyHandler;
import com.webank.keygen.model.PkeyInfo;
import com.webank.keygen.utils.KeyUtils;
import com.webank.keysign.utils.RandomUtils;
import org.bouncycastle.crypto.digests.SHA512Digest;
import org.bouncycastle.crypto.generators.PKCS5S2ParametersGenerator;
import org.bouncycastle.crypto.params.KeyParameter;
import org.web3j.crypto.ECKeyPair;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.web3j.crypto.Hash.sha256;


/**
 * PkeyService . Generates secp256k1 private key.
 *
 * @Description: PkeyService
 * @author graysonzhang
 * @date 2019-07-15 10:36:17
 *
 */
public class PkeyByRandomService implements PrivateKeyCreator{
    
    private static final int SEED_ITERATIONS = 2048;
    private static final int SEED_KEY_SIZE = 512;

    /**
     * Generate secp256k1 private key.
     * @return secp256k1 private key.
     * @throws KeyGenException
     */
    @Override
    public PkeyInfo generatePrivateKey() throws KeyGenException {
        
        ECKeyPair ecKeyPair = ECKeyHandler.generateECKeyPair();
        byte[] chaincode = RandomUtils.randomBytes(32);
        return KeyUtils.createPkeyInfo(ecKeyPair.getPrivateKey(), ecKeyPair.getPublicKey(), EccTypeEnums.SECP256K1.getEccName(), chaincode);
    }

    //Please refer to PkeyHDDeriveService
    @Deprecated
    public PkeyInfo generatePrivateKeyByChainCode(byte[] privateKey, String chainCode){
        
        PKCS5S2ParametersGenerator gen = new PKCS5S2ParametersGenerator(new SHA512Digest());
        gen.init(privateKey, chainCode.getBytes(UTF_8), SEED_ITERATIONS);

        byte[] seed = ((KeyParameter) gen.generateDerivedParameters(SEED_KEY_SIZE)).getKey();
        ECKeyPair ecKeyPair = ECKeyPair.create(sha256(seed));
        return KeyUtils.createPkeyInfo(ecKeyPair.getPrivateKey(), ecKeyPair.getPublicKey(), EccTypeEnums.SECP256K1.getEccName());
    }

}
