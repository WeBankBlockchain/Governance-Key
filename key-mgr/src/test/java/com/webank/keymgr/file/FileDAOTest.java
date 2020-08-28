package com.webank.keymgr.file;

import com.webank.keymgr.config.SystemConfig;
import com.webank.keymgr.file.dao.FileKeyDAO;
import com.webank.keymgr.file.entity.SingleKey;
import com.webank.keymgr.file.entity.UserKeys;
import com.webank.keymgr.utils.JacksonUtils;
import org.junit.Test;

/**
 * @author aaronchu
 * @Description
 * @data 2020/06/22
 */
public class FileDAOTest {

    @Test
    public void test() throws Exception{
        SingleKey singleKey = new SingleKey();
        singleKey.setUserId("user1");
        singleKey.setKeyAddress("dummyAddr");
        singleKey.setEncryptKey("dummyEncrypt");
        singleKey.setKeyName("dummyName");
        UserKeys userKeys = new UserKeys();
        userKeys.setUserId("user1");
        userKeys.addKey(singleKey);

        SystemConfig systemConfig = new SystemConfig();
        systemConfig.setMgrStyle("file");
        systemConfig.setEccType("secp256k1");
        systemConfig.setDataFileDir("C:\\Users\\aaronchu\\Desktop");
        FileKeyDAO dao = new FileKeyDAO(systemConfig);
        dao.saveUserKeys(userKeys);

        UserKeys recovered = dao.loadUserKeys("user1");
        System.out.println(JacksonUtils.toJson(recovered));
    }

}
