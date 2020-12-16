package com.webank.keysign.utils;

import org.bouncycastle.asn1.sec.SECNamedCurves;
import org.bouncycastle.crypto.generators.SCrypt;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.math.ec.ECCurve;
import org.bouncycastle.math.ec.ECPoint;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.Arrays;

/**
 * @author aaronchu
 * @Description
 * @data 2020/06/19
 */
public class ECCUtil {

    public static final ECCurve curve ;
    private static final ECPoint G;

    private static final int N_STANDARD = 1 << 18;
    private static final int P_STANDARD = 1;
    private static final int KEY_LEN = 32;
    public static byte[] encrypt(byte[] input, ECPoint pubkey) throws Exception{
        // step1: generate  random r
        BigInteger r = RandomUtils.random(curve.getOrder());
        // step2: generate aux data to help recover shared secret. R=r·G
        ECPoint R = G.multiply(r);
        byte[] RBytes = R.getEncoded(false);
        // step3: generate shared secret: S=r·pub.
        ECPoint S = pubkey.multiply(r).normalize();
        byte[] secretBytes = S.getEncoded(false);
        // step4: now use shared secret to encrypt input msg. We take process similiar to pkey-sign keystore encrypt
        // step4.1: S may be too weak, strengthen it using kdf: S' = kdf(S)
        byte[] salt = RandomUtils.randomBytes(16);//help kdf
        byte[] derived = kdf(secretBytes, salt);
        // step4.2: split S' into (mk, ek)
        byte[] mk = Arrays.copyOfRange(derived, 0,16);
        byte[] ek = Arrays.copyOfRange(derived, 16,32);
        // step4.3: do encryption, we take aes here: C=enc(input, ek)
        byte[] iv = RandomUtils.randomBytes(16);//help aes
        byte[] C = performCipherOperation(input,Cipher.ENCRYPT_MODE, iv, ek);
        // step4.4: do mac: mac=mac(cipher, mk). we take sha3 instead of hmac
        byte[] mac = generateMac(C, mk);
        // step5: build result:R || iv || salt || mac || C
        byte[] result = new byte[RBytes.length + iv.length + salt.length + mac.length + C.length];
        int position = 0;
        System.arraycopy(RBytes, 0, result, position, RBytes.length);
        position+= RBytes.length;
        System.arraycopy(iv, 0, result, position, iv.length);
        position+= iv.length;
        System.arraycopy(salt, 0, result, position, salt.length);
        position+= salt.length;
        System.arraycopy(mac, 0, result, position, mac.length);
        position+= mac.length;
        System.arraycopy(C, 0, result, position, C.length);
        return result;
    }

    public static byte[] decrypt(byte[] cipher, BigInteger privKey) throws Exception{
        //step1: decode R || iv || salt || mac || C
        byte[] RBytes = Arrays.copyOfRange(cipher, 0, 65);
        byte[] iv = Arrays.copyOfRange(cipher,65, 81);
        byte[] salt = Arrays.copyOfRange(cipher, 81, 97);
        byte[] mac = Arrays.copyOfRange(cipher, 97, 129);
        byte[] C = Arrays.copyOfRange(cipher, 129, cipher.length);
        //step2: recover shared secret from aux info: S = priv·R (so priv·R=priv·r·G=r·priv·G=r·pub)
        ECPoint R = curve.decodePoint(RBytes);
        ECPoint S = R.multiply(privKey).normalize();
        byte[] secretBytes = S.getEncoded(false);
        //step3: decrypt cipher using shared secret
        //step3.1: compute S'= kdf(S)
        byte[] derived = kdf(secretBytes, salt);
        //step3.2: split into (mk, ek)
        byte[] mk = Arrays.copyOfRange(derived,0,16);
        byte[] ek = Arrays.copyOfRange(derived,16,32);
        //step3.3: verify mac
        byte[] computedMac = generateMac(C, mk);
        if(!Arrays.equals(computedMac, mac)) throw new IllegalArgumentException("mac not match");
        //step3.4: decrypt using aes
        byte[] plain = performCipherOperation(C, Cipher.DECRYPT_MODE, iv, ek);
        //step4: return result
        return plain;
    }


    private static byte[] kdf(byte[] src, byte[] salt){
        //If N-STANDARD is chosen the performance will be too low
        return SCrypt.generate(src, salt,N_STANDARD, 8, P_STANDARD, KEY_LEN);
    }

    private static byte[] performCipherOperation(
            byte[] text, int mode, byte[] iv, byte[] encryptKey) throws Exception {
        IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);
        Cipher cipher = Cipher.getInstance("AES/CTR/NoPadding");

        SecretKeySpec secretKeySpec = new SecretKeySpec(encryptKey, "AES");
        cipher.init(mode, secretKeySpec, ivParameterSpec);
        return cipher.doFinal(text);
    }

    private static byte[] generateMac(byte[] cipherText,byte[] mk) throws Exception {
        byte[] result = new byte[mk.length + cipherText.length];

        System.arraycopy(mk, 0, result, 0, mk.length);
        System.arraycopy(cipherText, 0, result, mk.length, cipherText.length);
        MessageDigest digest = MessageDigest.getInstance("SHA256", new BouncyCastleProvider());
        return digest.digest(result);
    }


    static {
        curve = SECNamedCurves.getByName("secp256k1").getCurve();
        G = curve.decodePoint(
                Numeric.hexStringToByteArray(
                        "0479BE667EF9DCBBAC55A06295CE870B07029BFCDB2DCE28D959F2815B16F81798483ADA7726A3C4655DA4FBFC0E1108A8FD17B448A68554199C47D08FFB10D4B8")
        );
    }
    private ECCUtil(){}
}
