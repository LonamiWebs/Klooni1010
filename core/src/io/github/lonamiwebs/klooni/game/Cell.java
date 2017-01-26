package io.github.lonamiwebs.klooni.game;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

class Cell {

    private boolean empty;
    private Color color;

    Cell() {
        setEmpty();
    }

    void set(Color c) {
        empty = false;
        color = c;
    }

    void setEmpty() {
        empty = true;
        color = Color.WHITE;
    }

    void draw(SpriteBatch batch, NinePatch patch, float x, float y, int size) {
        draw(color, batch, patch, x, y, size);
    }

    static void draw(Color color, SpriteBatch batch, NinePatch patch,
                     float x, float y, float size) {
        // TODO Use skin atlas
        batch.setColor(color);
        patch.draw(batch, x, y, size, size);
    }

    boolean isEmpty() {
        return empty;
    }

    public void vanish() {
        empty = true;
    }
}
