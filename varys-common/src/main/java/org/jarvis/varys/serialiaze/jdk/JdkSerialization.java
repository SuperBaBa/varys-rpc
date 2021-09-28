package org.jarvis.varys.serialiaze.jdk;

import io.netty.buffer.ByteBuf;
import org.jarvis.varys.serialiaze.ObjectInput;
import org.jarvis.varys.serialiaze.ObjectOutput;
import org.jarvis.varys.serialiaze.Serialization;

import java.io.*;

/**
 * jdk序列化
 *
 * @author cqjia
 * @date 2021/09/28
 */
public class JdkSerialization implements Serialization {

    @Override
    public ObjectOutput serialize(OutputStream output) throws IOException {
        return new JdkObjectOutput(output);
    }

    @Override
    public ObjectInput deserialize(InputStream input) throws IOException {
        return new JdkObjectInput(input);
    }

    @Override
    public ObjectOutput serialize(ByteBuf byteBufOutput) throws IOException {
        return new JdkObjectOutput(byteBufOutput);
    }

    @Override
    public ObjectInput deserialize(ByteBuf byteBufInput) throws IOException {
        return new JdkObjectInput(byteBufInput);
    }

    /**
     * 通过jdk的方式反序列化
     *
     * @param data 数据
     * @return {@link Object}
     */
    public Object deSerializeByJDK(byte[] data) {
        // 使用JDK反序列化
        try {
            ByteArrayInputStream bais = new ByteArrayInputStream(data);
            ObjectInputStream ois = new ObjectInputStream(bais);
            return ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 使用jdk方式序列化
     *
     * @param obj obj
     * @return {@link byte[]}
     */
    public byte[] serializeByJDK(Object obj) {
        ByteArrayOutputStream baos = null;
        try {
            baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(obj);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return baos.toByteArray();
    }
}
