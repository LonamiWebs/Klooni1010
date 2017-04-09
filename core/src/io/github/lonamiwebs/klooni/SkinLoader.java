package io.github.lonamiwebs.klooni;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

public class SkinLoader {
    private static float[] multipliers = {0.75f, 1.0f, 1.25f, 1.5f, 2.0f, 4.0f};
    private static String[] ids = {
            "play", "play_saved", "star", "stopwatch", "palette", "home", "replay",
            "share", "sound_on", "sound_off", "snap_on", "snap_off", "issues", "credits",
            "web", "back", "ok", "cancel"
    };

    private static float bestMultiplier;

    static {
        // Use the height to determine the best match
        // We cannot use a size which is over the device height,
        // so use the closest smaller one
        int i;
        float desired = (float)Gdx.graphics.getHeight() / (float)Klooni.GAME_HEIGHT;
        for (i = multipliers.length - 1; i > 0; --i) {
            if (multipliers[i] < desired)
                break;
        }

        // Now that we have the right multiplier, load the skin
        Gdx.app.log("SkinLoader", "Using assets multiplier x" + multipliers[i]);
        bestMultiplier = multipliers[i];
    }

    static Skin loadSkin() {
        final String folder = "ui/x" + bestMultiplier + "/";

        // Base skin
        Skin skin = new Skin(Gdx.files.internal("skin/uiskin.json"));

        // Nine patches
        final int border = (int)(28 * bestMultiplier);
        skin.add("button_up", new NinePatch(new Texture(
                Gdx.files.internal(folder + "button_up.png")), border, border, border, border));

        skin.add("button_down", new NinePatch(new Texture(
                Gdx.files.internal(folder + "button_down.png")), border, border, border, border));

        for (String id : ids) {
            skin.add(id + "_texture", new Texture(Gdx.files.internal(folder + id + ".png")));
        }

        // TODO Best fonts too!
        skin.add("font", new BitmapFont(Gdx.files.internal("font/geosans-light.fnt")));
        skin.add("font_small", new BitmapFont(Gdx.files.internal("font/geosans-light32.fnt")));
        skin.add("font_bonus", new BitmapFont(Gdx.files.internal("font/the-next-font.fnt")));

        return skin;
    }

    public static Texture loadPng(String name) {
        final String filename = "ui/x" + bestMultiplier + "/" + name + ".png";
        return new Texture(Gdx.files.internal(filename));
    }
}
