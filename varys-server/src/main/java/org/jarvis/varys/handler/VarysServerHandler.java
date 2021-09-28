package org.jarvis.varys.handler;

import com.alibaba.fastjson.JSONObject;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.SimpleChannelInboundHandler;
import net.sf.cglib.reflect.FastClass;
import net.sf.cglib.reflect.FastMethod;
import org.jarvis.varys.dto.VarysRequest;
import org.jarvis.varys.dto.VarysResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;

/**
 * @author Marcus
 * @date 2021/2/16-14:10
 */
public class VarysServerHandler extends ChannelInboundHandlerAdapter {
    private final Logger log = LoggerFactory.getLogger(VarysServerHandler.class);
    /**
     * 存放 服务名 与 服务对象 之间的映射关系
     */
    private final Map<String, Object> handlerMap;

    public VarysServerHandler(final Map<String, Object> handlerMap) {
        this.handlerMap = handlerMap;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("request established link");
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {

    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
    }


    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        VarysRequest request = (VarysRequest) msg;
        // 创建并初始化 RPC 响应对象
        VarysResponse response = new VarysResponse();
        response.setRequestId(request.getRequestId());
        try {
            // 反射调用服务方的方法，并响应返回结果
            Object result = handle(request);
            response.setResult(result);
        } catch (InvocationTargetException e) {
            log.error("Invoke target method handle result failure", e);
            response.setException(e);
        }
        // 写入 RPC 响应对象并自动关闭连接
        ctx.writeAndFlush(response);
    }

    private Object handle(VarysRequest request) throws InvocationTargetException {
        // 获取服务对象
        String serviceName = request.getInterfaceName();
        String serviceVersion = request.getServiceVersion();
        if (!StringUtils.isEmpty(serviceVersion)) {
            serviceName += "-" + serviceVersion;
        }
        Object serviceBean = handlerMap.get(serviceName);
        if (serviceBean == null) {
            throw new RuntimeException(String.format("can not find service bean by key: %s", serviceName));
        }
        // 获取反射调用所需的参数
        Class<?> serviceClass = serviceBean.getClass();
        String methodName = request.getMethodName();
        Class<?>[] parameterTypes = request.getParameterTypes();
        Object[] parameters = request.getParameters();
        // 执行反射调用
        //Method method = serviceClass.getMethod(methodName, parameterTypes);
        //method.setAccessible(true);
        //return method.invoke(serviceBean, parameters);
        // 使用 CGLib 执行反射调用
        FastClass serviceFastClass = FastClass.create(serviceClass);
        FastMethod serviceFastMethod = serviceFastClass.getMethod(methodName, parameterTypes);
        return serviceFastMethod.invoke(serviceBean, parameters);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        log.error("org.jarvis.server caught exception", cause);
        ctx.close();
    }
}
