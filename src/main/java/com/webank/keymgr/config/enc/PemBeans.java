package com.webank.keymgr.config.enc;

import com.webank.keygen.key.KeyBytesConverter;
import com.webank.keygen.key.KeyEncryptAlgorithm;
import com.webank.keygen.key.impl.PemBytesConverter;
import com.webank.keygen.key.impl.PemEncryptAlgorithm;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author aaronchu
 * @Description
 * @data 2020/06/30
 */
@Configuration
@ConditionalOnExpression("'${system.keyEncType:keystore}'.equals('pem')")
public class PemBeans {
    @Bean
    public KeyEncryptAlgorithm pem() {
        return new PemEncryptAlgorithm();
    }

    @Bean
    public KeyBytesConverter keyToBytes(){
        return new PemBytesConverter();
    }

}
