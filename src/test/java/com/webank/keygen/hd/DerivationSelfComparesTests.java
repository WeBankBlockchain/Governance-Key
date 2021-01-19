package com.webank.keygen.hd;

import com.webank.keygen.enums.EccTypeEnums;
import com.webank.keygen.hd.bip32.ExtendedPrivateKey;
import com.webank.keygen.hd.bip32.ExtendedPublicKey;
import com.webank.keygen.model.PkeyInfo;
import com.webank.keygen.service.PkeyByMnemonicService;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author aaronchu
 * @Description
 * @data 2020/11/20
 */
public class DerivationSelfComparesTests {

    private PkeyByMnemonicService facade = new PkeyByMnemonicService();

    @Test
    public void test() throws Exception{
        for(int i=0;i<100;i++){
            String mnemonic = facade.createMnemonic();
            PkeyInfo pkeyInfo = facade.generatePrivateKeyByMnemonic(mnemonic, "123", EccTypeEnums.SECP256K1);
            ExtendedPrivateKey root = new ExtendedPrivateKey(pkeyInfo);
            ExtendedPrivateKey subPriv = root.deriveChild(i);
            ExtendedPublicKey subPub1 = subPriv.neuter();

            ExtendedPublicKey rootPub = root.neuter();
            ExtendedPublicKey subPub2 = rootPub.deriveChild(i);

            Assert.assertEquals(subPub1, subPub2);
        }
    }

}
