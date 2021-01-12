package com.webank.keygen.hd.bip32;

import com.webank.keygen.crypto.EccOperations;
import com.webank.keygen.crypto.HmacSha512;
import com.webank.keygen.enums.EccTypeEnums;
import com.webank.keygen.model.PkeyInfo;
import com.webank.keygen.model.PubKeyInfo;
import com.webank.keygen.utils.ExtendedKeyUtil;
import com.webank.keygen.utils.KeyUtils;
import com.webank.keysign.utils.Numeric;
import org.web3j.crypto.ECKeyPair;

import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.util.Arrays;

/**
 * @author aaronchu
 * @Description
 * @data 2020/11/18
 */
public class ExtendedPrivateKey implements ChildKeyDerivable<ExtendedPrivateKey> {
    //Computation needs context informations like pub key generation, address generation
    private EccTypeEnums eccTypeEnums;
    private EccOperations eccOperations;

    private PkeyInfo privKey;
    private PubKeyInfo publicKey;

    public ExtendedPrivateKey(PkeyInfo priv){
        this.eccTypeEnums = EccTypeEnums.getEccByName(priv.getEccName());
        this.eccOperations = new EccOperations(eccTypeEnums);

        this.privKey = priv;
        this.publicKey = this.privKey.getPublicKey();
}

    /**
     * Derive a sub private key by chaincode and child index
     * @param childIdx ExtendedPrivateKey
     * @return
     */
    @Override
    public ExtendedPrivateKey deriveChild(int childIdx) {
        //Buffer contains "pubkey(33bytes)" + "childIdx(4bytes)"
        ByteBuffer indexBuffer = ByteBuffer.allocate(37);
        if (ExtendedKeyUtil.isHardened(childIdx)) {
            indexBuffer.position(1);
            indexBuffer.put(privKey.getPrivateKey());
        } else {
            indexBuffer.put(this.eccOperations.compress(publicKey.getPublicKey()));
        }
        indexBuffer.putInt(childIdx);//Big endian!!!!
        //hash(cc, pub + childIdx)
        byte[] I = HmacSha512.INSTANCE.macHash(privKey.getChainCode(), indexBuffer.array());
        byte[] Il = Arrays.copyOfRange(I, 0, 32);
        byte[] Ir = Arrays.copyOfRange(I, 32, 64);
        //derive child key = priv + Il
        BigInteger key = new BigInteger(1, privKey.getPrivateKey());
        BigInteger Ili = new BigInteger(1, Il);
        final BigInteger childKey = key.add(Ili).mod(this.eccOperations.getN());
        //Note: this has probability lower than 1 in 2^127
        if (!this.eccOperations.verifyPrivateKey(childKey)) {
            return deriveChild(childIdx + 1);
        }

        byte[] ckey = Numeric.toBytesPadded(childKey, 32);
        ECKeyPair ecKeyPair =this.eccOperations.getKeyPair(ckey);

        PkeyInfo pkeyInfo =  KeyUtils.createPkeyInfo(ecKeyPair.getPrivateKey(), ecKeyPair.getPublicKey(),
                this.eccTypeEnums.getEccName(), Ir);
        return new ExtendedPrivateKey(pkeyInfo);
    }

    /**
     * Get corresponding public key
     * @return ExtendedPublicKey
     */
    public ExtendedPublicKey neuter(){
        return new ExtendedPublicKey(publicKey);
    }

    /**
     * Get public key of sub private key
     * @param childIdx
     * @return ExtendedPublicKey
     */
    public ExtendedPublicKey deriveChildPubkey(int childIdx){
        return this.neuter().deriveChild(childIdx);
    }

    public PkeyInfo getPkeyInfo() {
        return this.privKey;
    }
}
