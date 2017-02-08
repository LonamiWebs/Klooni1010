package io.github.lonamiwebs.klooni;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class GameSerializer {
    // integer version
    // integer game mode:
    //   integer current/high score
    //
    // TODO: Scorer specific
    // Board on row major order:
    //   true color if cell is filled
    //   false      if cell is empty
    // 3 held pieces IDs (-1 if null)

    // Modify this if the saving scheme changes
    private static final int VERSION = 1;

    public static void serialize(final int /*GameScreen*/ game, final OutputStream output)
            throws IOException {
        DataOutputStream out = new DataOutputStream(output);
        try {
            out.writeInt(VERSION);
        } finally {
            out.close();
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
