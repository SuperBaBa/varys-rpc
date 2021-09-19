package org.jarvis.varys.client.netty4;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.concurrent.DefaultThreadFactory;
import org.apache.zookeeper.Version;
import org.jarvis.varys.channel.VarysChannel;
import org.jarvis.varys.client.AbstractClient;
import org.jarvis.varys.client.exception.RemotingException;
import org.jarvis.varys.client.handler.VarysClientHandler;
import org.jarvis.varys.codec.VarysMessageDecoder;
import org.jarvis.varys.codec.VarysMessageEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static org.jarvis.varys.NettyEventLoopFactory.socketChannelClass;

public class NettyClient extends AbstractClient {

    static int DEFAULT_IO_THREADS = Math.min(Runtime.getRuntime().availableProcessors() + 1, 32);
    int DEFAULT_CONNECT_TIMEOUT = 3000;


    private static final Logger logger = LoggerFactory.getLogger(NettyClient.class);
    /**
     * netty client bootstrap
     */
    private static final EventLoopGroup EVENT_LOOP_GROUP = new NioEventLoopGroup(DEFAULT_IO_THREADS, new DefaultThreadFactory("NettyClientWorker"));

    private static final String SOCKS_PROXY_HOST = "socksProxyHost";

    private static final String SOCKS_PROXY_PORT = "socksProxyPort";

    private static final String DEFAULT_SOCKS_PROXY_PORT = "1080";

    private Bootstrap bootstrap;

    private volatile Channel channel;


    public NettyClient() throws RemotingException {
        super();
    }

    @Override
    protected void doOpen() throws Throwable {

        bootstrap = new Bootstrap();
        bootstrap.group(EVENT_LOOP_GROUP)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .option(ChannelOption.TCP_NODELAY, true)
                .option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
                .channel(socketChannelClass());

        bootstrap.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, DEFAULT_CONNECT_TIMEOUT);
        bootstrap.handler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
                ch.pipeline()//.addLast("logging",new LoggingHandler(LogLevel.INFO))//for debug
                        .addLast("decoder", new VarysMessageDecoder())
                        .addLast("encoder", new VarysMessageEncoder())
                        .addLast("handler", new VarysClientHandler());
            }
        });

    }

    @Override
    protected void doClose() throws Throwable {

    }

    @Override
    protected void doConnect() throws Throwable {
        ChannelFuture future = bootstrap.connect(getConnectAddress());
        try {
            boolean ret = future.awaitUninterruptibly(500, MILLISECONDS);

            if (ret && future.isSuccess()) {
                Channel newChannel = future.channel();
                try {
                    // Close old channel
                    // copy reference
                    Channel oldChannel = NettyClient.this.channel;
                    if (oldChannel != null) {
                        if (logger.isInfoEnabled()) {
                            logger.info("Close old netty channel " + oldChannel + " on create new netty channel " + newChannel);
                        }
                        oldChannel.close();
                    }
                } finally {
                    if (NettyClient.this.isClosed()) {
                        try {
                            if (logger.isInfoEnabled()) {
                                logger.info("Close new netty channel " + newChannel + ", because the client closed.");
                            }
                            newChannel.close();
                        } finally {
                            NettyClient.this.channel = null;
                        }
                    } else {
                        NettyClient.this.channel = newChannel;
                    }
                }
            }
        } finally {
            // just add new valid channel to NettyChannel's cache
            if (!isConnected()) {
                //future.cancel(true);
            }
        }
    }

    @Override
    protected void doDisConnect() throws Throwable {

    }

    @Override
    protected VarysChannel getChannel() {
        return null;
    }

    public InetSocketAddress getConnectAddress() {
        return new InetSocketAddress("127.0.0.1",8001);
    }
}
