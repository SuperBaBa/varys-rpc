package org.jarvis.varys.serialiaze.jdk;

import io.netty.buffer.ByteBuf;
import org.jarvis.varys.serialiaze.ObjectInput;

import java.io.*;
import java.lang.reflect.Type;

public class JdkObjectInput implements ObjectInput {

    private ObjectInputStream objectInputStream;

    private ByteBuf byteBufInput;

    public JdkObjectInput(InputStream inputStream) throws IOException {
        this.objectInputStream = new ObjectInputStream(inputStream);
    }

    public JdkObjectInput(ByteBuf byteBufInput) throws IOException {
        this.byteBufInput = byteBufInput;
    }

    @Override
    public Object readObjectByByteBuf() throws IOException {
        byte[] dataBytes = new byte[byteBufInput.readableBytes()];
        byteBufInput.readBytes(dataBytes);
        // 使用JDK反序列化
        try {
            ByteArrayInputStream bais = new ByteArrayInputStream(dataBytes);
            ObjectInputStream ois = new ObjectInputStream(bais);
            return ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
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
        byte[] dataBytes = new byte[byteBufInput.readableBytes()];
        byteBufInput.readBytes(dataBytes);
        // 使用JDK反序列化
        try {
            ByteArrayInputStream bais = new ByteArrayInputStream(dataBytes);
            ObjectInputStream ois = new ObjectInputStream(bais);
            return (T) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
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
