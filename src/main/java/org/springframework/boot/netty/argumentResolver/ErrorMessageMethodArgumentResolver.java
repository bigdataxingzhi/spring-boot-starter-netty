package org.springframework.boot.netty.argumentResolver;

import org.springframework.beans.BeanUtils;
import org.springframework.boot.netty.exception.BindingResultException;
import org.springframework.boot.netty.exception.ConvertException;
import org.springframework.boot.netty.listener.Message;
import org.springframework.boot.netty.support.Error;

/**
 * Author: huoxingzhi
 * Date: 2020/12/16
 * Email: hxz_798561819@163.com
 */
public class ErrorMessageMethodArgumentResolver extends HandlerMethodArgumentResolverAdapter {

    @Override
    public boolean supportsParameter(MethodParameter methodParameter) {
        return Error.class.isAssignableFrom(methodParameter.getParameter().getType());
    }

    @Override
    public Object resolveArgument(Message message, MethodParameter methodParameter) throws Exception {
        Object bindingResult = BeanUtils.instantiateClass(methodParameter.getParameter().getType());
        if(message.getError()==null){
           throw new BindingResultException(Thread.currentThread()+": The BindingResult must be use with @Valid");
        }
        BeanUtils.copyProperties(message.getError(),bindingResult);
        return bindingResult;
    }

}
