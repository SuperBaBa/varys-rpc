package org.jarvis.varys.discovery;

public interface VarysServiceDiscovery {
    String discover(String name);

    default void balanceStrategy() {
    }
}
