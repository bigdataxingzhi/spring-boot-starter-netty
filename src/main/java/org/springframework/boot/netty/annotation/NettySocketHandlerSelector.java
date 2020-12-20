package org.springframework.boot.netty.annotation;

import org.springframework.boot.autoconfigure.netty.channelInitializer.DefaultProtoNettyServerChannelInitialzer;
import org.springframework.boot.netty.handler.DefaultProtoBufServerHandlerFactory;
import org.springframework.boot.netty.listener.NettyMessageListener;
import org.springframework.boot.netty.sync.AsyncTaskConfig;
import org.springframework.context.annotation.DeferredImportSelector;
import org.springframework.core.type.AnnotationMetadata;

/**
 * Author: huoxingzhi
 * Date: 2020/12/10
 * Email: hxz_798561819@163.com
 */
public class NettySocketHandlerSelector implements DeferredImportSelector {

    @Override
    public String[] selectImports(AnnotationMetadata importingClassMetadata) {
        return new String[] {
                DefaultProtoBufServerHandlerFactory.class.getName(),
                DefaultProtoNettyServerChannelInitialzer.class.getName(),
                NettyMessageListener.class.getName(),
                AsyncTaskConfig.class.getName()
        };
    }

}