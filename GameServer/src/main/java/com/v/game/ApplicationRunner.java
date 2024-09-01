package com.v.game;

import com.v.game.netty.NettyWebSocketServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.stereotype.Component;

@Component
public class ApplicationRunner implements org.springframework.boot.ApplicationRunner {

    @Autowired()
    private NettyWebSocketServer nettyWebSocketServer;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        nettyWebSocketServer.run();
    }
}
