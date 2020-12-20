package org.springframework.boot.netty.handler.common;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.boot.autoconfigure.netty.NettyProperties;
import org.springframework.boot.netty.service.UserDetailService;
import org.springframework.boot.netty.enums.MessageEnum;
import org.springframework.boot.netty.listener.Message;
import org.springframework.boot.netty.listener.WebSocketEvent;
import org.springframework.boot.netty.sync.MessageBlockQueue;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEventPublisher;

/**
 * Author: huoxingzhi
 * Date: 2020/12/14
 * Email: hxz_798561819@163.com
 */
public abstract class AbstractWebSocketChannelInboundHandlerAdapter<T> extends SimpleChannelInboundHandler<T> {

    // 用于记录和管理所有客户端的channle，必须为static，被都类对象共享
    public static ChannelGroup users =
            new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    protected static WebSocketEvent event = new WebSocketEvent(MessageEnum.RECEIVE);

    protected ApplicationContext applicationContext;

    protected ApplicationEventPublisher applicationEventPublisher;

    protected UserDetailService userDetailService;

    protected NettyProperties nettyProperties;

    private final static Logger logger = LoggerFactory.getLogger(AbstractWebSocketChannelInboundHandlerAdapter.class);




    /**
     * 当客户端连接服务端之后（打开连接）
     * 获取客户端的channle，并且放到ChannelGroup中去进行管理
     */
    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        users.add(ctx.channel());
        event.setMessage(MessageEnum.HANDLER_CONNECTION);
        event.setMapIndex(getPartition(ctx.channel().hashCode(),this.nettyProperties.getMessageMapCapacity()));
        this.applicationEventPublisher.publishEvent(event);


    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {

        String channelId = ctx.channel().id().asShortText();
        logger.debug("客户端被移除，channelId为：{}" ,channelId);
        // 当触发handlerRemoved，ChannelGroup会自动移除对应客户端的channel
        users.remove(ctx.channel());
        event.setMessage(MessageEnum.HANDLER_REMOVED);
        this.applicationEventPublisher.publishEvent(event);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        // 发生异常之后关闭连接（关闭channel），随后从ChannelGroup中移除
        ctx.channel().close();
        users.remove(ctx.channel());
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        logger.debug("channel注册了");
        super.channelRegistered(ctx);
        event.setMapIndex(getPartition(ctx.channel().hashCode(),this.nettyProperties.getMessageMapCapacity()));
        event.setMessage(MessageEnum.CHANNEL_REGISTERED);
        this.applicationEventPublisher.publishEvent(event);
        if(MessageBlockQueue.isNeedInit){
            MessageBlockQueue.initMessageQueue(this.nettyProperties.getMessageMapCapacity(),this.nettyProperties.getMessageQueueCapatity());
        }
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        logger.debug("channel 活跃");
        event.setMessage(MessageEnum.CHANNEL_ACTIVE);
        this.applicationEventPublisher.publishEvent(event);
        super.channelActive(ctx);
    }

    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {

        this.applicationContext = applicationContext;

    }


    public void afterPropertiesSet(ApplicationEventPublisher applicationEventPublisher){
        this.userDetailService = this.applicationContext.getBean(UserDetailService.class);
        this.applicationEventPublisher = applicationEventPublisher;
        this.nettyProperties = this.applicationContext.getBean(NettyProperties.class);
    }

    public int getPartition(int hashCode, int numReduceTasks) {

        return (hashCode & Integer.MAX_VALUE) % numReduceTasks;

    }



    @Override
    protected abstract void channelRead0(ChannelHandlerContext ctx, T msg) throws Exception;
}
