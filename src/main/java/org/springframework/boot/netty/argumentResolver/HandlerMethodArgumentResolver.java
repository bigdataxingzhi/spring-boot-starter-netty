package org.springframework.boot.netty.argumentResolver;


import org.springframework.boot.netty.listener.Message;
import org.springframework.context.ApplicationContextAware;

public interface HandlerMethodArgumentResolver extends ApplicationContextAware {

    //该解析器是否支持parameter参数的解析
    boolean supportsParameter(MethodParameter methodParameter);

    //将方法参数从给定请求解析为参数值并返回
    Object resolveArgument(Message message, MethodParameter methodParameter) throws Exception;

}