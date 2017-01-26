package io.github.lonamiwebs.klooni.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import io.github.lonamiwebs.klooni.Klooni;

public class DesktopLauncher {
    public static void main (String[] arg) {
        LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        config.width = 408;
        config.height = 680;
        new LwjglApplication(new Klooni(), config);
    }
}
