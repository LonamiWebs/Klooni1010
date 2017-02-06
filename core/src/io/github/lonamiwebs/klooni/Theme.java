package io.github.lonamiwebs.klooni;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
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
    public Color emptyCell;

    public Color currentScore;
    public Color highScore;
    private Color bandColor;

    private Color[] cells;
    private Color[] buttons;

    public static Skin skin;

    public NinePatch cellPatch;

    // Save the button styles so the changes here get reflected
    private ImageButton.ImageButtonStyle[] buttonStyles;

    //endregion

    //region Constructor

    private Theme() {
        buttonStyles = new ImageButton.ImageButtonStyle[4];
    }

    //endregion

    //region Static methods

    // Gets all the available themes on the available on the internal game storage
    public static Theme[] getThemes() {
        FileHandle[] handles = Gdx.files.internal("themes").list();

        Theme[] result = new Theme[handles.length];
        for (int i = 0; i < handles.length; ++i)
            result[i] = Theme.fromFile(handles[i]);

        return result;
    }

    static Theme getTheme(final String name) {
        return new Theme().update(name);
    }

    private static Theme fromFile(FileHandle handle) {
        return new Theme().update(handle);
    }

    //endregion

    //region Theme updating

    // Updates the theme with all the values from the specified file or name
    public Theme update(final String name) {
        return update(Gdx.files.internal("themes/"+name+".theme"));
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
        background = new Color( // Java won't allow unsigned integers, we need to use Long
                (int)Long.parseLong(colors.getString("background"), 16));

        JsonValue buttonColors = colors.get("buttons");
        buttons = new Color[buttonColors.size];
        for (int i = 0; i < buttons.length; ++i) {
            buttons[i] = new Color((int)Long.parseLong(buttonColors.getString(i), 16));
            if (buttonStyles[i] == null) {
                buttonStyles[i] = new ImageButton.ImageButtonStyle();
            }
            // Update the style. Since every button uses an instance from this
            // array, the changes will appear on screen automatically.
            buttonStyles[i].up = skin.newDrawable("button_up", buttons[i]);
            buttonStyles[i].down = skin.newDrawable("button_down", buttons[i]);
        }

        currentScore = new Color((int)Long.parseLong(colors.getString("current_score"), 16));
        highScore = new Color((int)Long.parseLong(colors.getString("high_score"), 16));
        bandColor = new Color((int)Long.parseLong(colors.getString("band"), 16));

        emptyCell = new Color((int)Long.parseLong(colors.getString("empty_cell"), 16));

        JsonValue cellColors = colors.get("cells");
        cells = new Color[cellColors.size];
        for (int i = 0; i < cells.length; ++i) {
            cells[i] = new Color((int)Long.parseLong(cellColors.getString(i), 16));
        }

        String cellTextureFile = json.getString("cell_texture");
        cellPatch = new NinePatch(new Texture(
                Gdx.files.internal("ui/cells/"+cellTextureFile)), 4, 4, 4, 4);

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
        return cells[colorIndex];
    }

    public Color getBandColor() {
        return bandColor;
    }

    public void glClearBackground() {
        Gdx.gl.glClearColor(background.r, background.g, background.b, background.a);
    }

    public void updateStyle(ImageButton.ImageButtonStyle style, int styleIndex) {
        style.imageUp = buttonStyles[styleIndex].imageUp;
        style.imageDown = buttonStyles[styleIndex].imageDown;
    }

    //endregion

    //region Disposal

    void dispose() {

    }

    //endregion
}
