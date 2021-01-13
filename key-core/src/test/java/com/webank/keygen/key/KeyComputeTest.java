package com.webank.keygen.key;

import com.webank.keygen.model.PkeyInfo;
import com.webank.keygen.service.PkeyEncryptService;
import com.webank.keygen.utils.KeyUtils;
import com.webank.keysign.utils.Numeric;
import org.fisco.bcos.sdk.crypto.keypair.CryptoKeyPair;
import org.fisco.bcos.sdk.crypto.keypair.ECDSAKeyPair;
import org.fisco.bcos.sdk.crypto.keypair.SM2KeyPair;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;

/**
 * @author aaronchu
 * @Description
 * @data 2021/01/13
 */
public class KeyComputeTest {

    @Test
    public void test() throws Exception{
        CryptoKeyPair cryptoKeyPair = new ECDSAKeyPair().generateKeyPair();
        PkeyInfo pkeyInfo = PkeyInfo
                .fromCryptoKeypair(cryptoKeyPair);


        Assert.assertTrue(KeyUtils.isAddressEquals(pkeyInfo.getAddress(), cryptoKeyPair.getAddress()));
        Assert.assertTrue(Arrays.equals(pkeyInfo.getPublicKey().getPublicKey(), Numeric.hexStringToByteArray(cryptoKeyPair.getHexPublicKey())));
    }
    @Test
    public void gmTest() throws Exception{
        CryptoKeyPair cryptoKeyPair = new SM2KeyPair().generateKeyPair();
        PkeyInfo pkeyInfo = PkeyInfo
                .fromCryptoKeypair(cryptoKeyPair);


        Assert.assertTrue(KeyUtils.isAddressEquals(pkeyInfo.getAddress(), cryptoKeyPair.getAddress()));
        Assert.assertTrue(Arrays.equals(pkeyInfo.getPublicKey().getPublicKey(), Numeric.hexStringToByteArray(cryptoKeyPair.getHexPublicKey())));

    }
    
}
