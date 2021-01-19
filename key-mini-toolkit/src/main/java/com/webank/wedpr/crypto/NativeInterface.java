package com.webank.wedpr.crypto;

import com.webank.wedpr.utils.EnvironmentUtils;
import com.webank.wedpr.utils.NativeUtils;

import java.io.IOException;

/**
 * Native interface for signature
 * @author aaronchu
 * @Description
 * @data 2020/06/18
 */
public class NativeInterface {

    private static final String LIB_FFI_RESOURCE_PATH;
    private static final String LIB_FFI_NAME = "WeDPR_dynamic_lib/libffi_java_sdk";
    static {
        String os = System.getProperty("os.name");
        String tail = EnvironmentUtils.getResourceTailByOs(os);
        LIB_FFI_RESOURCE_PATH = LIB_FFI_NAME + tail;
        try {
            NativeUtils.loadLibrary(LIB_FFI_RESOURCE_PATH);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load library", e);
        }
    }

    public static native CryptoResult secp256k1Sign(String priKeyHex, String message);
    public static native CryptoResult secp256k1verify(String pubKeyHex, String message, String signature);
    public static native CryptoResult secp256k1keyPair();
    public static native CryptoResult sm2Sign(String priKeyHex, String message);
    public static native CryptoResult sm2SignWithPub(String priKeyHex, String pubKeyHex,String message);
    public static native CryptoResult sm2verify(String pubKeyHex, String message, String signature);
    public static native CryptoResult sm2keyPair();
    public static native CryptoResult keccak256(String messageHex);
    public static native CryptoResult sm3(String messageHex);

    private static String resolveLibTail(String os){
        os = os.toLowerCase();
        String tail;
        if(os.contains("windows")){
            tail = ".dll";
        }
        else if(os.contains("linux")){
            tail = ".so";
        }
        else if(os.contains("mac")){
            tail = ".dylib";
        }
        else{
            throw new RuntimeException("Unsupported os: "+os);
        }
        return tail;
    }

    private NativeInterface(){}
}
