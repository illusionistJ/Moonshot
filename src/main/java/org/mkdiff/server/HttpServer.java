package org.mkdiff.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.InetSocketAddress;

public class HttpServer {
    private static Logger LOGGER = LogManager.getLogger(HttpServer.class);
    private int port;
    EventLoopGroup group;

    public HttpServer(int port){
        this.port=port;
    }

    public  void start() throws Exception {
        final EchoServerHandler serverHandler = new EchoServerHandler();
        group = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(group)
                    .channel(NioServerSocketChannel.class)
                    .localAddress(new InetSocketAddress(port))
                    .childHandler(new HttpPipelineInitializer());
            ChannelFuture f = b.bind().sync();
            f.channel().closeFuture().sync();
        }catch (Exception e){
            LOGGER.error("[Moonshot] Error occured",e);
        }finally{
            group.shutdownGracefully().sync();
        }
    }

    public void stop() throws Exception {
        group.shutdownGracefully().sync();
    }
}
