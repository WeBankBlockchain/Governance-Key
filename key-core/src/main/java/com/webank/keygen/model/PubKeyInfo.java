package com.webank.keygen.model;

import lombok.Builder;
import lombok.Data;

import java.util.Arrays;

@Data
@Builder
public class PubKeyInfo implements Cloneable {
    private byte[] publicKey;
    private byte[] chaincode;

    @Override
    public boolean equals(Object other){
        if(other ==  null) return false;
        if(!(other instanceof PubKeyInfo)){
            return false;
        }
        return Arrays.equals(publicKey, ((PubKeyInfo) other).publicKey)
                && Arrays.equals(chaincode, ((PubKeyInfo) other).chaincode);
    }

    @Override
    public int hashCode(){
        int result = 17;
        result = 31 * result + Arrays.hashCode(publicKey);
        result = 31 * result + Arrays.hashCode(chaincode);
        return result;
    }

    @Override
    public Object clone(){
        return PubKeyInfo
                .builder()
                .publicKey(publicKey)
                .chaincode(chaincode)
                .build();
    }
}
