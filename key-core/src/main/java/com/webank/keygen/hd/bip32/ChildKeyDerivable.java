package com.webank.keygen.hd.bip32;

/**
 * @author aaronchu
 * @Description
 * @data 2020/11/18
 */
public interface ChildKeyDerivable<T> {

    T deriveChild(int childIdx);

}
