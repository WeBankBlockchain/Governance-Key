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

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.webank.keygen.BaseTest;

import lombok.extern.slf4j.Slf4j;

/**
 * PkeySharidingServiceTest
 *
 * @Description: PkeySharidingServiceTest
 * @author graysonzhang
 * @date 2019-09-09 15:53:58
 *
 */
@Slf4j
public class PkeySharidingServiceTest extends BaseTest {

    private PkeyShardingService service = new PkeyShardingService();
    
    @Test
    public void testSharding(){
        String testStr = "this is a test string";
        List<String> list = service.shardingPKey(testStr, 5, 3);
        for (String str : list) {
            log.info(str);
        }
        
        List<String> newList = new ArrayList<>();
        newList.add(list.get(0));
        newList.add(list.get(1));
        newList.add(list.get(4));
        log.info(service.recoverPKey(newList));
    }

}
