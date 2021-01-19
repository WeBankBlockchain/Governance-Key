package com.webank.keymgr.config;

import com.webank.keygen.service.PkeyByMnemonicService;
import com.webank.keygen.service.PkeyEncryptService;
import com.webank.keygen.service.PkeyShardingService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by unnamed on 2020/2/21.
 */
@Configuration
public class PkeyGenBeans {

    //Pkey-gen beans
    @Bean
    public PkeyShardingService getPkeyShardingService(){
        return new PkeyShardingService();
    }

    @Bean
    public PkeyByMnemonicService getPkeyByMnemonicService(){
        return new PkeyByMnemonicService();
    }

    @Bean
    public PkeyEncryptService getPkeyEncryptService(){
        return new PkeyEncryptService();
    }

}
