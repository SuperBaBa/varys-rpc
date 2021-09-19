package org.jarvis.varys.serialiaze;

import java.io.IOException;
import java.lang.reflect.Type;

/**
 * 对象的输入
 *
 * @author marcus
 * @date 2021/09/09
 */
public interface ObjectInput {

    Object readObjectByByteBuf() throws IOException;

    /**
     * 阅读对象
     *
     * @param cls cls
     * @return {@link T}
     * @throws IOException ioexception
     */
    <T> T readObject(Class<T> cls) throws IOException;

    /**
     * 阅读对象
     *
     * @param cls  cls
     * @param type 类型
     * @return {@link T}
     * @throws IOException ioexception
     */
    <T> T readObject(Class<T> cls, Type type) throws IOException;

    /**
     * 阅读对象的字节缓冲区
     *
     * @param cls  cls
     * @param type 类型
     * @return {@link T}
     * @throws IOException ioexception
     */
    <T> T readObjectByByteBuf(Class<T> cls, Type type) throws IOException;

    /**
     * 读bool
     * Read boolean.
     *
     * @return boolean.
     * @throws IOException ioexception
     */
    boolean readBool() throws IOException;

    /**
     * 读取字节
     * Read byte.
     *
     * @return byte value.
     * @throws IOException ioexception
     */
    byte readByte() throws IOException;

    /**
     * 读短
     * Read short integer.
     *
     * @return short.
     * @throws IOException ioexception
     */
    short readShort() throws IOException;

    /**
     * 读int
     * Read integer.
     *
     * @return integer.
     * @throws IOException ioexception
     */
    int readInt() throws IOException;

    /**
     * 读长
     * Read long.
     *
     * @return long.
     * @throws IOException ioexception
     */
    long readLong() throws IOException;

    /**
     * 读浮动
     * Read float.
     *
     * @return float.
     * @throws IOException ioexception
     */
    float readFloat() throws IOException;

    /**
     * 读双
     * Read double.
     *
     * @return double.
     * @throws IOException ioexception
     */
    double readDouble() throws IOException;

    /**
     * 读utf
     * Read UTF-8 string.
     *
     * @return string.
     * @throws IOException ioexception
     */
    String readUTF() throws IOException;

    /**
     * 读取字节
     * Read byte array.
     *
     * @return byte array.
     * @throws IOException ioexception
     */
    byte[] readBytes() throws IOException;
}
