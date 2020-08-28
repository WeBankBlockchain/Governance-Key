package com.webank.keymgr.file.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author aaronchu
 * @Description
 * @data 2020/06/22
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
public class SingleKey {

    /**
     * User id
     */
    //Ignored because it is stored in UserKeys,so not store it here
    @JsonIgnore
    private String userId;
    /**
     * Address of the key
     */
    //Ignored because it is stored as key in UserKeys,so not store it here
    @JsonIgnore
    private String keyAddress;

    /**
     * Encrypted key data
     */
    private String encryptKey;

    /**
     * Name of the key
     */
    private String keyName;

    /**
     * Parent key address of current user
     */
    private String parentAddress;

    /**
     * Enc type
     */
    private String encType;

    /**
     * Ecc Type
     */
    private String eccType;
}
