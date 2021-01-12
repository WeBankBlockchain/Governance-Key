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
        Assert.assertTrue(Objects.equals("1a365843abffad89104d9d07a7752f92908962efdf743ad6c60ef4e7ea5ea7e7", Numeric.toHexString(pkeyInfo1.getPrivateKey())));
        Assert.assertTrue(Objects.equals("e3a4a7f8ed6cb8f8b9b17c42b5769ccc81edf52d8c72775b8dd1c21ed1a6708d", Numeric.toHexString(pkeyInfo1.getChainCode())));
        Assert.assertTrue(Objects.equals("0xb432446eab1b40f12410ee50e6865a619b858dbc", pkeyInfo1.getAddress()));
        Assert.assertTrue(Objects.equals("sm2p256v1", pkeyInfo1.getEccName()));
    }

    @Test
    public void testDerivePath2() throws Exception{
        String mnemonic = "medal shed task apart range accident ride matrix fire citizen motion ridge";
        PkeyInfo pkeyInfo = mnemonicService.generatePrivateKeyByMnemonic(mnemonic, "123", EccTypeEnums.SM2P256V1);

        ExtendedPrivateKey extPrivateKey = hdService.derivePath(pkeyInfo.getPrivateKey(), pkeyInfo.getChainCode(), EccTypeEnums.SM2P256V1, "m/44'/2'/3'/4/5");
        PkeyInfo pkeyInfo1 = extPrivateKey.getPkeyInfo();

        Assert.assertTrue(Objects.equals("1a365843abffad89104d9d07a7752f92908962efdf743ad6c60ef4e7ea5ea7e7", Numeric.toHexString(pkeyInfo1.getPrivateKey())));
        Assert.assertTrue(Objects.equals("e3a4a7f8ed6cb8f8b9b17c42b5769ccc81edf52d8c72775b8dd1c21ed1a6708d", Numeric.toHexString(pkeyInfo1.getChainCode())));
        Assert.assertTrue(Objects.equals("0xb432446eab1b40f12410ee50e6865a619b858dbc", pkeyInfo1.getAddress()));
        Assert.assertTrue(Objects.equals("sm2p256v1", pkeyInfo1.getEccName()));
    }

    @Test
    public void testDeriveChild() throws Exception {
        String mnemonic = "medal shed task apart range accident ride matrix fire citizen motion ridge";
        PkeyInfo pkeyInfo = mnemonicService.generatePrivateKeyByMnemonic(mnemonic, "123", EccTypeEnums.SM2P256V1);

        ExtendedPrivateKey extPrivateKey = hdService.deriveChild(pkeyInfo, 10);
        PkeyInfo pkeyInfo1 = extPrivateKey.getPkeyInfo();

        Assert.assertTrue(Objects.equals("1a4f455cbec1608c948b3eee15a73e45ceb8fffe75b770df0beb5e83345cbf6a", Numeric.toHexString(pkeyInfo1.getPrivateKey())));
        Assert.assertTrue(Objects.equals("ebe916ee66bac5af9ae2ed5d7d749d08b079ba5327fe8a9ba2fc1bdc10e1646c", Numeric.toHexString(pkeyInfo1.getChainCode())));
        String addr = pkeyInfo1.getAddress();
        Assert.assertTrue(Objects.equals("0x438dc6fe9c1ab388da478924b8cee4f09e72b857", addr));
        Assert.assertTrue(Objects.equals("sm2p256v1", pkeyInfo1.getEccName()));
    }

    @Test
    public void testDeriveChild2() throws Exception {
        String mnemonic = "medal shed task apart range accident ride matrix fire citizen motion ridge";
        PkeyInfo pkeyInfo = mnemonicService.generatePrivateKeyByMnemonic(mnemonic, "123", EccTypeEnums.SM2P256V1);

        ExtendedPrivateKey extPrivateKey = hdService.deriveChild(pkeyInfo.getPrivateKey(),pkeyInfo.getChainCode(), EccTypeEnums.SM2P256V1, 10);
        PkeyInfo pkeyInfo1 = extPrivateKey.getPkeyInfo();

        Assert.assertTrue(Objects.equals("1a4f455cbec1608c948b3eee15a73e45ceb8fffe75b770df0beb5e83345cbf6a", Numeric.toHexString(pkeyInfo1.getPrivateKey())));
        Assert.assertTrue(Objects.equals("ebe916ee66bac5af9ae2ed5d7d749d08b079ba5327fe8a9ba2fc1bdc10e1646c", Numeric.toHexString(pkeyInfo1.getChainCode())));
        Assert.assertTrue(Objects.equals("0x438dc6fe9c1ab388da478924b8cee4f09e72b857", pkeyInfo1.getAddress()));
        Assert.assertTrue(Objects.equals("sm2p256v1", pkeyInfo1.getEccName()));
    }

}
