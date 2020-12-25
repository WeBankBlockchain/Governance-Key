package com.webank.keysign.utils;

import org.bouncycastle.math.ec.ECCurve;
import org.bouncycastle.math.ec.ECPoint;

import java.math.BigInteger;
import java.util.Arrays;

/**
 * @author aaronchu
 * @Description
 * @data 2020/06/22
 */
public class KeyUtils {

    public static String ensureStandard32BytesPrivateKey(String privateKey){
        BigInteger privVal = new BigInteger(privateKey,16);
        byte[] privBytes = Numeric.toBytesPadded(privVal, 32);
        privateKey = Numeric.toHexString(privBytes);
        return privateKey;
    }

    public static String ensureStandard65BytesPublickey(String publicKey){
        byte[] pubBytes = Numeric.hexStringToByteArray(publicKey);
        if(pubBytes.length == 65) return publicKey;
        byte[] newPub = new byte[65];
        newPub[0] = 0x04;
        System.arraycopy(pubBytes, 0, newPub, 1, pubBytes.length);
        return Numeric.toHexString(newPub);
    }

    public static ECPoint pubKeyStrToECPoint(String publicKey, ECCurve ecCurve){
        publicKey = KeyUtils.ensureStandard65BytesPublickey(publicKey);
        byte[] pubBytes = Numeric.hexStringToByteArray(publicKey);
        byte[] x = Arrays.copyOfRange(pubBytes, 1, 33);
        byte[] y = Arrays.copyOfRange(pubBytes, 33, 65);
        BigInteger xVal = Numeric.toBigInt(x);
        BigInteger yVal = Numeric.toBigInt(y);
        ECPoint point = ecCurve.createPoint(xVal, yVal);
        return point;
    }

    private KeyUtils(){}

}
