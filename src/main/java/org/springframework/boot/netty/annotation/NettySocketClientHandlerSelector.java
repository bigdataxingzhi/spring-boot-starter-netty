package org.springframework.boot.netty.annotation;

import org.springframework.boot.autoconfigure.netty.channelInitializer.DefaultProtoNettyServerChannelInitialzer;
import org.springframework.boot.netty.handler.DefaultProtoBufClientHandlerFactory;
import org.springframework.context.annotation.DeferredImportSelector;
import org.springframework.core.type.AnnotationMetadata;

/**
 * Author: huoxingzhi
 * Date: 2020/12/17
 * Email: hxz_798561819@163.com
 */
public class NettySocketClientHandlerSelector implements DeferredImportSelector {
    @Override
    public String[] selectImports(AnnotationMetadata importingClassMetadata) {
        return new String[] {
                DefaultProtoBufClientHandlerFactory.class.getName(),
                DefaultProtoNettyServerChannelInitialzer.class.getName(),
                NettyServiceRegistrar.class.getName()
        };
    }
}
