package org.jarvis.varys.state;


import org.jarvis.varys.circuitbreaker.AbstractCircuitBreaker;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * 关闭断路器状态
 *
 * @author cqjia
 * @date 2021/09/27
 */
public class CloseCircuitBreakerState implements CircuitBreakerState {
    /**
     * 状态的时间
     */
    private long stateStartTime = System.currentTimeMillis();

    /**
     * 失败次数
     */
    private final AtomicInteger failureCount = new AtomicInteger(0);

    @Override
    public String currentStateName() {
        return this.getClass().getSimpleName();
    }

    @Override
    public void checkAndSwitchState(AbstractCircuitBreaker circuitBreaker) {
        // 获取在失败状态下，触发断路器切换到全开状态的失败次数阈值
        int maxFailureCountAtClose = Integer.parseInt(circuitBreaker.getSwitchOpenThresholdCount().split("/")[0]);
        // 如果失败次数大于阈值则切换到全开状态
        if (failureCount.get() > maxFailureCountAtClose) {
            System.out.println("熔断器从 closeState -> openState");
            circuitBreaker.setState(new FullOpenCircuitBreakerState());
        }
    }

    @Override
    public boolean isPassCheck(AbstractCircuitBreaker circuitBreaker) {
        // 熔断器关闭状态，则直接通过检查
        return true;
    }

    @Override
    public void collectFailureCount(AbstractCircuitBreaker circuitBreaker) {
        // 获取切换阈值的时间段条件，如在600秒内失败10次，则是 10/600
        long period = Long.parseLong(circuitBreaker.getSwitchOpenThresholdCount().split("/")[1]) * 1000;
        long nowTimestamp = System.currentTimeMillis();
        // 若超过一个周期则认为需要重新计算失败次数
        //TODO 若一直是该状态那么一直都是超时的，则失败次数一直是0
        if (stateStartTime + period <= nowTimestamp) {
            failureCount.set(0);
            // 超时后则重新进行一周期的统计
            stateStartTime = nowTimestamp;
        }
        // 失败计数
        failureCount.incrementAndGet();
        // 检查是否需要切换熔断器状态
        checkAndSwitchState(circuitBreaker);
    }
}
