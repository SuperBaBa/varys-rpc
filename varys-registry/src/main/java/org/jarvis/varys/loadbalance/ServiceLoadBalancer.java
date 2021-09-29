package org.jarvis.varys.loadbalance;

import java.util.List;

public interface ServiceLoadBalancer<T> {
    T select(List<T> servers, int hashCode);

}
