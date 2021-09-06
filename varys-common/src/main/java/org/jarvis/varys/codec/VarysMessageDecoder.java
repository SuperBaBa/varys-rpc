package org.jarvis.varys.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import org.jarvis.varys.dto.VarysResponse;
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
    private final Class<?> genericClass;

    /**
     * 不同信息译码器
     *
     * @param genericClass 泛型类
     */
    public VarysMessageDecoder(Class<?> genericClass) {
        this.genericClass = genericClass;
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
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) {
        if (in.readableBytes() < 4) {
            return;
        }
        // 标记字节流开始位置，读取指针在索引的位置
        in.markReaderIndex();
        // 获取data的字节流长度
        int dataLength = in.readInt();
        if (in.readableBytes() < dataLength) {
            // 重新回到读取指针标记的位置
            in.resetReaderIndex();
            return;
        }
        byte[] data = new byte[dataLength];
        in.readBytes(data);
        //out.add(SerializationUtil.deserialize(data, genericClass));
        out.add(deSerializeByJDK(data));
    }

    public Object deSerializeByJDK(byte[] data) {
        // 使用JDK反序列化
        try {
            ByteArrayInputStream bais = new ByteArrayInputStream(data);
            ObjectInputStream ois = new ObjectInputStream(bais);
            return ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public <T> T deSerializeByProtobuf(byte[] data, Class<T> genericClass) {
        return SerializationUtil.deserialize(data, genericClass);
    }

}
