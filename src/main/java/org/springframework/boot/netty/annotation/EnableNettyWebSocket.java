package org.springframework.boot.netty.annotation;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * Author: huoxingzhi
 * Date: 2020/12/10
 * Email: hxz_798561819@163.com
 */

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(NettyWebSocketHandlerSelector.class)
public @interface EnableNettyWebSocket {

}
