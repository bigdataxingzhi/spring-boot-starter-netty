package org.springframework.boot.netty.annotation;

import org.springframework.boot.autoconfigure.netty.channelInitializer.WebSocketServerChannelInitialzer;
import org.springframework.boot.netty.handler.HttpServerHandlerFactory;
import org.springframework.context.annotation.DeferredImportSelector;
import org.springframework.core.type.AnnotationMetadata;

/**
 * Author: huoxingzhi
 * Date: 2020/12/10
 * Email: hxz_798561819@163.com
 */
public class NettyWebSocketHandlerSelector implements DeferredImportSelector {

    @Override
    public String[] selectImports(AnnotationMetadata importingClassMetadata) {
        return new String[] {
                WebSocketServerChannelInitialzer.class.getName(),
                HttpServerHandlerFactory.class.getName(),
                NettyServiceRegistrar.class.getName()
        };
    }

}