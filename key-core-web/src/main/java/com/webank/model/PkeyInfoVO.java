package com.webank.model;

import lombok.Data;

@Data
public class PkeyInfoVO {

    private String privateKeyHex;

    private String pubKeyHex;

    private String address;

}
