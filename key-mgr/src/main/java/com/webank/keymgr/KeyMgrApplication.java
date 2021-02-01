package com.webank.keymgr;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

//@SpringBootApplication(exclude= {DataSourceAutoConfiguration.class, XADataSourceAutoConfiguration.class})
@SpringBootApplication
public class KeyMgrApplication {

    public static void main(String[] args) {
        SpringApplication.run(KeyMgrApplication.class, args);
    }
    
    //(exclude = { DataSourceAutoConfiguration.class, HibernateJpaAutoConfiguration.class,
    //DataSourceTransactionManagerAutoConfiguration.class })

}
