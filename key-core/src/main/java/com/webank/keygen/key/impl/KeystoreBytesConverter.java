package com.webank.keygen.key.impl;

import com.webank.keygen.key.KeyBytesConverter;
import org.apache.commons.io.Charsets;

/**
 * @author aaronchu
 * @Description
 * @data 2020/07/01
 */
public class KeystoreBytesConverter implements KeyBytesConverter {
    @Override
    public byte[] toBytes(String encryptKey) {
        return encryptKey.getBytes(Charsets.UTF_8);
    }

    @Override
    public String fromBytes(byte[] bytes) {
        return new String(bytes);
    }
}
