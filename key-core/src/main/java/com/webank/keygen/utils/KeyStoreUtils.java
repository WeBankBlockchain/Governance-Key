/*
 * Copyright 2014-2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.webank.keygen.utils;


import com.webank.keygen.enums.KeyFileTypeEnums;
import lombok.extern.slf4j.Slf4j;

import java.io.File;

/**
 * KeyFileUtils
 *
 * @Description: KeyFileUtils
 * @author graysonzhang
 * @date 2020-01-02 14:15:13
 *
 */
@Slf4j
public class KeyStoreUtils {

    /*
    public static void writeFormatKey(KeyFormatInfo keyFormatInfo, String destinationDirectory)
            throws PkeyGenException, JsonGenerationException, JsonMappingException, IOException {
        KeyFileTypeEnums fileType = KeyFileTypeEnums.getKeyFileTypeEnum(keyFormatInfo.getFormatType());
        if(fileType == null){
            throw new PkeyGenException(ExceptionCodeEnums.KEY_FORMAT_TYPE_ERROR);
        }
        String address = keyFormatInfo.getAddress();
        address = address.startsWith("0x")?address:"0x"+address;
        if(fileType == KeyFileTypeEnums.KEYSTORE_FILE){

            return filePath;
        }
        if(fileType == KeyFileTypeEnums.PEM_FILE){
            String fileName = address + fileType.getKeyFilePostfix();
            String filePath = FileOperationUtils.generateFilePath(fileName, destinationDirectory);
            FileOperationUtils.writeFile(filePath, keyFormatInfo.getFormatKey());
            return filePath;
        }
        if(fileType == KeyFileTypeEnums.P12_FILE){
            String fileName = address + fileType.getKeyFilePostfix();
            String filePath = FileOperationUtils.generateFilePath(fileName, destinationDirectory);
            FileOperationUtils.writeBinary(filePath, keyFormatInfo.getFormatKey());
            return filePath;
        }
        throw new PkeyGenException(ExceptionCodeEnums.KEY_FORMAT_TYPE_ERROR);
    }

     */
    
	public static boolean isValidFile(String filePath, KeyFileTypeEnums keyFileTypeEnums) {
		if(filePath == null) {
            log.error("file path cannot be null");
			return false;
		}
		if(!filePath.endsWith(keyFileTypeEnums.getKeyFilePostfix())) {
	        log.error("file path {} is not a {} format file", filePath, keyFileTypeEnums.getKeyFilePostfix());
	        return false;
		}
		File file = FileOperationUtils.getFile(filePath);
        if(!file.exists()){
            log.error("key data file does not exist : {}", filePath);
            return false;
        }
        return true;
	}
}
