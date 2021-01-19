package com.webank.keymgr.model.request;

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
public class CreatePrivateKeyRequest {

    private String userId;

    private String password;
    
    private String keyName;

}
