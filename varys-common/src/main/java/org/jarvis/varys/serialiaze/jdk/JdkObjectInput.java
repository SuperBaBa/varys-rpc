package org.jarvis.varys.serialiaze.jdk;

import org.jarvis.varys.serialiaze.ObjectInput;

import java.io.*;
import java.lang.reflect.Type;

public class JdkObjectInput implements ObjectInput {

    private final ObjectInputStream objectInputStream;

    public JdkObjectInput(InputStream inputStream) throws IOException {
        this.objectInputStream = new ObjectInputStream(inputStream);
    }

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
