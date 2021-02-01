package com.webank.keymgr.service;

import com.webank.keygen.encrypt.PemEncrypt;
import com.webank.keygen.enums.EccTypeEnums;
import org.junit.Test;
import org.web3j.utils.Numeric;

public class OtherTest {

    @Test
    public void go() throws Exception{
        String val = PemEncrypt.encryptPrivateKey(Numeric.hexStringToByteArray("b48898407fcd3fbbee55055b24f7e51a4b8dc1bca1298596fd420dc8db6d5182"),
                EccTypeEnums.SECP256K1);

        System.out.println(val);
        String pem = "-----BEGIN PRIVATE KEY-----\n" +
                "MIGEAgEAMBAGByqGSM49AgEGBSuBBAAKBG0wawIBAQQgNQDbaEM92paO97/loO1p\n" +
                "JrjoWqvNLKpU+DJ8oHrHNSahRANCAAQQlKqfga9ut+Bc0EOG2F/cMItXwPYzMAVG\n" +
                "ge8SqN6v0T6DQ4aZUIQzVvcm1m7d5QPjUgxlg2Q09BBahG9dLYm0\n" +
                "-----END PRIVATE KEY-----";

        byte[] bytes = PemEncrypt.decryptPrivateKey(pem);
        System.out.println(Numeric.toHexStringNoPrefix(bytes));
        //System.out.println("b48898407fcd3fbbee55055b24f7e51a4b8dc1bca1298596fd420dc8db6d5182".length());
    }
}
