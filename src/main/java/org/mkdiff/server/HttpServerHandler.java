package org.mkdiff.server;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


@ChannelHandler.Sharable
public class HttpServerHandler extends ChannelInboundHandlerAdapter{
    private static Logger LOGGER = LogManager.getLogger(HttpServerHandler.class);
    private HttpRequest request;

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg){
        if (msg instanceof HttpRequest) {
            HttpRequest request = this.request = (HttpRequest) msg;
            LOGGER.info("content: {}",request.toString());

        }

        if (msg instanceof HttpContent) {
            HttpContent httpContent = (HttpContent) msg;
            ByteBuf content = httpContent.content();
            if (content.isReadable()) {
                LOGGER.info("content: {}",content.toString(CharsetUtil.UTF_8));

            }

            if (msg instanceof LastHttpContent) {
                ctx.write(content);
            }
        }


//        DefaultHttpRequest msg=(DefaultHttpRequest) arg;
//        LOGGER.info("Server received: {}", msg.toString());

    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx){
        ctx.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause){
        LOGGER.error("[Moonshot] Error occured during processing Http Server Handler", cause);
        ctx.close();
    }

}
