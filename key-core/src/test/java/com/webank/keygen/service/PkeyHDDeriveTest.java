package com.webank.keygen.service;

import com.webank.keygen.enums.EccTypeEnums;
import com.webank.keygen.hd.bip32.ExtendedPrivateKey;
import com.webank.keygen.model.PkeyInfo;
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
                .purpose44().coinType(2)
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
        Assert.assertTrue(Objects.equals("2ebcc1a45ebd60ef12c55eb7cc4d08d09cfd433690d6e9c4d92bf67f573ec322", Numeric.toHexString(pkeyInfo1.getPrivateKey())));
        Assert.assertTrue(Objects.equals("0x1a2e7b795a54e70fa0253b406de769785d2f72f0", pkeyInfo1.getAddress()));
        Assert.assertTrue(Objects.equals("13122af63f9c002eecfaa187cd09887cd89ed151d99065628072291ae710b47f", Numeric.toHexString(pkeyInfo1.getChainCode())));
        Assert.assertTrue(Objects.equals("sm2p256v1", pkeyInfo1.getEccName()));
    }

    @Test
    public void testDerivePath2() throws Exception{
        String mnemonic = "medal shed task apart range accident ride matrix fire citizen motion ridge";
        PkeyInfo pkeyInfo = mnemonicService.generatePrivateKeyByMnemonic(mnemonic, "123", EccTypeEnums.SM2P256V1);

        ExtendedPrivateKey extPrivateKey = hdService.derivePath(pkeyInfo.getPrivateKey(), pkeyInfo.getChainCode(), EccTypeEnums.SM2P256V1, "m/44'/2'/3'/4/5");
        PkeyInfo pkeyInfo1 = extPrivateKey.getPkeyInfo();
        Assert.assertTrue(Objects.equals("2ebcc1a45ebd60ef12c55eb7cc4d08d09cfd433690d6e9c4d92bf67f573ec322", Numeric.toHexString(pkeyInfo1.getPrivateKey())));
        Assert.assertTrue(Objects.equals("0x1a2e7b795a54e70fa0253b406de769785d2f72f0", pkeyInfo1.getAddress()));
        Assert.assertTrue(Objects.equals("13122af63f9c002eecfaa187cd09887cd89ed151d99065628072291ae710b47f", Numeric.toHexString(pkeyInfo1.getChainCode())));
        Assert.assertTrue(Objects.equals("sm2p256v1", pkeyInfo1.getEccName()));
    }

    @Test
    public void testDeriveChild() throws Exception {
        String mnemonic = "medal shed task apart range accident ride matrix fire citizen motion ridge";
        PkeyInfo pkeyInfo = mnemonicService.generatePrivateKeyByMnemonic(mnemonic, "123", EccTypeEnums.SM2P256V1);

        ExtendedPrivateKey extPrivateKey = hdService.deriveChild(pkeyInfo, 10);
        PkeyInfo pkeyInfo1 = extPrivateKey.getPkeyInfo();

        Assert.assertTrue(Objects.equals("0c00f7db0f4bc8a6999f50fae7b1412898bcf7792e293823b3b72bcc8be6330b", Numeric.toHexString(pkeyInfo1.getPrivateKey())));
        Assert.assertTrue(Objects.equals("884bb0da73cfe4d069fd929120c3cd18071bdc07f7becf3725ad930f026f7d04", Numeric.toHexString(pkeyInfo1.getChainCode())));

        Assert.assertTrue(Objects.equals("0xd3fab4812d1b4084a4f1f03fc4b923d54bf50d67", pkeyInfo1.getAddress()));
        Assert.assertTrue(Objects.equals("sm2p256v1", pkeyInfo1.getEccName()));
    }

    @Test
    public void testDeriveChild2() throws Exception {
        String mnemonic = "medal shed task apart range accident ride matrix fire citizen motion ridge";
        PkeyInfo pkeyInfo = mnemonicService.generatePrivateKeyByMnemonic(mnemonic, "123", EccTypeEnums.SM2P256V1);

        ExtendedPrivateKey extPrivateKey = hdService.deriveChild(pkeyInfo.getPrivateKey(),pkeyInfo.getChainCode(), EccTypeEnums.SM2P256V1, 10);
        PkeyInfo pkeyInfo1 = extPrivateKey.getPkeyInfo();

        Assert.assertTrue(Objects.equals("0c00f7db0f4bc8a6999f50fae7b1412898bcf7792e293823b3b72bcc8be6330b", Numeric.toHexString(pkeyInfo1.getPrivateKey())));
        Assert.assertTrue(Objects.equals("884bb0da73cfe4d069fd929120c3cd18071bdc07f7becf3725ad930f026f7d04", Numeric.toHexString(pkeyInfo1.getChainCode())));

        Assert.assertTrue(Objects.equals("0xd3fab4812d1b4084a4f1f03fc4b923d54bf50d67", pkeyInfo1.getAddress()));
        Assert.assertTrue(Objects.equals("sm2p256v1", pkeyInfo1.getEccName()));
    }


}
