package org.springframework.boot.netty.handler;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.websocketx.CloseWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrameAggregator;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshaker;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshakerFactory;
import io.netty.handler.codec.http.websocketx.extensions.compression.WebSocketServerCompressionHandler;
import io.netty.util.CharsetUtil;
import org.springframework.beans.TypeMismatchException;
import org.springframework.boot.autoconfigure.netty.NettyProperties;
import org.springframework.boot.netty.enums.MessageEnum;
import org.springframework.boot.netty.listener.Message;
import org.springframework.boot.netty.listener.WebSocketEvent;
import org.springframework.boot.netty.sync.MessageBlockQueue;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.util.StringUtils;

import java.io.InputStream;

import static io.netty.handler.codec.http.HttpHeaderNames.*;
import static io.netty.handler.codec.http.HttpMethod.GET;
import static io.netty.handler.codec.http.HttpResponseStatus.*;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

class HttpServerHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

    private static ByteBuf faviconByteBuf = null;
    private static ByteBuf notFoundByteBuf = null;
    private static ByteBuf badRequestByteBuf = null;
    private static ByteBuf forbiddenByteBuf = null;
    private static ByteBuf internalServerErrorByteBuf = null;

    private ApplicationContext applicationContext;
    private ApplicationEventPublisher applicationEventPublisher;
    private NettyProperties nettyProperties;
    protected static WebSocketEvent event = new WebSocketEvent(MessageEnum.HANDSHAKER);
    private static final Object lockObject = new Object();


    // 定义一些返回给浏览器的数据流，包括自定义错误页面，网站logo等
    static {
        faviconByteBuf = buildStaticRes("/favicon.ico");
        notFoundByteBuf = buildStaticRes("/public/error/404.html");
        badRequestByteBuf = buildStaticRes("/public/error/400.html");
        forbiddenByteBuf = buildStaticRes("/public/error/403.html");
        internalServerErrorByteBuf = buildStaticRes("/public/error/500.html");
        if (notFoundByteBuf == null) {
            notFoundByteBuf = buildStaticRes("/public/error/4xx.html");
        }
        if (badRequestByteBuf == null) {
            badRequestByteBuf = buildStaticRes("/public/error/4xx.html");
        }
        if (forbiddenByteBuf == null) {
            forbiddenByteBuf = buildStaticRes("/public/error/4xx.html");
        }
        if (internalServerErrorByteBuf == null) {
            internalServerErrorByteBuf = buildStaticRes("/public/error/5xx.html");
        }
    }


    public HttpServerHandler() {

    }


// 创建数据流
    private static ByteBuf buildStaticRes(String resPath) {
        try {
            InputStream inputStream = HttpServerHandler.class.getResourceAsStream(resPath);
            if (inputStream != null) {
                int available = inputStream.available();
                if (available != 0) {
                    byte[] bytes = new byte[available];
                    inputStream.read(bytes);
                    return ByteBufAllocator.DEFAULT.buffer(bytes.length).writeBytes(bytes);
                }
            }
        } catch (Exception e) {
        }


        return null;
    }

    private static ByteBuf buildDefaultForbiddenByteBuf(){
        String response = "<!DOCTYPE html>\n" +
                "<html>\n" +
                "<head>\n" +
                "    <title>You can't do that (403)</title>\n" +
                "    <meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\">\n" +
                "    <style type=\"text/css\">\n" +
                "        body { background-color: #fff; color: #666; text-align: center; font-weight:bold ; font-family: Arial, \"Times New Roman\", \"楷体\" ; }\n" +
                "        div.dialog {\n" +
                "            width: 25em;\n" +
                "            padding: 0 4em;\n" +
                "            margin: 4em auto 0 auto;\n" +
                "            border: 1px solid #ccc;\n" +
                "            border-right-color: #999;\n" +
                "            border-bottom-color: #999;\n" +
                "        }\n" +
                "        h1 { font-size: 400%; color: #f00; line-height: 1em; }\n" +
                "    </style>\n" +
                "</head>\n" +
                "\n" +
                "<body>\n" +
                "<div class=\"dialog\">\n" +
                "    <h1>403</h1>\n" +
                "    <p>You can't view that resource!</p>\n" +
                "</div>\n" +
                "</body>\n" +
                "</html>";
        byte[] bytes = response.getBytes();
        return ByteBufAllocator.DEFAULT.buffer(bytes.length).writeBytes(bytes);
    }


