package org.springframework.boot.netty.support;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.framework.Advised;
import org.springframework.aop.support.AopUtils;
import org.springframework.boot.netty.annotation.NettySockerHandler;
import org.springframework.boot.netty.argumentResolver.HandlerMethodArgumentResolverComposite;
import org.springframework.boot.netty.argumentResolver.MethodParameter;
import org.springframework.boot.netty.argumentResolver.ValatatorMethodArgumentVerify;
import org.springframework.boot.netty.exception.BindingResultException;
import org.springframework.boot.netty.exception.ConvertException;
import org.springframework.boot.netty.exception.NoHandlerException;
import org.springframework.boot.netty.listener.Message;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.convert.ConversionService;
import org.springframework.util.CollectionUtils;
import org.springframework.util.MethodInvoker;
import org.springframework.util.ReflectionUtils;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Author: huoxingzhi
 * Date: 2020/12/11
 * Email: hxz_798561819@163.com
 *
 * 构造方法执行对象
 */
public class SocketDataBinder {

    private List<Method> multiMethods = new ArrayList<>();


    private ConversionService conversionService;

    private HandlerMethodArgumentResolverComposite handlerMethodArgumentResolverComposite;

    private ValatatorMethodArgumentVerify valatatorMethodArgumentVerify;

    private final Logger logger = LoggerFactory.getLogger(getClass());

    public SocketDataBinder(ConversionService conversionService, HandlerMethodArgumentResolverComposite handlerMethodArgumentResolverComposite) {
        this.conversionService = conversionService;
        this.handlerMethodArgumentResolverComposite = handlerMethodArgumentResolverComposite;
        this.valatatorMethodArgumentVerify = new ValatatorMethodArgumentVerify();
    }


    public MethodInvoker doBind(Message message, Object target, MethodInvoker methodInvoker) {

        try {
            if(CollectionUtils.isEmpty(multiMethods)) {
                Class<?> targetClass = AopUtils.getTargetClass(target);
                ReflectionUtils.doWithMethods(targetClass, method -> {
                    NettySockerHandler nettySockerHandler = AnnotationUtils.findAnnotation(method, NettySockerHandler.class);
                    if(nettySockerHandler!=null){
                        this.multiMethods.add(method);
                    }
                }, ReflectionUtils.USER_DECLARED_METHODS);

                if (CollectionUtils.isEmpty(multiMethods)) {
                    throw new RuntimeException("NettySockerHandler must one");
                }

                // find method from an AOP proxy
                this.multiMethods = this.multiMethods.stream().map(sourceMethod ->{
                    return checkProxy(sourceMethod,target);
                }).collect(Collectors.toList());

            }
            // 解析方法参数
            parseArguments(this.multiMethods,methodInvoker,message);
        } catch (Exception e) {

        }

        return methodInvoker;
    }


    /**
     * 寻找合适的 method can invoker
     * @param multiMethods
     * @param methodInvoker
     * @param message
     */
    private void parseArguments(List<Method> multiMethods, MethodInvoker methodInvoker, Message message) throws Exception {
        Object[] args = null;
        Method targetMethod = null;

        for (Method method : multiMethods) {
            try {
                args = buildListenerArguments(method, message);
                targetMethod = method;
                break;
            }catch (ConvertException e){
                if(logger.isDebugEnabled()){
                    logger.debug( e.getMessage());
                }
                continue;
            }
        }

        if(args==null){
            throw new NoHandlerException("no handler to process this message : "+message);
        }else {
            methodInvoker.setTargetMethod(targetMethod.getName());
            methodInvoker.setArguments(args);
        }
    }


    protected Object[] buildListenerArguments(Method method,Message message) {
        try {

            MethodParameter methodParameter = new MethodParameter();
            BindingResult bindingResult = null;
            // 获取参数的实际类型
            Parameter[] parameters = method.getParameters();
            ArrayList<Object> objects = new ArrayList<>(parameters.length);
            methodParameter.setParameterTypes(parameters);
            for (int index = 0; index < parameters.length; index ++) {
                methodParameter.setParamIndex(index);
                Parameter currentParamter = parameters[index];
                Object param = new Object();
                methodParameter.setMethod(method);
                methodParameter.setParameter(currentParamter);
                message.setError(bindingResult);
                if(this.conversionService.canConvert(message.getContent().getClass(),currentParamter.getType())){
                    param = this.conversionService.convert(message.getContent(), currentParamter.getType());
                    objects.add(param);
                } else {
                    if(this.handlerMethodArgumentResolverComposite.supportsParameter(methodParameter)){
                        param = this.handlerMethodArgumentResolverComposite.resolveArgument(message,methodParameter);
                        objects.add(param);
                    }
                }
                bindingResult = validateIfNecessary(methodParameter,param);

            }
            return  objects.toArray();
        }catch (BindingResultException b){
            b.printStackTrace();
            throw new ConvertException(this.getClass().getName()+" inappropriate method for :"+method.getName()+method.getParameters().toString());
        } catch (Exception e) {
            throw new ConvertException(this.getClass().getName()+" inappropriate method for :"+method.getName()+method.getParameters().toString());

        }

    }

    /**
     * 如果没有valid 注解返回null
     * 校验错误且没有BindingResult类型的参数跑出异常
     * 校验正确且没有BindingResult类型的参数，返回空的BindingResult实例
     * @param methodParameter
     * @param paramResult
     * @return
     */
    private BindingResult validateIfNecessary(MethodParameter methodParameter, Object paramResult) {
        if(this.valatatorMethodArgumentVerify.supportsParameter(methodParameter)){
            Message message = new Message();
            message.setContent(paramResult);
            return this.valatatorMethodArgumentVerify.resolveArgument(message,methodParameter);
        }
        return null;
    }



    private Method checkProxy(Method methodArg, Object bean) {
        Method method = methodArg;
        if (AopUtils.isJdkDynamicProxy(bean)) {
            try {
                // Found a @NettySockerHandler method on the target class for this JDK proxy ->
                // is it also present on the proxy itself?
                method = bean.getClass().getMethod(method.getName(), method.getParameterTypes());
                Class<?>[] proxiedInterfaces = ((Advised) bean).getProxiedInterfaces();
                for (Class<?> iface : proxiedInterfaces) {
                    try {
                        method = iface.getMethod(method.getName(), method.getParameterTypes());
                        break;
                    }
                    catch (@SuppressWarnings("unused") NoSuchMethodException noMethod) {
                    }
                }
            }
            catch (SecurityException ex) {
                ReflectionUtils.handleReflectionException(ex);
            }
            catch (NoSuchMethodException ex) {
                throw new IllegalStateException(String.format(
                        "@NettySockerHandler method '%s' found on bean target class '%s', " +
                                "but not found in any interface(s) for a bean JDK proxy. Either " +
                                "pull the method up to an interface or switch to subclass (CGLIB) " +
                                "proxies by setting proxy-target-class/proxyTargetClass " +
                                "attribute to 'true'", method.getName(), method.getDeclaringClass().getSimpleName()), ex);
            }
        }
        return method;
    }
}
