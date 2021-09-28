package org.jarvis.varys.serialiaze.protostuff;

import org.jarvis.varys.serialiaze.ObjectInput;

import java.io.IOException;
import java.lang.reflect.Type;

/**
 * protostuff对象输入
 *
 * @author cqjia
 * @date 2021/09/28
 */
public class ProtostuffObjectInput implements ObjectInput {

    @Override
    public Object readObjectByByteBuf() throws IOException {
        return null;
    }

    @Override
    public <T> T readObject(Class<T> cls) throws IOException {
        return null;
    }

    @Override
    public <T> T readObject(Class<T> cls, Type type) throws IOException {
        return null;
    }

    @Override
    public <T> T readObjectByByteBuf(Class<T> cls, Type type) throws IOException {
        return null;
    }

    @Override
    public boolean readBool() throws IOException {
        return false;
    }

    @Override
    public byte readByte() throws IOException {
        return 0;
    }

    @Override
    public short readShort() throws IOException {
        return 0;
    }

    @Override
    public int readInt() throws IOException {
        return 0;
    }

    @Override
    public long readLong() throws IOException {
        return 0;
    }

    @Override
    public float readFloat() throws IOException {
        return 0;
    }

    @Override
    public double readDouble() throws IOException {
        return 0;
    }

    @Override
    public String readUTF() throws IOException {
        return null;
    }

    @Override
    public byte[] readBytes() throws IOException {
        return new byte[0];
    }
}
