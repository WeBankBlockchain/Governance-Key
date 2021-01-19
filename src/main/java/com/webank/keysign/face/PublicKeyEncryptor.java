package com.webank.keysign.face;

/**
 * @author aaronchu
 * @Description
 * @data 2020/06/22
 */
public interface PublicKeyEncryptor {

    String encrypt(String plain, String pubHex);

    String decrypt(String cipher, String privHex);
}
