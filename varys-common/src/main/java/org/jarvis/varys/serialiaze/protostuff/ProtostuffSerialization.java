package org.jarvis.varys.serialiaze.protostuff;

import io.netty.buffer.ByteBuf;
import org.jarvis.varys.serialiaze.ObjectInput;
import org.jarvis.varys.serialiaze.ObjectOutput;
import org.jarvis.varys.serialiaze.Serialization;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class ProtostuffSerialization implements Serialization {
    @Override
    public ObjectOutput serialize(OutputStream output) throws IOException {
        return null;
    }

    @Override
    public ObjectInput deserialize(InputStream input) throws IOException {
        return null;
    }

    @Override
    public ObjectOutput serialize(ByteBuf byteBufOutput) throws IOException {
        return null;
    }

    @Override
    public ObjectInput deserialize(ByteBuf byteBufInput) throws IOException {
        return null;
    }
}
