package org.springframework.boot.autoconfigure.netty.channel;

import com.google.protobuf.Message;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.netty.converter.MessageConverter;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Author: huoxingzhi
 * Date: 2020/12/11
 * Email: hxz_798561819@163.com
 */
public class Channel {

    private io.netty.channel.Channel nettyChannel;

    public MessageConverter messageConverter;

    private final static Logger logger = LoggerFactory.getLogger(Channel.class);


    public Channel(io.netty.channel.Channel nettyChannel, MessageConverter messageConverter) {
        this.nettyChannel = nettyChannel;
        this.messageConverter = messageConverter;
    }

    public io.netty.channel.Channel getNettyChannel() {
        return this.nettyChannel;
    }

    public void sendToNettyWithWebSocket(Object object){
        InetSocketAddress socketAddress = (InetSocketAddress)nettyChannel.remoteAddress();
        String clientIp = socketAddress.getAddress().getHostAddress();
        sendToNettyWithWebSocket(object,clientIp);
    }

    public void sendToNettyWithWebSocket(Object object,String receiverId){
        io.netty.channel.Channel receiverChannel = UserChannelRel.get(receiverId);
        receiverChannel.writeAndFlush(
                new TextWebSocketFrame(this.messageConverter.toMessage(null,object).toString()));
    }


    public void sendToNettyWithProtoBuf(Class<? extends Message> clazz, byte[] conetnt){
        InetSocketAddress socketAddress = (InetSocketAddress)nettyChannel.remoteAddress();
        String clientIp = socketAddress.getAddress().getHostAddress();
        sendToNettyWithProtoBuf(clazz,conetnt,clientIp);
    }

    public void sendToNettyWithProtoBuf(Class<? extends Message> clazz, byte[] conetnt,String receiverId){
        io.netty.channel.Channel receiverChannel = UserChannelRel.get(receiverId);
        Object message = this.messageConverter.toMessage(clazz, conetnt);
        receiverChannel.writeAndFlush(message);
    }


    /**
     * @Description: 用户id和channel的关联关系处理
     */
    public static class UserChannelRel {

        private static ConcurrentMap<String, io.netty.channel.Channel> manager = new ConcurrentHashMap<>(256);

        public static synchronized void put(String senderId, io.netty.channel.Channel channel) {
            if(manager.get(senderId)!=null && manager.get(senderId).isActive()){
                manager.remove(senderId);
            }else {
                manager.put(senderId, channel);
            }

        }

        public static io.netty.channel.Channel get(String senderId) {
            return manager.get(senderId);
        }

        public static void output() {
            for (HashMap.Entry<String, io.netty.channel.Channel> entry : manager.entrySet()) {
                Channel.logger.debug("UserId: {}, ChannelId: {}",entry.getKey(),entry.getValue().id().asLongText());
            }
        }
    }

}
