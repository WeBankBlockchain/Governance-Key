/**
 * Copyright 2014-2019  the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package com.webank.keygen.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * ExceptionCodeEnums
 *
 * @Description: ExceptionCodeEnums
 * @author graysonzhang
 * @date 2019-07-02 16:38:13
 *
 */
@Getter
@ToString
@AllArgsConstructor
public enum ExceptionCodeEnums {
    
    /** @Fields SUCCEED : run success */
    SUCCESS(0, "success"),
    
    /** @Fields PARAM_EXCEPTION : param exception */
    PARAM_EXCEPTION(1000, "param error"),
    PRIVATEKEY_FORMAT_ERROR(1001, "private key format error"),
    PRIVATE_KEY_DECODE_FAIL(1002,"private key decode fail"),
    AES_KEY_ERROR(1003,"aes key error"),
    CHAIN_CODE_ERROR(1004,"chain code length must be 256"),

    KEY_LEN_TOO_LONG(1005,"private key length must be 256"),
    
    KEY_FORMAT_TYPE_ERROR(1006, "key format type is not right"),
    ECC_TYPE_ERROR(1007, "ecc type is not right"),
    /** @Fields SYSTEM_ERROR : system error */
    SYSTEM_ERROR(9999, "system error");

    private int exceptionCode;
    
    @Setter
    private String exceptionMessage;
}
