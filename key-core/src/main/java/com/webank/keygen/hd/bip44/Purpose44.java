package com.webank.keygen.hd.bip44;

import com.webank.keygen.hd.bip32.PathComponent;

/**
 * @author aaronchu
 * @Description
 * @data 2020/11/19
 */
public class Purpose44 {

    private PathComponent pathComponent;
    public Purpose44(PathComponent previous){
        this.pathComponent = new PathComponent(previous, 44, true);
    }

    public SceneType coinType(int index){
        return new SceneType(this.pathComponent, index);
    }





}
