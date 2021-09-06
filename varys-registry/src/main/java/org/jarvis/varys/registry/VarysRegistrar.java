package org.jarvis.varys.registry;

/**
 * 不同的注册
 *
 * @author marcus
 * @date 2021/08/22
 */
public interface VarysRegistrar {
    /**
     * 注册表
     *
     * @param interfaceName  接口名称
     * @param serviceAddress 服务地址
     */
    void registry(String interfaceName, String serviceAddress);
}
