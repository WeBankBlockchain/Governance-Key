package com.webank.keygen.crypto;

import com.webank.keygen.enums.EccTypeEnums;
import com.webank.keygen.exception.KeyGenException;
import com.webank.keygen.handler.SM2KeyHandler;
import org.bouncycastle.asn1.x9.X9ECParameters;
import org.bouncycastle.crypto.ec.CustomNamedCurves;
import org.bouncycastle.math.ec.ECPoint;
import org.web3j.crypto.ECKeyPair;

import java.math.BigInteger;

public class EccOperations {

    private EccTypeEnums eccTypeEnums;

    private final X9ECParameters CURVE;

    public EccOperations(EccTypeEnums eccTypeEnums){
        this.eccTypeEnums = eccTypeEnums;
        this.CURVE =  CustomNamedCurves.getByName(eccTypeEnums.getEccName());
    }


    public byte[] generatePublicKeys(byte[] privateKey, boolean compressed) {
        //1. ECPoint pub = priv * G
        BigInteger k = new BigInteger(1, privateKey);
        ECPoint pubPoint = CURVE.getG().multiply(k);
        //2. serialize
        return pubPoint.getEncoded(compressed);
    }

    public BigInteger getN() {
        return CURVE.getN();
    }


    public boolean verifyPrivateKey(BigInteger ki) {
        return ki.compareTo(getN()) < 0 && ki.compareTo(BigInteger.ZERO) > 0;
    }

    public byte[] pubkeyAdd(byte[] pub1, byte[] pub2, boolean verify, boolean compressed){
        ECPoint point1 = CURVE.getCurve().decodePoint(pub1);
        ECPoint point2 = CURVE.getCurve().decodePoint(pub2);

        ECPoint sum = point1.add(point2);
        if(verify && sum.isInfinity()){
            return null;
        }
        return sum.getEncoded(compressed);
    }

    public byte[] decompress(byte[] compressedPoint) {
        ECPoint ecPoint = CURVE.getCurve().decodePoint(compressedPoint);
        return ecPoint.getEncoded(false);
    }

    public byte[] compress(byte[] compressedPoint) {
        ECPoint ecPoint = CURVE.getCurve().decodePoint(compressedPoint);
        return ecPoint.getEncoded(true);
    }

    public ECKeyPair getKeyPair(byte[] privateKey) {
        ECKeyPair ecKeyPair = null;
        if(this.eccTypeEnums == EccTypeEnums.SECP256K1){
            ecKeyPair = ECKeyPair.create(privateKey);
        }else{
            ecKeyPair = SM2KeyHandler.create(privateKey);
        }
        return ecKeyPair;
    }
}
