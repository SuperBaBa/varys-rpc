package org.jarvis.varys.codec;

import org.jarvis.varys.dto.VarysRequest;
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
    /**
     * 不同信息译码器
     */
    VarysMessageDecoder varysMessageDecoder;
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
        varysMessageDecoder = new VarysMessageDecoder(VarysRequest.class);
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
        byte[] bytes = varysMessageEncoder.serializeByJDK(varysRequest);
        Object obj = varysMessageDecoder.deSerializeByJDK(bytes);
        System.out.println(obj.toString());
    }


    /**
     * 测试由jdk序列化
     */
    @Test
    public void testSerializeByJDK() {
        byte[] bytes = varysMessageEncoder.serializeByJDK(varysRequest);
        System.out.println(Arrays.toString(bytes));
    }

    /**
     * 测试反序列化protobuf
     */
    @Test
    public void testDeserializeByProtobuf() {
        byte[] bytes = varysMessageEncoder.serializeByProtobuf(varysRequest);
        Object obj = varysMessageDecoder.deSerializeByProtobuf(bytes, VarysRequest.class);
        System.out.println(obj.toString());
    }


    /**
     * 测试序列化protobuf
     */
    @Test
    public void testSerializeByProtobuf() {
        byte[] bytes = varysMessageEncoder.serializeByProtobuf(varysRequest);
        System.out.println(Arrays.toString(bytes));
    }
}