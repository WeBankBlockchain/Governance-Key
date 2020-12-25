/*
 * Copyright 2012-2019 the original author or authors.
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
package com.webank.keygen.exception;

import com.webank.keygen.enums.ExceptionCodeEnums;


/**
 * BaseException
 *
 * @Description: BaseException
 * @author graysonzhang
 * @date 2019-07-02 16:43:45
 *
 */
public class KeyGenException extends Exception {

    /** @Fields serialVersionUID : TODO */
    private static final long serialVersionUID = 1L;
    
    private ExceptionCodeEnums enums;

    public KeyGenException(ExceptionCodeEnums enums) {
        super(enums.getExceptionMessage());
        this.enums = enums;
    }

    public KeyGenException(ExceptionCodeEnums enums, Throwable cause) {
        super(enums.getExceptionMessage(), cause);
        this.enums = enums;
    }
    
    public KeyGenException(String msg) {
        super(msg);
        this.enums.setExceptionMessage(msg);
    }
    
    public KeyGenException(String msg, Throwable cause) {
        super(msg, cause);
        this.enums.setExceptionMessage(msg);
    }

    public ExceptionCodeEnums getCodeMessageEnums() {
        return enums;
    }
}
