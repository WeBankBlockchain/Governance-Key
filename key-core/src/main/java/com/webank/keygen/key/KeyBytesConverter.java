package com.webank.keygen.key;

/**
 * @author aaronchu
 * @Description
 * @data 2020/07/01
 */
public interface KeyBytesConverter {

    byte[] toBytes(String encryptKey);

    String fromBytes(byte[] bytes);

}
