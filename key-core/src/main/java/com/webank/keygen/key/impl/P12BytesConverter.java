package com.webank.keygen.key.impl;

import com.webank.keygen.key.KeyBytesConverter;
import org.web3j.utils.Numeric;

/**
 * @author aaronchu
 * @Description
 * @data 2020/07/01
 */
public class P12BytesConverter implements KeyBytesConverter {

    @Override
    public byte[] toBytes(String encryptKey) {
        return Numeric.hexStringToByteArray(encryptKey);
    }

    @Override
    public String fromBytes(byte[] bytes) {
        return Numeric.toHexString(bytes);
    }
}
