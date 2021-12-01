package com.webank.wedpr.crypto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author aaronchu
 * @Description
 * @data 2020/06/18
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CryptoResult {
    public String signature;
    public String publicKey;
    public String privteKey;
    public boolean result;
    public String hash;
    public String wedprErrorMessage;
    public String vrfProof;
    public boolean vrfVerifyResult;
    public String vrfPublicKey;
    public String vrfHash;
    public boolean isValidVRFPublicKey;
}