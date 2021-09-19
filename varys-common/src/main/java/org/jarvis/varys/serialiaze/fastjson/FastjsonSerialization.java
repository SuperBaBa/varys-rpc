package org.jarvis.varys.serialiaze.fastjson;

import io.netty.buffer.ByteBuf;
import org.jarvis.varys.serialiaze.ObjectInput;
import org.jarvis.varys.serialiaze.ObjectOutput;

import org.jarvis.varys.serialiaze.Serialization;

import java.io.*;

/**
 * 快速的json序列化
 *
 * @author cqjia
 * @date 2021/09/09
 */
public class FastjsonSerialization implements Serialization {
    /**
     * 序列化
     *
     * @param output 输出
     * @return {@link ObjectOutput}
     * @throws IOException ioexception
     */
    @Override
    public ObjectOutput serialize(OutputStream output) throws IOException {
        return new FastjsonObjectOutput(output);
    }

    public ObjectOutput serialize(ByteBuf byteBuf) throws IOException {
        return new FastjsonObjectOutput(byteBuf);
    }

    /**
     * 反序列化
     *
     * @param input 输入
     * @return {@link ObjectInput}
     * @throws IOException ioexception
     */
    @Override
    public ObjectInput deserialize(InputStream input) throws IOException {
        return new FastjsonObjectInput(input);
    }

    public ObjectInput deserialize(ByteBuf byteBuf) throws IOException {
        return new FastjsonObjectInput(byteBuf);
    }
}
