/*
 * Copyright 2014-2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.webank.keygen.model;

import com.webank.keygen.crypto.EccOperations;
import com.webank.keygen.enums.EccTypeEnums;
import com.webank.keygen.utils.KeyPresenter;
import com.webank.keygen.utils.KeyUtils;
import com.webank.keysign.utils.Numeric;
import com.webank.keysign.utils.RandomUtils;
import lombok.*;
import org.fisco.bcos.sdk.crypto.keypair.CryptoKeyPair;
import org.fisco.bcos.sdk.crypto.keypair.ECDSAKeyPair;
import org.fisco.bcos.sdk.crypto.keypair.SM2KeyPair;

import java.math.BigInteger;

/**
 * Self-described private key
 *
 * @Description: PkeyInfo
 * @author graysonzhang
 * @date 2019-09-08 01:39:48
 *
 */
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PkeyInfo {
    @Getter
    @Setter
    private byte[] privateKey;
    @Getter
    @Setter
    private byte[] chainCode;
    @Getter
    @Setter
    private String eccName;

    private PubKeyInfo publicKey;
    private String address;

    public static PkeyInfo fromCryptoKeypair(CryptoKeyPair cryptoKeyPair) {
        byte[] chaincode = RandomUtils.randomBytes(32);

        return fromCryptoKeypair(cryptoKeyPair, chaincode);
    }

    public static PkeyInfo fromCryptoKeypair(CryptoKeyPair cryptoKeyPair, byte[] chaincode) {
        PkeyInfo pkeyInfo = new PkeyInfo();
        pkeyInfo.setPrivateKey(KeyPresenter.asBytes(cryptoKeyPair.getHexPrivateKey()));
        pkeyInfo.setChainCode(chaincode);
        pkeyInfo.setEccName(KeyUtils.getEccType(cryptoKeyPair));
        return pkeyInfo;
    }

    public static PkeyInfo fromHexString(String hexPrivateKey, EccTypeEnums eccTypeEnums) {
        BigInteger pkeyBigIng = new BigInteger(1, Numeric.hexStringToByteArray(hexPrivateKey));
        hexPrivateKey = Numeric.toHexString(KeyPresenter.asBytes(pkeyBigIng, 32));
        CryptoKeyPair cryptoKeyPair = getCryptoKeyPair(hexPrivateKey, eccTypeEnums);
        return fromCryptoKeypair(cryptoKeyPair);
    }

    /**
     * Get public key
     * @return
     */
    public PubKeyInfo getPublicKey(){
        if(publicKey == null){
            publicKey = doBuildPubkeyInfo();
        }
        return this.publicKey;
    }

    /**
     * Get address
     * @return
     */
    public String getAddress(){
        if(this.address == null){
            this.address = getPublicKey().getAddress();
        }
        return address;
    }

    private static CryptoKeyPair getCryptoKeyPair(String hexPrivateKey, EccTypeEnums eccTypeEnums){
        if(eccTypeEnums == EccTypeEnums.SECP256K1){
            return new ECDSAKeyPair().createKeyPair(hexPrivateKey);
        }
        else{
            return new SM2KeyPair().createKeyPair(hexPrivateKey);
        }
    }

    private PubKeyInfo doBuildPubkeyInfo(){
        EccTypeEnums eccTypeEnums = EccTypeEnums.getEccByName(this.eccName);
        CryptoKeyPair cryptoKeyPair = KeyUtils.getCryptKeyPair(privateKey, eccTypeEnums);
        byte[] pubkeyBytes = Numeric.hexStringToByteArray(cryptoKeyPair.getHexPublicKey());
        return PubKeyInfo.builder().publicKey(pubkeyBytes)
                .chaincode(this.chainCode)
                .eccName(this.eccName)
                .build();
    }

}
