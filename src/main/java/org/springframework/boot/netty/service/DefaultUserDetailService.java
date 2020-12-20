package org.springframework.boot.netty.service;

import org.springframework.boot.autoconfigure.netty.channel.Channel;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.UUID;

/**
 * Author: huoxingzhi
 * Date: 2020/12/11
 * Email: hxz_798561819@163.com
 */
public class DefaultUserDetailService implements UserDetailService {

    public String getUserClientId(Object object) {

        if(object.getClass().isAssignableFrom(Channel.class)){
            Channel channel = (Channel) object;
            InetSocketAddress socketAddress = (InetSocketAddress)channel.getNettyChannel().remoteAddress();
            String clientIp = socketAddress.getAddress().getHostAddress();
            return clientIp;
        }
        return UUID.randomUUID().toString();
    }

}
