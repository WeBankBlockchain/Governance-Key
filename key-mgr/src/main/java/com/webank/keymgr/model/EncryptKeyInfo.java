package com.webank.keymgr.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author aaronchu
 * @Description
 * @data 2020/06/23
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EncryptKeyInfo {

    private String keyAddress;

    private String keyName;

    private String userId;

    private String encryptKey;

    private String parentAddress;

    private String encType;

    private String eccType;

}
