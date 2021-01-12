package com.webank.keygen.utils;

import com.webank.keygen.model.PkeyInfo;
import org.bouncycastle.util.BigIntegers;
import org.bouncycastle.util.encoders.Hex;
import org.web3j.utils.Numeric;

import java.math.BigInteger;

/**
 * @author aaronchu
 * @Description
 * @data 2021/01/12
 */
public class KeyPresenter {

    private KeyPresenter(){}

    public static String asString(byte[] keyBytes){
        String s = Numeric.toHexString(keyBytes);
        if(s.contains("0x") || s.contains("0X")) return s;
        return "0x" + s;
    }

    public static BigInteger asBigInteger(byte[] keyBytes){
        return new BigInteger(1, keyBytes);
    }

    public static byte[] asBytes(String hexKey){
        return Numeric.hexStringToByteArray(hexKey);
    }

    public static byte[] asBytes(BigInteger bigIntegerKey){
        return BigIntegers.asUnsignedByteArray(bigIntegerKey);
    }


}
