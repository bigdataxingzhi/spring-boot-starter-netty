package org.springframework.boot.netty.handler;

import org.springframework.beans.BeansException;
import org.springframework.boot.netty.handler.common.NettyCustomerizerChannelInboundHandlerAdapterFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEventPublisher;

public class HearBeatHandlerFactory implements NettyCustomerizerChannelInboundHandlerAdapterFactory<HeartBeatHandler> {


    @Override
    public Class<?> getObjectType() {
        return HeartBeatHandler.class;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {

    }

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {

    }
}
