package com.webank.keygen.utils;

/**
 * @author aaronchu
 * @Description
 * @data 2020/11/18
 */
public class ExtendedKeyUtil {

    private ExtendedKeyUtil(){}

    public static int hardIndex(int index) {
        return index | 0x80000000;
    }

    public static boolean isHardened(int i) {
        return (i & 0x80000000) != 0;
    }

    public static boolean isCompressedPubkey(byte[] pubkey){
        return pubkey.length == 33 && (pubkey[0] == 2 || pubkey[0] == 3);
    }
}
