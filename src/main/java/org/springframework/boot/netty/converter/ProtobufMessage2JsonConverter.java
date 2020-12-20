package org.springframework.boot.netty.converter;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.serializer.PropertyFilter;
import com.google.common.collect.Lists;
import com.google.protobuf.Message;
import com.google.protobuf.util.JsonFormat;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.boot.netty.exception.ConvertException;
import org.springframework.boot.netty.support.JsonUtil;
//import org.springframework.core.convert.converter.Converter;

import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.charset.Charset;
import java.util.concurrent.ConcurrentHashMap;
 
/**
 * Support Proto3
 */
public class ProtobufMessage2JsonConverter implements MessageConverter {
 
    public static final Charset DEFAULT_CHARSET = Charset.forName("UTF-8");

    private static final ConcurrentHashMap<Class<?>, Method> METHOD_CACHE;

    /*在response中需要保留的字段名*/
    private static final List<String> usableFieldNames = Lists.newArrayList();
    /*在response中需要删除的字段*/
    private static final Map<String,String> unusableFields = new HashMap<String,String>(16);


    static {
        METHOD_CACHE = new ConcurrentHashMap<>();
    }
 
    public ProtobufMessage2JsonConverter() {

    }

    protected static boolean supports(Class<?> clazz) {

        return Message.class.isAssignableFrom(clazz);
    }


    /**
     * protobuf 格式化为 JSON
     * @param object
     * @param clazz
     * @param <T>
     * @return
     * @throws ConvertException
     */
    @Override
    public <T> T fromMessage(Object object, Class<T> clazz) throws ConvertException {
        try {
            return (T) writeInternal((Message) object);
        } catch (IOException e) {
            return (T) "";
        }
    }


    /**
     * JSON 格式化为 protobuf
     * @param clazz
     * @param object
     * @return
     */
    @Override
    public Object toMessage(Class<?> clazz, Object object) {
        byte[] bytes = JsonUtil.obj2String(object).getBytes();
        return readInternal(clazz,bytes);
    }

    /**
     * convert com.google.protoBuf Message to jsonString
     * @param message
     * @return
     * @throws IOException
     */
    private String writeInternal(Message message) throws IOException {

            String resultJson =
                    JsonFormat.printer()
                            .includingDefaultValueFields()
                            .preservingProtoFieldNames()
                            .omittingInsignificantWhitespace()
                            .print(message);
            return resultJson;
    }


    private Message readInternal(Class<?> clazz, byte[] body) {

        if(!supports(clazz)){
            throw new RuntimeException("");
        }
        Message.Builder builder;
        try {
            builder = getMessageBuilder(clazz);
            JsonFormat.parser()
                    .ignoringUnknownFields()
                    .merge(new String(body, DEFAULT_CHARSET), builder);

            return  builder.build();
        } catch (Exception e) {
            throw new RuntimeException("");
        }
    }

    /**
     * get protobuf Mesage.Builder from given Class
     * @param clazz
     * @return
     * @throws NoSuchMethodException
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    private  Message.Builder getMessageBuilder(Class<?> clazz) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method method = METHOD_CACHE.get(clazz);
        if (method == null) {
            method = clazz.getMethod("newBuilder", new Class[0]);
            METHOD_CACHE.put(clazz, method);
        }
        return (Message.Builder) method.invoke(clazz, new Object[0]);
    }
 

    /**
     * 过滤掉响应中的一些空字段（或使用proto默认值的字段）
     * <p>1.枚举类型字段，响应中都是有意义的值，如果使用枚举默认值则删除</p>
     * <p>2.字符串/数组类型字段，为空则过滤掉</p>
     * <p>3.数字类型字段，除fieldNames定义的字段外值为0的则过滤掉</p>
     */
    private String formatJsonString(String input) {
        PropertyFilter filter = (Object o, String name, Object value)
            -> {
            boolean usable = true;
 
            if (value instanceof JSONArray && ((JSONArray) value).isEmpty()) {
                usable = false;
            } else if (value instanceof Number
                && !usableFieldNames.contains(name)
                && ((Number) value).doubleValue() == NumberUtils.DOUBLE_ZERO) {
                usable = false;
            } else if (value instanceof String && StringUtils.isBlank((String) value)) {
                usable = false;
            } else if (unusableFields.containsKey(name)
                && Objects.equals(unusableFields.get(name), value)) {
                usable = false;
            }
            return usable;
        };
 
        return JSON.toJSONString(JSON.parseObject(input), filter);
    }



}