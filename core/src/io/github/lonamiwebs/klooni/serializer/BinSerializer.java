/*
    1010! Klooni, a free customizable puzzle game for Android and Desktop
    Copyright (C) 2017  Lonami Exo | LonamiWebs

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/
package io.github.lonamiwebs.klooni.serializer;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;

public class BinSerializer {

    // ascii (klooni) and binary (1010b)
    private final static byte[] HEADER = {0x6B, 0x6C, 0x6F, 0x6F, 0x6E, 0x69, 0xa};

    // MODIFY THIS VALUE EVERY TIME A BinSerializable IMPLEMENTATION CHANGES
    // Or unwanted results will happen and corrupt the game in an unknown way.
    private final static int VERSION = 2;

    public static void serialize(final BinSerializable serializable, final OutputStream output)
            throws IOException {
        DataOutputStream out = new DataOutputStream(output);
        try {
            out.write(HEADER);
            out.writeInt(VERSION);
            serializable.write(out);
        } finally {
            try {
                out.close();
            } catch (IOException ignored) {
            }
        }
    }

    public static void deserialize(final BinSerializable serializable, final InputStream input)
            throws IOException {
        DataInputStream in = new DataInputStream(input);
        try {
            // Read the HEADER and the VERSION (checks)
            byte[] savedBuffer = new byte[HEADER.length];
            in.readFully(savedBuffer);
            if (!Arrays.equals(savedBuffer, HEADER))
                throw new IOException("Invalid saved header found.");

            int savedVersion = in.readInt();
            if (savedVersion != VERSION) {
                throw new IOException(
                        "Invalid saved version found. Should be " + VERSION + ", not " + savedVersion);
            }

            // Read the saved data if the checks passed
            serializable.read(in);
        } finally {
            try {
                in.close();
            } catch (IOException ignored) {
            }
        }
    }
}
