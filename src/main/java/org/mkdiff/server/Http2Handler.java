package org.mkdiff.server;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http2.*;
import io.netty.util.CharsetUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static io.netty.buffer.Unpooled.copiedBuffer;
import static io.netty.buffer.Unpooled.unreleasableBuffer;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;


public class Http2Handler extends Http2ConnectionHandler implements Http2FrameListener {
    private static Logger LOGGER = LogManager.getLogger(Http2Handler.class);
    static final ByteBuf RESPONSE_BYTES = unreleasableBuffer(copiedBuffer("Hello World", CharsetUtil.UTF_8));

    Http2Handler(Http2ConnectionDecoder decoder, Http2ConnectionEncoder encoder,Http2Settings initialSettings){
        super(decoder,encoder, initialSettings);
    }

    private static Http2Headers http1HeadersToHttp2Headers(FullHttpRequest request) {
        CharSequence host = request.headers().get(HttpHeaderNames.HOST);
        Http2Headers http2Headers = new DefaultHttp2Headers()
                .method(HttpMethod.GET.asciiName())
                .path(request.uri())
                .scheme(HttpScheme.HTTP.name());
        if (host != null) {
            http2Headers.authority(host);
        }
        return http2Headers;
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext channelHandlerContext, Object evt) throws Exception {
        if (evt instanceof HttpServerUpgradeHandler.UpgradeEvent) {
            HttpServerUpgradeHandler.UpgradeEvent upgradeEvent = (HttpServerUpgradeHandler.UpgradeEvent) evt;
            onHeadersRead(channelHandlerContext, 1, http1HeadersToHttp2Headers(upgradeEvent.upgradeRequest()), 0 , true);
        }
        super.userEventTriggered(channelHandlerContext, evt);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext channelHandlerContext, Throwable cause) throws Exception {
        super.exceptionCaught(channelHandlerContext, cause);
        LOGGER.error(cause);
        channelHandlerContext.close();
    }

    private void sendResponse(ChannelHandlerContext channelHandlerContext, int streamId, ByteBuf payload) {
        Http2Headers headers = new DefaultHttp2Headers().status(OK.codeAsText());
        encoder().writeHeaders(channelHandlerContext, streamId, headers, 0, false, channelHandlerContext.newPromise());
        encoder().writeData(channelHandlerContext, streamId, payload, 0, true, channelHandlerContext.newPromise());
    }

    @Override
    public int onDataRead(ChannelHandlerContext channelHandlerContext, int i, ByteBuf byteBuf, int i1, boolean b) throws Http2Exception {
        int processed = byteBuf.readableBytes() + i1;
        if (b) {
            sendResponse(channelHandlerContext, i, byteBuf.retain());
        }
        return processed;
    }

    @Override
    public void onHeadersRead(ChannelHandlerContext channelHandlerContext, int i, Http2Headers http2Headers, int i1, boolean b) throws Http2Exception {
        if (b) {
            ByteBuf content = channelHandlerContext.alloc().buffer();
            content.writeBytes(RESPONSE_BYTES.duplicate());
            ByteBufUtil.writeAscii(content, " - via HTTP/2");
            sendResponse(channelHandlerContext, i, content);
        }
    }

    @Override
    public void onHeadersRead(ChannelHandlerContext channelHandlerContext, int i, Http2Headers http2Headers, int i1, short i2, boolean b, int i3, boolean b1) throws Http2Exception {
        onHeadersRead(channelHandlerContext, i, http2Headers, i3, b1);
    }

    @Override
    public void onPriorityRead(ChannelHandlerContext channelHandlerContext, int i, int i1, short i2, boolean b) throws Http2Exception {

    }

    @Override
    public void onRstStreamRead(ChannelHandlerContext channelHandlerContext, int i, long l) throws Http2Exception {

    }

    @Override
    public void onSettingsAckRead(ChannelHandlerContext channelHandlerContext) throws Http2Exception {

    }

    @Override
    public void onSettingsRead(ChannelHandlerContext channelHandlerContext, Http2Settings http2Settings) throws Http2Exception {

    }

    @Override
    public void onPingRead(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf) throws Http2Exception {

    }

    @Override
    public void onPingAckRead(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf) throws Http2Exception {

    }

    @Override
    public void onPushPromiseRead(ChannelHandlerContext channelHandlerContext, int i, int i1, Http2Headers http2Headers, int i2) throws Http2Exception {

    }

    @Override
    public void onGoAwayRead(ChannelHandlerContext channelHandlerContext, int i, long l, ByteBuf byteBuf) throws Http2Exception {

    }

    @Override
    public void onWindowUpdateRead(ChannelHandlerContext channelHandlerContext, int i, int i1) throws Http2Exception {

    }

    @Override
    public void onUnknownFrame(ChannelHandlerContext channelHandlerContext, byte b, int i, Http2Flags http2Flags, ByteBuf byteBuf) throws Http2Exception {

    }
}
