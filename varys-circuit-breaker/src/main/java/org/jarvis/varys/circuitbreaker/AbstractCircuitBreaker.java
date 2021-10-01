package org.jarvis.varys.circuitbreaker;

import org.jarvis.varys.state.CircuitBreakerState;
import org.jarvis.varys.state.CloseCircuitBreakerState;
import org.jarvis.varys.state.State;

import java.util.concurrent.atomic.AtomicReference;

public abstract class AbstractCircuitBreaker {
    /**
     * 熔断器当前状态，默认状态时关闭状态
     */
    private final AtomicReference<State> stateAtomicReference = new AtomicReference<>(State.CLOSE);

    /**
     * 在熔断器关闭的情况下，在多少秒内失败多少次进入，熔断打开状态（默认10分钟内，失败10次进入打开状态）
     */
    private String switchOpenThresholdCount = "10/600";

    /**
     * 在熔断器打开的情况下，熔断多少秒切换到半开状态，（默认熔断30分钟）
     */
    private int switchHalfThresholdTimeAtOpen = 1800;

    /**
     * 在熔断器半开的情况下, 在多少秒内放多少次请求，去试探(默认10分钟内，放10次请求)
     */
    private String retryThresholdAtHalf = "10/600";

    /**
     * 在熔断器半开的情况下, 试探期间，如果有超过多少次失败的，重新进入熔断打开状态，否者进入熔断关闭状态。
     */
    private int reopenThresholdCountAtHalf = 1;

    public AbstractCircuitBreaker() {
    }

    public AtomicReference<State> getStateAtomicReference() {
        return stateAtomicReference;
    }


    public String getSwitchOpenThresholdCount() {
        return switchOpenThresholdCount;
    }

    public void setSwitchOpenThresholdCount(String switchOpenThresholdCount) {
        this.switchOpenThresholdCount = switchOpenThresholdCount;
    }

    public int getSwitchHalfThresholdTimeAtOpen() {
        return switchHalfThresholdTimeAtOpen;
    }

    public void setSwitchHalfThresholdTimeAtOpen(int switchHalfThresholdTimeAtOpen) {
        this.switchHalfThresholdTimeAtOpen = switchHalfThresholdTimeAtOpen;
    }

    public String getRetryThresholdAtHalf() {
        return retryThresholdAtHalf;
    }

    public void setRetryThresholdAtHalf(String retryThresholdAtHalf) {
        this.retryThresholdAtHalf = retryThresholdAtHalf;
    }

    public int getReopenThresholdCountAtHalf() {
        return reopenThresholdCountAtHalf;
    }

    public void setReopenThresholdCountAtHalf(int reopenThresholdCountAtHalf) {
        this.reopenThresholdCountAtHalf = reopenThresholdCountAtHalf;
    }

}
