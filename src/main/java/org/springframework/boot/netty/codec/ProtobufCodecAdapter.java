package org.springframework.boot.netty.codec;

import com.google.protobuf.Message;
import com.google.protobuf.MessageLite;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageCodec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.boot.autoconfigure.netty.NettyProperties;
import org.springframework.boot.autoconfigure.netty.support.ScanSupport;
import org.springframework.boot.netty.codec.proto.Frame;
import org.springframework.boot.netty.exception.ConvertException;
import org.springframework.boot.netty.support.ProtoBufUtil;
import org.springframework.context.ApplicationContext;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Author: huoxingzhi
 * Date: 2020/12/14
 * Email: hxz_798561819@163.com
 */
public class ProtobufCodecAdapter extends MessageToMessageCodec<Frame, MessageLite> {

    private ApplicationContext applicationContext;

    private ScanSupport scanSupport = null;

    private NettyProperties nettyProperties;

    private Map<String,Class<?>> cachedProtoBufBeanClass = new HashMap<>();

    private final static Logger logger = LoggerFactory.getLogger(ProtobufCodecAdapter.class);


    public ProtobufCodecAdapter() {
        super();
    }

    public ProtobufCodecAdapter(ApplicationContext applicationContext) {
        super();
        this.applicationContext = applicationContext;
        this.afterPropertiesSet();
    }

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, MessageLite messageLite, List<Object> list) throws Exception {
        //messageName 为全类名 方便从jvm中加载类
        list.add(Frame.newBuilder()
                //.setMessageName(messageLite.getClass().getName())
                .setPayload(messageLite.toByteString())
                .build());
    }

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, Frame frame, List<Object> list) throws Exception {
        //数据进行crc32校验，防止篡改。
        validMessageByCrc32(frame);
        // 根据Frame类解析出其中的body
        list.add(ProtoBufUtil.parse(frame,this.cachedProtoBufBeanClass));
    }


    private void validMessageByCrc32(Frame frame){

        String originalCode = frame.getCrcCode();
        String targetCrc32Code = String.valueOf(CRC32Validate.getInstance().encrypt(frame.getPayload().toByteArray()));
        if(!originalCode.equals(targetCrc32Code)){
            throw new ConvertException(getClass().getName()+"frame.getPayload() is incorrect");
        }

    }



    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }


    public void afterPropertiesSet() {
        this.nettyProperties = this.applicationContext.getBean(NettyProperties.class);
        try {
            this.scanSupport = this.applicationContext.getBean("scanSupport", ScanSupport.class);
            scanSupport
                    .doScan(this.nettyProperties.getProtoBufBasePackage())
                    .stream()
                    .filter(protoBufBeanClass -> {
                        boolean filtered = Message.class.isAssignableFrom(protoBufBeanClass);
                        return filtered;
                    })
                    .forEach(protoBufBeanClass -> {
                this.cachedProtoBufBeanClass.put(protoBufBeanClass.getSimpleName(),protoBufBeanClass);
            });

        }catch (BeansException e){
            logger.debug("IOC container not found bean of scanSupport.class");
        } catch (IOException e) {
            throw new ConvertException("no package of "+this.nettyProperties.getProtoBufBasePackage());
        }

    }
}
