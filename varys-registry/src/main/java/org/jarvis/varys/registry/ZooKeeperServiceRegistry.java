package org.jarvis.varys.registry;

import org.I0Itec.zkclient.ZkClient;
import org.jarvis.varys.Constant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * zookeeper服务注册中心
 *
 * @author marcus
 * @date 2021/08/22
 */
public class ZooKeeperServiceRegistry implements VarysRegistrar {

    /**
     * 日志记录器
     */
    private static final Logger log = LoggerFactory.getLogger(ZooKeeperServiceRegistry.class);

    /**
     * zk的客户端
     */
    private final ZkClient zkClient;

    /**
     * zookeeper服务注册中心
     *
     * @param zkAddress zk地址
     */
    public ZooKeeperServiceRegistry(String zkAddress) {
        // 创建 ZooKeeper 客户端
        this.zkClient = new ZkClient(zkAddress, Constant.ZK_SESSION_TIMEOUT, Constant.ZK_CONNECTION_TIMEOUT);
        log.debug("connect zookeeper");
    }

    public ZooKeeperServiceRegistry(ZkClient zkClient) {
        // ZooKeeper 客户端设置为全局变量
        this.zkClient = zkClient;
        log.debug("connect zookeeper");
    }

    /**
     * 注册服务
     *
     * @param serviceName    服务名称
     * @param serviceAddress 服务地址
     */
    @Override
    public void registry(String serviceName, String serviceAddress) {
        String registryPath = Constant.ZK_REGISTRY_PATH;
        // 创建 registry 节点（持久）
        if (!zkClient.exists(registryPath)) {
            zkClient.createPersistent(registryPath, true);
            log.debug("create registry node: {}", registryPath);
        }
        // 创建 service 节点（持久）
        String servicePath = registryPath + "/" + serviceName;
        if (!zkClient.exists(servicePath)) {
            zkClient.createPersistent(servicePath);
            log.debug("create service node: {}", servicePath);
        }
        // 创建 address 节点（临时）
        String addressPath = servicePath + "/address-";
        // 创建 address 节点（持久）
        String addressNode = zkClient.createPersistentSequential(addressPath, serviceAddress);
        //String addressNode = zkClient.createEphemeralSequential(addressPath, serviceAddress);
        log.debug("create address node: {}", addressNode);
    }

    @Override
    public void removeAllService() {
        zkClient.delete(Constant.ZK_REGISTRY_PATH);
    }
}