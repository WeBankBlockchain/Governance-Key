package com.webank.keygen.model;

import com.webank.keygen.crypto.EccOperations;
import com.webank.keygen.enums.EccTypeEnums;
import com.webank.keygen.utils.KeyUtils;
import com.webank.keysign.utils.Numeric;
import lombok.*;
import org.fisco.bcos.sdk.crypto.keypair.CryptoKeyPair;
import org.web3j.crypto.Keys;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Objects;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class PubKeyInfo implements Cloneable {
    private byte[] publicKey;
    private byte[] chaincode;
    private String eccName;


    public String getAddress(){
        EccTypeEnums eccTypeEnums = EccTypeEnums.getEccByName(eccName);
        CryptoKeyPair cryptoKeyPair = KeyUtils.getCryptKeyPair(eccTypeEnums);
        return Numeric.toHexString(cryptoKeyPair.getAddress(Arrays.copyOfRange(publicKey,1,publicKey.length)));
    }

    @Override
    public boolean equals(Object other){
        if(other ==  null) return false;
        if(!(other instanceof PubKeyInfo)){
            return false;
        }
        return Arrays.equals(publicKey, ((PubKeyInfo) other).publicKey)
                && Arrays.equals(chaincode, ((PubKeyInfo) other).chaincode)
                && Objects.equals(eccName, ((PubKeyInfo) other).eccName);
    }

    @Override
    public int hashCode(){
        int result = 17;
        result = 31 * result + Arrays.hashCode(publicKey);
        result = 31 * result + Arrays.hashCode(chaincode);
        result = 31 * result + eccName.hashCode();

        return result;
    }

    @Override
    public Object clone(){
        return PubKeyInfo
                .builder()
                .publicKey(publicKey)
                .chaincode(chaincode)
                .eccName(this.eccName)
                .build();
    }
}
