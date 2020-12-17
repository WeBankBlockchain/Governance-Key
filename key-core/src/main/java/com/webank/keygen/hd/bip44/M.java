package com.webank.keygen.hd.bip44;

import com.webank.keygen.hd.bip32.PathComponent;

/**
 * @author aaronchu
 * @Description
 * @data 2020/11/19
 */
public class M {

    private PathComponent pathComponent;

    public M(){
        pathComponent = new PathComponent();
    }

    public Purpose44 purpose44(){
        return new Purpose44(this.pathComponent);
    }

}
