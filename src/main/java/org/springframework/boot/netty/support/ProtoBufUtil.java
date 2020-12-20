package org.springframework.boot.netty.support;

import com.google.protobuf.ByteString;
import com.google.protobuf.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.netty.codec.proto.Frame;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

public class ProtoBufUtil {

    private final static Logger logger = LoggerFactory.getLogger(ProtoBufUtil.class);
    /**
     * 根据Frame类解析出其中的body
     *根据全类名加载类
     * @param msg
     * @return
     */
    public static Message parse(Frame msg, Map<String,Class<?>> cachedProtoBufBeanClass)  {
        Object invoke = null;
        Class<?> messageClass = cachedProtoBufBeanClass.get(msg.getHead());
        if(messageClass==null){
            throw new RuntimeException("unknown Message type :" + msg.getHead());
        }
        String protoBufBeanClassName = messageClass.getName();
        ByteString body = msg.getPayload();
            Class clazz = null;
            try {
                // 根据全类名加载类
                clazz = Class.forName(protoBufBeanClassName);
            } catch (ClassNotFoundException e) {
               throw new RuntimeException("unknown Message type :" + protoBufBeanClassName);
            }
            Method method = null;
            try {
                //执行解析方法
                /**
                 *   public static org.springframework.boot.netty.codec.proto.Frame parseFrom(
                 *       com.google.protobuf.ByteString data)
                 *       throws com.google.protobuf.InvalidProtocolBufferException {
                 *     return PARSER.parseFrom(data);
                 *   }
                 */
                method = clazz.getMethod("parseFrom", ByteString.class);
            } catch (NoSuchMethodException e) {
                throw new RuntimeException("unknown Message type :" + protoBufBeanClassName);
            }

            try {
               invoke = method.invoke(null, body);
            } catch (IllegalAccessException e) {
                throw new RuntimeException("unknown Message type :" + protoBufBeanClassName);
            } catch (InvocationTargetException e) {
                throw new RuntimeException("unknown Message type :" + protoBufBeanClassName);
            }

            return (Message)invoke;
        }


}
