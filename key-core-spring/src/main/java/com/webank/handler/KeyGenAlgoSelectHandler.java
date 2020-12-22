package com.webank.handler;

import com.webank.keygen.enums.ExceptionCodeEnums;
import com.webank.keygen.exception.KeyGenException;
import com.webank.keygen.face.PrivateKeyCreator;
import com.webank.keygen.key.KeyBytesConverter;
import com.webank.keygen.key.KeyComputeAlgorithm;
import com.webank.keygen.key.KeyEncryptAlgorithm;

import java.util.Map;

public class KeyGenAlgoSelectHandler{

    private Map<String, PrivateKeyCreator> keyCreators;
    private Map<String, KeyComputeAlgorithm> keyComputors;
    private Map<String, KeyEncryptAlgorithm> keyEncryptors;
    private Map<String, KeyBytesConverter> keyConvertors;


    public KeyGenAlgoSelectHandler(Map<String, PrivateKeyCreator> keyCreators,
                                   Map<String, KeyComputeAlgorithm> keyComputors,
                                   Map<String, KeyEncryptAlgorithm> keyEncryptors,
                                   Map<String, KeyBytesConverter> keyConvertors){
        this.keyCreators = keyCreators;
        this.keyComputors = keyComputors;
        this.keyEncryptors = keyEncryptors;
        this.keyConvertors = keyConvertors;
    }


    public PrivateKeyCreator selectKeyCreator(String curve) throws Exception{
        PrivateKeyCreator result =  keyCreators.get(curve);
        if(result == null){
            throw new KeyGenException(ExceptionCodeEnums.ECC_TYPE_ERROR);
        }
        return result;
    }

    public KeyComputeAlgorithm selectKeyComputor(String curve) throws Exception{
        KeyComputeAlgorithm result = keyComputors.get(curve);
        if(result == null){
            throw new KeyGenException(ExceptionCodeEnums.ECC_TYPE_ERROR);
        }
        return result;
    }

    public KeyEncryptAlgorithm selectKeyEncryptor(String encType) throws Exception{
        KeyEncryptAlgorithm result = keyEncryptors.get(encType);
        if(result == null){
            throw new KeyGenException(ExceptionCodeEnums.PARAM_EXCEPTION);
        }
        return result;
    }

    public KeyBytesConverter selectKeyConvertor(String encType) throws Exception{
        KeyBytesConverter result = this.keyConvertors.get(encType);
        if(result == null){
            throw new KeyGenException(ExceptionCodeEnums.PARAM_EXCEPTION);
        }
        return result;
    }
}
