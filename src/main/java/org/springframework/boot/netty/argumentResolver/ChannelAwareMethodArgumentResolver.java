package org.springframework.boot.netty.argumentResolver;

import org.springframework.boot.autoconfigure.netty.NettyProperties;
import org.springframework.boot.autoconfigure.netty.channel.Channel;
import org.springframework.boot.autoconfigure.netty.enums.StartUpMode;
import org.springframework.boot.netty.converter.JsonMessageConverter;
import org.springframework.boot.netty.converter.MessageConverter;
import org.springframework.boot.netty.converter.ProtobufMessage2JsonConverter;
import org.springframework.boot.netty.listener.Message;

/**
 * Author: huoxingzhi
 * Date: 2020/12/16
 * Email: hxz_798561819@163.com
 */
public class ChannelAwareMethodArgumentResolver extends HandlerMethodArgumentResolverAdapter {

    @Override
    public boolean supportsParameter(MethodParameter methodParameter) {
        return Channel.class.isAssignableFrom(methodParameter.getParameter().getType());
    }

    @Override
    public Object resolveArgument(Message message, MethodParameter methodParameter) throws Exception {
        MessageConverter messageConverter = null;
        StartUpMode startUpMode = this.nettyProperties.getStartUpMode();
        if(startUpMode.equals(StartUpMode.WEBSOCKET)){
            messageConverter = this.applicationContext.getBean("jsonMessageConverter", JsonMessageConverter.class);
        }else {
            messageConverter = this.applicationContext.getBean("protoBufMessageConverter",ProtobufMessage2JsonConverter.class);
        }

        return new Channel(message.getCurrentChannel(), messageConverter);
    }
}
