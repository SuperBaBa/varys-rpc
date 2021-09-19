package org.jarvis.varys.serialiaze.fastjson;


import com.alibaba.fastjson.JSON;
import io.netty.buffer.ByteBuf;
import org.jarvis.varys.serialiaze.ObjectInput;
import org.jarvis.varys.util.StringUtil;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;

/**
 * fastjson对象输入
 *
 * @author marcus
 * @date 2021/09/09
 */
public class FastjsonObjectInput implements ObjectInput {

    /**
     * 字符输入流
     */
    private BufferedReader reader;

    private ByteBuf byteBuf;

    /**
     * fastjson对象输入
     *
     * @param in 在
     */
    public FastjsonObjectInput(InputStream in) {
        this(new InputStreamReader(in));
    }

    /**
     * fastjson对象输入
     *
     * @param reader 读者
     */
    public FastjsonObjectInput(Reader reader) {
        this.reader = new BufferedReader(reader);
    }

    public FastjsonObjectInput(ByteBuf byteBuf) {
        this.byteBuf = byteBuf;
    }

    public Object readObjectByByteBuf() throws IOException {
        String json = readLineByByteBuf();
        if (StringUtil.isEmpty(json)) {
            throw new IOException("Not found data");
        }
        return JSON.parse(json);
    }

    /**
     * 阅读对象
     *
     * @param cls cls
     * @return {@link T}
     * @throws IOException ioexception
     */
    @Override
    public <T> T readObject(Class<T> cls) throws IOException {
        String json = readLine();
        return JSON.parseObject(json, cls);
    }

    /**
     * 阅读对象
     *
     * @param cls  cls
     * @param type 类型
     * @return {@link T}
     * @throws IOException ioexception
     */
    @Override
    public <T> T readObject(Class<T> cls, Type type) throws IOException {
        String json = readLine();
        return JSON.parseObject(json, type);
    }

    public <T> T readObjectByByteBuf(Class<T> cls, Type type) throws IOException {
        String json = readLineByByteBuf();
        if (StringUtil.isEmpty(json)) {
            throw new IOException("Not found data");
        }
        return JSON.parseObject(json, type);
    }

    /**
     * 读bool
     *
     * @return boolean
     * @throws IOException ioexception
     */
    @Override
    public boolean readBool() throws IOException {
        return read(boolean.class);
    }

    /**
     * 读取字节
     *
     * @return byte
     * @throws IOException ioexception
     */
    @Override
    public byte readByte() throws IOException {
        return read(byte.class);
    }

    /**
     * 读短
     *
     * @return short
     * @throws IOException ioexception
     */
    @Override
    public short readShort() throws IOException {
        return read(short.class);
    }

    /**
     * 读int
     *
     * @return int
     * @throws IOException ioexception
     */
    @Override
    public int readInt() throws IOException {
        return read(int.class);
    }

    /**
     * 读长
     *
     * @return long
     * @throws IOException ioexception
     */
    @Override
    public long readLong() throws IOException {
        return read(long.class);
    }

    /**
     * 读浮动
     *
     * @return float
     * @throws IOException ioexception
     */
    @Override
    public float readFloat() throws IOException {
        return read(float.class);
    }

    /**
     * 读双
     *
     * @return double
     * @throws IOException ioexception
     */
    @Override
    public double readDouble() throws IOException {
        return read(double.class);
    }

    /**
     * 读utf
     *
     * @return {@link String}
     * @throws IOException ioexception
     */
    @Override
    public String readUTF() throws IOException {
        return read(String.class);
    }

    /**
     * 读取字节
     *
     * @return {@link byte[]}
     * @throws IOException ioexception
     */
    @Override
    public byte[] readBytes() throws IOException {
        return readLine().getBytes();
    }


    /**
     * 读
     *
     * @param tClass t类
     * @return {@link T}
     * @throws IOException ioexception
     */
    public <T> T read(Class<T> tClass) throws IOException {
        String json = readLine();
        return JSON.parseObject(json, tClass);
    }

    /**
     * 读取一行
     *
     * @return {@link String}
     * @throws IOException ioexception
     */
    private String readLine() throws IOException {
        String line = reader.readLine();
        if (line == null || line.trim().length() == 0) {
            throw new EOFException();
        }
        return line;
    }

    private String readLineByByteBuf() throws IOException {
        if (byteBuf.readableBytes() < 4) {
            return "";
        }
        // 标记字节流开始位置，读取指针在索引的位置
        byteBuf.markReaderIndex();
        // 获取data的字节流长度
        int dataLength = byteBuf.readInt();
        if (byteBuf.readableBytes() < dataLength) {
            // 重新回到读取指针标记的位置
            byteBuf.resetReaderIndex();
            return "";
        }
        byte[] data = new byte[dataLength];
        byteBuf.readBytes(data);
        return new String(data, StandardCharsets.UTF_8);
    }

}
