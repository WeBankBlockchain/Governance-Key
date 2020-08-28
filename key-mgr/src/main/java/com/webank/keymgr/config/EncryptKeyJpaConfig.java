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
package com.webank.keymgr.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;

import javax.persistence.EntityManager;
import javax.sql.DataSource;
import java.util.Map;


/**
 * EncryptKeyJpaConfig
 *
 * @Description: EncryptKeyJpaConfig
 * @author graysonzhang
 * @date 2020-01-02 20:00:17
 *
 */
@ConditionalOnExpression("'${system.mgrStyle:file}'.equals('db')")
@Configuration
@DependsOn("transactionManager")
@EnableJpaRepositories(
        basePackages = "com.webank.keymgr.db.encryptkey.repository",
        entityManagerFactoryRef = "encryptKeyEntityManagerFactory", 
        transactionManagerRef = "transactionManager")
public class EncryptKeyJpaConfig {
    
    @Autowired
    @Qualifier("encryptKeyDataSource")
    private DataSource encryptKeyDataSource;

    @Autowired
    private JpaProperties jpaProperties; 
    
    private Map<String, String> getVendorProperties() {
        return jpaProperties.getProperties();
    }
    
    @Bean(name = "encryptKeyEntityManager")
    @Primary
    public EntityManager keystoreEntityManager(EntityManagerFactoryBuilder builder) {
        return keystoreEntityManagerFactory(builder).getObject().createEntityManager();
    }

    @Primary
    @Bean(name = "encryptKeyEntityManagerFactory")
    @DependsOn("transactionManager")
    public LocalContainerEntityManagerFactoryBean keystoreEntityManagerFactory(EntityManagerFactoryBuilder builder) {
            
        jpaProperties.getProperties().put("hibernate.transaction.jta.platform", AtomikosJtaPlatform.class.getName());
        jpaProperties.getProperties().put("javax.persistence.transactionType", "JTA");
        
        return builder.dataSource(encryptKeyDataSource).properties(getVendorProperties())
                .packages("com.webank.keymgr.db.encryptkey.entity").persistenceUnit("encryptKeyPersistenceUnit").build();
    }

    @Primary
    @Bean(name = "encryptKeyTransactionManager")
    public PlatformTransactionManager keyStoreTransactionManager(EntityManagerFactoryBuilder builder) {
        return new JpaTransactionManager(keystoreEntityManagerFactory(builder).getObject());
    }

}
