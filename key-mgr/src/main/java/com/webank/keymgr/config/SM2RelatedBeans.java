package com.webank.keymgr.config;

import com.webank.keygen.face.PrivateKeyCreator;
import com.webank.keygen.key.KeyComputeAlgorithm;
import com.webank.keygen.key.impl.Sm2KeyAlgorithm;
import com.webank.keygen.service.PkeySM2ByRandomService;
import com.webank.keymgr.exception.KeyMgrException;
import com.webank.keysign.face.PrivateKeySigner;
import com.webank.keysign.service.SM2SignService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnExpression("'${system.eccType:secp256k1}'.equals('sm2p256v1')")
public class SM2RelatedBeans {

    @Bean
    public PrivateKeyCreator sm2Creator() throws KeyMgrException {
    	return new PkeySM2ByRandomService();
    }    
    
    @Bean
    public PrivateKeySigner SM2Signer() {
    	return new SM2SignService();
    }
    
    @Bean
    public KeyComputeAlgorithm Sm2AddressAlgorithm() {
    	return new Sm2KeyAlgorithm();
    }
}
