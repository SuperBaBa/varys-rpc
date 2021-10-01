package org.jarvis.varys.state;


import org.jarvis.varys.circuitbreaker.AbstractCircuitBreaker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicInteger;

public class HalfOpenCircuitBreakerState implements CircuitBreakerState {

    private static final Logger log = LoggerFactory.getLogger(HalfOpenCircuitBreakerState.class);

    /**
     * 状态初始化时间戳
     */
    private final long stateStartTimestamp = System.currentTimeMillis();

    /**
     * 半开状态，失败计数器
     */
    private final AtomicInteger failureCount = new AtomicInteger(0);

    /**
     * 半开状态，尝试请求阈值
     */
    private final AtomicInteger retryCount = new AtomicInteger(0);

    @Override
    public String currentStateName() {
        return this.getClass().getSimpleName();
    }

    @Override
    public void checkAndSwitchState(AbstractCircuitBreaker circuitBreaker) {
        // 判断半开时间是否结束
        long idleTime = Long.parseLong(circuitBreaker.getRetryThresholdAtHalf().split("/")[1]) * 1000L;
        long nowStamp = System.currentTimeMillis();
        // 超过半开状态周期时，判断是否回到全开状态，还是切换到关闭状态
        if (stateStartTimestamp + idleTime < nowStamp) {
            if (failureCount.get() > circuitBreaker.getReopenThresholdCountAtHalf()) {
                log.warn("熔断器从 halfState -> fullState");
                circuitBreaker.getStateAtomicReference().compareAndSet(State.HALF,State.FULL);
            } else {
                log.warn("熔断器从 halfState -> closeState");
                circuitBreaker.getStateAtomicReference().compareAndSet(State.HALF,State.CLOSE);

            }
            retryCount.set(0);
        }
        retryCount.incrementAndGet();
    }

    @Override
    public boolean isPassCheck(AbstractCircuitBreaker circuitBreaker) {
        // 检查是否切换状态
        checkAndSwitchState(circuitBreaker);
        // 如果还在一个半开状态的周期内时，判断失败次数是否超过可重试请求次数阈值，超过阈值则不允许请求通过
        int maxPassCount = Integer.parseInt(circuitBreaker.getSwitchOpenThresholdCount().split("/")[0]);
        // 如果半开状态没有超过是最大请求，那么可以通过检查，继续试探请求
        return retryCount.get() < maxPassCount;
    }

    @Override
    public void collectFailureCount(AbstractCircuitBreaker circuitBreaker) {
        // 半开状态也是需要统计失败次数的
        failureCount.incrementAndGet();
        checkAndSwitchState(circuitBreaker);
    }

    public void doSomething(DoSomething doSomething) {
        doSomething.apply();
    }
}
