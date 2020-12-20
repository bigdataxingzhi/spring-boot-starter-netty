package org.springframework.boot.autoconfigure.netty.server;

import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.netty.NettyProperties;
import org.springframework.boot.autoconfigure.netty.channelInitializer.AbstractChannelInitializer;
import org.springframework.boot.autoconfigure.netty.enums.StartUpMode;

/**
 * Author: huoxingzhi
 * Date: 2020/12/10
 * Email: hxz_798561819@163.com
 */
public class NettySocketServer {

    private final static Logger logger = LoggerFactory.getLogger(NettySocketServer.class);

    private EventLoopGroup bossGroup;
    private EventLoopGroup workGroup;
    private ServerBootstrap server;
    private ChannelFuture future;
    private NettyProperties nettyProperties;
    private Bootstrap client;

    public NettySocketServer(NettyProperties nettyProperties) {
        this.nettyProperties = nettyProperties;
        if(this.nettyProperties.getStartUpMode()==StartUpMode.SERVER || this.nettyProperties.getStartUpMode()==StartUpMode.WEBSOCKET){
            bossGroup = new NioEventLoopGroup(this.nettyProperties.getBossGroupLoopThreads());
            workGroup = new NioEventLoopGroup(this.nettyProperties.getWorkGroupLoopThreads());
            server = new ServerBootstrap();
        }

        if(this.nettyProperties.getStartUpMode()==StartUpMode.CLINET){
            workGroup = new NioEventLoopGroup(this.nettyProperties.getWorkGroupLoopThreads());
            client = new Bootstrap();
        }

    }

    public void initAndStart(AbstractChannelInitializer channelInitializer) throws InterruptedException {

        if(logger.isInfoEnabled()){
            logger.debug("{},channelInitializer is : {}",this.getClass().getName(),channelInitializer.toString());
        }

        if(this.nettyProperties.getStartUpMode()==StartUpMode.SERVER || this.nettyProperties.getStartUpMode()==StartUpMode.WEBSOCKET){
            server.group(bossGroup, workGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(channelInitializer);

            this.future = server
                    .bind(nettyProperties.getServerPort())
                    .sync();
            logger.info("netty server 启动完毕...");
        }

        if(this.nettyProperties.getStartUpMode()==StartUpMode.CLINET){
            client.group(workGroup)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_KEEPALIVE, true)
                    .handler(channelInitializer);

            this.future = client
                    .connect(this.nettyProperties.getServerIpAddress(), this.nettyProperties.getServerPort())
                    .sync();
            logger.info("netty client 启动完毕...");
        }

    }
}
