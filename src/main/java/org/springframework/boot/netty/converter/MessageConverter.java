package org.springframework.boot.netty.converter;

import org.springframework.boot.netty.exception.ConvertException;

/**
 * Author: huoxingzhi
 * Date: 2020/12/10
 * Email: hxz_798561819@163.com
 */
public interface MessageConverter {

    /**
     * from object to clazz
     * @param object
     * @param clazz
     * @param <T>
     * @return
     * @throws ConvertException
     */
    <T> T fromMessage(Object object, Class<T> clazz) throws ConvertException;


    /**
     * convert object to clazz
     * @param clazz
     * @param object
     * @return
     */
    Object toMessage(Class<?> clazz,Object object);
}
