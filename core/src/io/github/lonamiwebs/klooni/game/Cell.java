package io.github.lonamiwebs.klooni.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;

class Cell {

    private boolean empty;
    private Color color;

    Vector2 pos;
    float size;

    private Color vanishColor;
    private float vanishSize;
    private float vanishElapsed;
    private float vanishLifetime;

    private static float vanishDelta = 0.1f;

    Cell(float x, float y, float cellSize) {
        pos = new Vector2(x, y);
        size = cellSize;

        empty = true;
        color = Color.WHITE;
    }

    void set(Color c) {
        empty = false;
        color = c;
    }

    void draw(SpriteBatch batch, NinePatch patch) {
        draw(color, batch, patch, pos.x, pos.y, size);

        // Draw the previous vanishing cell
        if (vanishSize > vanishDelta) {
            vanishElapsed += Gdx.graphics.getDeltaTime();

            // vanishElapsed might be < 0 (delay), so clamp to 0
            float progress = Math.min(1f,
                    Math.max(vanishElapsed, 0f) / vanishLifetime);

            vanishSize = Interpolation.elasticIn.apply(size, 0, progress);

            float centerOffset = size * 0.5f - vanishSize * 0.5f;
            draw(vanishColor, batch, patch, pos.x + centerOffset, pos.y + centerOffset, vanishSize);

            if (progress == 1f) {
                vanishSize = 0f; // Stop vanishing
            }
        }
    }

    // TODO Use skin atlas
    static void draw(Color color, SpriteBatch batch, NinePatch patch,
                     float x, float y, float size) {
        batch.setColor(color);
        patch.draw(batch, x, y, size, size);
    }

    boolean isEmpty() {
        return empty;
    }

    // Vanish from indicates the point which caused the vanishing to happen,
    // in this case, a piece was put. The closer it was put, the faster
    // this piece will vanish.
    void vanish(Vector2 vanishFrom) {
        empty = true;

        vanishSize = size;
        vanishColor = color.cpy();
        vanishLifetime = 1.5f;

        // Square the size when calculating the vanish distance
        // because it will be used as delay, and without squaring,
        // the delay would be too large
        Vector2 center = new Vector2(pos.x + size * 0.5f, pos.y + 0.5f);
        float vanishDist = Vector2.dst(
                vanishFrom.x, vanishFrom.y, center.x, center.y) / (size * size);

        // Negative time indicates delay, + half lifetime because elastic has that delay
        vanishElapsed = vanishLifetime * 0.5f - vanishDist;

        color = Color.WHITE;
    }
}
