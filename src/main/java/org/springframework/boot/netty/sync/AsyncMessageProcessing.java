package org.springframework.boot.netty.sync;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.netty.NettyProperties;
import org.springframework.boot.netty.argumentResolver.HandlerMethodArgumentResolverComposite;
import org.springframework.boot.netty.listener.Message;
import org.springframework.boot.netty.listener.NettyMessageListener;
import org.springframework.boot.netty.support.SocketDataBinder;
import org.springframework.core.convert.ConversionService;
import org.springframework.format.support.DefaultFormattingConversionService;
import org.springframework.util.MethodInvoker;

import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * Author: huoxingzhi
 * Date: 2020/12/11
 * Email: hxz_798561819@163.com
 */
public class AsyncMessageProcessing implements Runnable {

    public static final Object lockObject = new Object();

    private final CountDownLatch start;

    private int mapIndex;

    private NettyProperties nettyProperties;

    private MethodInvoker methodInvoker;

    private Object target;

    private ConversionService conversionService;

    private HandlerMethodArgumentResolverComposite handlerMethodArgumentResolverComposite;

    private final SocketDataBinder socketDataBinder;

    private final static Logger logger = LoggerFactory.getLogger(NettyMessageListener.class);



    public AsyncMessageProcessing(NettyProperties nettyProperties,
                                  Object target,
                                  ConversionService conversionService,
                                  HandlerMethodArgumentResolverComposite handlerMethodArgumentResolverComposite,
                                  int mapIndex) {
        this.nettyProperties = nettyProperties;
        this.target = target;
        this.conversionService = (conversionService==null)?new DefaultFormattingConversionService():conversionService;
        this.handlerMethodArgumentResolverComposite = handlerMethodArgumentResolverComposite;
        this.mapIndex = mapIndex;
        this.socketDataBinder = new SocketDataBinder(this.conversionService,this.handlerMethodArgumentResolverComposite);
        this.start = new CountDownLatch(1);
    }

    @Override
    public void run() {

        try {
            initialize();
            while (!MessageBlockQueue.shutdown){
                mainLoop();
            }
        } catch (Exception e) {
            this.start.countDown();
        }

    }


    private void initialize() {
        methodInvoker= new MethodInvoker();
        methodInvoker.setTargetObject(this.target);
        this.start.countDown();
    }

    private void mainLoop() {
        try {

            Message message = (Message) MessageBlockQueue.getMessage(this.mapIndex,this.nettyProperties.getMessageQueueTimeout(), TimeUnit.MILLISECONDS);
            if(message!=null){
                this.socketDataBinder.doBind(message,this.target,methodInvoker);
                this.methodInvoker.prepare();
                logger.info(Thread.currentThread().getName());
                this.methodInvoker.invoke();
            }else {
                logger.info(Thread.currentThread().getName());
                Thread.sleep(nettyProperties.getConsumerThreadSleep());
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

    }


}
