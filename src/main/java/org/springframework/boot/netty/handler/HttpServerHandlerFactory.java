package org.springframework.boot.netty.handler;

import org.apache.commons.lang3.ClassUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.boot.netty.exception.ChannelHandlerException;
import org.springframework.boot.netty.handler.common.NettyCustomerizerChannelInboundHandlerAdapterFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEventPublisher;

public class HttpServerHandlerFactory implements NettyCustomerizerChannelInboundHandlerAdapterFactory<HttpServerHandler>, SmartInitializingSingleton {

    private ApplicationContext applicationContext;

    private ApplicationEventPublisher applicationEventPublisher;

    @Override
    public Class<?> getObjectType() {
        return HttpServerHandler.class;
    }


    @Override
    public HttpServerHandler getObject() {

        try {
            HttpServerHandler httpServerHandler = (HttpServerHandler) getObjectType().newInstance();
            httpServerHandler.setApplicationContext(this.applicationContext);
            httpServerHandler.setApplicationEventPublisher(applicationEventPublisher);
            httpServerHandler.afterPropertiesSet();
            return httpServerHandler;
        }catch (ReflectiveOperationException e){
            throw new ChannelHandlerException(ClassUtils.getName(this)+" can not create channelHandler");
        }

    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;
    }

    //仅仅为了测试
    @Override
    public void afterSingletonsInstantiated() {

        getObject();
    }
}
