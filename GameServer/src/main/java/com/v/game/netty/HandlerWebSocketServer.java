package com.v.game.netty;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 自定义的Handler需要继承Netty规定好的HandlerAdapter
 * 才能被Netty框架所关联，有点类似SpringMVC的适配器模式
 **/
@Slf4j
@Component
public class HandlerWebSocketServer extends SimpleChannelInboundHandler<TextWebSocketFrame> {

    @Autowired
    private  ChannelContextUtils channelContextUtils;

    @Override
    public void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg) throws Exception {
        // 处理消息
        //ctx.channel().writeAndFlush(new TextWebSocketFrame("Server received: " + msg.text()));
        Channel channel = ctx.channel();
        Attribute<String> attr = channel.attr(AttributeKey.valueOf(channel.id().toString()));
        String userId = attr.get();
        log.info("Received message: " + msg.text() + " " + "from user: " + userId);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        // 添加连接
        log.info("Client connected: " + ctx.channel());
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        // 断开连接
        log.info("Client disconnected: " + ctx.channel());
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        super.userEventTriggered(ctx, evt);

        if (evt instanceof WebSocketServerProtocolHandler.HandshakeComplete) {
            WebSocketServerProtocolHandler.HandshakeComplete handshakeComplete = (WebSocketServerProtocolHandler.HandshakeComplete) evt;
            String url = handshakeComplete.requestUri();
            String token = GetToken(url);
            log.info("Handshake complete, url: " + url + ", token: " + token);
            if(token == null) {
                ctx.channel().close();
                return;
            }
            //TODO:检测token是否有效

            //根据token获取userId
            String userId = token;
            channelContextUtils.addContext(userId, ctx.channel());
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        // 异常处理
        cause.printStackTrace();
        ctx.close();
    }

    private String GetToken(String url)
    {
        if(!url.contains("?"))
            return  null;

        String[] query = url.split("\\?");
        if(query.length == 2)
        {
            String[] params = query[1].split("=");
            if(params.length == 2)
            {
                return params[1];
            }
        }
        return null;
    }
}

