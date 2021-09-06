package org.jarvis.varys.client;

import org.apache.commons.lang3.StringUtils;
import org.jarvis.varys.discovery.VarysServiceDiscovery;
import org.jarvis.varys.dto.VarysRequest;
import org.jarvis.varys.dto.VarysResponse;
import org.jarvis.varys.registry.VarysRegistrar;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Proxy;
import java.util.UUID;

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

    /**
     * 不同的rpc代理
     *
     * @param serviceDiscovery 服务发现
     */
    public VarysRpcProxy(VarysServiceDiscovery serviceDiscovery) {
        this.serviceDiscovery = serviceDiscovery;
    }

    /**
     * 创建
     *
     * @param interfaceClass 接口类
     * @return {@link T}
     */
    public <T> T createProxy(final Class<?> interfaceClass){
        return  (T) createProxy(interfaceClass,"SNAPSHOT");
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
                    VarysResponse response = client.sendCommand(request);
                    log.debug("time: {}ms", System.currentTimeMillis() - time);
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
