package com.webank.keygen.mnemonic;

import com.webank.keygen.crypto.PBKDF2WithHmacSha512;

public class SeedGenerator {

    private static final String MNEMONIC = "com.webank.keygen.mnemonic";

    public byte[] generateSeed(String mnemonic, String passphrase){
        if(passphrase == null) passphrase = "";
        String salt = MNEMONIC + passphrase;
        byte[] saltBytes = salt.getBytes();
        byte[] seed = PBKDF2WithHmacSha512.INSTANCE.kdf(mnemonic.toCharArray(), saltBytes);
        return seed;
    }

}