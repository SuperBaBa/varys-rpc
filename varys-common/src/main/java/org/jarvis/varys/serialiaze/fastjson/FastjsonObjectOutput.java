package org.jarvis.varys.serialiaze.fastjson;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.JSONSerializer;
import com.alibaba.fastjson.serializer.SerializeWriter;
import com.alibaba.fastjson.serializer.SerializerFeature;
import io.netty.buffer.ByteBuf;
import org.jarvis.varys.serialiaze.ObjectOutput;

import java.io.*;

public class FastjsonObjectOutput implements ObjectOutput {

    private PrintWriter writer;

    private ByteBuf byteBuf;

    public FastjsonObjectOutput(OutputStream writer) {
        this(new OutputStreamWriter(writer));
    }

    public FastjsonObjectOutput(OutputStreamWriter writer) {
        this.writer = new PrintWriter(writer);
    }

    public FastjsonObjectOutput(ByteBuf byteBuf) {
        this.byteBuf = byteBuf;
    }

    @Override
    public void writeObject(Object obj) throws IOException {
        SerializeWriter out = new SerializeWriter();
        JSONSerializer serializer = new JSONSerializer(out);
        serializer.config(SerializerFeature.WriteEnumUsingToString, true);
        serializer.write(obj);
        out.writeTo(writer);
        out.close(); // for reuse SerializeWriter buf
        writer.println();
        writer.flush();
    }

    @Override
    public void writeObjectByByteBuf(Object obj) throws IOException {
        byte[] jsonBytes = JSON.toJSONBytes(obj, SerializerFeature.NotWriteDefaultValue);
        byteBuf.writeBytes(jsonBytes);
    }

    @Override
    public void writeBool(boolean v) throws IOException {
        writeObject(v);
    }

    @Override
    public void writeByte(byte v) throws IOException {
        writeObject(v);
    }

    @Override
    public void writeShort(short v) throws IOException {
        writeObject(v);
    }

    @Override
    public void writeInt(int v) throws IOException {
        writeObject(v);
    }

    @Override
    public void writeLong(long v) throws IOException {
        writeObject(v);
    }

    @Override
    public void writeFloat(float v) throws IOException {
        writeObject(v);
    }

    @Override
    public void writeDouble(double v) throws IOException {
        writeObject(v);
    }

    @Override
    public void writeUTF(String v) throws IOException {
        writeObject(v);
    }

    @Override
    public void writeBytes(byte[] b) throws IOException {
        writer.println(new String(b));
    }

    @Override
    public void writeBytes(byte[] v, int off, int len) throws IOException {
        writer.println(new String(v, off, len));

    }

    @Override
    public void flushBuffer() throws IOException {
        writer.flush();
    }

}
