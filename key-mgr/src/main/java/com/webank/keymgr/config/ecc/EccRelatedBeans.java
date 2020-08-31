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
package com.webank.keymgr.config.ecc;

import com.webank.keygen.face.PrivateKeyCreator;
import com.webank.keygen.key.KeyComputeAlgorithm;
import com.webank.keygen.key.impl.EccKeyAlgorithm;
import com.webank.keygen.service.PkeyByRandomService;
import com.webank.keymgr.exception.KeyMgrException;
import com.webank.keysign.face.PrivateKeySigner;
import com.webank.keysign.service.ECCSignService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * EccRelatedBeans
 *
 * @Description: EccRelatedBeans
 * @author graysonzhang
 * @author yuzhichu
 * @data 2019-07-28 09:02:12
 *
 */
@Configuration
@ConditionalOnExpression("'${system.eccType:secp256k1}'.equals('secp256k1')")
public class EccRelatedBeans {
        
    @Bean
    public PrivateKeyCreator eccCreator() throws KeyMgrException {
    	return new PkeyByRandomService();
    }
    
    @Bean
    public PrivateKeySigner eccSigner() {
    	return new ECCSignService();
    }

    @Bean
    public KeyComputeAlgorithm eccAddressAlgorithm() {
    	return new EccKeyAlgorithm();
    }
       
}
