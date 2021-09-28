package org.jarvis.varys.serialiaze;

import io.netty.buffer.ByteBuf;

import java.io.*;

/**
 * 序列化
 *
 * @author cqjia
 * @date 2021/09/28
 */
public interface Serialization {
    /**
     * 序列化
     *
     * @param output 输出
     * @return {@link ObjectOutput}
     * @throws IOException ioexception
     */
    ObjectOutput serialize(OutputStream output) throws IOException;

    /**
     * 反序列化
     *
     * @param input 输入
     * @return {@link ObjectInput}
     * @throws IOException ioexception
     */
    ObjectInput deserialize(InputStream input) throws IOException;

    /**
     * 序列化
     *
     * @param byteBufOutput 字节缓冲区输出
     * @return {@link ObjectOutput}
     * @throws IOException ioexception
     */
    ObjectOutput serialize(ByteBuf byteBufOutput) throws IOException;

    /**
     * 反序列化
     *
     * @param byteBufInput 字节缓冲区的输入
     * @return {@link ObjectInput}
     * @throws IOException ioexception
     */
    ObjectInput deserialize(ByteBuf byteBufInput) throws IOException;

}
