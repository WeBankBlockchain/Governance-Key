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
package com.webank.keymgr.db.keypwd.entity;

import com.webank.keymgr.db.entity.IdEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Accessors;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Table;


/**
 * KeyPwdInfo
 *
 * @Description: KeyPwdInfo
 * @author graysonzhang
 * @date 2020-01-02 16:25:49
 *
 */
@SuppressWarnings("serial")
@Data
@Accessors(chain = true)
@Entity(name = "key_pwds_info")
@Table(name = "key_pwds_info", indexes = { @Index(name = "user_id", columnList = "user_id"),
        @Index(name = "key_address", columnList = "key_address") })
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class KeyPwdInfo extends IdEntity {
    
    @Column(name = "key_address")
    private String keyAddress;
    
    @Column(name = "user_id")
    private String userId;

    @Column(name = "key_pwd")
    private String keyPwd;
}
