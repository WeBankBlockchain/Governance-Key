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
package com.webank.keysign.face;


/**
 * RrivateKeySigner
 *
 * @Description: RrivateKeySigner
 * @author graysonzhang
 * @date 2020-01-02 18:33:19
 *
 */
public interface PrivateKeySigner {

    String sign(byte[] plain, String privateKey);

    String sign(String utf8Msg, String privateKey);

    boolean verify(byte[] plain, String signStr, String publicKey);

    boolean verify(String utf8Msg, String ssignStr, String publicKey);

}

