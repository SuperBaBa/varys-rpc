package org.jarvis.varys.state;


import org.jarvis.varys.circuitbreaker.AbstractCircuitBreaker;

/**
 * 断路器状态
 *
 * @author cqjia
 * @date 2021/09/27
 */
public interface CircuitBreakerState {
    /**
     * 获取当前状态名称
     *
     * @return {@link String}
     */
    String currentStateName();

    /**
     * 检查以及校验当前状态是否需要切换熔断器状态
     *
     * @param circuitBreaker circuitBreaker
     */
    void checkAndSwitchState(AbstractCircuitBreaker circuitBreaker);

    /**
     * 是否通过熔断器检查
     *
     * @param circuitBreaker circuitBreaker
     * @return boolean
     */
    boolean isPassCheck(AbstractCircuitBreaker circuitBreaker);

    /**
     * 统计失败次数
     *
     * @param circuitBreaker circuitBreaker
     */
    void collectFailureCount(AbstractCircuitBreaker circuitBreaker);
}
