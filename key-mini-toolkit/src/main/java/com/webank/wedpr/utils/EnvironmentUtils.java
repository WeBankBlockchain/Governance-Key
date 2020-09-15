package com.webank.wedpr.utils;

/**
 * @author aaronchu
 * @Description
 * @data 2020/06/19
 */
public class EnvironmentUtils {

    public static String getResourceTailByOs(String osName){
        if(osName == null || osName.isEmpty()){
            throw new IllegalArgumentException("osName cannot be null or empty");
        }
        osName = osName.toLowerCase();
        if(osName.contains("windows")) return ".dll";
        if(osName.contains("linux")) return ".so";
        if(osName.contains("mac")) return ".dylib";
        throw new IllegalArgumentException("does not support os :"+osName);
    }

    private EnvironmentUtils(){}
}
