package org.jarvis.varys.server;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;

public class EchoServerHandler extends ChannelInboundHandlerAdapter {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public void channelActive(ChannelHandlerContext ctx) { // 建立并且准备进行通信时被调用
        final ByteBuf time = ctx.alloc().buffer(4); // 分配一个包含这个消息的新的缓冲,写一个 int 整数需要4字节
        time.writeInt((int) (System.currentTimeMillis() / 1000L + 2208988800L));

        String message = "Hello Client\n";
        final ByteBuf activeMessage = ctx.alloc().buffer(message.getBytes().length);
        activeMessage.writeBytes(message.getBytes());

        final ChannelFuture f = ctx.writeAndFlush(activeMessage); // ChannelFuture 代表了一个还没有发生的 I/O 操作
        /*f.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) {
                assert f == future;
                ctx.close();// 操作完成时关闭 Channel
            }
        }); */
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) { // (2)
        System.out.println("server channel read...");
        ByteBuf buf = (ByteBuf) msg;
        byte[] req = new byte[buf.readableBytes()];
        buf.readBytes(req);
        try {
            String body = new String(req, StandardCharsets.UTF_8);
            System.out.println(body);
            logger.info("org.jarvis.server channel read msg:{}", body);
        } catch (Exception e) {
            e.printStackTrace();
        }
        String response = "hello from org.jarvis.server";
        ByteBuf resp = Unpooled.copiedBuffer(response.getBytes());
        ctx.write(resp);// 将消息缓冲到内部缓存
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        logger.info("server channel read complete");
        ctx.flush();// 把缓冲区中数据强行输出。或者你可以用更简洁的 cxt.writeAndFlush(msg) 以达到同样的目的
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) { // (4)
        // Close the connection when an exception is raised.
        logger.error("server caught exception", cause);
        ctx.close();
    }
}