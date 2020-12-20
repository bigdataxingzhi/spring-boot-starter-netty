package org.springframework.boot.netty.handler;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.netty.codec.proto.SpeakMessage;
import org.springframework.boot.netty.converter.MessageConverter;
import org.springframework.boot.netty.converter.ProtobufMessage2JsonConverter;
import org.springframework.boot.netty.exception.ConvertException;
import org.springframework.boot.netty.handler.common.AbstractChannelInboundHandlerAdapter;
import org.springframework.boot.netty.listener.Message;
import org.springframework.boot.netty.service.UserDetailService;
import org.springframework.context.ApplicationContext;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Author: huoxingzhi
 * Date: 2020/12/14
 * Email: hxz_798561819@163.com
 */
public class DefaultProtoBufClientHandler extends AbstractChannelInboundHandlerAdapter {


    private final static Logger logger = LoggerFactory.getLogger(DefaultProtoBufClientHandler.class);

    private MessageConverter messageConverter;

    private UserDetailService userDetailService;

    private Timer timer;

    public DefaultProtoBufClientHandler() {
    }


    public DefaultProtoBufClientHandler(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
        this.messageConverter = this.applicationContext.getBean("protoBufMessageConverter",ProtobufMessage2JsonConverter.class);
        this.userDetailService = this.applicationContext.getBean(UserDetailService.class);
    }
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        // 获取服务端传输过来的消息
        if(!com.google.protobuf.Message.class.isAssignableFrom(msg.getClass())){
            throw new ConvertException("DefaultProtoBufServerHandler.clas-->channelRead-->"+msg.getClass()+"can not convert com.google.protobuf.Message.class");
        }

        com.google.protobuf.Message content = (com.google.protobuf.Message) msg;

        String jsonContent = messageConverter.fromMessage(content,String.class);


        super.channelRead(ctx,jsonContent);

    }


    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        super.channelRegistered(ctx);
        timer = new Timer();
    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx,cause);
        timer.cancel();
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        super.handlerRemoved(ctx);
        timer.cancel();
    }


    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        //连接建立以后定时给server端发消息
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                ctx.writeAndFlush(
                        SpeakMessage.newBuilder()
                        .setText("active")
                        .setId(userDetailService.getUserClientId(ctx.channel()))
                        .build());
            }
        },0,8000);

    }

}
