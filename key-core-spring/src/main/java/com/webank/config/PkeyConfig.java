package com.webank.config;

import com.webank.handler.KeyGenAlgoSelectHandler;
import com.webank.keygen.enums.EccTypeEnums;
import com.webank.keygen.face.PrivateKeyCreator;
import com.webank.keygen.key.KeyBytesConverter;
import com.webank.keygen.key.KeyComputeAlgorithm;
import com.webank.keygen.key.KeyEncryptAlgorithm;
import com.webank.keygen.key.impl.*;
import com.webank.keygen.service.PkeyByMnemonicService;
import com.webank.keygen.service.PkeyByRandomService;
import com.webank.keygen.service.PkeySM2ByRandomService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class PkeyConfig {

    @Bean
    public KeyGenAlgoSelectHandler selector(){
        String secp256k1 = EccTypeEnums.SECP256K1.getEccName();
        String sm2p256v1 = EccTypeEnums.SM2P256V1.getEccName();

        Map<String, PrivateKeyCreator> keyCreators = new HashMap<>();
        keyCreators.put(secp256k1, new PkeyByRandomService());
        keyCreators.put(sm2p256v1, new PkeySM2ByRandomService());

        Map<String, KeyComputeAlgorithm> keyComputors = new HashMap<>();
        keyComputors.put(secp256k1, new EccKeyAlgorithm());
        keyComputors.put(sm2p256v1, new Sm2KeyAlgorithm());

        String pem = "pem";
        String keystore = "keystore";
        String p12 = "p12";

        Map<String, KeyEncryptAlgorithm> keyEncryptors = new HashMap<>();
        keyEncryptors.put(pem, new PemEncryptAlgorithm());
        keyEncryptors.put(keystore, new KeystoreEncryptAlgorithm());
        keyEncryptors.put("json", new KeystoreEncryptAlgorithm());
        keyEncryptors.put(p12, new P12EncryptAlgorithm());

        Map<String, KeyBytesConverter> keyConvertors = new HashMap<>();
        keyConvertors.put(pem, new PemBytesConverter());
        keyConvertors.put(keystore, new KeystoreBytesConverter());
        keyConvertors.put("json", new KeystoreBytesConverter());
        keyConvertors.put(p12, new P12BytesConverter());

        KeyGenAlgoSelectHandler handler = new KeyGenAlgoSelectHandler(keyCreators, keyComputors, keyEncryptors, keyConvertors);
        return handler;
    }

    @Bean
    public PkeyByMnemonicService pkeyByMnemonicService(){
        return new PkeyByMnemonicService();
    }

}