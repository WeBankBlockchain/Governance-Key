/*
 * Copyright 2014-2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.webank.keygen.service;

import com.webank.keygen.handler.ShamirHandler;
import com.webank.keygen.model.ShardingInfo;
import com.webank.keygen.utils.JacksonUtils;
import com.webank.keygen.utils.SecureRandomUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * PkeyShardingService
 *
 * @Description: PkeyShardingService
 * @author graysonzhang
 * @date 2019-09-09 14:54:38
 *
 */
public class PkeyShardingService {
    
    /**    
     * sharding private key to n parts, and can recover by at least k parts.
     * 
     * @param secret
     * @param n the number of parts to produce, n must be <= 255
     * @param k the threshold of joinable parts, k must be <= n
     *       
     * @return List<String>       
     */
    public List<String> shardingPKey(byte[] secret, int n, int k){
        if(secret == null || secret.length == 0){
            throw new IllegalArgumentException("secret is empty");
        }
        final Map<Integer, byte[]> parts = ShamirHandler.split(secret, SecureRandomUtils.secureRandom() , n, k);
        List<String> list = new ArrayList<>();
        for(Map.Entry<Integer, byte[]> part : parts.entrySet()){
            ShardingInfo shardingInfo = new ShardingInfo();
            shardingInfo.setShardingNum(part.getKey());
            shardingInfo.setShardingContent(part.getValue());
            list.add(JacksonUtils.toJson(shardingInfo));
        }
        return list;
    }
    
    /**    
     * recover private key by at least k parts
     * 
     * @param partList
     *       
     * @return String       
     */
    public byte[] recoverPKey(List<String> partList){
        
        if(partList == null) return null;
        Map<Integer, byte[]> parts = new HashMap<Integer, byte[]>();
        for (String part : partList) {
            ShardingInfo shardingInfo = JacksonUtils.fromJson(part, ShardingInfo.class);
            parts.put(shardingInfo.getShardingNum(), shardingInfo.getShardingContent());
        }
        final byte[] recovered = ShamirHandler.join(parts);
        return recovered;
    }
}
