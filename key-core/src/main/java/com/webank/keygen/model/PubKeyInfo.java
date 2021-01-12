package com.webank.keygen.model;

import com.webank.keygen.crypto.EccOperations;
import com.webank.keygen.enums.EccTypeEnums;
import com.webank.keysign.utils.Numeric;
import lombok.*;
import org.web3j.crypto.Keys;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Objects;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PubKeyInfo implements Cloneable {
    private byte[] publicKey;
    private byte[] chaincode;
    private String eccName;

    private String address;

    public String getAddress(){
        if(address == null){
            address = doComputeAddress();
        }
        return address;
    }

    private String doComputeAddress(){
        byte[] keyBytes = this.publicKey;
        if(keyBytes.length == 65){
            keyBytes = Arrays.copyOfRange(keyBytes, 1, keyBytes.length);
        }
        return Numeric.toHexStringWithPrefix(new BigInteger(1, Keys.getAddress(keyBytes)));
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
