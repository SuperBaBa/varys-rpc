package org.jarvis.varys.server;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;

public class EchoClientHandler extends ChannelInboundHandlerAdapter { // (1)
    private Logger logger = LoggerFactory.getLogger(getClass());
    private final ByteBuf firstMessage;
    private ByteBuf buf;

    public EchoClientHandler() {
        byte[] req = "Hello from client".getBytes();
        firstMessage = Unpooled.buffer(req.length);
        firstMessage.writeBytes(req);
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        buf = ctx.alloc().buffer(4);
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) {
        buf.release(); // (1)
        buf = null;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        logger.info("client channel active");
        // Send the message to Server
        logger.info("client send req...");
        //ctx.writeAndFlush(firstMessage);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) { // (2)
        ByteBuf msgBuf = (ByteBuf) msg;
        /*buf.writeBytes(msgBuf);
        msgBuf.release();*/

        if (msgBuf.readableBytes() >= 4) { // (3)
            byte[] bytes = new byte[msgBuf.readableBytes()];
            msgBuf.readBytes(bytes);
            System.out.println(new String(bytes, StandardCharsets.UTF_8));
            /*long currentTimeMillis = (buf.readUnsignedInt() - 2208988800L) * 1000L;
            System.out.println(new Date(currentTimeMillis));*/
            ctx.writeAndFlush(msgBuf);
            ctx.close();
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) { // (4)
        // Close the connection when an exception is raised.
        cause.printStackTrace();
        logger.error("client caught exception", cause);
        ctx.close();
    }
}