package com.webank.keygen.service;

import com.webank.keygen.enums.EccTypeEnums;
import com.webank.keygen.hd.bip32.ExtendedPrivateKey;
import com.webank.keygen.hd.bip44.Purpose44PathBuilder;
import com.webank.keygen.hd.bip44.path.Purpose44Path;
import com.webank.keygen.hd.bip44.path.TreePathDeriver;
import com.webank.keygen.model.PkeyInfo;

/**
 * @author aaronchu
 * @Description
 * @data 2020/12/24
 */
public class PkeyHDDeriveService {

    public ExtendedPrivateKey buildExtendedPrivateKey(PkeyInfo pkeyInfo){
        return  new ExtendedPrivateKey(pkeyInfo);
    }

    public ExtendedPrivateKey deriveChild(byte[] keyBytes , byte[] chaincode, EccTypeEnums eccType, int childIdx){
        PkeyInfo pkeyInfo = new PkeyInfo();
        pkeyInfo.setPrivateKey(keyBytes);
        pkeyInfo.setChainCode(chaincode);
        pkeyInfo.setEccName(eccType.getEccName());
        return this.deriveChild(pkeyInfo, childIdx);
    }


    public ExtendedPrivateKey deriveChild(PkeyInfo pkeyInfo, int childIdx){
        ExtendedPrivateKey privateKey = new ExtendedPrivateKey(pkeyInfo);
        return privateKey.deriveChild(childIdx);
    }

    public ExtendedPrivateKey derivePath(PkeyInfo pkeynIfo, String derivePath){
        ExtendedPrivateKey privateKey = new ExtendedPrivateKey(pkeynIfo);
        TreePathDeriver deriver = new TreePathDeriver();
        return deriver.derive(derivePath, privateKey);
    }

    public ExtendedPrivateKey derivePath(byte[] keyBytes , byte[] chaincode, EccTypeEnums eccType, String path){
        PkeyInfo pkeyInfo = new PkeyInfo();
        pkeyInfo.setPrivateKey(keyBytes);
        pkeyInfo.setChainCode(chaincode);
        pkeyInfo.setEccName(eccType.getEccName());
        return derivePath(pkeyInfo, path);
    }

    public ExtendedPrivateKey derivePath(byte[] keyBytes , byte[] chaincode, EccTypeEnums eccType, Purpose44Path purpose44Path){
        PkeyInfo pkeyInfo = new PkeyInfo();
        pkeyInfo.setPrivateKey(keyBytes);
        pkeyInfo.setChainCode(chaincode);
        pkeyInfo.setEccName(eccType.getEccName());
        return derivePath(pkeyInfo, purpose44Path);
    }


    public ExtendedPrivateKey derivePath(PkeyInfo pkeyInfo, Purpose44Path purpose44Path){
        return this.derivePath(pkeyInfo, purpose44Path.toString());
    }

    public Purpose44PathBuilder getPurpose44PathBuilder(){
        return Purpose44PathBuilder.builder();
    }
}
