package com.chat.imbackend.util;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class JsonUtils {

    // 定义jackson对象
    private static ObjectMapper MAPPER = new ObjectMapper();

    {
        MAPPER.setSerializationInclusion(JsonInclude.Include.ALWAYS);
    }

    /**
     * 将对象转换成json字符串。
     * <p>Title: pojoToJson</p>
     * <p>Description: </p>
     * @param data
     * @return
     */
    public static String objectToJson(Object data) {
        try {
            return MAPPER.writeValueAsString(data);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static JsonNode stringToJsonNode(String data) {
        try {
            return MAPPER.readTree(data);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 将json结果集转化为对象
     *
     * @param jsonData json数据
     * @param clazz 对象中的object类型
     * @return
     * @throws Exception
     */
    public static <T> T objectToPojo(Object jsonData, Class<T> beanType) throws Exception {
        T t = MAPPER.readValue(MAPPER.writeValueAsString(jsonData), beanType);
        return t;
    }

    /**
     * 将json结果集转化为对象
     *
     * @param jsonData json数据
     * @param clazz 对象中的object类型
     * @return
     * @throws Exception
     */
    public static <T> T jsonToPojo(String jsonData, Class<T> beanType) throws Exception {
        T t = MAPPER.readValue(jsonData, beanType);
        return t;
    }

    /**
     * 将json数据转换成pojo对象list
     * <p>Title: jsonToList</p>
     * <p>Description: </p>
     * @param jsonData
     * @param beanType
     * @return
     */
    public static <T> List<T> jsonToList(String jsonData, Class<T> beanType) {
        JavaType javaType = MAPPER.getTypeFactory().constructParametricType(List.class, beanType);
        try {
            return MAPPER.readValue(jsonData, javaType);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * 将object数据转换成pojo对象list
     * <p>Title: jsonToList</p>
     * <p>Description: </p>
     * @param jsonData
     * @param beanType
     * @return
     */
    public static <T>List<T> objectToList(Object jsonData, Class<T> beanType) {
        JavaType javaType = MAPPER.getTypeFactory().constructParametricType(List.class, beanType);
        try {
            return MAPPER.readValue(MAPPER.writeValueAsString(jsonData), javaType);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * 将JSON数据转换成Map
     *
     * @param jsonData
     * @return
     */
    public static Map jsonToMap(String jsonData) {
        try {
            return MAPPER.readValue(jsonData, Map.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
