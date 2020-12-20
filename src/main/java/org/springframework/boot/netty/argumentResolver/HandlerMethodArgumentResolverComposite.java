package org.springframework.boot.netty.argumentResolver;


import org.springframework.boot.netty.listener.Message;

import java.util.List;

public class HandlerMethodArgumentResolverComposite extends HandlerMethodArgumentResolverAdapter {


   private final List<HandlerMethodArgumentResolver> handlerMethodArgumentResolverComposite ;


   public HandlerMethodArgumentResolverComposite(List<HandlerMethodArgumentResolver> handlerMethodArgumentResolver){
        this.handlerMethodArgumentResolverComposite = handlerMethodArgumentResolver;
    }

    //该解析器是否支持parameter参数的解析
    public boolean supportsParameter(MethodParameter methodParameter){

        for (HandlerMethodArgumentResolver handlerMethodArgumentResolver : handlerMethodArgumentResolverComposite) {
          if(handlerMethodArgumentResolver.supportsParameter(methodParameter)){
              return true;
          }
        }
        return false;
    }

    //将方法参数从给定请求解析为参数值并返回
    public Object resolveArgument(Message message, MethodParameter methodParameter) throws Exception{

        HandlerMethodArgumentResolver argumentResolver = findArgumentResolver(methodParameter);
        return argumentResolver.resolveArgument(message,methodParameter);
    }

    private HandlerMethodArgumentResolver findArgumentResolver(MethodParameter methodParameter){
        for (HandlerMethodArgumentResolver handlerMethodArgumentResolver : handlerMethodArgumentResolverComposite) {
            if(handlerMethodArgumentResolver.supportsParameter(methodParameter)){
                return handlerMethodArgumentResolver;
            }
        }
        return null;
    }


}