package io.github.lonamiwebs.klooni.serializer;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public interface BinSerializable {
    void write(final DataOutputStream out) throws IOException;
    void read(final DataInputStream in) throws IOException;
}
