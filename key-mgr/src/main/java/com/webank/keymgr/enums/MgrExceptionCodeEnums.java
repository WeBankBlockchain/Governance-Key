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
package com.webank.keymgr.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * ExceptionCodeEnums
 *
 * @Description: ExceptionCodeEnums
 * @author graysonzhang
 * @data 2019-07-02 16:38:13
 *
 */
@Getter
@ToString
@AllArgsConstructor
public enum MgrExceptionCodeEnums {

    PKEY_MGR_STYLE_ERROR(1000, "private key manager style error"),
    PKEY_MGR_CREATE_ERROR(1001,"create user account info file fail"),
    PEKY_MGR_CREATE_KEYSTORE_BY_MNEMONIC_ERROR(1002,"create keystore file by mnemonic error"),
    PKEY_MGR_KEY_FILE_NOT_EXISTS(1003, "key file does not exist"),
    PKEY_MGR_FILE_PARSE_ERROR(1004,"file parse error"),
    PKEY_MGR_ACCOUNT_EXIST(1005,"user account has exist"),
    PKEY_MGR_ACCOUNT_NOT_EXIST(1006,"user account does not exist"),
    PKEY_MGR_KEY_STORE_FILE_NOT_EXIST(1007, "key store file does not exist"),
    PKEY_MGR_ECC_TYPE_ERROR(1008, "ecc type must be secp256k1 or sm2p256v1"),
    PKEY_MGR_ENCRYPT_TYPE_ERROR(1009, "enrypt type must be p12 or keystore"),
    PKEY_MGR_ENCRYPT_DATA_RECORD_NOT_EXIST(1010, "encrypt private key data record not exist"),
    PKEY_MGR_KEY_FORMAT_ERROR(1011, "private key must be 32 bytes"),
    PKEY_MGR_PARENT_NOT_EXIST(1012, "parent key not exists"),
    PKEY_MGR_CERT_KEY_ALG_NOT_EXIST(1013, "key algorithms are not supported"),
    PKEY_MGR_CERT_REQUEST_NOT_EXIST(1014, "certificate request not exists"),
    PKEY_MGR_CERT_NOT_EXIST(1015, "certificate not exists"),
    PKEY_MGR_CERT_KEY_NOT_EXIST(1016, "certificate private key not exists"),
    PKEY_MGR_CERT_VALIDITY_FAILURE(1017, "the current date is out of the validity of the certificate"),
    PKEY_MGR_KEY_ADDRESS_NOT_FOUND(1018, "key address not found");

    private int exceptionCode;

    @Setter
    private String exceptionMessage;
}
