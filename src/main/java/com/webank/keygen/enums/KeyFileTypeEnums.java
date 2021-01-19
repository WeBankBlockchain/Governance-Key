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

/**
 * KeyFileTypeEnums
 *
 * @Description: KeyFileTypeEnums
 * @author graysonzhang
 * @date 2020-01-02 11:48:46
 *
 */
@Getter
@ToString
@AllArgsConstructor
@Slf4j
public enum KeyFileTypeEnums {
      
    /** @Fields KEYSTORE_FILE : TODO */
    KEYSTORE_FILE(1, ".json"),
    /** @Fields P12_FILE : TODO */
    P12_FILE(2, ".p12"), 
    /** @Fields PEM_FILE : TODO */
    PEM_FILE(3, ".pem");

    private int keyFileType;
    private String keyFilePostfix;
    
    public static KeyFileTypeEnums getKeyFileTypeEnum(int keyFileType){
        for(KeyFileTypeEnums type : KeyFileTypeEnums.values()){
            if(type.getKeyFileType() == keyFileType){
                return type;
            }
        }
        log.error("keyFileType type {} can't be converted.", keyFileType);
        return null;
    }

}
