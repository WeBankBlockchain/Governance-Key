/**
 * Copyright (C) 2018 webank, Inc. All Rights Reserved.
 */

package com.webank.keymgr.db.entity;

import lombok.Data;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.io.Serializable;

/**
 * 
 * IdEntity
 *
 * @Description: IdEntity
 * @author graysonzhang
 * @data 2019-07-12 15:11:57
 *
 */
@Data
@MappedSuperclass
@Accessors(chain = true)
public abstract class IdEntity implements Serializable {

    private static final long serialVersionUID = 5903397383140175895L;
    /** @Fields pkId : primary key */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pk_id")
    protected Long pkId;
}
