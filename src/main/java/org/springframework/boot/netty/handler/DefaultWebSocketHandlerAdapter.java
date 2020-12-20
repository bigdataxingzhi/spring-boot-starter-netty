package org.springframework.boot.netty.handler;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.websocketx.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.netty.converter.JsonMessageConverter;
import org.springframework.boot.netty.converter.MessageConverter;
import org.springframework.boot.netty.handler.common.AbstractWebSocketChannelInboundHandlerAdapter;
import org.springframework.boot.netty.listener.Message;
import org.springframework.boot.netty.sync.MessageBlockQueue;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEventPublisher;

/**
 * 
 * @Description: 处理消息的handler
 * TextWebSocketFrame： 在netty中，是用于为websocket专门处理文本的对象，frame是消息的载体
 */

/**
 * handlerAdded() 当检测到新连接之后，调用 ch.pipeline().addLast(new XXXHandler()); 之后的回调
 * channelRegistered() 当前的 channel 的所有的逻辑处理已经和某个 NIO 线程建立了绑定关系
 * channelActive() channel 的 pipeline 中已经添加完所有的 handler，并且绑定好一个 NIO 线程之后，这条连接算是真正激活了，接下来就会回调到此方法。
 * channelRead() 收到发来的数据，每次都会回调此方法，表示有数据可读。
 * channelReadComplete() 数据读取完毕回调此方法
 * channelInactive()  表示这条连接已经被关闭了，这条连接在 TCP 层面已经不再是 ESTABLISH 状态了
 * channelUnregistered()  表示与这条连接对应的 NIO 线程移除掉对这条连接的处理
 * handlerRemoved() 这条连接上添加的所有的业务逻辑处理器都被移除掉后调用
 */
public class DefaultWebSocketHandlerAdapter extends AbstractWebSocketChannelInboundHandlerAdapter<WebSocketFrame> {


	private final static Logger logger = LoggerFactory.getLogger(DefaultWebSocketHandlerAdapter.class);

	private MessageConverter messageConverter;

	public DefaultWebSocketHandlerAdapter() {
	}


	public DefaultWebSocketHandlerAdapter(ApplicationContext applicationContext) {

		this.applicationContext = applicationContext;
		this.messageConverter = this.applicationContext.getBean("jsonMessageConverter", JsonMessageConverter.class);
	}

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, WebSocketFrame msg)
			throws Exception {
		handleWebSocketFrame(ctx, msg);
	}

	private void handleWebSocketFrame(ChannelHandlerContext ctx, WebSocketFrame frame) {
		if (frame instanceof TextWebSocketFrame) {

			TextWebSocketFrame msg = (TextWebSocketFrame)frame;
			// 获取客户端传输过来的消息
			String content = this.messageConverter.fromMessage(msg.text(),String.class);

			Channel currentChannel = ctx.channel();

			int partition = getPartition(currentChannel.hashCode(), this.nettyProperties.getMessageMapCapacity());

			String userClientId = this.userDetailService.getUserClientId(currentChannel);
			org.springframework.boot.autoconfigure.netty.channel.Channel.UserChannelRel.put(userClientId,currentChannel);

			// 压入队列，向容器中发布事件,缓存客户端id与Channel
			MessageBlockQueue.setMessage(partition,new Message<String>(currentChannel,content,partition));

			if(logger.isDebugEnabled()){
				org.springframework.boot.autoconfigure.netty.channel.Channel.UserChannelRel.output();
				logger.debug("receive message is :{}",msg);
			}
			return;
		}
		if (frame instanceof PingWebSocketFrame) {
			ctx.writeAndFlush(new PongWebSocketFrame(frame.content().retain()));
			return;
		}
		if (frame instanceof CloseWebSocketFrame) {
			ctx.writeAndFlush(frame.retainedDuplicate()).addListener(ChannelFutureListener.CLOSE);
			return;
		}
		if (frame instanceof BinaryWebSocketFrame) {
			return;
		}
		if (frame instanceof PongWebSocketFrame) {
			return;
		}
	}

	public void afterPropertiesSet(ApplicationEventPublisher applicationEventPublisher){
		super.afterPropertiesSet(applicationEventPublisher);
		this.messageConverter = this.applicationContext.getBean("jsonMessageConverter", JsonMessageConverter.class);
	}


}
