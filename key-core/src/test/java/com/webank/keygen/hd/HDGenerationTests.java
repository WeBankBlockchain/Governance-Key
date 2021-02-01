package com.webank.keygen.hd;

import com.webank.keygen.crypto.EccOperations;
import com.webank.keygen.enums.EccTypeEnums;
import com.webank.keygen.hd.bip32.ExtendedPrivateKey;
import com.webank.keygen.hd.bip32.ExtendedPublicKey;
import com.webank.keygen.hd.bip32.MasterKeyGenerator;
import com.webank.keygen.model.PkeyInfo;
import com.webank.keygen.model.PubKeyInfo;
import lombok.AllArgsConstructor;
import org.bouncycastle.util.encoders.Hex;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;

/**
 * @author aaronchu
 * @Description
 * @data 2020/11/20
 */
@RunWith(Parameterized.class)
@AllArgsConstructor
public class HDGenerationTests {
    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
                {"41e816aabbda4ad6d1d29c049e5f65e180e426bc97fd393bcc2b5cf2c5cc274cbc3b5e3c5109632599c7d73b6463ea69bfa88440aea766cd4e52a59666c17506",0,"44609a875e2821d8a18ab540f4aebd618468ebf17a1b32d50ac3dd2ecfd83994","02e3f36c68522bef5a3422333f1b59d72129588434fc8643802f4b3bf28e34a26b",},
                {"35b5b70e8975628f7fc096ead9176c9a17171168ab1758701e98c936e25d2b70e505a324f25164500d12ac0be4b7b89b462f7b50d700c3300da4531313ed8eb0",1,"6f107545a8d089a5880caeab6b1b1457da61c21bef222db1845a90c857a8c9b6","02880409ba77a1a07b177e1774d00fc0a0176fbb91839f459eae4469a9576ea9e7"},
                {"1f78e18c976d47c0598998598bf7a9d087d0162a4ec56e4d3088ef22f9e8b4f435260352cf66c4a78c8a57e871c5cf76e91efc09e2b5b8380834888d348b0cb8",2,"a6006e4f75ad9ee3cdb65cbdd3944fb15877169330aaee2465f0782d08699180","020d7c272201e4aedc100fa440696f39e34f64e415ae7bc4fd62c40c69a79e57c2"},
        });
    }

    private String seed;
    private int childIdx;
    private String subPriv;
    private String subPub;

    @Test
    public void test() throws Exception{
        byte[] keyChaincode = new MasterKeyGenerator().toMasterKey(Hex.decode(seed), MasterKeyGenerator.Bitcoin_Seed);;
        PkeyInfo rootKey = PkeyInfo.builder().privateKey(Arrays.copyOfRange(keyChaincode, 0, 32))
                .chainCode(Arrays.copyOfRange(keyChaincode, 32, 64))
                .eccName(EccTypeEnums.SECP256K1.getEccName())
               .build();
        ExtendedPrivateKey root = new ExtendedPrivateKey(rootKey);
        ExtendedPrivateKey cpriv = root.deriveChild(childIdx);
        ExtendedPublicKey cpub = root.deriveChildPubkey(childIdx);

        PkeyInfo privKey = cpriv.getPkeyInfo();
        PubKeyInfo pubKeyInfo = cpub.getPubInfo();
        EccOperations eccOperations = new EccOperations(EccTypeEnums.SECP256K1);
        Assert.assertEquals(subPriv, Hex.toHexString(privKey.getPrivateKey()));
        Assert.assertEquals(subPub, Hex.toHexString(eccOperations.compress(pubKeyInfo.getPublicKey())));

    }
}
