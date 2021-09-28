package org.jarvis.varys.serialiaze.jdk;

import io.netty.buffer.ByteBuf;
import org.jarvis.varys.serialiaze.ObjectOutput;

import java.io.*;

public class JdkObjectOutput implements ObjectOutput {

    private ObjectOutputStream outputStream;
    private ByteBuf byteBufOutput;

    public JdkObjectOutput(OutputStream outputStream) throws IOException {
        this.outputStream = new ObjectOutputStream(outputStream);
    }

    public JdkObjectOutput(ByteBuf byteBufOutput) throws IOException {
        this.byteBufOutput = byteBufOutput;
    }

    @Override
    public void writeObject(Object obj) throws IOException {
        if (obj == null) {
            getObjectOutputStream().writeByte(0);
        } else {
            getObjectOutputStream().writeByte(1);
            getObjectOutputStream().writeObject(obj);
        }
    }

    @Override
    public void writeObjectByByteBuf(Object v) throws IOException {
        ByteArrayOutputStream baos = null;
        try {
            baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(v);
        } catch (IOException e) {
            e.printStackTrace();
        }
        byte[] bytes = baos.toByteArray();
        byteBufOutput.writeBytes(bytes);
    }

    @Override
    public void writeBool(boolean v) throws IOException {
        outputStream.writeBoolean(v);
    }

    @Override
    public void writeByte(byte v) throws IOException {
        outputStream.writeByte(v);
    }

    @Override
    public void writeShort(short v) throws IOException {
        outputStream.writeShort(v);
    }

    @Override
    public void writeInt(int v) throws IOException {
        outputStream.writeInt(v);
    }

    @Override
    public void writeLong(long v) throws IOException {
        outputStream.writeLong(v);
    }

    @Override
    public void writeFloat(float v) throws IOException {
        outputStream.writeFloat(v);
    }

    @Override
    public void writeDouble(double v) throws IOException {
        outputStream.writeDouble(v);
    }

    @Override
    public void writeUTF(String v) throws IOException {
        outputStream.writeUTF(v);
    }

    @Override
    public void writeBytes(byte[] v) throws IOException {
        outputStream.writeBytes(new String(v));
    }

    @Override
    public void writeBytes(byte[] v, int off, int len) throws IOException {
        if (v == null) {
            outputStream.writeInt(-1);
        } else {
            outputStream.writeInt(len);
            outputStream.write(v, off, len);
        }
    }

    @Override
    public void flushBuffer() throws IOException {
        getObjectOutputStream().flush();
    }

    public ObjectOutputStream getObjectOutputStream() {
        return outputStream;
    }
}
