package org.springframework.boot.netty.argumentResolver;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import org.springframework.beans.BeanUtils;
import org.springframework.boot.netty.annotation.SocketBody;
import org.springframework.boot.netty.listener.Message;
import org.springframework.boot.netty.support.JsonUtil;
import org.springframework.core.annotation.AnnotationUtils;

import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

/**
 * Author: huoxingzhi
 * Date: 2020/12/16
 * Email: hxz_798561819@163.com
 */
public class NettyPayloadMethodProcessor extends HandlerMethodArgumentResolverAdapter {
    @Override
    public boolean supportsParameter(MethodParameter methodParameter) {
        Parameter parameter = methodParameter.getParameter();
        SocketBody annotation = AnnotationUtils.findAnnotation(parameter, SocketBody.class);
        return !BeanUtils.isSimpleProperty(methodParameter.getParameter().getType()) && annotation!=null;
    }

    @Override
    public Object resolveArgument(Message message, MethodParameter methodParameter) throws Exception {

        String requestContent = (String) message.getContent();
        Parameter parameter = methodParameter.getParameter();
        Type parameterType = parameter.getParameterizedType();
      if( List.class.isAssignableFrom(parameter.getType()) && isJSON(requestContent,true)){
          //判断当前参数类型为对象类型 or List(泛型)类型
          if(parameterType instanceof ParameterizedType) {
              Type actualTypeArgument = ((ParameterizedType)parameterType).getActualTypeArguments()[0];
              return JsonUtil.string2Obj(requestContent, List.class, actualTypeArgument.getClass());
          }
          return Lists.newArrayList();
      }else if(isJSON(requestContent,false)){
         return JsonUtil.string2Obj(requestContent, (Class<?>) parameterType);
      }else {
          return new Object();
      }

    }


    private boolean isJSON(String str, Boolean isJsonArray) {
        boolean result = true;
        try {
            if(isJsonArray){
                JSON.parseArray(str);
            }else {
                JSON.parseObject(str);
            }
        } catch (Exception e) {
            result=false;
        }
        return result;
    }


}
