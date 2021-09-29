package org.jarvis.varys.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import org.jarvis.varys.dto.VarysResponse;
import org.jarvis.varys.serialiaze.Serialization;
import org.jarvis.varys.serialiaze.fastjson.FastjsonSerialization;
import org.jarvis.varys.serialiaze.jdk.JdkSerialization;
import org.jarvis.varys.serialiaze.protostuff.ProtostuffSerialization;
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
     * 序列化类型
     */
    private String serializationType = "JDK";
    /**
     * 泛型类
     */
    private final Class<?> genericClass;

    /**
     * 不同信息译码器
     *
     * @param genericClass 泛型类
     */
    public VarysMessageDecoder(Class<?> genericClass) {
        this.genericClass = genericClass;
    }

    public VarysMessageDecoder(Class<?> genericClass, String serializationType) {
        this.genericClass = genericClass;
        this.serializationType = serializationType;
    }

    /**
     * 解码器对字节码反序列化
     *
     * @param ctx ctx
     * @param in  在
     * @param out 出
     * @throws Exception 异常
     */
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws IOException {
        Serialization serialization;
        switch (serializationType) {
            case "FASTJSON":
            case "fastjson":
                serialization = new FastjsonSerialization();
                break;
            case "PROTOSTUFF":
            case "protostuff":
                serialization = new ProtostuffSerialization();
                break;
            default:
                serialization = new JdkSerialization();
        }
        Object obj = serialization.deserialize(in).readObjectByByteBuf(genericClass);
        out.add(obj);
    }

}
