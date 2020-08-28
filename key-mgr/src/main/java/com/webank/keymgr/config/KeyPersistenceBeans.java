package com.webank.keymgr.config;

import com.webank.keymgr.db.DatabasePersistenceService;
import com.webank.keymgr.file.FilePersistenceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.File;
import java.io.IOException;

/**
 * @author aaronchu
 * @Description
 * @data 2020/06/22
 */
@Configuration
@Slf4j
public class KeyPersistenceBeans {

    @Autowired
    private SystemConfig config;

    @Value("${system.mgrStyle:file}")
    private String mgrStyle;

    @Bean
    @ConditionalOnExpression("'${system.mgrStyle:file}'.equals('file')")
    public FilePersistenceService fileStorageService() throws IOException{
        checkFilePath();
        return new FilePersistenceService();
    }

    @Bean
    @ConditionalOnExpression("'db'.equals('${system.mgrStyle:file}')")
    public DatabasePersistenceService dbStorageService(){
        return new DatabasePersistenceService();
    }

    private void checkFilePath() throws IOException{
        File dir = new File(config.getDataFileDir());
        if(!dir.exists() || !dir.isDirectory()){
            if(!dir.mkdir()){
                throw new IOException("invalid system.dataFileDir!");
            }
            log.info("dir created {}", config.getDataFileDir());
        }
    }
}


