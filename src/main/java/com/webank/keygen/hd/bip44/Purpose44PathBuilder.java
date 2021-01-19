package com.webank.keygen.hd.bip44;

/**
 * @author aaronchu
 * @Description
 * @data 2020/11/18
 */
public class Purpose44PathBuilder {

    public M m(){
        return new M();
    }

    public static Purpose44PathBuilder builder(){
        return new Purpose44PathBuilder();
    }
}
