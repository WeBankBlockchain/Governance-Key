package com.webank.keygen.hd.bip44.path;

import com.webank.keygen.hd.bip32.ExtendedPrivateKey;

import java.util.Iterator;

/**
 * derive BIP-44 path, like /m/44'/0'/0'/1/0
 * @author aaronchu
 * @Description
 * @data 2020/11/18
 */
public class TreePathDeriver {


    public ExtendedPrivateKey derive(String path, ExtendedPrivateKey root) {
        if (path == null) throw new IllegalArgumentException("path is null");
        if(!path.startsWith("m")) throw new IllegalArgumentException("Path must start with m");
        //may use regex to verify this path
        PathTokenLooper looper = new PathTokenLooper(root, path);
        Iterator<ExtendedPrivateKey> itr = looper.iterator();

        ExtendedPrivateKey extPkey = root;
        while(itr.hasNext()){
            extPkey = itr.next();
        }
        return extPkey;
    }

}
