package org.springframework.boot.netty.argumentResolver;


import org.springframework.beans.BeansException;
import org.springframework.boot.autoconfigure.netty.NettyProperties;
import org.springframework.boot.netty.listener.Message;
import org.springframework.context.ApplicationContext;

public class HandlerMethodArgumentResolverAdapter implements HandlerMethodArgumentResolver {

    protected ApplicationContext applicationContext;

    protected NettyProperties nettyProperties;

    @Override
    public boolean supportsParameter(MethodParameter methodParameter){
        return false;
    }

    @Override
    public Object resolveArgument(Message message, MethodParameter methodParameter) throws Exception{
        return new Object();
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
        this.nettyProperties = this.applicationContext.getBean(NettyProperties.class);
    }
}