package com.webank.keygen.handler;

import org.junit.Assert;
import org.junit.Test;
import org.web3j.crypto.ECKeyPair;

/**
 * @author aaronchu
 * @Description
 * @data 2020/06/29
 */
public class SM2KeyHandlerTest {

    @Test
    public void pubKeIs64Bytes() throws Exception{
        ECKeyPair ecKeyPair = SM2KeyHandler.generateSM2KeyPair();
        byte[] bytes = ecKeyPair.getPublicKey().toByteArray();
        if(bytes.length == 65 ){
            Assert.assertTrue(bytes[0] == 0);
            return;
        }
        Assert.assertEquals(64, bytes.length);
    }
}
