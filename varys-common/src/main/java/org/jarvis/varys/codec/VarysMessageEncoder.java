package org.jarvis.varys.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.jarvis.varys.dto.VarysRequest;
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
     * 泛型类
     */
    private final Class<?> genericClass;

    /**
     * 不同消息编码器
     *
     * @param genericClass 泛型类
     */
    public VarysMessageEncoder(Class<?> genericClass, boolean preferDirect) {
        super(preferDirect);
        this.genericClass = genericClass;
    }

    public VarysMessageEncoder(Class<?> genericClass) {
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
    public void encode(ChannelHandlerContext ctx, Object in, ByteBuf out) {
        if (genericClass.isInstance(in)) {
            // 将对象序列化为字节数组
            //byte[] bytes = SerializationUtil.serialize(in);
            byte[] bytes = serializeByJDK(in);
            out.writeInt(bytes.length);
            out.writeBytes(bytes);
        }
    }

    public byte[] serializeByJDK(Object obj) {
        ByteArrayOutputStream baos = null;
        try {
            baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(obj);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return baos.toByteArray();
    }

    public byte[] serializeByProtobuf(Object obj) {
        return SerializationUtil.serialize(obj);
    }
}