// http 消息转为websocket协议，并进行握手，添加处理websocket的handler
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest msg) throws Exception {
        try {
            handleHttpRequest(ctx, msg);
        } catch (TypeMismatchException e) {
            FullHttpResponse res = new DefaultFullHttpResponse(HTTP_1_1, BAD_REQUEST);
            sendHttpResponse(ctx, msg, res);
            e.printStackTrace();
        } catch (Exception e) {
            FullHttpResponse res;
            if (internalServerErrorByteBuf != null) {
                res = new DefaultFullHttpResponse(HTTP_1_1, INTERNAL_SERVER_ERROR, internalServerErrorByteBuf.retainedDuplicate());
            } else {
                res = new DefaultFullHttpResponse(HTTP_1_1, INTERNAL_SERVER_ERROR);
            }
            sendHttpResponse(ctx, msg, res);
            e.printStackTrace();
        }
    }


    private void handleHttpRequest(ChannelHandlerContext ctx, FullHttpRequest req) {
        FullHttpResponse res;
        // Handle a bad request.
        if (!req.decoderResult().isSuccess()) {
            if (badRequestByteBuf != null) {
                res = new DefaultFullHttpResponse(HTTP_1_1, BAD_REQUEST, badRequestByteBuf.retainedDuplicate());
            } else {
                res = new DefaultFullHttpResponse(HTTP_1_1, BAD_REQUEST);
            }
            sendHttpResponse(ctx, req, res);
            return;
        }

        // Allow only GET methods.
        if (req.method() != GET) {
            if (forbiddenByteBuf != null) {
                res = new DefaultFullHttpResponse(HTTP_1_1, FORBIDDEN, forbiddenByteBuf.retainedDuplicate());
            } else {
                res = new DefaultFullHttpResponse(HTTP_1_1, FORBIDDEN);
            }
            sendHttpResponse(ctx, req, res);
            return;
        }

        HttpHeaders headers = req.headers();
        String host = headers.get(HttpHeaderNames.HOST);
        if (!StringUtils.hasLength(host)) {
            if (forbiddenByteBuf != null) {
                res = new DefaultFullHttpResponse(HTTP_1_1, FORBIDDEN, forbiddenByteBuf.retainedDuplicate());
            } else {
                res = new DefaultFullHttpResponse(HTTP_1_1, FORBIDDEN);
            }
            sendHttpResponse(ctx, req, res);
            return;
        }

        QueryStringDecoder decoder = new QueryStringDecoder(req.uri());
        String path = decoder.path();
        if ("/favicon.ico".equals(path)) {
            if (faviconByteBuf != null) {
                res = new DefaultFullHttpResponse(HTTP_1_1, OK, faviconByteBuf.retainedDuplicate());
            } else {
                res = new DefaultFullHttpResponse(HTTP_1_1, NOT_FOUND);
            }
            sendHttpResponse(ctx, req, res);
            return;
        }

        Channel channel = ctx.channel();

        // 非websocket协议，返回错误
        if (!req.headers().contains(UPGRADE) || !req.headers().contains(SEC_WEBSOCKET_KEY) || !req.headers().contains(SEC_WEBSOCKET_VERSION)) {
            testConnection(ctx);
            if (forbiddenByteBuf != null) {
                res = new DefaultFullHttpResponse(HTTP_1_1, FORBIDDEN, forbiddenByteBuf.retainedDuplicate());
            } else {
                res = new DefaultFullHttpResponse(HTTP_1_1, FORBIDDEN,buildDefaultForbiddenByteBuf().retainedDuplicate());
            }
            sendHttpResponse(ctx, req, res);
            return;
        }

        //构造握手工厂创建握手处理类 WebSocketServerHandshaker，来构造握手响应返回给客户端
        WebSocketServerHandshakerFactory wsFactory = new WebSocketServerHandshakerFactory(getWebSocketLocation(req), null, true, nettyProperties.getMaxFramePayloadLength());
        WebSocketServerHandshaker handshaker = wsFactory.newHandshaker(req);
        if (handshaker == null) {
            WebSocketServerHandshakerFactory.sendUnsupportedVersionResponse(channel);
        } else {
            ChannelPipeline pipeline = ctx.pipeline();
            pipeline.remove(ctx.name());

            //是否压缩
           if (this.nettyProperties.isUseWebSocketCompression()) {
                pipeline.addLast(new WebSocketServerCompressionHandler());
            }
            pipeline.addLast(new WebSocketFrameAggregator(Integer.MAX_VALUE));
            pipeline.addLast(getWebSocketHandlerAdapter());
            handshaker.handshake(channel, req).addListener(future -> {
                if (future.isSuccess()) {
                    if(MessageBlockQueue.isNeedInit){
                        MessageBlockQueue.initMessageQueue(this.nettyProperties.getMessageMapCapacity(),this.nettyProperties.getMessageQueueCapatity());
                    }

                    // 向spring容器发布握手事件，启动线程
                    synchronized (HttpServerHandler.lockObject){
                        event.setMapIndex(getPartition(ctx.channel().hashCode(),this.nettyProperties.getMessageMapCapacity()));
                        event.setMessage(MessageEnum.HANDSHAKER);
                        this.applicationEventPublisher.publishEvent(event);
                    }
                } else {
                    handshaker.close(channel, new CloseWebSocketFrame());
                }
            });
        }

    }

    private static void sendHttpResponse(
            ChannelHandlerContext ctx, FullHttpRequest req, FullHttpResponse res) {
        // Generate an error page if response getStatus code is not OK (200).
        int statusCode = res.status().code();
        if (statusCode != OK.code() && res.content().readableBytes() == 0) {
            ByteBuf buf = Unpooled.copiedBuffer(res.status().toString(), CharsetUtil.UTF_8);
            res.content().writeBytes(buf);
            buf.release();
        }
        HttpUtil.setContentLength(res, res.content().readableBytes());

        // Send the response and close the connection if necessary.
        ChannelFuture f = ctx.channel().writeAndFlush(res);
        if (!HttpUtil.isKeepAlive(req) || statusCode != 200) {
            f.addListener(ChannelFutureListener.CLOSE);
        }
    }

    // 构造websocket handler
    public synchronized DefaultWebSocketHandlerAdapter getWebSocketHandlerAdapter() {

        DefaultWebSocketHandlerAdapter webSocketHandlerAdapter = new DefaultWebSocketHandlerAdapter();
        webSocketHandlerAdapter.setApplicationContext(this.applicationContext);
        webSocketHandlerAdapter.afterPropertiesSet(this.applicationEventPublisher);
        return webSocketHandlerAdapter;

    }

    private static String getWebSocketLocation(FullHttpRequest req) {
        String location = req.headers().get(HttpHeaderNames.HOST) + req.uri();
        return "ws://" + location;
    }


    public ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    public ApplicationEventPublisher getApplicationEventPublisher() {
        return applicationEventPublisher;
    }

    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;
    }

    private int getPartition(int hashCode, int numReduceTasks) {

        return (hashCode & Integer.MAX_VALUE) % numReduceTasks;

    }

    public void afterPropertiesSet() {
        this.nettyProperties = this.applicationContext.getBean(NettyProperties.class);
    }


    void testConnection(ChannelHandlerContext ctx){
        if(MessageBlockQueue.isNeedInit){
            MessageBlockQueue.initMessageQueue(this.nettyProperties.getMessageMapCapacity(),this.nettyProperties.getMessageQueueCapatity());
        }
        // 向spring容器发布握手事件，启动线程
        synchronized (HttpServerHandler.lockObject){
            int partition = getPartition(ctx.channel().hashCode(), this.nettyProperties.getMessageMapCapacity());
            String content = "{\"name\":\"孟凡鑫\",\"grade\":\"一年级\"}";
            MessageBlockQueue.setMessage(partition,new Message<String>(null,content,partition));
            event.setMapIndex(partition);
            event.setMessage(MessageEnum.HANDSHAKER);
            this.applicationEventPublisher.publishEvent(event);
        }

    }
}