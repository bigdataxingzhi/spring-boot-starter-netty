package org.springframework.boot.autoconfigure.netty.channelInitializer;

import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;
import org.springframework.boot.netty.codec.ProtobufCodecAdapter;
import org.springframework.boot.netty.codec.proto.Frame;

public class DefaultProtoNettyServerChannelInitialzer extends AbstractChannelInitializer {


	@Override
	public void initChannel(SocketChannel ch) throws Exception {
		ch.pipeline()
				.addLast(new ProtobufVarint32FrameDecoder())
				.addLast(new ProtobufDecoder(Frame.getDefaultInstance()))
				.addLast(new ProtobufVarint32LengthFieldPrepender())
				.addLast(new ProtobufEncoder())
				.addLast(new ProtobufCodecAdapter(this.applicationContext));

		super.initChannel(ch);
	}



}
