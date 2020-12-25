package com.webank.keysign.service;

import com.webank.keysign.face.PublicKeyEncryptor;
import com.webank.keysign.utils.ECCUtil;
import com.webank.keysign.utils.KeyUtils;
import com.webank.keysign.utils.Numeric;
import com.webank.keysign.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.math.ec.ECPoint;

import java.math.BigInteger;

/**
 * @author aaronchu
 * @Description
 * @data 2020/06/18
 */
@Slf4j
public class ECCEncryptService implements PublicKeyEncryptor {

    /**
     * Encrypt data using secp256k1 curve
     * @param plain
     * @param publicKey Hex format of public key. Can be 64 bytes or 65 bytes which with a prefix 0x04
     * @return
     */
    public String encrypt(String plain, String publicKey){
        if(StringUtils.isEmpty(plain) || StringUtils.isEmpty(publicKey)){
            log.error("Args cannot be empty");
            return null;
        }
        try{
            ECPoint pubKeyPoint = KeyUtils.pubKeyStrToECPoint(publicKey, ECCUtil.curve);
            byte[] cipherText = ECCUtil.encrypt(plain.getBytes(), pubKeyPoint);
            return Numeric.toHexString(cipherText);
        }
        catch (Exception ex){
            log.error("Ecc encryption exception",ex);
            return null;
        }
    }

    /**
     * Decrypt data with secp256k1 curve
     * @param cipher
     * @param privateKey Hex format of private key. Can be 64 bytes or shorter.
     * @return
     */
    public String decrypt(String cipher, String privateKey){
        if(StringUtils.isEmpty(cipher) || StringUtils.isEmpty(privateKey)){
            log.error("Args cannot be empty");
            return null;
        }
        try{
            byte[] plainBytes = ECCUtil.decrypt(Numeric.hexStringToByteArray(cipher), new BigInteger(privateKey, 16));
            return new String(plainBytes);
        }
        catch (Exception ex){
            log.error("Ecc decrypt exception",ex);
            return null;
        }
    }

}
