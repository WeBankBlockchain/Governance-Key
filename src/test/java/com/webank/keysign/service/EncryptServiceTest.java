package com.webank.keysign.service;

import com.webank.keysign.utils.Numeric;
import org.bouncycastle.jcajce.provider.asymmetric.ec.BCECPrivateKey;
import org.bouncycastle.jcajce.provider.asymmetric.ec.BCECPublicKey;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.math.ec.ECPoint;
import org.junit.Assert;
import org.junit.Test;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.SecureRandom;
import java.security.Security;
import java.security.spec.ECGenParameterSpec;

/**
 * @author aaronchu
 * @Description
 * @data 2020/06/19
 */
public class EncryptServiceTest {

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
    public void hardcoreTest() throws Exception{

        for(int i=0;i<10;i++){
            sm2EncryptTest();
        }
        System.out.println("sm2加解密完成");
        for(int i=0;i<10;i++){
            eccEncryptTest();
        }
        System.out.println("ecc加解密完成");
    }

    @Test
    public void sm2EncryptTest() throws Exception{
        KeyPair keyPair = sm2Generator.generateKeyPair();
        BCECPrivateKey priv = (BCECPrivateKey)(keyPair.getPrivate());
        BCECPublicKey pub = (BCECPublicKey)(keyPair.getPublic());
        ECPoint ecPoint = pub.getQ();
        byte[] pubBytes = ecPoint.getEncoded(false);

        String msg= randomStr();
        String pubHex = Numeric.toHexString(pubBytes);
        String cipher = new SM2EncryptService().encrypt(msg, pubHex);
        String plainText = new SM2EncryptService().decrypt(cipher, Numeric.toHexStringNoPrefix(priv.getD()));
        Assert.assertEquals(msg, plainText);
    }

    @Test
    public void eccEncryptTest() throws Exception{
        KeyPair keyPair = eccGenerator.generateKeyPair();
        BCECPrivateKey priv = (BCECPrivateKey)(keyPair.getPrivate());
        BCECPublicKey pub = (BCECPublicKey)(keyPair.getPublic());
        String msg = randomStr();
        String cipher = new ECCEncryptService().encrypt(msg, Numeric.toHexString(pub.getQ().getEncoded(false)));

        String plain = new ECCEncryptService().decrypt(cipher, priv.getD().toString(16));
        Assert.assertEquals(msg, plain);
    }

    private static String randomStr() {
        byte[] bytes = new byte[16];
        random.nextBytes(bytes);
        return Numeric.toHexString(bytes);
    }
}
