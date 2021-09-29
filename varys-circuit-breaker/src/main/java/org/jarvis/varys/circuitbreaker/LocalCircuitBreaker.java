package org.jarvis.varys.circuitbreaker;


import org.jarvis.varys.state.CloseCircuitBreakerState;

public class LocalCircuitBreaker extends AbstractCircuitBreaker implements CircuitBreaker {
    private LocalCircuitBreaker() {
        super();
    }

    private LocalCircuitBreaker(String failRateForClose, int idleTimeForOpen, String passRateForHalfOpen, int failNumForHalfOpen) {
        setSwitchHalfThresholdTimeAtOpen(idleTimeForOpen);
        setReopenThresholdCountAtHalf(failNumForHalfOpen);
        setRetryThresholdAtHalf(passRateForHalfOpen);
        setSwitchOpenThresholdCount(failRateForClose);
    }

    LocalCircuitBreaker INSTANCE;

    @Override
    public void reset() {
        this.setState(new CloseCircuitBreakerState());
    }

    @Override
    public boolean isPassCheck() {
        return getState().isPassCheck(this);
    }

    @Override
    public void countFailNum() {
        getState().collectFailureCount(this);
    }

    private static class LocalCircuitBreakerSingle {
        private static LocalCircuitBreaker staticInnerClass = new LocalCircuitBreaker();
    }

    public static LocalCircuitBreaker getInstance() {
        return LocalCircuitBreakerSingle.staticInnerClass;
    }
}
