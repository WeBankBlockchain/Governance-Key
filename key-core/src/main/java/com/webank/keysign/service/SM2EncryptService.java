package com.webank.keysign.service;

import com.webank.keysign.face.PublicKeyEncryptor;
import com.webank.keysign.utils.KeyUtils;
import com.webank.keysign.utils.Numeric;
import com.webank.keysign.utils.SM2Util;
import com.webank.keysign.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.math.ec.ECPoint;

import java.math.BigInteger;

/**
 * @author aaronchu
 * @Description
 * @data 2020/06/19
 */
@Slf4j
public class SM2EncryptService implements PublicKeyEncryptor {
    /**
     * Encrypt data using sm2p256v1 curve
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
            ECPoint pubKeyPoint = KeyUtils.pubKeyStrToECPoint(publicKey, SM2Util.curve);
            byte[] cipherBytes = SM2Util.encrypt(plain, pubKeyPoint);
            return Numeric.toHexString(cipherBytes);
        }catch (Exception ex){
            log.error("SM2 encrypt exception ",ex);
            return null;
        }
    }

    /**
     * Decrypt data with sm2p256v1 curve
     * @param cipher
     * @param privateKey Hex format of private key. Can be 64 bytes or shorter.
     * @return
     */
    public String decrypt(String cipher, String privateKey) {
        //Args checking
        if(StringUtils.isEmpty(cipher) || StringUtils.isEmpty(privateKey)){
            log.error("Args cannot be empty");
            return null;
        }
        //Decrypt
        try{
            byte[] cipherBytes = Numeric.hexStringToByteArray(cipher);
            BigInteger privVal = new BigInteger(privateKey, 16);
            byte[] plainBytes = SM2Util.decrypt(cipherBytes, privVal);
            return new String(plainBytes);
        }
        catch (Exception ex){
            log.error("SM2 decrypt exception ",ex);
            return null;
        }
    }


}
