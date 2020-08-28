package com.webank.keysign.service;

import com.webank.keysign.utils.StringUtils;

import org.junit.Assert;
import org.junit.Test;

public class StringTest {

    @Test
    public void testEmpty(){
        Assert.assertTrue(StringUtils.isEmpty(null));
        Assert.assertTrue(StringUtils.isEmpty(""));
        Assert.assertTrue(StringUtils.isEmpty(" "));
        Assert.assertTrue(StringUtils.isEmpty(" "));
        Assert.assertTrue(StringUtils.isEmpty("     "));
        Assert.assertFalse(StringUtils.isEmpty("  a "));
    }
}
