package io.github.lonamiwebs.klooni.serializer;

import java.io.DataOutputStream;
import java.io.IOException;
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

        // todo uhm maybe make the classes serializable? like telethon, kinda, idk, bye.


        /*
        DataInputStream d = new DataInputStream(new FileInputStream("test.txt"));
        DataOutputStream out = new DataOutputStream(new FileOutputStream("test1.txt"));
        String count;
        d.readFully();
        while((count = d.readLine()) != null){
            String u = count.toUpperCase();
            System.out.println(u);
            out.writeBytes(u + "  ,");
        }
        d.close();
        out.close();
        */


    }
}
