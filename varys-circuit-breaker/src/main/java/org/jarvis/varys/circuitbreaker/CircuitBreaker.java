package org.jarvis.varys.circuitbreaker;

public interface CircuitBreaker {
    /**
     * 重置熔断器
     */
    void reset();

    /**
     * 是否允许通过熔断器
     */
    boolean isPassCheck();

    /**
     * 统计失败次数
     */
    void countFailNum();
}
