package com.webank.keysign.utils;

public class StringUtils {
    public static boolean isEmpty(String str){
        if(str == null || str.isEmpty()) return true;
        for(int i=0;i<str.length();i++){
            char ch = str.charAt(i);
            if(ch == ' ' || ch == '\t' || ch == '\r' || ch == '\n'){
                continue;
            }
            return false;
        }
        return true;
    }

    public static String removeHexPrefix(String hex){
        if(hex.startsWith("0x") || hex.startsWith("0X")){
            return hex.substring(2);
        }
        return hex;
    }

    private StringUtils(){}
}
