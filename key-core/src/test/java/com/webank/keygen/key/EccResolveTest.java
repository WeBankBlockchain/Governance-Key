package com.webank.keygen.key;

import com.webank.keygen.encrypt.P12Encrypt;
import com.webank.keygen.enums.EccTypeEnums;
import com.webank.keygen.key.impl.KeystoreEncryptAlgorithm;
import com.webank.keygen.key.impl.P12EncryptAlgorithm;
import com.webank.keygen.key.impl.PemEncryptAlgorithm;
import com.webank.keygen.model.PkeyInfo;
import com.webank.keygen.service.PkeyByRandomService;
import com.webank.keygen.service.PkeySM2ByRandomService;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author aaronchu
 * @Description
 * @data 2020/08/06
 */
public class EccResolveTest {
    @Test
    public void testKeystore() throws Exception{
        String ks = "{\"address\":\"0x5f5de00d67826c17225d59f379085f2a177f04a0\",\"id\":\"a76316af-2e59-489f-b292-e8fc590d1cef\",\"version\":3,\"crypto\":{\"cipher\":\"aes-128-ctr\",\"ciphertext\":\"933494a8f735e585041f9e8a8e6565711368d42e8ef8b4ff3e4103ceb22fe508\",\"cipherparams\":{\"iv\":\"8f9f195bc1d0c1ef0f9fb12606168bb6\"},\"kdf\":\"scrypt\",\"kdfparams\":{\"dklen\":32,\"n\":262144,\"p\":1,\"r\":8,\"salt\":\"786832a458ba1baf7c9a4b75e2b2dd3bba56ee796c152679f676026dac4a9459\"},\"mac\":\"96ea9c4b1c27bb01e8c970151848eebebd7369627e29b7d582b0c693057fc347\"}}";
        String curve = new KeystoreEncryptAlgorithm().decryptFully("123", ks).getEccType();
        Assert.assertEquals("secp256k1", curve);
    }

    @Test
    public void testPem() throws Exception{
        PkeyInfo key = new PkeySM2ByRandomService().generatePrivateKey();
        String pem =  new PemEncryptAlgorithm().encrypt("123", key.getPrivateKey(),null, key.getEccName());
        String curve = new PemEncryptAlgorithm().decryptFully("123", pem).getEccType();
        Assert.assertEquals("sm2p256v1", curve);
    }

    @Test
    public void testP12() throws Exception{
        PkeyInfo key = new PkeyByRandomService().generatePrivateKey();
        String p12 = P12Encrypt.encryptPrivateKey("123", key.getPrivateKey(), EccTypeEnums.getEccByName(key.getEccName()));
        String curve = new P12EncryptAlgorithm().decryptFully("123", p12).getEccType();
        //System.out.println(curve);
        Assert.assertEquals("secp256k1", curve);
    }
}
