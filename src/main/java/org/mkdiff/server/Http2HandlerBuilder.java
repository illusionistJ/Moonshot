package org.mkdiff.server;

import io.netty.handler.codec.http2.*;

import static io.netty.handler.logging.LogLevel.INFO;

public class Http2HandlerBuilder extends AbstractHttp2ConnectionHandlerBuilder<Http2Handler, Http2HandlerBuilder> {
    private static final Http2FrameLogger logger = new Http2FrameLogger(INFO, Http2Handler.class);

    Http2HandlerBuilder(){
        frameLogger(logger);
    }

    @Override
    protected Http2Handler build(){
        return super.build();
    }

    @Override
    protected Http2Handler build(Http2ConnectionDecoder http2ConnectionDecoder, Http2ConnectionEncoder http2ConnectionEncoder,
                                 Http2Settings http2Settings) throws Exception {
        Http2Handler handler = new Http2Handler(http2ConnectionDecoder, http2ConnectionEncoder, http2Settings);
        frameListener(handler);
        return handler;
    }
}
