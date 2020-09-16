package com.webank.wedpr.utils;

import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

/**
 * load native resource
 * @author aaronchu
 * @Description
 * @data 2020/06/18
 */
public class NativeUtils {

    public static void loadLibrary(String resourcePath) throws IOException{
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        loadLibrary(resourcePath, classLoader);
    }

    public static void loadLibrary(String resourcePath, ClassLoader classLoader) throws IOException {
        File tmpDir = new File(System.getProperty("user.home"));
        if (!tmpDir.exists() || !tmpDir.isDirectory()) {
            throw new IOException("user dir unavailable");
        }
        File ffiDir = new File(tmpDir, "nativeutils");
        if (!ffiDir.exists() || !ffiDir.isDirectory()) {
            if (!ffiDir.mkdir()) {
                throw new IOException("failed to create temp folder");
            }
        }
        String fileName = deduceFileName(resourcePath);
        File tmpFile = new File(ffiDir, fileName);
        try (InputStream input = classLoader.getResourceAsStream(resourcePath);) {
            if (input == null) {
                throw new IOException("Resource not found:" + resourcePath + " for classloader " + classLoader.toString());
            }
            Files.copy(input, tmpFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
        }


        System.load(tmpFile.getAbsolutePath());
    }

    private static String deduceFileName(String path){
        String[] parts = path.split("/");
        if(parts.length > 0){
            return parts[parts.length-1];
        }
        throw new IllegalArgumentException("invalid path "+path);
    }

    private NativeUtils(){}
}
