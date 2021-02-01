package com.webank.keygen.hd.bip32;

import com.webank.keygen.crypto.HmacSha512;
import com.webank.keygen.exception.KeyGenException;

import java.nio.charset.StandardCharsets;

public class MasterKeyGenerator {

    public static final String FISCOBCOS_Seed = "FISCO_BCOS seed";
    public static final String Bitcoin_Seed = "Bitcoin seed";


    public byte[] toMasterKey(byte[] data, String prefix) throws KeyGenException{
        byte[] shakey = prefix.getBytes(StandardCharsets.UTF_8);
        return HmacSha512.INSTANCE.macHash(shakey, data);
    }


    public byte[] toMasterKey(byte[] data) throws KeyGenException{
        return this.toMasterKey(data, FISCOBCOS_Seed);
    }
}
