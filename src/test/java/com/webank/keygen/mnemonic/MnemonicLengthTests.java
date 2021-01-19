package com.webank.keygen.mnemonic;

import com.webank.keygen.service.PkeyByMnemonicService;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author aaronchu
 * @Description
 * @data 2020/11/20
 */
public class MnemonicLengthTests {

    @Test
    public void testEmptyEntrophy(){
        //Make sure MS = (ENT + CS)/11 = (ENT + CS/32)/11
        String mnemonic = new PkeyByMnemonicService().createMnemonic();
        int len = mnemonic.split(" ").length;
        Assert.assertEquals(12, len);
    }

}
