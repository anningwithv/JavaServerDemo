package com.v.game.netty;

import com.v.game.config.AppConfig;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class NettyWebSocketServer {

    private static EventLoopGroup bossGroup;
    private static EventLoopGroup workerGroup;

    @Autowired
    private AppConfig appConfig;

    @Autowired
    private HearBeatHandler heartBeatHandler;

    @Autowired
    private HandlerWebSocketServer handlerWebSocketServer;

    public NettyWebSocketServer() {

    }

    @Async
    public void run() throws Exception
    {
        //创建两个线程组 bossGroup、workerGroup
        bossGroup = new NioEventLoopGroup();
        workerGroup = new NioEventLoopGroup();
        try {
            //创建服务端的启动对象，设置参数
            ServerBootstrap bootstrap = new ServerBootstrap();
            //设置两个线程组boosGroup和workerGroup
            bootstrap.group(bossGroup, workerGroup)
                    //设置服务端通道实现类型
                    .channel(NioServerSocketChannel.class)
                    //设置线程队列得到连接个数
                    .option(ChannelOption.SO_BACKLOG, 128)
                    //设置保持活动连接状态
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    //使用匿名内部类的形式初始化通道对象
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            //给pipeline管道设置处理器
                            ChannelPipeline p = socketChannel.pipeline();
                            //对http协议的支持，使用http的编码器，解码器
                            p.addLast(new HttpServerCodec());
                            //保证接收的http请求的完整性
                            p.addLast(new HttpObjectAggregator(65536));
                            //将http协议升级为ws协议，对websocket的支持
                            p.addLast(new WebSocketServerProtocolHandler("/websocket", null, true, 65536, true, true, 10000L));
                            //ws连接、消息手法处理
                            p.addLast(handlerWebSocketServer);
                            //定义超时规则
                            p.addLast(new IdleStateHandler(5, 0, 0, TimeUnit.SECONDS));
                            //心跳超时处理
                            p.addLast(heartBeatHandler);
                            p.addLast("decoder", new StringDecoder());
                            p.addLast("encoder", new StringEncoder());
                        }
                    });//给workerGroup的EventLoop对应的管道设置处理器
            log.info("Netty服务端已经准备就绪...");
            //绑定端口号，启动服务端
            ChannelFuture channelFuture = bootstrap.bind(appConfig.getNettyPort()).sync();
            //对关闭通道进行监听
            channelFuture.channel().closeFuture().sync();
        }
        catch (Exception exception) {
            log.info("启动netty失败");
            throw new RuntimeException(exception);
        }
        finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    @PreDestroy
    public void close(){
        bossGroup.shutdownGracefully();
        workerGroup.shutdownGracefully();
    }
//    public static void main(String[] args) {
//        int port = 9090;
//        if (args.length > 0) {
//            port = Integer.parseInt(args[0]);
//        }
//
//        try {
//            new NettyWebSocketServer(port).run();
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
//    }
}
