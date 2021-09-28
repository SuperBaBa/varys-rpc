package org.jarvis.varys.dto;

import io.netty.util.concurrent.Promise;

public class VarysRpcFuture<T> {
    private final Promise<T> promise;
    private final long timeout;

    public VarysRpcFuture(Promise<T> promise, long timeout) {
        this.promise = promise;
        this.timeout = timeout;
    }

    public Promise<T> getPromise() {
        return promise;
    }

    public long getTimeout() {
        return timeout;
    }
}
