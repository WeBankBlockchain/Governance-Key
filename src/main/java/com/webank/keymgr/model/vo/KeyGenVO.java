package com.webank.keymgr.model.vo;

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
public class KeyGenVO {

    /**
     * Hex format of private key
     */
    private String pkey;

    /**
     * Key address
     */
    private String address;

    /**
     * Curve
     */
    private String curve;

    private String format;
}
