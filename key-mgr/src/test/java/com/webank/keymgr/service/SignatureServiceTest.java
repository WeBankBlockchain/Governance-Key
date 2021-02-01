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
package com.webank.keymgr.service;

import com.webank.keygen.key.KeyComputeAlgorithm;
import com.webank.keymgr.BaseTest;
import com.webank.keymgr.model.EncryptKeyInfo;
import com.webank.keymgr.model.vo.PkeyInfoVO;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.web3j.utils.Numeric;

/**
 * SignatureServiceTest
 *
 * @Description: SignatureServiceTest
 * @author graysonzhang
 * @date 2020-01-07 16:03:05
 *
 */
public class SignatureServiceTest extends BaseTest {
    
    @Autowired
    private KeysManagerService keysManagerService;
    @Autowired
    private SignatureService signatureService;
    @Autowired
    private KeyComputeAlgorithm addressHandler;
    
    @Test
    public void testSignAndCheckSign() throws Exception{
        
        String userId = "u00001";
        String password = "123456";
        String keyName = "testKey";
        
        PkeyInfoVO pkeyInfoVO = keysManagerService.createPrivateKey(userId, password, keyName) ;
        EncryptKeyInfo encryptKey = keysManagerService.getEncryptPrivateKeyByUserIdAndAddress(userId, pkeyInfoVO.getKeyAddress());
        String privateKey = keysManagerService.decryptPrivateKey(password, encryptKey.getEncryptKey());
        
        String msg = "test message";
        String signStr = signatureService.sign(msg, privateKey);
        String publicKey = addressHandler.computePublicKey(Numeric.hexStringToByteArray(privateKey));
        boolean checkResult = signatureService.verify(msg, signStr, publicKey);
        
        Assert.assertTrue(checkResult);
    }
}
