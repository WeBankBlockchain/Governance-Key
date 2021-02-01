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
package com.webank.keymgr.db.encryptkey.entity;

import com.webank.keymgr.db.entity.IdEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Accessors;

import javax.persistence.*;


/**
 * EncryptKeyInfo
 *
 * @Description: EncryptKeyInfo
 * @author graysonzhang
 * @date 2020-01-02 16:04:56
 *
 */
@SuppressWarnings("serial")
@Data
@Accessors(chain = true)
@Entity(name = "encrypt_keys_info")
@Table(name = "encrypt_keys_info", indexes = { @Index(name = "user_id", columnList = "user_id"),
        @Index(name = "key_address", columnList = "key_address") })
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class EncryptKeyDO extends IdEntity {

    @Column(name = "key_address")
    private String keyAddress;

    @Column(name = "key_name")
    private String keyName;
    
    @Column(name = "user_id")
    private String userId;

    @Column(name = "encrypt_key")
    @Lob
    private String encryptKey;
    
    @Column(name = "parent_address")
    private String parentAddress;

    @Column(name = "enc_type")
    private String encType;

    @Column(name = "ecc_type")
    private String eccType;
}
