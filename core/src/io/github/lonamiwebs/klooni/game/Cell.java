package io.github.lonamiwebs.klooni.game;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

class Cell {

    private boolean empty;
    private Color color;

    Cell() {
        empty = true;
        color = Color.WHITE;
    }

    void set(Color c) {
        empty = false;
        color = c;
    }

    void draw(SpriteBatch batch, NinePatch patch, int x, int y, int size) {
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
