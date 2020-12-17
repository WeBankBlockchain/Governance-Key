package com.webank.keygen.hd.bip44.path;

import com.webank.keygen.hd.bip32.ExtendedPrivateKey;
import com.webank.keygen.hd.bip32.PathComponent;

/**
 * @author aaronchu
 * @Description
 * @data 2020/11/20
 */
public class Purpose44Path {

    private PathComponent pathComponent;

    public Purpose44Path(PathComponent pathComponent){
        this.pathComponent = pathComponent;
    }

    @Override
    public String toString(){
        return pathComponent.currentPath();
    }

    public ExtendedPrivateKey deriveKey(ExtendedPrivateKey root){
        return new TreePathDeriver().derive(toString(), root);
    }

}
