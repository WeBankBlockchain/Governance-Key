package com.webank.keygen.hd.bip44;

import com.webank.keygen.hd.bip32.PathComponent;
import com.webank.keygen.hd.bip44.path.Purpose44Path;

/**
 * @author aaronchu
 * @Description
 * @data 2020/11/19
 */
public class AddressIndex {

    private PathComponent pathComponent;

    public AddressIndex(PathComponent previous, int idx){
        this.pathComponent = new PathComponent(previous, idx, false);
    }

    public Purpose44Path build(){
        return new Purpose44Path(this.pathComponent);
    }
}
