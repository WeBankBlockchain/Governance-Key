package com.webank.keygen.service;

import com.webank.keygen.enums.EccTypeEnums;
import com.webank.keygen.hd.bip32.ExtendedPrivateKey;
import com.webank.keygen.hd.bip44.Purpose44PathBuilder;
import com.webank.keygen.hd.bip44.path.Purpose44Path;
import com.webank.keygen.hd.bip44.path.TreePathDeriver;
import com.webank.keygen.model.PkeyInfo;

/**
 * Derive pkey through chaincode and child id
 * @author aaronchu
 * @Description
 * @data 2020/12/24
 */
public class PkeyHDDeriveService {

    /**
     * convert a private key to ExtendedPrivateKey
     * @param pkeyInfo
     * @return ExtendedPrivateKey
     */
    public ExtendedPrivateKey buildExtendedPrivateKey(PkeyInfo pkeyInfo){
        return  new ExtendedPrivateKey(pkeyInfo);
    }

    /**
     * Derive a child
     * @param keyBytes plain private key bytes
     * @param chaincode The code used to derive sub private key
     * @param eccType Ecc type
     * @param childIdx child id
     * @return ExtendedPrivateKey
     */
    public ExtendedPrivateKey deriveChild(byte[] keyBytes , byte[] chaincode, EccTypeEnums eccType, int childIdx){
        PkeyInfo pkeyInfo = new PkeyInfo();
        pkeyInfo.setPrivateKey(keyBytes);
        pkeyInfo.setChainCode(chaincode);
        pkeyInfo.setEccName(eccType.getEccName());
        return this.deriveChild(pkeyInfo, childIdx);
    }


    /**
     * Derive a child
     * @param pkeyInfo private key
     * @param childIdx child index
     * @return ExtendedPrivateKey
     */
    public ExtendedPrivateKey deriveChild(PkeyInfo pkeyInfo, int childIdx){
        ExtendedPrivateKey privateKey = new ExtendedPrivateKey(pkeyInfo);
        return privateKey.deriveChild(childIdx);
    }

    /**
     * Derive from a path
     * @param pkeynIfo private key
     * @param derivePath The path containing a sequence of child numbers like "2/3/4" that the private key will derive with.
     * @return ExtendedPrivateKey
     */
    public ExtendedPrivateKey derivePath(PkeyInfo pkeynIfo, String derivePath){
        ExtendedPrivateKey privateKey = new ExtendedPrivateKey(pkeynIfo);
        TreePathDeriver deriver = new TreePathDeriver();
        return deriver.derive(derivePath, privateKey);
    }

    /**
     * Derive from a path
     * @param keyBytes plain private key bytes
     * @param chaincode The code used to derive sub private key
     * @param eccType Ecc type
     * @param path The path containing a sequence of child numbers like "2/3/4" that the private key will derive with.
     * @return ExtendedPrivateKey
     */
    public ExtendedPrivateKey derivePath(byte[] keyBytes , byte[] chaincode, EccTypeEnums eccType, String path){
        PkeyInfo pkeyInfo = new PkeyInfo();
        pkeyInfo.setPrivateKey(keyBytes);
        pkeyInfo.setChainCode(chaincode);
        pkeyInfo.setEccName(eccType.getEccName());
        return derivePath(pkeyInfo, path);
    }

    /**
     * Derive from a path
     * @param keyBytes plain private key bytes
     * @param chaincode The code used to derive sub private key
     * @param eccType Ecc type
     * @param purpose44Path The bip-44 path containing a sequence of child numbers like "2/3/4" that the private key will derive with.
     * @return ExtendedPrivateKey
     */
    public ExtendedPrivateKey derivePath(byte[] keyBytes , byte[] chaincode, EccTypeEnums eccType, Purpose44Path purpose44Path){
        PkeyInfo pkeyInfo = new PkeyInfo();
        pkeyInfo.setPrivateKey(keyBytes);
        pkeyInfo.setChainCode(chaincode);
        pkeyInfo.setEccName(eccType.getEccName());
        return derivePath(pkeyInfo, purpose44Path);
    }

    /**
     * Derive from a path
     * @param pkeyInfo plain private key bytes
     * @param purpose44Path The bip-44 path containing a sequence of child numbers like "2/3/4" that the private key will derive with.
     * @return ExtendedPrivateKey
     */
    public ExtendedPrivateKey derivePath(PkeyInfo pkeyInfo, Purpose44Path purpose44Path){
        return this.derivePath(pkeyInfo, purpose44Path.toString());
    }

    /**
     * returns a Purpose44PathBuilder to build BIP-44 Path
     * @return
     */
    public Purpose44PathBuilder getPurpose44PathBuilder(){
        return Purpose44PathBuilder.builder();
    }
}
