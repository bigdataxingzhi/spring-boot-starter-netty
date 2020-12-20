package org.springframework.boot.autoconfigure.netty;

import org.springframework.beans.BeansException;
import org.springframework.boot.autoconfigure.netty.channelInitializer.AbstractChannelInitializer;
import org.springframework.boot.autoconfigure.netty.server.NettySocketServer;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

public class NettyStartupListener implements ApplicationListener<ContextRefreshedEvent>, ApplicationContextAware {

	NettySocketServer nettySocketServer;
	AbstractChannelInitializer channelInitializer;
	private ApplicationContext applicationContext;

	NettyStartupListener(NettySocketServer wsServer,AbstractChannelInitializer channelInitializer){
		this.nettySocketServer = wsServer;
		this.channelInitializer = channelInitializer;
	}

	@Override
	public void onApplicationEvent(ContextRefreshedEvent event) {
		if (event.getApplicationContext().getParent() == null) {
			try {
				this.nettySocketServer.initAndStart(channelInitializer);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext=applicationContext;
	}
}
