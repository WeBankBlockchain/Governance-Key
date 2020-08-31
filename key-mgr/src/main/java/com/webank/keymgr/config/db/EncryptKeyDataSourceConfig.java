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

import com.mysql.cj.jdbc.MysqlXADataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jta.atomikos.AtomikosDataSourceBean;
import org.springframework.context.annotation.*;

import javax.sql.DataSource;
import java.sql.SQLException;

/**
 * EncryptKeyDataSourceConfig
 *
 * @Description: EncryptKeyDataSourceConfig
 * @author graysonzhang
 * @date 2020-01-02 19:59:57
 *
 */
@ConditionalOnExpression("'db'.equals('${system.mgrStyle}')")
@Configuration
@DependsOn("transactionManager")
public class EncryptKeyDataSourceConfig {

    @Primary
    @Bean(name = "encryptKeyDataSourceProperties")
    @Qualifier("encryptKeyDataSourceProperties")
    @ConfigurationProperties(prefix = "spring.datasource.encryptkeydata")
    public DataSourceProperties keyStoreDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Primary
    @Bean(name = "encryptKeyDataSource", initMethod = "init", destroyMethod = "close")
    @Qualifier("encryptKeyDataSource")
    public DataSource keyStoreDataSource(@Qualifier("encryptKeyDataSourceProperties") DataSourceProperties properties) throws SQLException {

        MysqlXADataSource mysqlXaDataSource = new MysqlXADataSource();
        mysqlXaDataSource.setUrl(properties.getUrl());
        mysqlXaDataSource.setPinGlobalTxToPhysicalConnection(true);
        mysqlXaDataSource.setPassword(properties.getPassword());
        mysqlXaDataSource.setUser(properties.getUsername());

        AtomikosDataSourceBean xaDataSource = new AtomikosDataSourceBean();
        xaDataSource.setXaDataSource(mysqlXaDataSource);
        xaDataSource.setUniqueResourceName("encryptKeyXDataSource");
        xaDataSource.setBorrowConnectionTimeout(60);
        xaDataSource.setMaxPoolSize(20);
        return xaDataSource;
    }
}
