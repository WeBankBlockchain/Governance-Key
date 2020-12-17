package com.webank.keygen.hd.bip44;

import com.webank.keygen.hd.bip32.PathComponent;

/**
 * @author aaronchu
 * @Description
 * @data 2020/11/19
 */
public class Account {

    private PathComponent pathComponent;
    public Account(PathComponent previous, int idx){
        this.pathComponent = new PathComponent(previous, idx, true);
    }

    public Change change(int index){
        return new Change(this.pathComponent, index);
    }

}
