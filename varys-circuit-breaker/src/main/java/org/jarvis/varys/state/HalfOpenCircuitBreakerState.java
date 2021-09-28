package org.jarvis.varys.state;


import org.jarvis.varys.circuitbreaker.AbstractCircuitBreaker;

import java.util.concurrent.atomic.AtomicInteger;

public class HalfOpenCircuitBreakerState implements CircuitBreakerState {
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
        if (stateStartTimestamp + idleTime <= nowStamp) {
            if (failureCount.get() > circuitBreaker.getReopenThresholdCountAtHalf()) {
                System.out.println("熔断器从 halfState -> openState");
                circuitBreaker.setState(new FullOpenCircuitBreakerState());
            } else {
                System.out.println("熔断器从 halfState -> closeState");
                circuitBreaker.setState(new CloseCircuitBreakerState());
            }
        }
    }

    @Override
    public boolean isPassCheck(AbstractCircuitBreaker circuitBreaker) {
        // 检查是否切换状态
        checkAndSwitchState(circuitBreaker);
        // 如果还在一个半开状态的周期内时，判断失败次数是否超过可重试请求次数阈值，超过阈值则不允许请求通过
        int maxPassCount = Integer.parseInt(circuitBreaker.getSwitchOpenThresholdCount().split("/")[0]);
        if (retryCount.get() < maxPassCount) {
            return true;
        }
        return false;
    }

    @Override
    public void collectFailureCount(AbstractCircuitBreaker circuitBreaker) {
        failureCount.incrementAndGet();
        checkAndSwitchState(circuitBreaker);
    }

    public void doSomething(DoSomething doSomething) {
        doSomething.apply();
    }
}
