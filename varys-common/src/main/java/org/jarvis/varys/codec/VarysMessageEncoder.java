package org.jarvis.varys.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.jarvis.varys.dto.VarysRequest;
import org.jarvis.varys.serialiaze.Serialization;
import org.jarvis.varys.serialiaze.fastjson.FastjsonObjectInput;
import org.jarvis.varys.serialiaze.fastjson.FastjsonSerialization;
import org.jarvis.varys.serialiaze.jdk.JdkSerialization;
import org.jarvis.varys.serialiaze.protostuff.ProtostuffSerialization;
import org.jarvis.varys.util.SerializationUtil;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * 不同消息编码器
 *
 * @author marcus
 * @date 2021/8/22-13:02
 */
public class VarysMessageEncoder extends MessageToByteEncoder {
    /**
     * 序列化类型
     */
    private String serializationType = "JDK";
    /**
     * 泛型类
     */
    private Class<?> genericClass;

    public VarysMessageEncoder(Class<?> genericClass) {
        this.genericClass = genericClass;
    }
    public VarysMessageEncoder(Class<?> genericClass,String serializationType) {
        this.genericClass = genericClass;
        this.serializationType = serializationType;
    }

    /**
     * 不同消息编码器
     *
     * @param genericClass 泛型类
     */
    public VarysMessageEncoder(Class<?> genericClass, boolean preferDirect) {
        super(preferDirect);
        this.genericClass = genericClass;
    }

    /**
     * 编码
     *
     * @param ctx ctx
     * @param in  在
     * @param out 出
     * @throws Exception 异常
     */
    @Override
    public void encode(ChannelHandlerContext ctx, Object in, ByteBuf out) throws IOException {
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
        serialization.serialize(out).writeObjectByByteBuf(in);
    }
}
