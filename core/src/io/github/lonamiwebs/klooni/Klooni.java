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
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

import io.github.lonamiwebs.klooni.screens.MainMenuScreen;
import io.github.lonamiwebs.klooni.screens.TransitionScreen;

public class Klooni extends Game {

    //region Members

    // TODO Not sure whether the theme should be static or not since it might load textures
    public static Theme theme;
    public Effect effect;

    public Skin skin;

    public ShareChallenge shareChallenge;

    public static boolean onDesktop;

    private final static float SCORE_TO_MONEY = 1f / 100f;

    public static final int GAME_HEIGHT = 680;
    public static final int GAME_WIDTH = 408;

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
        effect = new Effect(prefs.getString("effectName"));
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
        effect.dispose();
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
    public static boolean isEffectBought(Effect effect) {
        if (effect.price == 0)
            return true;

        String[] effects = prefs.getString("boughtEffects", "").split(":");
        for (String e : effects)
            if (e.equals(effect.name))
                return true;

        return false;
    }

    public static boolean buyEffect(Effect effect) {
        final float money = getRealMoney();
        if (effect.price > money)
            return false;

        setMoney(money - effect.price);

        String bought = prefs.getString("boughtEffects", "");
        if (bought.equals(""))
            bought = effect.name;
        else
            bought += ":" + effect.name;

        prefs.putString("boughtEffects", bought);

        return true;
    }

    public void updateEffect(Effect newEffect) {
        prefs.putString("effectName", newEffect.name).flush();
        // Create a new effect, since the one passed through the parameter may dispose later
        effect = new Effect(newEffect.name);
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
