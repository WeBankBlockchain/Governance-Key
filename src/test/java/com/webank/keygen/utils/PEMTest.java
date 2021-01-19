package com.webank.keygen.utils;

import com.webank.keygen.encrypt.PemEncrypt;
import com.webank.keygen.enums.EccTypeEnums;
import org.junit.Assert;
import org.junit.Test;
import org.web3j.utils.Numeric;

public class PEMTest {

    @Test
    public  void ensureSameWithConsole() throws Exception{
        String privateKey = "3500db68433dda968ef7bfe5a0ed6926b8e85aabcd2caa54f8327ca07ac73526";
        String encFromPeyGen = PemEncrypt.encryptPrivateKey(Numeric.hexStringToByteArray(privateKey), EccTypeEnums.SECP256K1);

        String fromConsole = "-----BEGIN PRIVATE KEY-----\r\n" +
                "MIGEAgEAMBAGByqGSM49AgEGBSuBBAAKBG0wawIBAQQgNQDbaEM92paO97/loO1p\r\n" +
                "JrjoWqvNLKpU+DJ8oHrHNSahRANCAAQQlKqfga9ut+Bc0EOG2F/cMItXwPYzMAVG\r\n" +
                "ge8SqN6v0T6DQ4aZUIQzVvcm1m7d5QPjUgxlg2Q09BBahG9dLYm0\r\n" +
                "-----END PRIVATE KEY-----\r\n";
        Assert.assertTrue(encFromPeyGen.equals(fromConsole));

        String recovered = Numeric.toHexStringNoPrefix(PemEncrypt.decryptPrivateKey(encFromPeyGen));
        Assert.assertTrue(recovered.equals(privateKey));
    }

    @Test
    public  void t2() throws Exception{
        String privateKey = "b48898407fcd3fbbee55055b24f7e51a4b8dc1bca1298596fd420dc8db6d5182";
        String encFromPeyGen = PemEncrypt.encryptPrivateKey(Numeric.hexStringToByteArray(privateKey), EccTypeEnums.SECP256K1);
        System.out.println(encFromPeyGen);
    }

}
