package com.webank;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * @author aaronchu
 * @Description
 * @data 2020/12/22
 */
@Component
@Slf4j
public class Starter implements CommandLineRunner {

    @Value("${server.port}")
    private int port;

    @Override
    public void run(String... args) throws Exception {
       // System.out.println("开始自动加载指定的页面");
        try {
            Runtime.getRuntime().exec("cmd   /c   start   http://localhost:"+port);
        } catch (Exception ex) {
            log.warn("Failed to automatically start browser{}",ex.getMessage());
        }
        log.info("Please visit http://localhost:{}",port);
    }
}