package org.jarvis.varys.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import org.jarvis.varys.dto.VarysResponse;
import org.jarvis.varys.serialiaze.fastjson.FastjsonSerialization;
import org.jarvis.varys.serialiaze.jdk.JdkSerialization;
import org.jarvis.varys.util.SerializationUtil;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.List;

/**
 * 不同信息译码器
 *
 * @author marcus
 * @date 2021/8/22-12:40
 */
public class VarysMessageDecoder extends ByteToMessageDecoder {
    /**
     * 泛型类
     */
    private Class<?> genericClass;

    /**
     * 不同信息译码器
     *
     * @param genericClass 泛型类
     */
    public VarysMessageDecoder(Class<?> genericClass) {
        this.genericClass = genericClass;
    }

    public VarysMessageDecoder() {
    }

    /**
     * 解码
     *
     * @param ctx ctx
     * @param in  在
     * @param out 出
     * @throws Exception 异常
     */
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws IOException {
        //FastjsonSerialization fastjsonSerialization = new FastjsonSerialization();
        //Object obj = fastjsonSerialization.deserialize(in).readObjectByByteBuf();
        JdkSerialization jdkSerialization=new JdkSerialization();
        Object obj = jdkSerialization.deserialize(in).readObjectByByteBuf();
        out.add(obj);
    }

}
