package io.github.lonamiwebs.klooni.game;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

class Cell {

    private boolean empty;
    private Texture texture;

    Cell() {
        empty = true;
        texture = Piece.getTexture(Color.WHITE);

        // texture or color? like in a future, uhm, nah, all cells same texture diff colorz
    }

    void set(Texture tex) {
        empty = false;
        texture = tex; // TODO Disposing uhm? Or use the skin better
    }

    void draw(SpriteBatch batch, int x, int y, int size) {
        //batch.setColor(color);
        batch.draw(texture, x, y, size, size);
    }

    boolean isEmpty() {
        return empty;
    }

    public void vanish() {
        empty = true;
    }
}
