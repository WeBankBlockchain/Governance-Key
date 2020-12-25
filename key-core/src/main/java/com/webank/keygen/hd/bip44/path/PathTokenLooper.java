package com.webank.keygen.hd.bip44.path;

import com.webank.keygen.hd.bip32.ExtendedPrivateKey;
import lombok.Getter;

import java.util.Iterator;

/**
 * @author aaronchu
 * @Description
 * @data 2020/11/19
 */
@Getter
final class PathTokenLooper implements Iterable<ExtendedPrivateKey> {

    private ExtendedPrivateKey rootKey;
    private String pathString;

    public PathTokenLooper(ExtendedPrivateKey rootKey, String pathString){
        this.rootKey = rootKey;
        this.pathString = pathString;
    }

    @Override
    public Iterator<ExtendedPrivateKey> iterator() {
        return new ExtKeyIterator(this);
    }
}
