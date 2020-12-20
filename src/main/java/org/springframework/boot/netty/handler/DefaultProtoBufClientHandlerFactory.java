package org.springframework.boot.netty.handler;

import org.apache.commons.lang3.ClassUtils;
import org.springframework.beans.BeansException;
import org.springframework.boot.netty.exception.ChannelHandlerException;
import org.springframework.boot.netty.handler.common.NettyCustomerizerChannelInboundHandlerAdapterFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEventPublisher;

/**
 * Author: huoxingzhi
 * Date: 2020/12/14
 * Email: hxz_798561819@163.com
 */
public class DefaultProtoBufClientHandlerFactory implements NettyCustomerizerChannelInboundHandlerAdapterFactory<DefaultProtoBufServerHandler> {

    private ApplicationContext applicationContext;

    private ApplicationEventPublisher applicationEventPublisher;

    @Override
    public Class<?> getObjectType() {
        return DefaultProtoBufServerHandler.class;
    }


    @Override
    public DefaultProtoBufServerHandler getObject() {

        try {
            DefaultProtoBufServerHandler defaultProtoBufServerHandler = (DefaultProtoBufServerHandler) getObjectType().newInstance();
            defaultProtoBufServerHandler.setApplicationContext(this.applicationContext);
            defaultProtoBufServerHandler.afterPropertiesSet(this.applicationEventPublisher);
            return defaultProtoBufServerHandler;
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
}
