package hd;

import com.webank.keygen.hd.bip32.MasterKeyGenerator;
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
public class MasterKeyGenerationTests {

    private String seed;
    private String expectedPrivateKey;

    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
                {"5efab9572ffe5e868d970344f817f5aea0196d43c7cc36d9301b4be361c9fecc7a80213bf608464cab75a8777e26b1eaa3c8be317271da9c8d49d8af22fb12be","3ab89c0ac538a7f3a028166a8b916f90252da9ec1290ae98e8d0f87b610902e2"},
                {"2956fddfef6dfd5230f2f9b353091eba64a0165e1bf4ee26523c6d47cbf5b9b5b75b2698ab52c252a13af8aa855d6fcee3301e864fdbb79eaebedb4091373549","76852699861e92df4fd11233c4aa3ca68990f4165f8306ca2c6f2a3bab7e7e42"},
                {"f8df57ca3e98f30066bf4e89ac9f21e8fe4a9ab465aab5bb670710e5f95bb93c1aeee6a93e6a5f3c5dc5b3a4daee5c1d85e3b6b29f0a777cbb8c3f1ca431d2c2","cd2ad6bc90b0e9bd03d3dbc48a6074e45a0311474293389ba3e711d1d1a20fb9"},
                {"072d92bb618c185b49b18d3da60d6d39d784b21e3276d783e71482f7fff38a5b40c01b708f9bc4c7c5d35849758cccc50573f87735ec5e1d7d130dd0555b3b7d","d9ffad0d1e41db6369f53e3b2990231c72a39bfee8e810b00d2d7238f3fa56eb"},
                {"fc4cb9ad44525c7d5c2c72db1b972ad6c2417528fa2f552fe715a9ff98b7cffec16b5a258fec4eb4191bf716830d173f6a68d9f94a84b668c98c6c382c32f71d","8683655e77720b178148d27afd39843831d82b8466368210f589913b204ac390"},
        });
    }

    public MasterKeyGenerationTests(String seed, String expectedPrivateKey){
        this.seed = seed;
        this.expectedPrivateKey = expectedPrivateKey;
    }

    @Test
    public void test() throws Exception{
        byte[] keyWithChaincode = new MasterKeyGenerator().toMasterKey(Hex.decode(this.seed),MasterKeyGenerator.Bitcoin_Seed);

        Assert.assertEquals(Hex.toHexString(Arrays.copyOfRange(keyWithChaincode, 0, 32)), expectedPrivateKey);
    }

}
