package com.webank.keymgr.config.db;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.XADataSourceAutoConfiguration;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;

/**
 * Exclude database
 * @author aaronchu
 * @Description
 * @data 2020/08/31
 */
@Configuration
@Conditional(value={ExclusionDatabaseCondition.class})
@EnableAutoConfiguration(exclude = {DataSourceAutoConfiguration.class, XADataSourceAutoConfiguration.class})
public class ExclusionConfiguration {



}
