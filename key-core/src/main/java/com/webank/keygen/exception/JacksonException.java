/**
 * Copyright (C) 2018 webank, Inc. All Rights Reserved.
 */

package com.webank.keygen.exception;


public class JacksonException extends RuntimeException {

    private static final long serialVersionUID = -3313868940376241665L;

    public JacksonException() {
        super();
    }

    public JacksonException(String message, Throwable cause) {
        super(message, cause);
    }

    public JacksonException(String message) {
        super(message);
    }

    public JacksonException(Throwable cause) {
        super(cause);
    }

}
