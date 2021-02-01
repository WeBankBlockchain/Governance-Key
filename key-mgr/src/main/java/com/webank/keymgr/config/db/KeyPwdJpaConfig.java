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
package com.webank.keymgr.config.db;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;

import javax.persistence.EntityManager;
import javax.sql.DataSource;
import java.util.Map;

/**
 * 
 * KeyPwdJpaConfig
 *
 * @Description: KeyPwdJpaConfig
 * @author graysonzhang
 * @data 2019-07-26 21:54:06
 *
 */
@Configuration
@EnableJpaRepositories(
        basePackages = "com.webank.keymgr.db.keypwd.repository",
        entityManagerFactoryRef = "keyPwdEntityManagerFactory",
        transactionManagerRef = "transactionManager")
@DependsOn("transactionManager")
@ConditionalOnExpression("${system.storePwd:false}")
public class KeyPwdJpaConfig {
    
    @Autowired
    @Qualifier("keyPwdDataSource")
    private DataSource keyPwdDataSource;
    
    @Autowired
    private JpaProperties jpaProperties; 
    
    private Map<String, String> getVendorProperties() {
        return jpaProperties.getProperties();
    }
    
    @Bean(name = "keyPwdEntityManager")
    public EntityManager keyPwdEntityManager(EntityManagerFactoryBuilder builder) {
        return keyPwdEntityManagerFactory(builder).getObject().createEntityManager();
    }

    @Bean(name = "keyPwdEntityManagerFactory")
    @DependsOn("transactionManager")
    public LocalContainerEntityManagerFactoryBean keyPwdEntityManagerFactory(EntityManagerFactoryBuilder builder) {
        
        jpaProperties.getProperties().put("hibernate.transaction.jta.platform", AtomikosJtaPlatform.class.getName());
        jpaProperties.getProperties().put("javax.persistence.transactionType", "JTA");
        
        return builder.dataSource(keyPwdDataSource).properties(getVendorProperties())
                .packages("com.webank.keymgr.db.keypwd.entity").persistenceUnit("keyPwdPersistenceUnit").build();
    }
    
    @Bean(name = "keyPwdTransactionManager")
    public PlatformTransactionManager keyPwdTransactionManager(EntityManagerFactoryBuilder builder) {
        return new JpaTransactionManager(keyPwdEntityManagerFactory(builder).getObject());
    }

}
