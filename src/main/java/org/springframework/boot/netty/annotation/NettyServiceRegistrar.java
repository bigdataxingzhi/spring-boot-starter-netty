package org.springframework.boot.netty.annotation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.boot.netty.listener.NettyMessageListener;
import org.springframework.boot.netty.sync.AsyncTaskConfig;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.Ordered;
import org.springframework.core.type.AnnotationMetadata;

import static org.springframework.aop.config.AopConfigUtils.AUTO_PROXY_CREATOR_BEAN_NAME;

/**
 * Author: huoxingzhi
 * Date: 2020/12/18
 * Email: hxz_798561819@163.com
 */
public class NettyServiceRegistrar  implements ImportBeanDefinitionRegistrar {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {

        registerNettyMessageListener(importingClassMetadata,registry);
        registerAsyncTaskConfig(importingClassMetadata,registry);

    }

    private void registerNettyMessageListener(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry){
        RootBeanDefinition beanDefinition = new RootBeanDefinition(NettyMessageListener.class);
        registry.registerBeanDefinition("nettyMessageListener", beanDefinition);
    }


    private void registerAsyncTaskConfig(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry){
        RootBeanDefinition beanDefinition = new RootBeanDefinition(AsyncTaskConfig.class);
        registry.registerBeanDefinition("asyncTaskConfig", beanDefinition);

    }

}
