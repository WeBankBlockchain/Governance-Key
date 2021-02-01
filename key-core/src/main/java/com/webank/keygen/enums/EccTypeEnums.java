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
package com.webank.keygen.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;

/**
 * EccTypeEnums
 *
 * @Description: EccTypeEnums
 * @author graysonzhang
 * @date 2019-12-24 15:02:32
 *
 */
@Getter
@ToString
@AllArgsConstructor
@Slf4j
public enum EccTypeEnums {
   
    /** @Fields SECP256K1 : TODO */
    SECP256K1(1, "secp256k1"),
    /** @Fields SM2P256V1 : TODO */
    SM2P256V1(2, "sm2p256v1");

    private int eccType;
    private String eccName;
    
    public static EccTypeEnums getEccByType(int eccType){
        for(EccTypeEnums type : EccTypeEnums.values()){
            if(type.getEccType() == eccType){
                return type;
            }
        }
        log.error("ecc type {} can't be converted.", eccType);
        return null;
    }

    public static EccTypeEnums getEccByName(String eccName){
        for(EccTypeEnums type : EccTypeEnums.values()){
            if(Objects.equals(type.eccName, eccName)){
                return type;
            }
        }
        log.error("ecc type {} can't be converted.", eccName);
        return null;
    }

}
