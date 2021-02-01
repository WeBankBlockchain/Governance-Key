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
package com.webank.keysign.service;

import com.webank.keysign.utils.Numeric;
import org.bouncycastle.jcajce.provider.asymmetric.ec.BCECPrivateKey;
import org.bouncycastle.jcajce.provider.asymmetric.ec.BCECPublicKey;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.junit.Assert;
import org.junit.Test;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.SecureRandom;
import java.security.Security;
import java.security.spec.ECGenParameterSpec;

/**
 * SignServiceTest
 *
 * @Description: SignServiceTest
 * @author graysonzhang
 * @data 2019-08-16 15:49:43
 *
 */
public class SignServiceTest{
    private static SecureRandom random;
    private static KeyPairGenerator sm2Generator;
    private static ECGenParameterSpec sm2spec;
    private static KeyPairGenerator eccGenerator;
    private static ECGenParameterSpec eccspec;

    static {
        if (Security.getProvider(BouncyCastleProvider.PROVIDER_NAME) == null) {
            Security.addProvider(new BouncyCastleProvider());
        }
        random = new SecureRandom();
        try {
            eccspec = new ECGenParameterSpec("secp256k1");
            eccGenerator = KeyPairGenerator.getInstance("ECDSA", "BC");
            eccGenerator.initialize(eccspec, random);
            sm2spec = new ECGenParameterSpec("sm2p256v1");
            sm2Generator = KeyPairGenerator.getInstance("ECDSA", "BC");
            sm2Generator.initialize(sm2spec, random);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    @Test
    public void hardcoreTest(){
        for(int i=0;i<2000;i++){
            testECCSign();
        }

        for(int i=0;i<2000;i++){
            testSM2Sign();
        }
    }


    @Test
    public void testECCSign(){
        KeyPair keyPair = eccGenerator.generateKeyPair();
        ECCSignService signService  =new ECCSignService();
        String privateKey = ((BCECPrivateKey)keyPair.getPrivate()).getD().toString(16);
        String dataStr = randomStr();
        String signData = signService.sign(dataStr, privateKey);
        byte[] pubKeyBytes =  ((BCECPublicKey)keyPair.getPublic()).getQ().getEncoded(false);
        boolean verify = signService.verify(dataStr, signData, Numeric.toHexString(pubKeyBytes));

        Assert.assertTrue(verify);
    }

    /**
     private key 59d27c0a2a015bfa4f37a87dd2b2073962057bd5865453293a77d72e1fa86c6a
     public key 04614fa1f14daeeea22b0d10499b59aa742ed0e90a3a76df0b45cf360f58ee42776593b7c42492cb650bfc710fa9b6bb365424a6f225b6d4019446eb8f4ad70300
     sig result  fca33aba74e026087884e00eaa7a9e23bcbf7aaf8f2ddfb699dc64b7b44926e8687c8692773451e3e3d8e94d79d90490f575d158b0329f68f77ca633eb09a86500
     */

    @Test
    public void testWithWeb3(){
        String dataStr = "abcd----0";
        String signData = new ECCSignService().sign(dataStr, "59d27c0a2a015bfa4f37a87dd2b2073962057bd5865453293a77d72e1fa86c6a");
        Assert.assertTrue("fca33aba74e026087884e00eaa7a9e23bcbf7aaf8f2ddfb699dc64b7b44926e8687c8692773451e3e3d8e94d79d90490f575d158b0329f68f77ca633eb09a86500"
        .equals(signData));
        boolean verify =  new ECCSignService().verify(dataStr, signData, "04614fa1f14daeeea22b0d10499b59aa742ed0e90a3a76df0b45cf360f58ee42776593b7c42492cb650bfc710fa9b6bb365424a6f225b6d4019446eb8f4ad70300");
        Assert.assertTrue(verify);
    }

    @Test
    public void testECCSignBytes(){
        KeyPair keyPair = eccGenerator.generateKeyPair();
        ECCSignService signService  =new ECCSignService();
        String privateKey = ((BCECPrivateKey)keyPair.getPrivate()).getD().toString(16);
        String dataStr = randomStr();
        String signData = signService.sign(dataStr.getBytes(), privateKey);

        byte[] pubKeyBytes =  ((BCECPublicKey)keyPair.getPublic()).getQ().getEncoded(false);
        boolean verify = signService.verify(dataStr.getBytes(), signData, Numeric.toHexString(pubKeyBytes));
        Assert.assertTrue(verify);
    }

    @Test
    public void testSM2Sign(){
        KeyPair keyPair = sm2Generator.generateKeyPair();
        SM2SignService signService  =new SM2SignService();
        String privateKey = ((BCECPrivateKey)keyPair.getPrivate()).getD().toString(16);
        String dataStr = randomStr();

        String signData = signService.sign(dataStr, privateKey);
        byte[] pubKeyBytes =  ((BCECPublicKey)keyPair.getPublic()).getQ().getEncoded(false);
        boolean verify = signService.verify(dataStr, signData, Numeric.toHexString(pubKeyBytes));
        Assert.assertTrue(verify);
    }

    @Test
    public void testSM2SignBytes(){
        KeyPair keyPair = sm2Generator.generateKeyPair();
        SM2SignService signService  =new SM2SignService();
        String privateKey = ((BCECPrivateKey)keyPair.getPrivate()).getD().toString(16);
        String dataStr = randomStr();

        String signData = signService.sign(dataStr.getBytes(), privateKey);
        byte[] pubKeyBytes =  ((BCECPublicKey)keyPair.getPublic()).getQ().getEncoded(false);
        boolean verify = signService.verify(dataStr.getBytes(), signData, Numeric.toHexString(pubKeyBytes));
        if(!verify){
            System.out.println(dataStr);
            System.out.println(privateKey);
            System.out.println(Numeric.toHexString(pubKeyBytes));
        }
        Assert.assertTrue(verify);
    }


	private static String randomStr() {
		byte[] bytes = new byte[16];
		random.nextBytes(bytes);
		return Numeric.toHexString(bytes);
	}
}
