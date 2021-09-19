package org.jarvis.varys.client.handler;

import io.netty.channel.*;
import io.netty.handler.timeout.IdleStateEvent;
import org.apache.commons.lang3.StringUtils;
import org.apache.zookeeper.server.Request;
import org.jarvis.varys.dto.VarysRequest;
import org.jarvis.varys.dto.VarysResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class VarysClientHandler extends ChannelDuplexHandler {
    private static final Logger logger = LoggerFactory.getLogger(VarysClientHandler.class);

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        logger.info("The connection of " + channel.localAddress().toString() + " -> " + channel.remoteAddress().toString() + " is established.");
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        ChannelFuture future = channel.disconnect();
        future.removeListeners(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                logger.info("The connection of " + channel.localAddress().toString() + " -> " + channel.remoteAddress().toString() + " is disconnected.");
            }
        });
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        Channel channel = ctx.channel();
        //handler.received(channel, msg);
    }

    /*@Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        super.write(ctx, msg, promise);
        final NettyChannel channel = NettyChannel.getOrAddChannel(ctx.channel(), url, handler);
        final boolean isRequest = msg instanceof Request;

        // We add listeners to make sure our out bound event is correct.
        // If our out bound event has an error (in most cases the encoder fails),
        // we need to have the request return directly instead of blocking the invoke process.
        promise.addListener(future -> {
            if (future.isSuccess()) {
                // if our future is success, mark the future to sent.
                handler.sent(channel, msg);
                return;
            }

            Throwable t = future.cause();
            if (t != null && isRequest) {
                Request request = (Request) msg;
                Response response = buildErrorResponse(request, t);
                handler.received(channel, response);
            }
        });
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        // send heartbeat when read idle.
        if (evt instanceof IdleStateEvent) {
            try {
                NettyChannel channel = NettyChannel.getOrAddChannel(ctx.channel(), url, handler);
                if (logger.isDebugEnabled()) {
                    logger.debug("IdleStateEvent triggered, send heartbeat to channel " + channel);
                }
                Request req = new Request();
                req.setVersion(Version.getProtocolVersion());
                req.setTwoWay(true);
                req.setEvent(HEARTBEAT_EVENT);
                channel.send(req);
            } finally {
                NettyChannel.removeChannelIfDisconnected(ctx.channel());
            }
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }*/

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
            throws Exception {
    }

    /**
     * build a bad request's response
     *
     * @param request the request
     * @param t       the throwable. In most cases, serialization fails.
     * @return the response
     */
    private static VarysResponse buildErrorResponse(VarysRequest request, Throwable t) {
        VarysResponse response = new VarysResponse(request.getRequestId(), request.getServiceVersion());
        response.setStatus(VarysResponse.BAD_REQUEST);
        //response.setErrorMessage(StringUtils.toString(t));
        return response;
    }

}
