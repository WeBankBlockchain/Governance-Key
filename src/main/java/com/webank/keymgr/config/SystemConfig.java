/**
 * Copyright (C) 2018 webank, Inc. All Rights Reserved.
 */
package com.webank.keymgr.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

import java.io.File;

@Configuration
@ConfigurationProperties("system")
@Data
@Order(Ordered.HIGHEST_PRECEDENCE )
public class SystemConfig {
    /**
     * file or db. Default use file.
     */
    private String mgrStyle;
    /**
     * if mgrStyle==0, then dataFileDir indicates the path of file key output
     */
    private String dataFileDir=System.getProperty("user.home")+ File.separator+".keymgr";

    /**
     * if mgrStye==1， then storePwd indicates whether store password
     */
    private boolean storePwd;
    /**
     * keystore,p12
     */
    private String keyEncType = "keystore";
    /**
     * secp256k1, sm2p256v1
     */
    private String eccType = "secp256k1";
}
