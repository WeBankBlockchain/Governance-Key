package com.webank.keygen.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author aaronchu
 * @Description
 * @data 2020/08/06
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DecryptResult {

    private byte[] privateKey;

    private String eccType;

}
