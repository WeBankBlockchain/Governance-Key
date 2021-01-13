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
package com.webank.keygen.handler;

import com.webank.keygen.constants.SM2Constants;
import com.webank.keygen.enums.EccTypeEnums;
import com.webank.keygen.exception.KeyGenException;
import com.webank.keygen.utils.KeyPresenter;
import com.webank.keygen.utils.KeyUtils;
import com.webank.wedpr.crypto.CryptoResult;
import com.webank.wedpr.crypto.NativeInterface;
import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.math.ec.ECPoint;
import org.fisco.bcos.sdk.crypto.keypair.CryptoKeyPair;
import org.fisco.bcos.sdk.crypto.keypair.SM2KeyPair;
import org.web3j.crypto.ECKeyPair;
import org.web3j.utils.Numeric;

import java.math.BigInteger;
import java.security.KeyPairGenerator;
import java.security.SecureRandom;
import java.security.Security;
import java.security.spec.ECGenParameterSpec;
import java.util.Arrays;

/**
 * @Description SM2KeyHandler
 * @author yuzhichu
 * @date 2019-12-18 
 */
@Slf4j
public class SM2KeyHandler {
    public static CryptoKeyPair generateSM2KeyPair() {
		return new SM2KeyPair().generateKeyPair();
    }
    
    public static CryptoKeyPair create(byte[] privKeyBytes) {
		return KeyUtils.getCryptKeyPair(privKeyBytes, EccTypeEnums.SM2P256V1);
    }
}














