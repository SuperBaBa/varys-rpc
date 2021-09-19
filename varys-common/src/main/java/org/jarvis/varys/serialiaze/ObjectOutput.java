package org.jarvis.varys.serialiaze;

import java.io.IOException;
import java.lang.reflect.Type;

public interface ObjectOutput {
    /**
     * Write Object
     *
     * @param v value.
     * @throws IOException
     */
    void writeObject(Object v) throws IOException;

    /**
     * Write Object By ByteBuf
     *
     * @param v value.
     * @throws IOException
     */
    void writeObjectByByteBuf(Object v) throws IOException;

    /**
     * Write boolean.
     *
     * @param v value.
     * @throws IOException
     */
    void writeBool(boolean v) throws IOException;

    /**
     * Write byte.
     *
     * @param v value.
     * @throws IOException
     */
    void writeByte(byte v) throws IOException;

    /**
     * Write short.
     *
     * @param v value.
     * @throws IOException
     */
    void writeShort(short v) throws IOException;

    /**
     * Write integer.
     *
     * @param v value.
     * @throws IOException
     */
    void writeInt(int v) throws IOException;

    /**
     * Write long.
     *
     * @param v value.
     * @throws IOException
     */
    void writeLong(long v) throws IOException;

    /**
     * Write float.
     *
     * @param v value.
     * @throws IOException
     */
    void writeFloat(float v) throws IOException;

    /**
     * Write double.
     *
     * @param v value.
     * @throws IOException
     */
    void writeDouble(double v) throws IOException;

    /**
     * Write string.
     *
     * @param v value.
     * @throws IOException
     */
    void writeUTF(String v) throws IOException;

    /**
     * Write byte array.
     *
     * @param v value.
     * @throws IOException
     */
    void writeBytes(byte[] v) throws IOException;

    /**
     * Write byte array.
     *
     * @param v   value.
     * @param off the start offset in the data.
     * @param len the number of bytes that are written.
     * @throws IOException
     */
    void writeBytes(byte[] v, int off, int len) throws IOException;

    /**
     * Flush buffer.
     *
     * @throws IOException
     */
    void flushBuffer() throws IOException;
}
