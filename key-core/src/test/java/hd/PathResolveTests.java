package hd;

import com.webank.keygen.hd.bip32.ExtendedPrivateKey;
import com.webank.keygen.hd.bip32.MasterKeyGenerator;
import com.webank.keygen.hd.bip44.Purpose44PathBuilder;
import com.webank.keygen.hd.bip44.path.Purpose44Path;
import com.webank.keygen.model.PkeyInfo;
import com.webank.keysign.utils.Numeric;
import org.bouncycastle.util.encoders.Hex;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;

/**
 * @author aaronchu
 * @Description
 * @data 2020/11/20
 */
public class PathResolveTests {


    @Test
    public void testBip44() throws Exception{
        Purpose44Path path = Purpose44PathBuilder.builder()
                .m()
                .purpose44()
                .coinType(1)
                .account(2)
                .change(30)
                .addressIndex(66)
                .build();
        String pathStr = path.toString();
        Assert.assertEquals("m/44'/1'/2'/30/66", pathStr);


        byte[] seed = Hex.decode("0dbfee9c2ff8c934df919e0de37e8cdf0b2b2aa26addf88bcf3838bb68e2eb96ffdb4984e595390385205c15dbb84c23da75a700455939cc2715fdfecf26f8a1");
        byte[] keyWithChaincode = new MasterKeyGenerator().toMasterKey(seed, MasterKeyGenerator.Bitcoin_Seed);
        PkeyInfo pkeyInfo = PkeyInfo.builder().privateKey(Arrays.copyOfRange(keyWithChaincode, 0,32))
                .chainCode(Numeric.toHexString(Arrays.copyOfRange(keyWithChaincode, 32, 64)))
                .build();
        ExtendedPrivateKey root = new ExtendedPrivateKey(pkeyInfo);
        ExtendedPrivateKey derived= path.deriveKey(root);
        Assert.assertEquals("c599b12a2bc6d40fa78fdb211ce1680e93b0c58554302fe729ba5bcb948b6af8",Hex.toHexString(derived.getPkeyInfo().getPrivateKey()));
    }
}
