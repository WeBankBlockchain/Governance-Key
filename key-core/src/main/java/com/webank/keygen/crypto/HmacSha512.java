package com.webank.keygen.crypto;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public enum  HmacSha512 {
    INSTANCE;

    private static final String HMAC_SHA512 = "HmacSHA512";

    public byte[] macHash(byte[] key, byte[] data) {
        try{
            Mac mac =  Mac.getInstance(HMAC_SHA512);
            SecretKeySpec keySpec = new SecretKeySpec(key, HMAC_SHA512);
            mac.init(keySpec);
            return mac.doFinal(data);
        }
        catch (Exception ex){
            throw new RuntimeException(ex);
        }

    }
}