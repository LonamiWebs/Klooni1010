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
package io.github.lonamiwebs.klooni;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

public class SkinLoader {
    private final static float[] multipliers = {0.75f, 1.0f, 1.25f, 1.5f, 2.0f, 4.0f};
    private final static String[] ids = {
            "play", "play_saved", "star", "stopwatch", "palette", "home", "replay",
            "share", "sound_on", "sound_off", "snap_on", "snap_off", "issues", "credits",
            "web", "back", "ok", "cancel", "power_off", "effects"
    };

    private final static float bestMultiplier;

    // FIXME this static code is exposed to a race condition and will fail if called class gets loaded before execution of Klooni.create
    static {
        // Use the height to determine the best match
        // We cannot use a size which is over the device height,
        // so use the closest smaller one
        int i;
        float desired = (float) Gdx.graphics.getHeight() / (float) Klooni.GAME_HEIGHT;
        for (i = multipliers.length - 1; i > 0; --i) {
            if (multipliers[i] < desired)
                break;
        }

        // Now that we have the right multiplier, load the skin
        Gdx.app.log("SkinLoader", "Using assets multiplier x" + multipliers[i]);
        bestMultiplier = multipliers[i];
    }

    static Skin loadSkin() {
        String folder = "ui/x" + bestMultiplier + "/";

        // Base skin
        Skin skin = new Skin(Gdx.files.internal("skin/uiskin.json"));

        // Nine patches
        final int border = (int) (28 * bestMultiplier);
        skin.add("button_up", new NinePatch(new Texture(
                Gdx.files.internal(folder + "button_up.png")), border, border, border, border));

        skin.add("button_down", new NinePatch(new Texture(
                Gdx.files.internal(folder + "button_down.png")), border, border, border, border));

        for (String id : ids) {
            skin.add(id + "_texture", new Texture(Gdx.files.internal(folder + id + ".png")));
        }

        folder = "font/x" + bestMultiplier + "/";
        skin.add("font", new BitmapFont(Gdx.files.internal(folder + "geosans-light64.fnt")));
        skin.add("font_small", new BitmapFont(Gdx.files.internal(folder + "geosans-light32.fnt")));
        skin.add("font_bonus", new BitmapFont(Gdx.files.internal(folder + "the-next-font.fnt")));

        return skin;
    }

    public static Texture loadPng(String name) {
        final String filename = "ui/x" + bestMultiplier + "/" + name;
        return new Texture(Gdx.files.internal(filename));
    }
}
