package com.webank.keygen.mnemonic;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.spongycastle.util.encoders.Hex;

import java.util.Arrays;
import java.util.Collection;

/**
 * @author aaronchu
 * @Description
 * @data 2020/11/20
 */
@RunWith(Parameterized.class)
public class SeedGenerationTests {
    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
                {"blame poem use price poet lava ostrich seminar time humor sail room","123","2fdc19fae076e6986e6e8832b5b7071686b00a8a3f8cf9f9c63f6b243a0ea5eebf1e9aa9e0dca90b8f88b139264fd10a78fa70a56479bb5dbe9dcab962eb0528"},
                {"deposit trick fiber spirit open pulp holiday legend present happy plug provide","123","f0eed837f5a54a220ba788d84a42e0c7429a18d67b2e606cb6c82e7dcf320770bb21529267890a47b30f63cd2a1ecf3b0e7337eaa82cec51e54b93add99706b2"},
                {"salmon lottery extra notice weapon divert plunge this south next cheese differ","123","d9d060cbdaf2054803e0d0811811b8fe128989780b054345d73299f6cbe042837824be3eb549b3a47efb9c1f9f7db3f945069ece691b244a69ab6a7c9cef387c"},
        });
    }

    private String mnemonic;
    private String passphrase;
    private String expected;

    public SeedGenerationTests(String mnemonic, String passphrase, String expected) {
        this.mnemonic = mnemonic;
        this.passphrase = passphrase;
        this.expected = expected;
    }
    @Test
    public void test(){
        byte[] seed  = new SeedGenerator().generateSeed(this.mnemonic, this.passphrase);
        String seedStr = Hex.toHexString(seed);
        Assert.assertEquals(expected, seedStr);
    }

}
