package org.jarvis.varys.state;

public enum State {
    CLOSE(new CloseCircuitBreakerState(), "关闭状态"),
    HALF(new HalfOpenCircuitBreakerState(), "半开状态"),
    FULL(new FullOpenCircuitBreakerState(), "全开状态");

    private final CircuitBreakerState circuitBreakerState;
    private final String stateName;

    State(CircuitBreakerState circuitBreakerState, String stateName) {
        this.circuitBreakerState = circuitBreakerState;
        this.stateName = stateName;
    }

    public CircuitBreakerState getCircuitBreakerState() {
        return circuitBreakerState;
    }

    public String getStateName() {
        return stateName;
    }
}
