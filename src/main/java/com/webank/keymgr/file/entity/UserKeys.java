package com.webank.keymgr.file.entity;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author aaronchu
 * @Description
 * @data 2020/06/22
 */
@Getter
public class UserKeys {

    /**
     * UserId
     */
    private String userId;

    /**
     * KeyAddress with encrypted key mapping
     */
    Map<String, SingleKey> keys = new HashMap<>();

    public void addKey(SingleKey singleKey){
        if(!Objects.equals(this.userId, singleKey.getUserId())){
            throw new IllegalArgumentException("key user id not match ");
        }
        if(this.keys.containsKey(singleKey.getKeyAddress())){
            throw new IllegalArgumentException("duplicate key adderss "+singleKey.getKeyAddress());
        }
        this.keys.put(singleKey.getKeyAddress(), singleKey);
    }

    public UserKeys setUserId(String userId){
        this.userId = userId;
        return this;
    }

    public UserKeys setKeys(Map<String, SingleKey> keys){
        this.keys = keys;
        return this;
    }
}
