package com.webank.service;

import com.webank.handler.KeyGenAlgoSelectHandler;
import com.webank.keygen.enums.EccTypeEnums;
import com.webank.keygen.enums.ExceptionCodeEnums;
import com.webank.keygen.exception.KeyGenException;
import com.webank.keygen.face.PrivateKeyCreator;
import com.webank.keygen.key.KeyBytesConverter;
import com.webank.keygen.key.KeyComputeAlgorithm;
import com.webank.keygen.key.KeyEncryptAlgorithm;
import com.webank.keygen.model.DecryptResult;
import com.webank.keygen.model.PkeyInfo;
import com.webank.keygen.service.PkeyByMnemonicService;
import com.webank.keygen.utils.KeyPresenter;
import com.webank.keysign.utils.Numeric;
import com.webank.model.PkeyInfoVO;
import com.webank.model.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.util.Objects;

@Service
public class KeyGeneratorService {

    @Autowired
    private KeyGenAlgoSelectHandler selectHandler;

    @Autowired
    private PkeyByMnemonicService mnemonicService;

    public R random(String curve) throws Exception{
        PrivateKeyCreator keyCreator = selectHandler.selectKeyCreator(curve);

        PkeyInfo pkey = keyCreator.generatePrivateKey();

        PkeyInfoVO vo = new PkeyInfoVO();
        vo.setAddress(pkey.getAddress());
        vo.setPrivateKeyHex(KeyPresenter.asString(pkey.getPrivateKey()));
        vo.setPubKeyHex(KeyPresenter.asString(pkey.getPublicKey().getPublicKey()));

        return R.ok().put("data", vo);
    }

    public R mnemonic(){
        String mnemonic = mnemonicService.createMnemonic();
        return R.ok().put("data", mnemonic);
    }



    public byte[] downloadEncryptKey(String privKey, String eccType, String encType, String password, HttpServletResponse response) throws Exception{
        //Encrypt
        KeyEncryptAlgorithm keyEncryptor = this.selectHandler.selectKeyEncryptor(encType);
        KeyComputeAlgorithm keyComputor = this.selectHandler.selectKeyComputor(eccType);
        KeyBytesConverter keyConvertor = this.selectHandler.selectKeyConvertor(encType);

        byte[] pkeyBytes = Numeric.hexStringToByteArray(privKey);
        String address = keyComputor.computeAddress(pkeyBytes);
        String encryptKey = keyEncryptor.encrypt(password, pkeyBytes, address, eccType);

        byte[] encryptKeyBytes = keyConvertor.toBytes(encryptKey);

        //Download
        String suffix = Objects.equals("keystore", encType)?"json":encType;
        String filename = address + "." + suffix;
        response.setHeader("Content-Disposition", "attachment; filename=\"" + filename+"\"");
        response.addHeader("Content-Length", "" + encryptKeyBytes.length);
        response.setContentType("application/octet-stream; charset=UTF-8");
        response.setHeader("filename", filename);

        response.getOutputStream().write(encryptKeyBytes);
        return encryptKeyBytes;
    }

    public R decryptFile(byte[] data,String fileName, String password) throws Exception{
        //get file type(pem, p12, keystore..)
        int index = fileName.lastIndexOf(".");
        if(index == -1){
            throw new KeyGenException(ExceptionCodeEnums.PARAM_EXCEPTION);
        }
        String fileType = fileName.substring(index+1);
        String encType = Objects.equals("json", fileType)?"keystore":fileType;
        //Convert
        String dataStr = this.selectHandler.selectKeyConvertor(encType).fromBytes(data);
        KeyEncryptAlgorithm algorithm = this.selectHandler.selectKeyEncryptor(encType);
        byte[] rawkey = algorithm.decrypt(password, dataStr);
        if(rawkey == null){
            return R.error("请确保密码正确");
        }
        return R.ok().put("data", KeyPresenter.asString(rawkey));
    }

    public R getKeyDetail(String privKey, String eccType) throws Exception {
        if(privKey == null || privKey.isEmpty()){
            throw new IllegalArgumentException("privKey cannot be null");
        }
        byte[] keyBytes = Numeric.hexStringToByteArray(privKey);
        PkeyInfo pkeyInfo = PkeyInfo
                .builder().privateKey(keyBytes)
                .eccName(eccType)
                .chainCode(null)
                .build();

        PkeyInfoVO pkeyDetail = new PkeyInfoVO();
        pkeyDetail.setPrivateKeyHex(privKey);
        pkeyDetail.setPubKeyHex(KeyPresenter.asString(pkeyInfo.getPublicKey().getPublicKey()));
        pkeyDetail.setAddress(pkeyInfo.getAddress());
        return R.ok().put("data", pkeyDetail);
    }

    public void transform(byte[] bytes, String inputFile, String password, String tgtFormat, HttpServletResponse response) throws Exception {
        //extract encrypt type from fileName
        int index = inputFile.lastIndexOf(".");
        if(index == -1){
            throw new KeyGenException(ExceptionCodeEnums.PARAM_EXCEPTION);
        }
        String encType = inputFile.substring(index+1);
        KeyEncryptAlgorithm decAlgorithm = this.selectHandler.selectKeyEncryptor(encType);
        KeyBytesConverter converter = this.selectHandler.selectKeyConvertor(encType);
        //decrypt it
        DecryptResult decryptResult = decAlgorithm.decryptFully(password, converter.fromBytes(bytes));
        //encrypt to target format
        KeyEncryptAlgorithm encryptAlgorithm = this.selectHandler.selectKeyEncryptor(tgtFormat);
        byte[] pkey = decryptResult.getPrivateKey();
        String eccType = decryptResult.getEccType();
        KeyComputeAlgorithm computeAlgorithm = this.selectHandler.selectKeyComputor(eccType);
        String address = computeAlgorithm.computeAddress(pkey);
        String tgtEncrypt = encryptAlgorithm.encrypt(password, pkey, address, eccType);
        byte[] tgtEncryptBytes = this.selectHandler.selectKeyConvertor(tgtFormat).toBytes(tgtEncrypt);
        //Download
        String suffix = Objects.equals("keystore", tgtFormat)?"json":tgtFormat;
        String outputFile = address + "." + suffix;
        response.addHeader("Content-Length", "" + tgtEncryptBytes.length);
        response.setContentType("application/octet-stream; charset=UTF-8");
        response.setHeader("fname", outputFile);
        response.setHeader("Access-Control-Expose-Headers", "fname");
        response.setHeader("Content-Disposition", "attachment; filename=\"" + outputFile+"\"");
        response.getOutputStream().write(tgtEncryptBytes);

        return ;
    }


    public R mnemonicExport(String mnemonic, String password, String eccType) throws Exception{
        EccTypeEnums eccTypeEnums = EccTypeEnums.getEccByName(eccType);
        PkeyInfo pkeyInfo = mnemonicService.generatePrivateKeyByMnemonic(mnemonic, password, eccTypeEnums);
        PkeyInfoVO pkeyDetail = new PkeyInfoVO();
        pkeyDetail.setPrivateKeyHex(KeyPresenter.asString(pkeyInfo.getPrivateKey()));
        pkeyDetail.setPubKeyHex(KeyPresenter.asString(pkeyInfo.getPublicKey().getPublicKey()));
        pkeyDetail.setAddress(pkeyInfo.getAddress());
        return R.ok().put("data", pkeyDetail);
    }
}