package org.jarvis.varys.client;

import io.netty.channel.DefaultChannelPromise;
import io.netty.channel.DefaultEventLoop;
import io.netty.util.concurrent.DefaultPromise;
import org.apache.commons.lang3.StringUtils;
import org.jarvis.varys.core.VarysHolder;
import org.jarvis.varys.discovery.VarysServiceDiscovery;
import org.jarvis.varys.dto.VarysRequest;
import org.jarvis.varys.dto.VarysResponse;
import org.jarvis.varys.dto.VarysRpcFuture;
import org.jarvis.varys.registry.VarysRegistrar;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Proxy;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * varys 客户端发送远程调用代理
 *
 * @author marcus
 * @date 2021/8/23-0:20
 */
public class VarysRpcProxy {
    /**
     * 日志
     */
    private static final Logger log = LoggerFactory.getLogger(VarysRpcProxy.class);

    /**
     * 服务发现
     */
    private final VarysServiceDiscovery serviceDiscovery;
    /**
     * 服务地址
     */
    private String serviceAddress;

    private final long timeout;

    /**
     * 不同的rpc代理
     *
     * @param serviceDiscovery 服务发现
     */
    public VarysRpcProxy(VarysServiceDiscovery serviceDiscovery, long timeout) {
        this.serviceDiscovery = serviceDiscovery;
        this.timeout = timeout;
    }

    /**
     * 创建
     *
     * @param interfaceClass 接口类
     * @return {@link T}
     */
    public <T> T createProxy(final Class<T> interfaceClass) {
        return createProxy(interfaceClass, "SNAPSHOT");
    }

    /**
     * 创建
     *
     * @param interfaceClass 接口类
     * @param serviceVersion 服务版本
     * @return {@link T}
     */
    @SuppressWarnings("unchecked")
    public <T> T createProxy(final Class<?> interfaceClass, final String serviceVersion) {
        return (T) Proxy.newProxyInstance(
                interfaceClass.getClassLoader(),
                new Class<?>[]{interfaceClass},
                (proxy, method, args) -> {
                    // 创建 RPC 请求对象并设置请求属性
                    VarysRequest request = new VarysRequest();
                    request.setRequestId(UUID.randomUUID().toString());
                    request.setInterfaceName(method.getDeclaringClass().getName());
                    request.setMethodName(method.getName());
                    request.setParameters(args);
                    request.setServiceVersion(serviceVersion);
                    // 获取 RPC 服务地址
                    if (serviceDiscovery != null) {
                        String serviceName = interfaceClass.getName();
                        if (!StringUtils.isEmpty(serviceVersion)) {
                            serviceName += "-" + serviceVersion;
                        }
                        serviceAddress = serviceDiscovery.discover(serviceName);
                        log.debug("discover service: {} => {}", serviceName, serviceAddress);
                        if (StringUtils.isEmpty(serviceAddress)) {
                            throw new RuntimeException("org.jarvis.server address is empty");
                        }
                    }
                    // 从 RPC 服务地址中解析主机名与端口号
                    String[] array = StringUtils.split(serviceAddress, ":");
                    assert array != null;
                    String host = array[0];
                    int port = Integer.parseInt(array[1]);
                    // 创建 RPC 客户端对象并发送 RPC 请求
                    VarysRpcClient client = new VarysRpcClient(host, port);
                    long time = System.currentTimeMillis();
                    VarysRpcFuture<VarysResponse> future = new VarysRpcFuture<>(new DefaultPromise<>(new DefaultEventLoop()), timeout);
                    VarysHolder.REQUEST_MAP.put(1L, future);
                    client.sendCommand(request);
                    //VarysResponse response = future.getPromise().get(future.getTimeout(), TimeUnit.MILLISECONDS);
                    VarysResponse response = client.getResponse();
                    log.debug("cost time: {} ms", System.currentTimeMillis() - time);
                    if (response == null) {
                        throw new RuntimeException("response is null");
                    }
                    // 返回 RPC 响应结果
                    if (response.getException() != null) {
                        throw response.getException();
                    } else {
                        return response.getResult();
                    }
                });
    }
}
