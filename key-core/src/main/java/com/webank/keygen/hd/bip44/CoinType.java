package com.webank.keygen.hd.bip44;

import com.webank.keygen.hd.bip32.PathComponent;

/**
 * @author aaronchu
 * @Description
 * @data 2020/11/19
 */
public class CoinType {

    private PathComponent pathComponent;
    public CoinType(PathComponent previous, int idx){
        this.pathComponent = new PathComponent(previous, idx, true);
    }

    public Account account(int index){
        return new Account(this.pathComponent, index);
    }

}
