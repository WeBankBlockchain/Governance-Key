package com.webank.keymgr.model;

import com.webank.keymgr.exception.KeyMgrException;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author aaronchu
 * @Description
 * @data 2020/06/30
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
@SuppressWarnings("unchecked")
public class CommonResponse<TBody> {

    private static final int SUCCESS_CODE = 0;

    private int code;

    private String message;

    private TBody data;

    public static <T> CommonResponse success(T data){
        return new CommonResponse(SUCCESS_CODE, "", data);
    }

    public static CommonResponse fail(Exception error){
        if(error instanceof KeyMgrException){
            KeyMgrException keyMgrException = (KeyMgrException)error;
            return new CommonResponse(keyMgrException.getCodeMessageEnums().getExceptionCode(),
                    keyMgrException.getCodeMessageEnums().getExceptionMessage(), null);
        }
        return new CommonResponse(-1, error.getMessage(), null);
    }
}
