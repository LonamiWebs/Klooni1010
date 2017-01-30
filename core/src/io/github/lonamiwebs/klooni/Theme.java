package io.github.lonamiwebs.klooni;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;

public class Theme {

    private String displayName;
    private String name;
    private int price;
    private Color background;
    private Color[] cells;
    private Color[] buttons;

    private Theme() { }

    public static Theme[] getThemes() {
        FileHandle[] handles = Gdx.files.internal("themes").list();

        Theme[] result = new Theme[handles.length];
        for (int i = 0; i < handles.length; i++) {
            result[i] = Theme.fromFile(handles[i]);
        }

        return result;
    }

    static Theme getTheme(final String name) {
        return new Theme().update(name);
    }

    private static Theme fromFile(FileHandle handle) {
        return new Theme().update(handle);
    }

    public Theme update(final String name) {
        return update(Gdx.files.internal("themes/"+name+".theme"));
    }

    private Theme update(final FileHandle handle) {
        final JsonValue json = new JsonReader().parse(handle.readString());

        name = handle.nameWithoutExtension();
        displayName = json.getString("name");
        price = json.getInt("price");

        JsonValue colors = json.get("colors");
        background = new Color( // Java won't allow unsigned integers, we need to use Long
                (int)Long.parseLong(colors.getString("background"), 16));

        JsonValue buttonColors = colors.get("buttons");
        buttons = new Color[buttonColors.size];
        for (int i = 0; i < buttons.length; i++) {
            buttons[i] = new Color((int)Long.parseLong(buttonColors.getString(i), 16));
        }

        JsonValue cellColors = colors.get("cells");
        cells = new Color[cellColors.size];
        for (int i = 0; i < cells.length; i++) {
            cells[i] = new Color((int)Long.parseLong(cellColors.getString(i), 16));
        }

        String cellTextureFile = json.getString("cell_texture");
        return this;
    }

    // TODO Avoid creating game.skin.newDrawable all the time without disposing…
    public ImageButton.ImageButtonStyle getStyle(final Skin skin, int button, final String imageName) {
        return new ImageButton.ImageButtonStyle(
                skin.newDrawable("button_up", buttons[button]),
                skin.newDrawable("button_down", buttons[button]),
                null, skin.getDrawable(imageName), null, null);
    }

    public Color getCellColor(int colorIndex) {
        return cells[colorIndex];
    }

    void dispose() {

    }

    public void glClearBackground() {
        Gdx.gl.glClearColor(background.r, background.g, background.b, background.a);
    }

    public String getName() {
        return name;
    }
}
