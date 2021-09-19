package org.jarvis.varys.serialiaze;

import java.io.*;

public interface Serialization {
    ObjectOutput serialize(OutputStream output) throws IOException;

    ObjectInput deserialize(InputStream input) throws IOException;

}
