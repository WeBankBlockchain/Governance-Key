package com.webank.keymgr.config;

import com.webank.keygen.key.KeyBytesConverter;
import com.webank.keygen.key.KeyEncryptAlgorithm;
import com.webank.keygen.key.impl.P12BytesConverter;
import com.webank.keygen.key.impl.P12EncryptAlgorithm;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnExpression("'${system.keyEncType:keystore}'.equals('p12')")
public class P12Beans {

	@Bean
	public KeyEncryptAlgorithm p12() {
		return new P12EncryptAlgorithm();
	}

	@Bean
	public KeyBytesConverter keyToBytes(){
		return new P12BytesConverter();
	}
}
