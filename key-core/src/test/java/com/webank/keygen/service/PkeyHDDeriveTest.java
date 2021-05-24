package com.webank.keygen.service;

import com.webank.keygen.enums.EccTypeEnums;
import com.webank.keygen.hd.bip32.ExtendedPrivateKey;
import com.webank.keygen.model.PkeyInfo;
import com.webank.keygen.utils.KeyUtils;
import com.webank.keysign.utils.Numeric;
import org.junit.Assert;
import org.junit.Test;

import java.util.Objects;

/**
 * @author aaronchu
 * @Description
 * @data 2020/12/24
 */
public class PkeyHDDeriveTest {

    private PkeyHDDeriveService hdService = new PkeyHDDeriveService();
    private PkeyByMnemonicService mnemonicService = new PkeyByMnemonicService();

    @Test
    public void testBuildBip44Path(){
        String path = hdService.getPurpose44PathBuilder().m()
                .purpose44().sceneType(2)
                .account(3)
                .change(4)
                .addressIndex(5)
                .build().toString();
        Assert.assertTrue(Objects.equals("m/44'/2'/3'/4/5", path));
    }

    @Test
    public void testBuildExtendedPrivateKey() throws Exception{
        String mnemonic = mnemonicService.createMnemonic();
        PkeyInfo pkeyInfo = mnemonicService.generatePrivateKeyByMnemonic(mnemonic, "123", EccTypeEnums.SECP256K1);

        ExtendedPrivateKey root = hdService.buildExtendedPrivateKey(pkeyInfo);
        PkeyInfo pkeyInfo1 = root.getPkeyInfo();
        Assert.assertEquals(pkeyInfo, pkeyInfo1);

    }
    @Test
    public void testDerivePath() throws Exception{
        String mnemonic = "medal shed task apart range accident ride matrix fire citizen motion ridge";
        PkeyInfo pkeyInfo = mnemonicService.generatePrivateKeyByMnemonic(mnemonic, "123", EccTypeEnums.SM2P256V1);

        ExtendedPrivateKey extPrivateKey = hdService.derivePath(pkeyInfo, "m/44'/2'/3'/4/5");
        PkeyInfo pkeyInfo1 = extPrivateKey.getPkeyInfo();
        String address =  pkeyInfo1.getAddress();
        String pkey1Str = Numeric.toHexString(pkeyInfo1.getPrivateKey());
        String pkey1Cc = Numeric.toHexString(pkeyInfo1.getChainCode());
        Assert.assertTrue(Objects.equals("cd2e0330c22f7d8d38e22ad8df4d15824a7ba0ef7150f4dd777bf036fde64eed", pkey1Str));
        Assert.assertTrue(Objects.equals("74a8a2f58bb1e4c7502ab9ebf9aec90a062e08be8dadc9741d75902bb50aae4a", pkey1Cc));
        Assert.assertTrue(KeyUtils.isAddressEquals("76bc156f9188b09d549117af9391ce9947d4f45b",address));
        Assert.assertTrue(Objects.equals("sm2p256v1", pkeyInfo1.getEccName()));
    }

    @Test
    public void testDerivePath2() throws Exception{
        String mnemonic = "medal shed task apart range accident ride matrix fire citizen motion ridge";
        PkeyInfo pkeyInfo = mnemonicService.generatePrivateKeyByMnemonic(mnemonic, "123", EccTypeEnums.SM2P256V1);

        ExtendedPrivateKey extPrivateKey = hdService.derivePath(pkeyInfo, "m/44'/2'/3'/4/5");
        PkeyInfo pkeyInfo1 = extPrivateKey.getPkeyInfo();
        String address =  pkeyInfo1.getAddress();
        String pkey1Str = Numeric.toHexString(pkeyInfo1.getPrivateKey());
        String pkey1Cc = Numeric.toHexString(pkeyInfo1.getChainCode());
        Assert.assertTrue(Objects.equals("cd2e0330c22f7d8d38e22ad8df4d15824a7ba0ef7150f4dd777bf036fde64eed",pkey1Str));
        Assert.assertTrue(Objects.equals("74a8a2f58bb1e4c7502ab9ebf9aec90a062e08be8dadc9741d75902bb50aae4a", pkey1Cc));
        Assert.assertTrue(KeyUtils.isAddressEquals("76bc156f9188b09d549117af9391ce9947d4f45b", address));
        Assert.assertTrue(Objects.equals("sm2p256v1", pkeyInfo1.getEccName()));
    }

    @Test
    public void testDeriveChild() throws Exception {
        String mnemonic = "medal shed task apart range accident ride matrix fire citizen motion ridge";
        PkeyInfo pkeyInfo = mnemonicService.generatePrivateKeyByMnemonic(mnemonic, "123", EccTypeEnums.SM2P256V1);

        ExtendedPrivateKey extPrivateKey = hdService.deriveChild(pkeyInfo, 10);
        PkeyInfo pkeyInfo1 = extPrivateKey.getPkeyInfo();
        String pkey1Str = Numeric.toHexString(pkeyInfo1.getPrivateKey());
        String pkey1Cc = Numeric.toHexString(pkeyInfo1.getChainCode());
        String addr = pkeyInfo1.getAddress();
        Assert.assertTrue(Objects.equals("4d251a16d57a02cfff7dc256d012a7ddafc85b52fcbd67a9b2f1897ed2c55708",pkey1Str));
        Assert.assertTrue(Objects.equals("982017c5d4cc0626050ced4f8f19b47885cca6bc2510a602277d7eaccb804d5d", pkey1Cc));
        Assert.assertTrue(KeyUtils.isAddressEquals("e2014c6d8f68812c074df292b26d17a50de01040", addr));
        Assert.assertTrue(Objects.equals("sm2p256v1", pkeyInfo1.getEccName()));
    }

}
