package com.webank.keymgr.config.enc;

import com.webank.keygen.key.KeyBytesConverter;
import com.webank.keygen.key.KeyEncryptAlgorithm;
import com.webank.keygen.key.impl.KeystoreBytesConverter;
import com.webank.keygen.key.impl.KeystoreEncryptAlgorithm;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnExpression("'${system.keyEncType:keystore}'.equals('keystore')")
public class KeyStoreBeans {

    @Bean
    public KeyEncryptAlgorithm keyStore() {
    	return new KeystoreEncryptAlgorithm();
    }

    @Bean
    public KeyBytesConverter keyToBytes(){
        return new KeystoreBytesConverter();
    }
}
