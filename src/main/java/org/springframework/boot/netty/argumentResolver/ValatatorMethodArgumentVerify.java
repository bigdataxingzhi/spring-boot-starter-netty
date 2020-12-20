package org.springframework.boot.netty.argumentResolver;

import org.springframework.boot.netty.annotation.Validation;
import org.springframework.boot.netty.exception.ValidatorException;
import org.springframework.boot.netty.listener.Message;
import org.springframework.boot.netty.support.BindingResult;
import org.springframework.boot.netty.support.ValidateUtil;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.validation.annotation.Validated;

import java.lang.reflect.Parameter;
import java.util.Arrays;

public class ValatatorMethodArgumentVerify{

    public boolean supportsParameter(MethodParameter methodParameter) {
        Validation annotation = AnnotationUtils.findAnnotation(methodParameter.getParameter(), Validation.class);
        return !ObjectUtils.isEmpty(annotation);
    }

    public BindingResult resolveArgument(Message message, MethodParameter methodParameter)  {
        Object target = message.getContent();
        BindingResult bindingResult ;
        if(ObjectUtils.isEmpty(target)){
            throw new ValidatorException("need @Validation object not be null");
        }
        Validation annotation = AnnotationUtils.findAnnotation(methodParameter.getParameter(), Validation.class);
        Class<?>[] groups = annotation.groups();
        if(CollectionUtils.isEmpty(Arrays.asList(groups))){
            bindingResult = ValidateUtil.validateEntity(target);
        }else {
            bindingResult = ValidateUtil.validateEntity(target,groups);
        }

        if(bindingResult.hasError() && isBindExceptionResult(methodParameter)){
            throw new ValidatorException(bindingResult.getMessage());
        }else {
            return bindingResult;
        }
    }

    /**
     * 是否有BindingResult类型的参数
     * @param methodParameter
     * @return
     */
    private boolean isBindExceptionResult(MethodParameter methodParameter){
        int paramIndex = methodParameter.getParamIndex();
        Parameter[] parameterTypes = methodParameter.getParameterTypes();
        boolean hasBindingResult = (parameterTypes.length > (paramIndex+1) && org.springframework.boot.netty.support.Error.class.isAssignableFrom(parameterTypes[paramIndex].getType()));
        return !hasBindingResult;
    }
}

