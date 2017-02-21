package io.github.lonamiwebs.klooni;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

import io.github.lonamiwebs.klooni.screens.MainMenuScreen;

public class Klooni extends Game {

    //region Members

    // TODO Not sure whether the theme should be static or not since it might load textures
    public static Theme theme;
    public Skin skin;

    public static boolean onDesktop;

    //endregion

    //region Creation

    @Override
    public void create() {
        onDesktop = Gdx.app.getType().equals(Application.ApplicationType.Desktop);
        prefs = Gdx.app.getPreferences("io.github.lonamiwebs.klooni.game");

        // TODO Better way to have this skin somewhere
        // Gotta create that darn .json…!
        skin = new Skin(Gdx.files.internal("skin/uiskin.json"));

        skin.add("button_up", new NinePatch(new Texture(
                Gdx.files.internal("ui/button_up.png")), 28, 28, 28, 28));

        skin.add("button_down", new NinePatch(new Texture(
                Gdx.files.internal("ui/button_down.png")), 28, 28, 28, 28));

        skin.add("play_texture", new Texture(Gdx.files.internal("ui/play.png")));
        skin.add("play_saved_texture", new Texture(Gdx.files.internal("ui/play_saved.png")));
        skin.add("star_texture", new Texture(Gdx.files.internal("ui/star.png")));
        skin.add("stopwatch_texture", new Texture(Gdx.files.internal("ui/stopwatch.png")));
        skin.add("palette_texture", new Texture(Gdx.files.internal("ui/palette.png")));
        skin.add("home_texture", new Texture(Gdx.files.internal("ui/home.png")));
        skin.add("replay_texture", new Texture(Gdx.files.internal("ui/replay.png")));
        skin.add("share_texture", new Texture(Gdx.files.internal("ui/share.png")));
        skin.add("sound_on_texture", new Texture(Gdx.files.internal("ui/sound_on.png")));
        skin.add("sound_off_texture", new Texture(Gdx.files.internal("ui/sound_off.png")));
        skin.add("snap_on_texture", new Texture(Gdx.files.internal("ui/snap_on.png")));
        skin.add("snap_off_texture", new Texture(Gdx.files.internal("ui/snap_off.png")));
        skin.add("issues_texture", new Texture(Gdx.files.internal("ui/issues.png")));
        skin.add("credits_texture", new Texture(Gdx.files.internal("ui/credits.png")));
        skin.add("web_texture", new Texture(Gdx.files.internal("ui/web.png")));
        skin.add("back_texture", new Texture(Gdx.files.internal("ui/back.png")));

        skin.add("font", new BitmapFont(Gdx.files.internal("font/geosans-light.fnt")));
        skin.add("font_small", new BitmapFont(Gdx.files.internal("font/geosans-light32.fnt")));
        skin.add("font_bonus", new BitmapFont(Gdx.files.internal("font/the-next-font.fnt")));

        // Use only one instance for the theme, so anyone using it uses the most up-to-date
        Theme.skin = skin; // Not the best idea
        final String themeName = prefs.getString("themeName", "default");
        if (Theme.exists(themeName))
            theme = Theme.getTheme(themeName);
        else
            theme = Theme.getTheme("default");

        Gdx.input.setCatchBackKey(true); // To show the pause menu
        setScreen(new MainMenuScreen(this));
    }

    //endregion

    //region Screen

    @Override
    public void render() {
        super.render();
    }

    //endregion

    //region Disposing

    @Override
    public void dispose() {
        super.dispose();
        skin.dispose();
        theme.dispose();
    }

    //endregion

    //region Settings

    private static Preferences prefs;

    public static int getMaxScore() {
        return prefs.getInteger("maxScore", 0);
    }

    public static int getMaxTimeScore() {
        return prefs.getInteger("maxTimeScore", 0);
    }

    public static void setMaxScore(int score) {
        prefs.putInteger("maxScore", score).flush();
    }

    public static void setMaxTimeScore(int maxTimeScore) {
        prefs.putInteger("maxTimeScore", maxTimeScore).flush();
    }

    public static boolean soundsEnabled() {
        return !prefs.getBoolean("muteSound", false);
    }

    public static void toggleSound() {
        prefs.putBoolean("muteSound", soundsEnabled()).flush();
    }

    public static boolean shouldSnapToGrid() {
        return prefs.getBoolean("snapToGrid", false);
    }

    public static void toggleSnapToGrid() {
        prefs.putBoolean("snapToGrid", !shouldSnapToGrid()).flush();
    }

    public static boolean isThemeBought(Theme theme) {
        if (theme.getPrice() == 0)
            return true;

        String[] themes = prefs.getString("boughtThemes", "").split(":");
        for (String t : themes)
            if (t.equals(theme.getName()))
                return true;

        return false;
    }

    public static void updateTheme(Theme newTheme) {
        prefs.putString("themeName", newTheme.getName()).flush();
        theme.update(newTheme.getName());
    }

    //endregion
}
