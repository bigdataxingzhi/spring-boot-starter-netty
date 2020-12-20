package org.springframework.boot.netty.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.autoconfigure.netty.NettyProperties;
import org.springframework.boot.netty.annotation.NettySocketListener;
import org.springframework.boot.netty.argumentResolver.HandlerMethodArgumentResolver;
import org.springframework.boot.netty.argumentResolver.HandlerMethodArgumentResolverComposite;
import org.springframework.boot.netty.enums.MessageEnum;
import org.springframework.boot.netty.exception.ThreadException;
import org.springframework.boot.netty.sync.AsyncMessageProcessing;
import org.springframework.boot.netty.sync.MessageBlockQueue;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationListener;
import org.springframework.core.annotation.MergedAnnotations;
import org.springframework.core.convert.ConversionService;
import org.springframework.util.CollectionUtils;

import java.lang.reflect.AnnotatedElement;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;


/**
 * Author: huoxingzhi
 * Date: 2020/12/10
 * Email: hxz_798561819@163.com
 */

/**
 * 作为监听器，监听器channel连接时间，启动对应线程
 */
public class NettyMessageListener implements ApplicationListener<WebSocketEvent>, ApplicationContextAware, BeanPostProcessor, SmartInitializingSingleton ,DisposableBean{


    private ApplicationContext applicationContext;

    private NettyProperties nettyProperties;

    private Object target;

    private List<AsyncMessageProcessing> asyncMessageProcessings = new ArrayList<>();

    private ConversionService conversionService;

    private HandlerMethodArgumentResolverComposite handlerMethodArgumentResolverComposite;

    private Map<Integer,Boolean> threadStartFlag = new ConcurrentHashMap<>(16);

    private Set<Integer> startedThread = new HashSet<>(16);

    Executor threadPoolTaskExecutor;

    private final static Logger logger = LoggerFactory.getLogger(NettyMessageListener.class);


    @Override
    public void onApplicationEvent(WebSocketEvent event) {
        if(event.getMessage().equals(MessageEnum.HANDSHAKER)){
            int mapIndex = event.getMapIndex();
            if(mapIndex==-1){
                throw new ThreadException("for mapIndex attribute of WebSocketEvent must be set a number");
            }else {
                // 双重if防止多线程
                if(threadStartFlag.get(mapIndex)==Boolean.FALSE){
                    synchronized (this){
                        if(threadStartFlag.get(mapIndex)==Boolean.FALSE){
                            AsyncMessageProcessing asyncMessageProcessing = new AsyncMessageProcessing(this.nettyProperties, this.target, this.conversionService,this.handlerMethodArgumentResolverComposite,mapIndex);
                            this.asyncMessageProcessings.add(asyncMessageProcessing);
                            this.threadStartFlag.remove(mapIndex);
                            this.threadStartFlag.put(mapIndex,Boolean.TRUE);
                            startedThread.add(mapIndex);
                            this.threadPoolTaskExecutor.execute(asyncMessageProcessing);
                        }
                    }
                }
            }

        }

    }


    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {

        // find targetClass class from an AOP proxy
        Class<?> targetClass = AopUtils.getTargetClass(bean);
        Collection<NettySocketListener> listenerAnnotations = findListenerAnnotations(targetClass);
        if(!CollectionUtils.isEmpty(listenerAnnotations)){
            this.target = bean;
        }
        return bean;
    }

    private Collection<NettySocketListener> findListenerAnnotations(AnnotatedElement element) {
        return MergedAnnotations.from(element, MergedAnnotations.SearchStrategy.TYPE_HIERARCHY)
                .stream(NettySocketListener.class)
                .map(ann -> ann.synthesize())
                .collect(Collectors.toList());
    }


    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public void afterSingletonsInstantiated() {

        this.nettyProperties = this.applicationContext.getBean(NettyProperties.class);
        List<HandlerMethodArgumentResolver> handlerMethodArgumentResolvers = this.applicationContext.getBeansOfType(HandlerMethodArgumentResolver.class).values().stream().collect(Collectors.toList());
        this.handlerMethodArgumentResolverComposite = new HandlerMethodArgumentResolverComposite(handlerMethodArgumentResolvers);
        this.threadPoolTaskExecutor = (Executor) this.applicationContext.getBean( "process-executor");
        try {
            this.conversionService = this.applicationContext.getBean(ConversionService.class);
        }catch (BeansException e){
            logger.debug("IOC container not found bean of conversionService.class");
        }

        for (int init = 0; init <this.nettyProperties.getMessageMapCapacity(); init++) {
            //初始化线程启动标记
            threadStartFlag.put(init,Boolean.FALSE);
        }

        MessageBlockQueue.initMessageQueue(nettyProperties.getMessageMapCapacity(),nettyProperties.getMessageQueueCapatity());

    }

    @Override
    public void destroy() throws Exception {

        while (MessageBlockQueue.getSize(this.startedThread)!=0){
           logger.info("{} : wait for all message consumered .....",this.getClass().getName());
            Thread.sleep(1000);
        }
        logger.info("{} : already message consumered .....",this.getClass().getName());
        MessageBlockQueue.shutdown = Boolean.TRUE;
    }
}
