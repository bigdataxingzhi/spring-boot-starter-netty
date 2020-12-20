package org.springframework.boot.netty.handler.common;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.apache.commons.lang3.ClassUtils;
import org.springframework.boot.netty.exception.ChannelHandlerException;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationEventPublisherAware;


/**
 * 多实例的ChannelInboundHandler
 */
public interface NettyCustomerizerChannelInboundHandlerAdapterFactory<T extends ChannelHandler> extends ApplicationContextAware, ApplicationEventPublisherAware {

    //返回的对象实例
   default ChannelHandler getObject() {
       try {
           return ((T) getObjectType().newInstance());
       }catch (Exception e){
           throw new ChannelHandlerException(ClassUtils.getName(this)+" can not create channelHandler");
       }
       
    }
    //Bean的类型
     Class<?> getObjectType();

    default boolean isSingleton(){
        return Boolean.FALSE;
    }



}
