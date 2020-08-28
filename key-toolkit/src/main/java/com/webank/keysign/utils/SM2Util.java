package com.webank.keysign.utils;

import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.asn1.gm.GMNamedCurves;
import org.bouncycastle.crypto.DerivationFunction;
import org.bouncycastle.crypto.digests.SHA256Digest;
import org.bouncycastle.crypto.digests.ShortenedDigest;
import org.bouncycastle.crypto.generators.KDF1BytesGenerator;
import org.bouncycastle.crypto.params.ISO18033KDFParameters;
import org.bouncycastle.math.ec.ECCurve;
import org.bouncycastle.math.ec.ECPoint;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Arrays;

/**
 *   <B>说 明<B/>:SM2的非对称加解密工具类，椭圆曲线方程为：y^2=x^3+ax+b 使用Fp-256
 */
@Slf4j
public class SM2Util {
    //TODO:GMNamedCurves
    /** 素数p */
    private static final BigInteger p = new BigInteger("FFFFFFFE" + "FFFFFFFF"
            + "FFFFFFFF" + "FFFFFFFF" + "FFFFFFFF" + "00000000" + "FFFFFFFF"
            + "FFFFFFFF", 16);

    /** 系数a */
    private static final BigInteger a = new BigInteger("FFFFFFFE" + "FFFFFFFF"
            + "FFFFFFFF" + "FFFFFFFF" + "FFFFFFFF" + "00000000" + "FFFFFFFF"
            + "FFFFFFFC", 16);

    /** 系数b */
    private static final BigInteger b = new BigInteger("28E9FA9E" + "9D9F5E34"
            + "4D5A9E4B" + "CF6509A7" + "F39789F5" + "15AB8F92" + "DDBCBD41"
            + "4D940E93", 16);

    /** 坐标x */
    private static final BigInteger xg = new BigInteger("32C4AE2C" + "1F198119"
            + "5F990446" + "6A39C994" + "8FE30BBF" + "F2660BE1" + "715A4589"
            + "334C74C7", 16);

    /** 坐标y */
    private static final BigInteger yg = new BigInteger("BC3736A2" + "F4F6779C"
            + "59BDCEE3" + "6B692153" + "D0A9877C" + "C62A4740" + "02DF32E5"
            + "2139F0A0", 16);

    /** 基点G, G=(xg,yg),其介记为n */
    private static final BigInteger n = new BigInteger("FFFFFFFE" + "FFFFFFFF"
            + "FFFFFFFF" + "FFFFFFFF" + "7203DF6B" + "21C6052B" + "53BBF409"
            + "39D54123", 16);

    private static SecureRandom random = new SecureRandom();
    public static final ECCurve curve;
    private static ECPoint G;



    private static boolean allZero(byte[] buffer) {
        for (int i = 0; i < buffer.length; i++) {
            if (buffer[i] != 0)
                return false;
        }
        return true;
    }

    /**
     * 加密
     * @param input 待加密消息M
     * @param publicKey 公钥
     * @return byte[] 加密后的字节数组
     */
    public static byte[] encrypt(String input, ECPoint publicKey) {
        byte[] inputBuffer = input.getBytes();
        BigInteger r = RandomUtils.random(n);
        ECPoint C1 = G.multiply(r);
        byte[] C1Buffer = C1.getEncoded(false);

        BigInteger h = curve.getCofactor();
        if(h != null){
            ECPoint result = publicKey.multiply(h);
            if (result.isInfinity()) {
                throw new IllegalArgumentException("Invalid publickey");
            }
        }

        ECPoint kPub = publicKey.multiply(r).normalize();
        byte[] kPubBytes = kPub.getEncoded(false);
        //Use kdf1
        DerivationFunction kdf = new KDF1BytesGenerator(new ShortenedDigest(
                new SHA256Digest(), 20));
        byte[] t = new byte[inputBuffer.length];
        kdf.init(new ISO18033KDFParameters(kPubBytes));
        kdf.generateBytes(t, 0, t.length);

        if (allZero(t)) {
            throw new IllegalArgumentException("all zero");
        }

        byte[] C2 = new byte[inputBuffer.length];
        for (int i = 0; i < inputBuffer.length; i++) {
            C2[i] = (byte) (inputBuffer[i] ^ t[i]);
        }

        byte[] C3 = calculateHash(kPub.getXCoord().toBigInteger(), inputBuffer,
                kPub.getYCoord().toBigInteger());

        byte[] encryptResult = new byte[C1Buffer.length + C2.length + C3.length];
        System.arraycopy(C1Buffer, 0, encryptResult, 0, C1Buffer.length);
        System.arraycopy(C2, 0, encryptResult, C1Buffer.length, C2.length);
        System.arraycopy(C3, 0, encryptResult, C1Buffer.length + C2.length,
                C3.length);

        return encryptResult;
    }

    public static byte[] decrypt(byte[] encryptData, BigInteger privateKey) {
        byte[] C1Byte = new byte[65];
        System.arraycopy(encryptData, 0, C1Byte, 0, C1Byte.length);

        ECPoint C1 = curve.decodePoint(C1Byte).normalize();

        //k·C1 = k·r·G = r·（k·G）=r·pub, now compute t from it
        ECPoint kC1 = C1.multiply(privateKey).normalize();

        byte[] kC1Bytes = kC1.getEncoded(false);
        DerivationFunction kdf = new KDF1BytesGenerator(new ShortenedDigest(
                new SHA256Digest(), 20));

        int klen = encryptData.length - 65 - 20;
        byte[] t = new byte[klen];
        kdf.init(new ISO18033KDFParameters(kC1Bytes));
        kdf.generateBytes(t, 0, t.length);

        if (allZero(t)) {
            throw new IllegalArgumentException("all zero");
        }

        byte[] M = new byte[klen];
        for (int i = 0; i < M.length; i++) {
            M[i] = (byte) (encryptData[C1Byte.length + i] ^ t[i]);
        }

        /* 6 计算 u = Hash(x2 || M' || y2) 判断 u == C3是否成立 */
        byte[] C3 = new byte[20];
        System.arraycopy(encryptData, encryptData.length - 20, C3, 0, 20);
        byte[] u = calculateHash(kC1.getXCoord().toBigInteger(), M, kC1
                .getYCoord().toBigInteger());
        if (Arrays.equals(u, C3)) {
            return M;
        } else {
            throw new IllegalArgumentException("decrypt failed");
        }
    }

    private static byte[] calculateHash(BigInteger x2, byte[] M, BigInteger y2) {
        ShortenedDigest digest = new ShortenedDigest(new SHA256Digest(), 20);
        byte[] buf = x2.toByteArray();
        digest.update(buf, 0, buf.length);
        digest.update(M, 0, M.length);
        buf = y2.toByteArray();
        digest.update(buf, 0, buf.length);

        buf = new byte[20];
        digest.doFinal(buf, 0);
        return buf;
    }

    static {
        curve = GMNamedCurves.getByName("sm2p256v1").getCurve();
        G = curve.createPoint(xg, yg);
    }

    private SM2Util(){}

}