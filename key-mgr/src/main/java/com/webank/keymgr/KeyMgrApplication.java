package com.webank.keymgr;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.XADataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;

//@SpringBootApplication(exclude= {DataSourceAutoConfiguration.class, XADataSourceAutoConfiguration.class})
@SpringBootApplication
public class KeyMgrApplication {

    public static void main(String[] args) {
        SpringApplication.run(KeyMgrApplication.class, args);
    }
    
    //(exclude = { DataSourceAutoConfiguration.class, HibernateJpaAutoConfiguration.class,
    //DataSourceTransactionManagerAutoConfiguration.class })

}
