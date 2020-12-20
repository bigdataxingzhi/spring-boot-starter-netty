package org.springframework.boot.netty.argumentResolver;


import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;

/**
 * Author: huoxingzhi
 * Date: 2020/12/16
 * Email: hxz_798561819@163.com
 */
public class MethodParameter {

    private volatile Parameter parameter;

    private volatile Method method;

    private volatile int paramIndex;

    private Parameter[] parameterTypes;

    public Parameter getParameter() {
        return parameter;
    }

    public void setParameter(Parameter parameter) {
        this.parameter = parameter;
    }

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }


    public Type getParamType() {
        return parameter.getParameterizedType();
    }

    public int getParamIndex() {
        return paramIndex;
    }

    public void setParamIndex(int paramIndex) {
        this.paramIndex = paramIndex;
    }

    public Parameter[] getParameterTypes() {
        return parameterTypes;
    }

    public void setParameterTypes(Parameter[] parameterTypes) {
        this.parameterTypes = parameterTypes;
    }
}
