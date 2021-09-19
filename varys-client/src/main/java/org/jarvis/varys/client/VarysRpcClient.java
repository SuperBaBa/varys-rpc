package org.jarvis.varys.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.jarvis.varys.client.handler.VarysClientHandler;
import org.jarvis.varys.codec.VarysMessageDecoder;
import org.jarvis.varys.codec.VarysMessageEncoder;
import org.jarvis.varys.dto.VarysRequest;
import org.jarvis.varys.dto.VarysResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * RPC 客户端（用于发送 RPC 请求）
 *
 * @author marcus
 */
public class VarysRpcClient extends ChannelInboundHandlerAdapter {

    private static final Logger log = LoggerFactory.getLogger(VarysRpcClient.class);

    private final String host;
    private final int port;

    private VarysResponse response;

    public VarysResponse getResponse() {
        return response;
    }

    public VarysRpcClient(String host, int port) {
        this.host = host;
        this.port = port;
    }


    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        super.handlerAdded(ctx);
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        super.handlerRemoved(ctx);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        System.out.println("channel read complete...");
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("channel active...");
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        // 由 VarysMessageDecoder 解码后可进行使用
        this.response = (VarysResponse) msg;
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("api caught exception", cause);
        ctx.close();
    }

    public VarysResponse sendCommand(VarysRequest request) throws Exception {
        EventLoopGroup group = new NioEventLoopGroup();
        VarysClientHandler varysClientHandler = new VarysClientHandler();
        try {
            // 创建并初始化 Netty 客户端 Bootstrap 对象
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel channel) throws Exception {
                            ChannelPipeline pipeline = channel.pipeline();
                            // 向管道中添加 ChannelInbound 和 ChannelOutbound 处理器
                            pipeline.addLast(new VarysMessageDecoder(VarysResponse.class)) // 解码 RPC 响应
                                    .addLast(new VarysMessageEncoder(VarysRequest.class, true)) // 编码 RPC 请求
                                    .addLast(varysClientHandler); // 处理 RPC 响应
                        }
                    });

            // 连接 RPC 服务器
            ChannelFuture future = bootstrap.connect(host, port).sync();
            // 写入 RPC 请求数据并关闭连接
            Channel channel = future.channel();
            channel.writeAndFlush(request).sync();
            channel.closeFuture().sync();
            // 返回 RPC 响应对象
            return response;
        } finally {
            group.shutdownGracefully();
        }
    }
}
