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
import lombok.*;

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
@Data
public class PkeyInfo {
    private byte[] privateKey;
    private byte[] chainCode;
    private String eccName;

    private PubKeyInfo publicKey;
    private String address;

    public PubKeyInfo getPublicKey(){
        if(publicKey == null){
            publicKey = doBuildPubkeyInfo();
        }
        return this.publicKey;
    }

    public String getAddress(){
        if(this.address == null){
            this.address = getPublicKey().getAddress();
        }
        return address;
    }

    private PubKeyInfo doBuildPubkeyInfo(){
        EccTypeEnums eccTypeEnums = EccTypeEnums.getEccByName(this.eccName);
        EccOperations eccOperations = new EccOperations(eccTypeEnums);
        byte[] pubkeyBytes = eccOperations.generatePublicKeys(this.privateKey, false);
        return PubKeyInfo.builder().publicKey(pubkeyBytes)
                .chaincode(this.chainCode)
                .eccName(this.eccName)
                .build();
    }

}
