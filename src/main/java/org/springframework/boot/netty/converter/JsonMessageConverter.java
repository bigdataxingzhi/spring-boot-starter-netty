package org.springframework.boot.netty.converter;

import org.springframework.boot.netty.exception.ConvertException;
import org.springframework.boot.netty.support.JsonUtil;

/**
 * Author: huoxingzhi
 * Date: 2020/12/10
 * Email: hxz_798561819@163.com
 */
public class JsonMessageConverter implements MessageConverter {


    public JsonMessageConverter() {

    }

    @Override
    public <T> T fromMessage(Object jsonMessage, Class<T> clazz) throws ConvertException {

        return JsonUtil.string2Obj((String)jsonMessage, clazz);
    }

    @Override
    public Object toMessage(Class<?> clazz, Object object) {

        return JsonUtil.obj2String(object);
    }


}
