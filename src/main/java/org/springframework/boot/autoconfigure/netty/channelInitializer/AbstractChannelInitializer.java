package org.springframework.boot.autoconfigure.netty.channelInitializer;

import com.google.common.collect.Maps;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.boot.netty.handler.common.NettyCustomerizerChannelInboundHandlerAdapterFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.util.CollectionUtils;

import java.util.Map;

/**
 * Author: huoxingzhi
 * Date: 2020/12/15
 * Email: hxz_798561819@163.com
 */
public class AbstractChannelInitializer extends ChannelInitializer<SocketChannel> implements SmartInitializingSingleton, ApplicationContextAware {

    protected ApplicationContext applicationContext;

    private Map<String, NettyCustomerizerChannelInboundHandlerAdapterFactory> compositechannelHandlerFactory = Maps.newHashMap();


    @Override
    public void afterSingletonsInstantiated() {
        Map<String, NettyCustomerizerChannelInboundHandlerAdapterFactory> channelHandlerFactory =
                this.applicationContext.getBeansOfType(NettyCustomerizerChannelInboundHandlerAdapterFactory.class);

        if(!CollectionUtils.isEmpty(channelHandlerFactory)){
            this.compositechannelHandlerFactory = channelHandlerFactory;
        }

    }


    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        initCustomerHandler(ch.pipeline(),compositechannelHandlerFactory);
    }

    private void initCustomerHandler(
            ChannelPipeline pipeline,
            Map<String, NettyCustomerizerChannelInboundHandlerAdapterFactory> compositechannelHandlerFactory) {
        compositechannelHandlerFactory.values().stream().forEach(handlerFactory ->{
            pipeline.addLast(handlerFactory.getObject());
        });

    }
}
