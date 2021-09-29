package org.jarvis.varys.loadbalance;

import java.util.List;

public class HashLoadBalance implements ServiceLoadBalancer {
    @Override
    public Object select(List servers, int hashCode) {
        return null;
    }
}
