package org.springframework.boot.netty.handler.common;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * 共享实例的ChannelInboundHandler
 */
@ChannelHandler.Sharable
public class NettyShareableChannelInboundHandlerAdapter extends ChannelInboundHandlerAdapter {

}
