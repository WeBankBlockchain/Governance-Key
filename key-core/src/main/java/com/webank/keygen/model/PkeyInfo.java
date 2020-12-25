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
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * PkeyInfo
 *
 * @Description: PkeyInfo
 * @author graysonzhang
 * @date 2019-09-08 01:39:48
 *
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PkeyInfo {
    private byte[] privateKey;
    private byte[] chainCode;

    private String address;
    private String eccName;

    public PubKeyInfo toPublic(EccTypeEnums eccTypeEnums, boolean compressed){
        EccOperations eccOperations = new EccOperations(eccTypeEnums);
        byte[] pubkeyBytes = eccOperations.generatePublicKeys(this.privateKey, compressed);
        return PubKeyInfo.builder().publicKey(pubkeyBytes)
                .chaincode(this.chainCode)
                .build();
    }

}
