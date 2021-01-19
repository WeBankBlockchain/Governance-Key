package com.webank.keysign.service;

import org.junit.Test;

/**
 * @author aaronchu
 * @Description
 * @data 2020/06/22
 */
public class Demo {
    public static void main(String[] args){
        //Case 1: Ecc(secp256k1) sign and verify
        String eccMsg = "HelloEccSign";
        String eccPrivateKey = "28018238ac7eec853401dfc3f31133330e78ac27a2f53481270083abb1a126f9";
        String eccPublicKey = "0460fc2bce5795ee2ac34d1f584f603b4e2920a95d8d3db5f5c664244a99fd76405831ffaf932f64eae3ec67bc8ff7bfed9039f29bf39ce6583d55ca449b64319e";

        ECCSignService eccSignService = new ECCSignService();
        String eccSignature = eccSignService.sign(eccMsg, eccPrivateKey);
        System.out.println("ecc signature:"+eccSignature);

        boolean eccVerifyResult = eccSignService.verify(eccMsg, eccSignature, eccPublicKey);
        System.out.println("ecc verify result:"+eccVerifyResult);

        //Case 2: Ecc(secp256k1) encryption and decryption
        ECCEncryptService eccEncryptService = new ECCEncryptService();
        String eccCipherText = eccEncryptService.encrypt(eccMsg, eccPublicKey);
        System.out.println("ecc encryption cipher:"+eccCipherText);

        String eccPlainText = eccEncryptService.decrypt(eccCipherText, eccPrivateKey);
        System.out.println("ecc decryption result:"+eccPlainText);

        //Case3: Gm(sm2p256v1) sign and verify
        String gmMsg = "HelloGM";
        String gmPrivateKey = "73c8a8054b5e42b0d089e24f16c665bc82a132082d258c5efb54c49a3b7273f9";
        String gmPublicKey = "0451c895673d372267a565c4a7711102108138132b21f22ed556df08fb4c8cfdcaf17dcb605f8a6394f8684aa1916df60929532faf808c36c133ce52356d0f45f3";

        SM2SignService sm2SignService = new SM2SignService();
        String gmSignature = sm2SignService.sign(gmMsg, gmPrivateKey);
        System.out.println("gm signature:"+gmSignature);

        boolean gmVerifyResult = sm2SignService.verify(gmMsg, gmSignature, gmPublicKey);
        System.out.println("gm verify result:"+gmVerifyResult);

        //Case4: Gm(sm2p256v1) encryption and decryption
        SM2EncryptService gmEncryptService = new SM2EncryptService();
        String gmCipherText = gmEncryptService.encrypt(gmMsg, gmPublicKey);
        System.out.println("gm encryption cipher:"+gmCipherText);

        String gmPlainText = gmEncryptService.decrypt(gmCipherText, gmPrivateKey);
        System.out.println("gm decryption result:"+gmPlainText);
    }

    @Test
    public void nonStandardKeyFormatTest(){
        //Case 1: Ecc(secp256k1) sign and verify
        String eccMsg = "HelloEccSign";
        String eccPrivateKey = "28018238ac7eec853401dfc3f31133330e78ac27a2f53481270083abb1a126f9";
        String eccPublicKey = "60fc2bce5795ee2ac34d1f584f603b4e2920a95d8d3db5f5c664244a99fd76405831ffaf932f64eae3ec67bc8ff7bfed9039f29bf39ce6583d55ca449b64319e";

        ECCSignService eccSignService = new ECCSignService();
        String eccSignature = eccSignService.sign(eccMsg, eccPrivateKey);
        System.out.println("ecc signature:"+eccSignature);

        boolean eccVerifyResult = eccSignService.verify(eccMsg, eccSignature, eccPublicKey);
        System.out.println("ecc verify result:"+eccVerifyResult);

        //Case 2: Ecc(secp256k1) encryption and decryption
        ECCEncryptService eccEncryptService = new ECCEncryptService();
        String eccCipherText = eccEncryptService.encrypt(eccMsg, eccPublicKey);
        System.out.println("ecc encryption cipher:"+eccCipherText);

        String eccPlainText = eccEncryptService.decrypt(eccCipherText, eccPrivateKey);
        System.out.println("ecc decryption result:"+eccPlainText);

        //Case3: Gm(sm2p256v1) sign and verify
        String gmMsg = "HelloGM";
        String gmPrivateKey = "73c8a8054b5e42b0d089e24f16c665bc82a132082d258c5efb54c49a3b7273f9";
        String gmPublicKey = "51c895673d372267a565c4a7711102108138132b21f22ed556df08fb4c8cfdcaf17dcb605f8a6394f8684aa1916df60929532faf808c36c133ce52356d0f45f3";

        SM2SignService sm2SignService = new SM2SignService();
        String gmSignature = sm2SignService.sign(gmMsg, gmPrivateKey);
        System.out.println("gm signature:"+gmSignature);

        boolean gmVerifyResult = sm2SignService.verify(gmMsg, gmSignature, gmPublicKey);
        System.out.println("gm verify result:"+gmVerifyResult);

        //Case4: Gm(sm2p256v1) encryption and decryption
        SM2EncryptService gmEncryptService = new SM2EncryptService();
        String gmCipherText = gmEncryptService.encrypt(gmMsg, gmPublicKey);
        System.out.println("gm encryption cipher:"+gmCipherText);

        String gmPlainText = gmEncryptService.decrypt(gmCipherText, gmPrivateKey);
        System.out.println("gm decryption result:"+gmPlainText);
    }
}
