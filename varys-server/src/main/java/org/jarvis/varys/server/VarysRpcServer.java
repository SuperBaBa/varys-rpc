package org.jarvis.varys.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.logging.LoggingHandler;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import org.jarvis.varys.annotation.VarysService;
import org.jarvis.varys.codec.VarysMessageDecoder;
import org.jarvis.varys.codec.VarysMessageEncoder;
import org.jarvis.varys.dto.VarysRequest;
import org.jarvis.varys.dto.VarysResponse;
import org.jarvis.varys.handler.VarysServerHandler;
import org.jarvis.varys.registry.VarysRegistrar;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.util.ConcurrentReferenceHashMap;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.StringJoiner;
import java.util.stream.Collectors;

/**
 * rpc服务器
 * 此类将用于注册当前所有需要暴露的服务,
 * 接口{@code ApplicationContextAware}实现是为了在初始化时
 * 调用该Bean的{@code ApplicationContextAware#setApplicationContext}方法
 * 会将容器本身作为参数传给该方法——该方法中的实现部分将Spring传入的参数（容器本身）赋给该类对象的applicationContext实例变量
 * <p>
 * 接口{@code InitializingBean}是为了在Bean初始化执行该方法,与init-method指定，两种方式可以同时使用
 * 实现InitializingBean接口是直接调用afterPropertiesSet方法
 * 比通过反射调用init-method指定的方法效率要高一点，但是init-method方式消除了对spring的依赖。
 *
 * @author Marcus
 * @date 2021/2/16-13:22
 */
public class VarysRpcServer implements ApplicationContextAware, InitializingBean {

    /**
     * 日志记录器
     */
    private static final Logger log = LoggerFactory.getLogger(VarysRpcServer.class);

    /**
     * 服务地址
     */
    private final String serviceAddress;

    private ApplicationContext applicationContext;

    /**
     * 服务注册中心
     */
    private final VarysRegistrar serviceRegistry;

    /**
     * 处理程序映射
     */
    private final Map<String, Object> handlerMap = new ConcurrentReferenceHashMap<>();

    /**
     * 不同的rpc服务器
     *
     * @param serviceAddress  服务地址
     * @param serviceRegistry 服务注册中心
     */
    public VarysRpcServer(String serviceAddress, VarysRegistrar serviceRegistry) {
        this.serviceAddress = serviceAddress;
        this.serviceRegistry = serviceRegistry;
    }

    /**
     * 设置应用程序上下文
     *
     * @param applicationContext 应用程序上下文
     * @throws BeansException 实体异常
     */
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
        // 扫描带有 VarysService 注解的类并初始化 handlerMap 对象
        Map<String, Object> serviceBeanMap = applicationContext.getBeansWithAnnotation(VarysService.class);
        log.debug("获取到需要暴露的服务共计{}个", serviceBeanMap.size());
        if (!serviceBeanMap.isEmpty()) {
            for (Object serviceBean : serviceBeanMap.values()) {
                // 获取已打上标记的类中注解的 serviceName 和 version
                VarysService varysService = serviceBean.getClass().getAnnotation(VarysService.class);
                StringJoiner strJoiner = new StringJoiner("-");
                strJoiner.add(varysService.value().getName())
                        .add(varysService.version());
                // 拼接完成服务名后放置Map中留存映射关系
                handlerMap.put(strJoiner.toString(), serviceBean);
            }
        }
    }

    /**
     * 在属性设置
     *
     * @throws Exception 异常
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        //boss线程监听端口，worker线程负责数据读写
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            // 创建并初始化 Netty 服务端 Bootstrap 对象
            ServerBootstrap bootstrap = new ServerBootstrap()
                    //设置线程池
                    .group(bossGroup, workerGroup)
                    //设置socket工厂
                    .channel(NioServerSocketChannel.class)
                    // 设置 NioServerSocketChannel 的处理器
                    .handler(new LoggingHandler())
                    // 设置连入服务端的 Client 的 SocketChannel 的处理器
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel channel) throws Exception {
                            //获取管道
                            ChannelPipeline pipeline = channel.pipeline()
                                    //字符串解码器
                                    .addLast(new VarysMessageDecoder(VarysRequest.class,"fastjson"))
                                    //字符串编码器
                                    .addLast(new VarysMessageEncoder(VarysResponse.class,"fastjson"))
                                    //处理类
                                    .addLast(new VarysServerHandler(handlerMap));
                        }
                    });
            //设置TCP参数
            //1.链接缓冲池的大小（ServerSocketChannel的设置）
            bootstrap.option(ChannelOption.SO_BACKLOG, 1024);
            //维持链接的活跃，清除死链接(SocketChannel的设置)
            bootstrap.childOption(ChannelOption.SO_KEEPALIVE, true);
            //关闭延迟发送
            bootstrap.childOption(ChannelOption.TCP_NODELAY, true);
            //TODO 优化 IP 和 端口号的获取，域名包括带http的均可以作为获取
            // 获取 RPC 服务器的 IP 地址与端口号
            String[] addressArray = serviceAddress.split(":");
            String host = addressArray[0];
            int port = Integer.parseInt(addressArray[1]);
            // 启动 RPC 服务器
            ChannelFuture future = bootstrap.bind(host, port).sync();
            // 注册 RPC 服务地址
            if (serviceRegistry != null) {
                for (String interfaceName : handlerMap.keySet()) {
                    serviceRegistry.registry(interfaceName, serviceAddress);
                    log.debug("register service: {} => {}", interfaceName, serviceAddress);
                }
            }
            log.debug("org.jarvis.server started on port {}", port);
            // 关闭 RPC 服务器
            ChannelFuture closeFuture = future.channel().closeFuture();
            closeFuture.addListeners(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture future) throws Exception {
                    serviceRegistry.removeAllService();
                }
            });
            closeFuture.sync();
        } finally {
            // 关闭接收的event loop后再关闭所有线程
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }

    }
}
