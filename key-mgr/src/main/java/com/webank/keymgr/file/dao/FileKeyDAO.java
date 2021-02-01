package com.webank.keymgr.file.dao;

import com.webank.keymgr.config.SystemConfig;
import com.webank.keymgr.file.entity.UserKeys;
import com.webank.keymgr.utils.JacksonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;

/**
 * @author aaronchu
 * @Description
 * @data 2020/06/22
 */
@Service
@Slf4j
public class FileKeyDAO {

    private static final String FILE_KEY_TAIL = ".pkey";

    @Autowired
    private SystemConfig systemConfig;

    public FileKeyDAO(SystemConfig systemConfig){
        this.systemConfig = systemConfig;
    }

    public UserKeys loadUserKeys(String userId) {
        String userKeyPath = createFilePath(userId);
        File userKeyFile = new File(userKeyPath);
        if(!userKeyFile.exists()){
            return new UserKeys().setUserId(userId);
        }
        try{
            byte[] bytes = Files.readAllBytes(userKeyFile.toPath());
            String content = new String(bytes);
            UserKeys result = JacksonUtils.fromJson(content, UserKeys.class);
            return result;
        }
        catch (Exception ex){
            log.error("Exception on loading user key",ex);
            return null;
        }
    }

    public void saveUserKeys(UserKeys userKeys) throws IOException {
        String userKeyPath = createFilePath(userKeys.getUserId());
        File userKeyFile = new File(userKeyPath);
        String json = JacksonUtils.toJson(userKeys);
        Files.write(userKeyFile.toPath(), json.getBytes(), StandardOpenOption.TRUNCATE_EXISTING,StandardOpenOption.CREATE);
        log.info("user keys writen to {}",userKeyFile.getAbsolutePath());
    }


    public void removeUser(String userId) throws IOException {
        String userKeyPath = createFilePath(userId);
        File userKeyFile = new File(userKeyPath);
        Files.delete(userKeyFile.toPath());
    }

    private String createFilePath(String userId){
        StringBuilder sb = new StringBuilder();
        sb.append(systemConfig.getDataFileDir());
        sb.append(File.separator);
        sb.append(userId);
        sb.append(FILE_KEY_TAIL);
        return sb.toString();
    }

}
