package io.github.lonamiwebs.klooni.serializer;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class BinSerializer {
    public static void serialize(final BinSerializable serializable, final OutputStream output)
            throws IOException {
        DataOutputStream out = new DataOutputStream(output);
        try {
            serializable.write(out);
        } finally {
            try {
                out.close();
            } catch (IOException ignored) { }
        }
    }

    public static void deserialize(final BinSerializable serializable, final InputStream input)
            throws IOException {
        DataInputStream in = new DataInputStream(input);
        try {
            serializable.read(in);
        } finally {
            try {
                in.close();
            } catch (IOException ignored) { }
        }
    }
}
