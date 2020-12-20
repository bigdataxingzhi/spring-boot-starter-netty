package org.springframework.boot.autoconfigure.netty.support;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.boot.netty.annotation.MethodArgumentResolver;
import org.springframework.boot.netty.support.ClassScanner;

import java.util.Set;

public class RegisterArguemnetResolver {

    /**
     * 动态注册bean
     * @param beanDefinitonRegistry
     * @param beanDefinition
     */
    private static void registerBean(BeanDefinitionRegistry beanDefinitonRegistry,BeanDefinition beanDefinition){
        //DefaultListableBeanFactory beanDefinitonRegistry = (DefaultListableBeanFactory) app.getAutowireCapableBeanFactory();
        String simpleNameString=beanDefinition.getBeanClassName();
        if(simpleNameString.contains(".")){
            simpleNameString=simpleNameString.substring(simpleNameString.lastIndexOf(".")+1);
        }
        simpleNameString= lowerFirstChar(simpleNameString);

        if(!beanDefinitonRegistry.containsBeanDefinition(simpleNameString)){
            beanDefinitonRegistry.registerBeanDefinition(simpleNameString, beanDefinition);
        }
    }

    private static BeanDefinitionBuilder getBeanDefinitionBuilder(Class clazz){
        return BeanDefinitionBuilder.genericBeanDefinition(clazz);
    }

    private static String lowerFirstChar(String simpleName){
        String className = simpleName;// 获取类名
        String firstChar = className.substring(0, 1).toLowerCase();// 将首字母变为小写
        return firstChar + className.substring(1);// 获得已小写字母开头的类名
    }


    public static void doRegisterArguements(BeanDefinitionRegistry beanDefinitonRegistry, String[] arguementResolverBasePackages) {

        Set<Class<?>> classes = ClassScanner.scanPackages(arguementResolverBasePackages, MethodArgumentResolver.class);
            for (Class<?> clzz : classes) {
                registerBean(beanDefinitonRegistry,getBeanDefinitionBuilder(clzz).getBeanDefinition());
            }
    }
}
