package org.mkdiff.server;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.*;
import io.netty.handler.codec.http.*;


import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.netty.util.internal.ObjectUtil.checkNotNull;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static io.netty.handler.codec.http.HttpHeaderNames.CONNECTION;
import static io.netty.handler.codec.http.HttpHeaderNames.CONTENT_LENGTH;
import static io.netty.handler.codec.http.HttpHeaderNames.CONTENT_TYPE;
import static io.netty.handler.codec.http.HttpResponseStatus.CONTINUE;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;


@ChannelHandler.Sharable
public class Http1Handler extends SimpleChannelInboundHandler<FullHttpRequest> {
    private static Logger LOGGER = LogManager.getLogger(Http1Handler.class);
    private final String establishApproach;

    public Http1Handler(String establishApproach){
        this.establishApproach = checkNotNull(establishApproach, "establishApproach");
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, FullHttpRequest fullHttpRequest) throws Exception {
        if (HttpUtil.is100ContinueExpected(fullHttpRequest)) {
            channelHandlerContext.write(new DefaultFullHttpResponse(HTTP_1_1, CONTINUE));
        }

        boolean keepAlive = HttpUtil.isKeepAlive(fullHttpRequest);

        ByteBuf content = channelHandlerContext.alloc().buffer();

        ByteBufUtil.writeAscii(content, "1. Protocol: \n"+ fullHttpRequest.protocolVersion() + " (" + establishApproach + ")\n\n2.Method: \n");
        ByteBufUtil.writeAscii(content, fullHttpRequest.method().toString()+"\n\n3.Headers: \n");
        ByteBufUtil.writeAscii(content, fullHttpRequest.headers().toString()+"\n\n4.Contents: \n");

        content.writeBytes(fullHttpRequest.content().duplicate());

        FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, OK, content);
        response.headers().set(CONTENT_TYPE, "text/plain; charset=UTF-8");
        response.headers().setInt(CONTENT_LENGTH, response.content().readableBytes());

        if (!keepAlive) {
            channelHandlerContext.write(response).addListener(ChannelFutureListener.CLOSE);
        } else {
            response.headers().set(CONNECTION, HttpHeaderValues.KEEP_ALIVE);
            channelHandlerContext.write(response);
        }
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext channelHandlerContext) throws Exception {
        channelHandlerContext.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext channelHandlerContext, Throwable cause) {
        LOGGER.error(cause);
        channelHandlerContext.close();
    }
}
