package com.v.game.netty;

import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class HearBeatHandler extends ChannelInboundHandlerAdapter {

    private int lossConnectCount = 0;

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        super.channelRead(ctx, msg);

        lossConnectCount=0;
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        super.userEventTriggered(ctx, evt);

        if (evt instanceof IdleStateEvent) {
            IdleStateEvent event = (IdleStateEvent) evt;
            if(event.state() == IdleState.READER_IDLE) {
                lossConnectCount++;
                if(lossConnectCount > 2) {
                    log.info("心跳超时");
                    ctx.close();
                }
            } else if (event.state() == IdleState.WRITER_IDLE) {
                ctx.writeAndFlush("beat");
            }
        }
    }
}
