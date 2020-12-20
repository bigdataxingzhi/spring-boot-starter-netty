package org.springframework.boot.autoconfigure.netty.testProtoBuf;

import com.google.common.collect.Maps;
import com.google.protobuf.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.netty.codec.CRC32Validate;
import org.springframework.boot.netty.codec.proto.Frame;
import org.springframework.boot.netty.codec.proto.TextMessage;
import org.springframework.boot.netty.converter.ProtobufMessage2JsonConverter;
import org.springframework.boot.netty.exception.ConvertException;
import org.springframework.boot.netty.support.ProtoBufUtil;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;

/**
 * Author: huoxingzhi
 * Date: 2020/12/15
 * Email: hxz_798561819@163.com
 */

public class Test {

    private final ProtobufMessage2JsonConverter protobufMessage2JsonConverter = new ProtobufMessage2JsonConverter();
    private final static Logger logger = LoggerFactory.getLogger(Test.class);

    @org.junit.jupiter.api.Test
    public void testProtoBufConverter() throws InvocationTargetException, IllegalAccessException {
        // 按照定义的数据结构，创建一个对象
        UserInfo.UserMsg.Builder userInfo = UserInfo.UserMsg.newBuilder();
        userInfo.setId(1);
        userInfo.setName("xuwujing");
        userInfo.setAge(18);
        UserInfo.UserMsg userMsg = userInfo.build();
        logger.info("send data crc32 code : {}",CRC32Validate.getInstance().encrypt(userMsg.toByteArray()));

        Frame.Builder frame = Frame.newBuilder();
        System.out.println(UserInfo.UserMsg.class.getName());
        frame.setHead(UserInfo.UserMsg.class.getSimpleName());
        frame.setPayload(userMsg.toByteString());
        frame.setCrcCode(CRC32Validate.getInstance().encrypt(userMsg.toByteString().toByteArray())+"" );
        Frame build = frame.build();

        logger.info("receive data crc32 code : {}",CRC32Validate.getInstance().encrypt(build.getPayload().toByteArray()));
        // test convert exception
        Map<String,Class<?>> classes = Maps.newHashMap();
        classes.put(TextMessage.class.getSimpleName(),TextMessage.class);
        classes.put(UserInfo.UserMsg.class.getSimpleName(),UserInfo.UserMsg.class);
        testConverter(ProtoBufUtil.parse(build,classes));
    }

    public void testConverter(Object msg){
        // 获取客户端传输过来的消息
        if(!com.google.protobuf.Message.class.isAssignableFrom(msg.getClass())){
            throw new ConvertException("DefaultProtoBufServerHandler.clas-->channelRead-->"+msg.getClass()+"can not convert com.google.protobuf.Message.class");
        }

        com.google.protobuf.Message content = (com.google.protobuf.Message) msg;
        logger.info("fromat json String is :{}",content.getClass().getName());

        String jsonContent = protobufMessage2JsonConverter.fromMessage(content,String.class);
        // {"id":1,"name":"xuwujing","age":18,"state":0}
        logger.info("fromat json String is :{}",jsonContent);

        Message message = (Message) protobufMessage2JsonConverter.toMessage(UserInfo.UserMsg.class, jsonContent);
        String jsonContent2 = protobufMessage2JsonConverter.fromMessage(message,String.class);
        logger.info("fromat json String is :{}",jsonContent2);
        logger.info("fromat json String is :{}",message.getClass().getName());


    }
}
