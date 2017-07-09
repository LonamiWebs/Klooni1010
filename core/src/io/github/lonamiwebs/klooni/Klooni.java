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

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

import io.github.lonamiwebs.klooni.effects.EvaporateEffect;
import io.github.lonamiwebs.klooni.effects.IEffect;
import io.github.lonamiwebs.klooni.effects.VanishEffect;
import io.github.lonamiwebs.klooni.game.Cell;
import io.github.lonamiwebs.klooni.screens.MainMenuScreen;
import io.github.lonamiwebs.klooni.screens.TransitionScreen;

public class Klooni extends Game {

    //region Members

    // TODO Not sure whether the theme should be static or not since it might load textures
    public static Theme theme;
    public Skin skin;

    public ShareChallenge shareChallenge;

    public static boolean onDesktop;

    private final static float SCORE_TO_MONEY = 1f / 100f;

    public static final int GAME_HEIGHT = 680;
    public static final int GAME_WIDTH = 408;

    private static int usedEffect;
    private Sound effectSound;

    //endregion

    //region Creation

    // TODO Possibly implement a 'ShareChallenge'
    //      for other platforms instead passing null
    public Klooni(final ShareChallenge shareChallenge) {
        this.shareChallenge = shareChallenge;
    }

    @Override
    public void create() {
        onDesktop = Gdx.app.getType().equals(Application.ApplicationType.Desktop);
        prefs = Gdx.app.getPreferences("io.github.lonamiwebs.klooni.game");

        // Load the best match for the skin (depending on the device screen dimensions)
        skin = SkinLoader.loadSkin();

        // Use only one instance for the theme, so anyone using it uses the most up-to-date
        Theme.skin = skin; // Not the best idea
        final String themeName = prefs.getString("themeName", "default");
        if (Theme.exists(themeName))
            theme = Theme.getTheme(themeName);
        else
            theme = Theme.getTheme("default");

        Gdx.input.setCatchBackKey(true); // To show the pause menu
        setScreen(new MainMenuScreen(this));

        usedEffect = effectNameToInt(getUsedEffect());
        setUsedEffect(null); // Update the effect sound
    }

    //endregion

    //region Screen
    
    // TransitionScreen will also dispose by default the previous screen
    public void transitionTo(Screen screen) {
        transitionTo(screen, true);
    }

    public void transitionTo(Screen screen, boolean disposeAfter) {
        setScreen(new TransitionScreen(this, getScreen(), screen, disposeAfter));
    }

    //endregion

    //region Disposing

    @Override
    public void dispose() {
        super.dispose();
        skin.dispose();
        theme.dispose();
        if (effectSound != null)
            effectSound.dispose();
    }

    //endregion

    //region Effects

    // Effects used when clearing a row
    public static IEffect createEffect(final Cell deadCell, final Vector2 culprit) {
        final IEffect effect;
        switch (usedEffect) {
            default:
            case 0:
                effect = new VanishEffect();
                break;
            case 1:
                effect = new EvaporateEffect();
                break;
        }
        effect.setInfo(deadCell, culprit);
        return effect;
    }

    public void playEffectSound() {
        effectSound.play(MathUtils.random(0.7f, 1f), MathUtils.random(0.8f, 1.2f), 0);
    }

    //endregion

    //region Settings

    private static Preferences prefs;

    // Score related
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

    // Settings related
    public static boolean soundsEnabled() {
        return !prefs.getBoolean("muteSound", false);
    }

    public static boolean toggleSound() {
        final boolean result = soundsEnabled();
        prefs.putBoolean("muteSound", result).flush();
        return !result;
    }

    public static boolean shouldSnapToGrid() {
        return prefs.getBoolean("snapToGrid", false);
    }

    public static boolean toggleSnapToGrid() {
        final boolean result = !shouldSnapToGrid();
        prefs.putBoolean("snapToGrid", result).flush();
        return result;
    }

    // Themes related
    public static boolean isThemeBought(Theme theme) {
        if (theme.getPrice() == 0)
            return true;

        String[] themes = prefs.getString("boughtThemes", "").split(":");
        for (String t : themes)
            if (t.equals(theme.getName()))
                return true;

        return false;
    }

    public static boolean buyTheme(Theme theme) {
        final float money = getRealMoney();
        if (theme.getPrice() > money)
            return false;

        setMoney(money - theme.getPrice());

        String bought = prefs.getString("boughtThemes", "");
        if (bought.equals(""))
            bought = theme.getName();
        else
            bought += ":" + theme.getName();

        prefs.putString("boughtThemes", bought);

        return true;
    }

    public static void updateTheme(Theme newTheme) {
        prefs.putString("themeName", newTheme.getName()).flush();
        theme.update(newTheme.getName());
    }

    // Effects related
    public static String getUsedEffect() {
        return prefs.getString("effectName");
    }

    public void setUsedEffect(final String name) {
        if (name != null)
            prefs.putString("effectName", name).flush();

        if (effectSound != null)
            effectSound.dispose();

        switch (effectNameToInt(getUsedEffect())) {
            default:
            case 0:
                effectSound = Gdx.audio.newSound(Gdx.files.internal("sound/effect_vanish.mp3"));
                break;
            case 1:
                effectSound = Gdx.audio.newSound(Gdx.files.internal("sound/effect_evaporate.mp3"));
                break;
        }
    }

    private static int effectNameToInt(final String name) {
        // String comparision is more expensive compared to a single integer one,
        // and when creating instances of a lot of effects it's better if we can
        // save some processor cycles.
        if (name.equals("vanish")) return 0;
        if (name.equals("evaporate")) return 1;
        return -1;
    }

    // Money related
    public static void addMoneyFromScore(int score) {
        setMoney(getRealMoney() + score * SCORE_TO_MONEY);
    }

    private static void setMoney(float money) {
        prefs.putFloat("money", money).flush();
    }

    public static int getMoney() {
        return (int)getRealMoney();
    }

    private static float getRealMoney() {
        return prefs.getFloat("money");
    }

    //endregion
}
