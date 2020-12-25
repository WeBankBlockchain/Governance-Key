package com.webank.keysign.utils;

import java.math.BigInteger;
import java.security.SecureRandom;

/**
 * @author aaronchu
 * @Description
 * @data 2020/06/19
 */
public class RandomUtils {
    private static SecureRandom secureRandom = new SecureRandom();

    public static BigInteger random(BigInteger max) {
        BigInteger r = new BigInteger(max.bitLength(), secureRandom);
        while (r.compareTo(max) >= 0 || r.equals(BigInteger.ZERO)) {
            r = new BigInteger(max.bitLength(), secureRandom);
        }
        return r;
    }

    public static byte[] randomBytes(int length){
        byte[] b = new byte[16];
        secureRandom.nextBytes(b);
        return b;
    }
}
