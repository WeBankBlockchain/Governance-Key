package com.webank.keygen.hd.bip32;

import com.webank.keygen.crypto.EccOperations;
import com.webank.keygen.crypto.HmacSha512;
import com.webank.keygen.enums.EccTypeEnums;
import com.webank.keygen.model.PkeyInfo;
import com.webank.keygen.model.PubKeyInfo;
import com.webank.keygen.utils.ExtendedKeyUtil;

import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.util.Arrays;

/**
 * @author aaronchu
 * @Description
 * @data 2020/11/18
 */
public class ExtendedPublicKey implements ChildKeyDerivable<ExtendedPublicKey>{

    private EccTypeEnums eccTypeEnums;
    private EccOperations eccOperations;

    private PubKeyInfo publicKey;


    public ExtendedPublicKey(PubKeyInfo publicKey){
        this.eccTypeEnums = EccTypeEnums.getEccByName(publicKey.getEccName());
        this.eccOperations = new EccOperations(eccTypeEnums);
        this.publicKey = publicKey;

    }

    //pub + G*hash(cc+i+pub) = G*priv + G*hash(cc+i+pub) = G(priv + hash(cc+i+pub)) = G(childPriv)

    /**
     * Derive a child key by chaincode and child index
     * @param childIdx
     * @return ExtendedPublicKey
     */
    @Override
    public ExtendedPublicKey deriveChild(int childIdx) {
        //Buffer contains "pubkey(33bytes)" + "childIdx(4bytes)"
        ByteBuffer indexBuffer = ByteBuffer.allocate(37);
        if (ExtendedKeyUtil.isHardened(childIdx)) {
            throw new RuntimeException("Hardened child cannot be derived for public key");
        } else {
            indexBuffer.put(this.eccOperations.compress(publicKey.getPublicKey()));
        }
        indexBuffer.putInt(childIdx);//Big endian!!!!
        //hash(cc, pub + childIdx)
        byte[] I = HmacSha512.INSTANCE.macHash(publicKey.getChaincode(), indexBuffer.array());
        byte[] Il = Arrays.copyOfRange(I, 0, 32);
        byte[] Ir = Arrays.copyOfRange(I, 32, 64);
        //derive child key = pub + Il*G
        BigInteger Ili = new BigInteger(1, Il);
        byte[] childPoint = eccOperations.pubkeyAdd(
                this.publicKey.getPublicKey(),
                eccOperations.generatePublicKeys(Il, false),//Il * G
                true,
                false
                );

        if (Ili.compareTo(eccOperations.getN()) >= 0 || childPoint == null) {
            return deriveChild(childIdx + 1);
        }
        PubKeyInfo pkeyInfo = PubKeyInfo.builder()
                .publicKey(childPoint)
                .chaincode(Ir)
                .eccName(this.eccTypeEnums.getEccName())
                .build();
        return new ExtendedPublicKey(pkeyInfo);
    }

    public PubKeyInfo getPubInfo() {
        return this.publicKey;
    }

    @Override
    public boolean equals(Object other){
        if(other == null) return false;
        if(!(other instanceof ExtendedPublicKey)){
            return false;
        }
        PubKeyInfo otherKey = ((ExtendedPublicKey) other).publicKey;
        return this.publicKey.equals(otherKey);
    }

    @Override
    public int hashCode(){
        int result = 17;
        return result * 31 + this.publicKey.hashCode();
    }
}
