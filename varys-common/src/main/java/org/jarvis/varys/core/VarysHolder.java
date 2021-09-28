package org.jarvis.varys.core;

import org.jarvis.varys.dto.VarysRpcFuture;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class VarysHolder {
    public final static AtomicLong REQUEST_ID_GEN = new AtomicLong(0);

    public static final Map<Long, VarysRpcFuture> REQUEST_MAP = new ConcurrentHashMap<>();
}
