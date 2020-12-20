package org.springframework.boot.netty.handler;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.netty.converter.MessageConverter;
import org.springframework.boot.netty.converter.ProtobufMessage2JsonConverter;
import org.springframework.boot.netty.exception.ConvertException;
import org.springframework.boot.netty.handler.common.AbstractChannelInboundHandlerAdapter;
import org.springframework.boot.netty.listener.Message;
import org.springframework.boot.netty.sync.MessageBlockQueue;
import org.springframework.context.ApplicationContext;

/**
 * Author: huoxingzhi
 * Date: 2020/12/14
 * Email: hxz_798561819@163.com
 */
public class DefaultProtoBufServerHandler extends AbstractChannelInboundHandlerAdapter {


    private final static Logger logger = LoggerFactory.getLogger(DefaultProtoBufServerHandler.class);

    private MessageConverter messageConverter;

    public DefaultProtoBufServerHandler() {
    }


    public DefaultProtoBufServerHandler(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
        this.messageConverter = this.applicationContext.getBean("protoBufMessageConverter",ProtobufMessage2JsonConverter.class);
    }
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        // 获取客户端传输过来的消息
        if(!com.google.protobuf.Message.class.isAssignableFrom(msg.getClass())){
            throw new ConvertException("DefaultProtoBufServerHandler.clas-->channelRead-->"+msg.getClass()+"can not convert com.google.protobuf.Message.class");
        }

        com.google.protobuf.Message content = (com.google.protobuf.Message) msg;

        String jsonContent = messageConverter.fromMessage(content,String.class);

        super.channelRead(ctx,jsonContent);

    }

}
