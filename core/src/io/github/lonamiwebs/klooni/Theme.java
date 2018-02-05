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
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;

// Represents a Theme for the current game.
// These are loaded from external files, so more
// can be easily added
public class Theme {

    //region Members

    private String displayName;
    private String name;
    private int price;

    public Color background;
    public Color foreground;
    private Color emptyCell;

    public Color currentScore;
    public Color highScore;
    public Color bonus;
    public Color bandColor;
    public Color textColor;

    private Color[] cells;

    public static Skin skin;

    public Texture cellTexture;

    // Save the button styles so the changes here get reflected
    private final ImageButton.ImageButtonStyle[] buttonStyles;

    //endregion

    //region Constructor

    private Theme() {
        buttonStyles = new ImageButton.ImageButtonStyle[4];
    }

    //endregion

    //region Static methods

    static boolean exists(final String name) {
        return Gdx.files.internal("themes/" + name + ".theme").exists();
    }

    // Gets all the available themes on the available on the internal game storage
    public static Array<Theme> getThemes() {
        String[] themes = Gdx.files.internal("themes/theme.list").readString().split("\n");

        Array<Theme> result = new Array<Theme>(themes.length);
        for (int i = 0; i < themes.length; ++i) {
            FileHandle file = Gdx.files.internal("themes/" + themes[i] + ".theme");
            if (file.exists())
                result.add(Theme.fromFile(file));
            else {
                Gdx.app.log(
                        "Theme/Info", "Non-existing theme '" + themes[i] +
                                "' found on theme.list (line " + (i + 1) + ")");
            }
        }

        return result;
    }

    static Theme getTheme(final String name) {
        return new Theme().update(name);
    }

    private static Theme fromFile(FileHandle handle) {
        return new Theme().update(handle);
    }

    // Used to determine the best foreground color (black or white) given a background color
    // Formula took from http://alienryderflex.com/hsp.html
    // Not used yet, but may be useful
    private final static double BRIGHTNESS_CUTOFF = 0.5;

    public static boolean shouldUseWhite(Color color) {
        double brightness = Math.sqrt(
                color.r * color.r * .299 +
                        color.g * color.g * .587 +
                        color.b * color.b * .114);

        return brightness < BRIGHTNESS_CUTOFF;
    }

    //endregion

    //region Theme updating

    // Updates the theme with all the values from the specified file or name
    public Theme update(final String name) {
        return update(Gdx.files.internal("themes/" + name + ".theme"));
    }

    private Theme update(final FileHandle handle) {
        if (skin == null) {
            throw new NullPointerException("A Theme.skin must be set before updating any Theme instance");
        }

        final JsonValue json = new JsonReader().parse(handle.readString());

        name = handle.nameWithoutExtension();
        displayName = json.getString("name");
        price = json.getInt("price");

        JsonValue colors = json.get("colors");
        // Java won't allow unsigned integers, we need to use Long
        background = new Color((int) Long.parseLong(colors.getString("background"), 16));
        foreground = new Color((int) Long.parseLong(colors.getString("foreground"), 16));

        JsonValue buttonColors = colors.get("buttons");
        Color[] buttons = new Color[buttonColors.size];
        for (int i = 0; i < buttons.length; ++i) {
            buttons[i] = new Color((int) Long.parseLong(buttonColors.getString(i), 16));
            if (buttonStyles[i] == null) {
                buttonStyles[i] = new ImageButton.ImageButtonStyle();
            }
            // Update the style. Since every button uses an instance from this
            // array, the changes will appear on screen automatically.
            buttonStyles[i].up = skin.newDrawable("button_up", buttons[i]);
            buttonStyles[i].down = skin.newDrawable("button_down", buttons[i]);
        }

        currentScore = new Color((int) Long.parseLong(colors.getString("current_score"), 16));
        highScore = new Color((int) Long.parseLong(colors.getString("high_score"), 16));
        bonus = new Color((int) Long.parseLong(colors.getString("bonus"), 16));
        bandColor = new Color((int) Long.parseLong(colors.getString("band"), 16));
        textColor = new Color((int) Long.parseLong(colors.getString("text"), 16));

        emptyCell = new Color((int) Long.parseLong(colors.getString("empty_cell"), 16));

        JsonValue cellColors = colors.get("cells");
        cells = new Color[cellColors.size];
        for (int i = 0; i < cells.length; ++i) {
            cells[i] = new Color((int) Long.parseLong(cellColors.getString(i), 16));
        }

        String cellTextureFile = json.getString("cell_texture");
        cellTexture = SkinLoader.loadPng("cells/" + cellTextureFile);

        return this;
    }

    //endregion

    //region Applying the theme

    public String getName() {
        return name;
    }

    public String getDisplay() {
        return displayName;
    }

    public int getPrice() {
        return price;
    }

    public ImageButton.ImageButtonStyle getStyle(int button) {
        return buttonStyles[button];
    }

    public Color getCellColor(int colorIndex) {
        return colorIndex < 0 ? emptyCell : cells[colorIndex];
    }

    public void glClearBackground() {
        Gdx.gl.glClearColor(background.r, background.g, background.b, background.a);
    }

    public void updateStyle(ImageButton.ImageButtonStyle style, int styleIndex) {
        style.imageUp = buttonStyles[styleIndex].imageUp;
        style.imageDown = buttonStyles[styleIndex].imageDown;
    }

    //endregion

    //region Styling utilities

    // A 1x1 blank pixel map to be tinted and used in multiple places
    public static Texture getBlankTexture() {
        final Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.WHITE);
        pixmap.fill();
        final Texture result = new Texture(pixmap);
        pixmap.dispose();
        return result;
    }

    //endregion

    //region Disposal

    void dispose() {
        cellTexture.dispose();
    }

    //endregion
}
