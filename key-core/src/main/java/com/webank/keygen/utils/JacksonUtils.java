/**
 * Copyright (C) 2018 webank, Inc. All Rights Reserved.
 */

package com.webank.keygen.utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.webank.keygen.exception.JacksonException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class JacksonUtils {
    public static final ObjectMapper objectMapper = new ObjectMapper();

    static {
        objectMapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
        objectMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_CONTROL_CHARS, true);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    public static <T> T fromJson(String json, Class<T> clazz) {
        try {
            return fromJsonWithException(json, clazz);
        } catch (Exception e) {
            log.error("json is: " + json, e);
            return null;
        }
    }

    public static <T> T fromJson(String json, Class<T> c, Class<?>...t) {
        try {
            return fromJsonWithException(json, c, t);
        } catch (IOException e) {
            throw new JacksonException(e);
        }
    }

    public static <T> T fromJson(String json, JavaType type) {
        try {
            return fromJsonWithException(json, type);
        } catch (IOException e) {
            throw new JacksonException(e);
        }
    }

    public static <T> T fromJsonWithException(String json, Class<T> clazz)
            throws JsonParseException, JsonMappingException, IOException {
        return objectMapper.readValue(json, clazz);
    }

    public static <T> T fromJsonWithException(String json, Class<T> c, Class<?>...t)
            throws JsonParseException, JsonMappingException, IOException {
        JavaType javaType = objectMapper.getTypeFactory().constructParametricType(c, t);
        return objectMapper.readValue(json, javaType);
    }

    public static <T> T fromJsonWithException(String json, JavaType type)
            throws JsonParseException, JsonMappingException, IOException {
        @SuppressWarnings("unchecked")
        T ret = (T) objectMapper.readValue(json, type);
        return ret;
    }

    public static <T> List<T> fromJsonList(String json, Class<T> c) {
        try {
            return fromJsonListWithException(json, c);
        } catch (IOException e) {
            throw new JacksonException(e);
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> List<T> fromJsonListWithException(String json, Class<T> c) throws IOException {
        JavaType type = getCollectionType(ArrayList.class, c);
        return (List<T>) objectMapper.readValue(json, type);
    }

    public static JavaType getCollectionType(Class<?> collectionClass, Class<?>...elementClasses) {
        return objectMapper.getTypeFactory().constructParametricType(collectionClass, elementClasses);
    }

    public static String toJsonWithException(Object o) throws JsonProcessingException {
        return objectMapper.writeValueAsString(o);
    }

    public static String toJson(Object o) {
        try {
            return toJsonWithException(o);
        } catch (Exception e) {
            throw new JacksonException(e);
        }
    }

    public static <T, K> Map<T, K> convertValue(Object req, Class<T> keyClazz, Class<K> valueClazz) {
        Map<T, K> ret = objectMapper.convertValue(req,
                objectMapper.getTypeFactory().constructMapType(Map.class, keyClazz, valueClazz));
        return ret;
    }

    public static <T> T convertMap(@SuppressWarnings("rawtypes") Map map, Class<T> retClazz) {
        return objectMapper.convertValue(map, retClazz);
    }

    public static <T> T strToObject(String str, Class<T> clazz) {
        ObjectMapper mapper = new ObjectMapper();
        T t = null;
        try {
            t = mapper.readValue(str, clazz);
            return t;
        } catch (IOException e) {
            log.error("read value failed",e);
        }
        return t;
    }

    public static <T> String objectToStr(T t) {
        ObjectMapper mapper = new ObjectMapper();
        String str = null;
        try {
            str = mapper.writeValueAsString(t);
        } catch (IOException e) {
            log.error("write value failed",e);
        }
        return str;
    }

}
