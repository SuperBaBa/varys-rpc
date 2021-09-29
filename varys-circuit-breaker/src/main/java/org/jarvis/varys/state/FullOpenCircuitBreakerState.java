package org.jarvis.varys.state;


import org.jarvis.varys.circuitbreaker.AbstractCircuitBreaker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicInteger;

public class FullOpenCircuitBreakerState implements CircuitBreakerState {

    private static final Logger log = LoggerFactory.getLogger(FullOpenCircuitBreakerState.class);

    /**
     * 进入当前状态的初始化时间
     */
    private final long stateStartTime = System.currentTimeMillis();


    /**
     * 半开状态，失败计数器
     */
    private final AtomicInteger failNum = new AtomicInteger(0);


    @Override
    public String currentStateName() {
        return this.getClass().getSimpleName();
    }

    @Override
    public void checkAndSwitchState(AbstractCircuitBreaker circuitBreaker) {
        // 获取熔断器全开状态持续时长
        long openStateKeepTime = circuitBreaker.getSwitchHalfThresholdTimeAtOpen() * 1000L;
        long nowTimestamp = System.currentTimeMillis();
        // 如果熔断器全开状态持续时间超过设定时长，则切换到半开状态进行少量流量的验证
        if (stateStartTime + openStateKeepTime < nowTimestamp) {
            log.info("超过熔断器全开状态时间从 openState -> halfState");
            System.out.println("超过熔断器全开状态时间从 openState -> halfState");
            circuitBreaker.setState(new HalfOpenCircuitBreakerState());
        }
    }

    @Override
    public boolean isPassCheck(AbstractCircuitBreaker circuitBreaker) {
        // 检查是否需要切换状态，全开状态主要由状态持续时间控制
        checkAndSwitchState(circuitBreaker);
        // 全开状态不放过任何一个请求
        return false;
    }

    @Override
    public void collectFailureCount(AbstractCircuitBreaker circuitBreaker) {
        //do nothing
        /*failNum.incrementAndGet();
        checkAndSwitchState(circuitBreaker);*/
    }
}
