package org.jarvis.varys.codec;

import org.jarvis.varys.dto.VarysRequest;
import org.jarvis.varys.serialiaze.jdk.JdkSerialization;
import org.jarvis.varys.util.SerializationUtil;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.UUID;

import static org.junit.Assert.*;

/**
 * 不同信息译码器测试
 *
 * @author cqjia
 * @date 2021/09/06
 */
public class VarysMessageDecoderTest {
    JdkSerialization jdkSerialization;
    /**
     * 不同消息编码器
     */
    VarysMessageEncoder varysMessageEncoder;
    /**
     * 不同的要求
     */
    VarysRequest varysRequest;

    /**
     * 设置
     *
     * @throws Exception 异常
     */
    @Before
    public void setUp() throws Exception {
        jdkSerialization = new JdkSerialization();
        varysRequest = new VarysRequest();
        varysRequest.setRequestId(UUID.randomUUID().toString());
        varysMessageEncoder = new VarysMessageEncoder(VarysRequest.class);
    }

    /**
     * 拆除
     */
    @After
    public void tearDown() throws Exception {
    }

    /**
     * 测试反序列化jdk
     */
    @Test
    public void testDeserializeByJDK() {
        byte[] bytes = jdkSerialization.serializeByJDK(varysRequest);
        Object obj = jdkSerialization.deSerializeByJDK(bytes);
        System.out.println(obj.toString());
    }


    /**
     * 测试由jdk序列化
     */
    @Test
    public void testSerializeByJDK() {
        byte[] bytes = jdkSerialization.serializeByJDK(varysRequest);
        System.out.println(Arrays.toString(bytes));
    }

    /**
     * 测试反序列化protobuf
     */
    @Test
    public void testDeserializeByProtobuf() {
        byte[] bytes = SerializationUtil.serialize(varysRequest);
        Object obj = SerializationUtil.deserialize(bytes, VarysRequest.class);
        System.out.println(obj.toString());
    }


    /**
     * 测试序列化protobuf
     */
    @Test
    public void testSerializeByProtobuf() {
        byte[] bytes = SerializationUtil.serialize(varysRequest);
        System.out.println(Arrays.toString(bytes));
    }
}