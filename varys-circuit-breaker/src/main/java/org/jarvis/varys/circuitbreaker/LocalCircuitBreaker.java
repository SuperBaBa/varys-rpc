package org.jarvis.varys.circuitbreaker;


import org.jarvis.varys.state.CircuitBreakerState;
import org.jarvis.varys.state.State;


public class LocalCircuitBreaker extends AbstractCircuitBreaker implements CircuitBreaker {

    final byte[] b = new byte[0];

    LocalCircuitBreaker INSTANCE;

    @Override
    public void reset() {
        getStateAtomicReference().set(State.CLOSE);
    }

    @Override
    public boolean isPassCheck() {
        return getStateAtomicReference().get().getCircuitBreakerState().isPassCheck(this);
    }

    @Override
    public void countFailNum() {
        synchronized (b) {
            CircuitBreakerState state = getStateAtomicReference().get().getCircuitBreakerState();
            state.collectFailureCount(this);
        }
    }

    private static class LocalCircuitBreakerSingle {
        private static final LocalCircuitBreaker staticInnerClass = new LocalCircuitBreaker();
    }

    public static LocalCircuitBreaker getInstance() {
        return LocalCircuitBreakerSingle.staticInnerClass;
    }
}
