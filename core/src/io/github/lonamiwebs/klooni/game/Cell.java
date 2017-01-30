package io.github.lonamiwebs.klooni.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;

public class Cell {

    private boolean empty;
    private Color color;

    Vector2 pos;
    float size;

    private Color vanishColor;
    private float vanishSize;
    private float vanishElapsed;
    private float vanishLifetime;

    Cell(float x, float y, float cellSize) {
        pos = new Vector2(x, y);
        size = cellSize;

        empty = true;
        color = Color.WHITE;
        vanishElapsed = Float.POSITIVE_INFINITY;
    }

    void set(Color c) {
        empty = false;
        color = c;
    }

    void draw(SpriteBatch batch, NinePatch patch) {
        draw(color, batch, patch, pos.x, pos.y, size);

        // Draw the previous vanishing cell
        if (vanishElapsed <= vanishLifetime) {
            vanishElapsed += Gdx.graphics.getDeltaTime();

            // vanishElapsed might be < 0 (delay), so clamp to 0
            float progress = Math.min(1f,
                    Math.max(vanishElapsed, 0f) / vanishLifetime);

            vanishSize = Interpolation.elasticIn.apply(size, 0, progress);

            float centerOffset = size * 0.5f - vanishSize * 0.5f;
            draw(vanishColor, batch, patch, pos.x + centerOffset, pos.y + centerOffset, vanishSize);
        }
    }

    // TODO Use skin atlas
    public static void draw(Color color, Batch batch, NinePatch patch,
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
        if (empty) // We cannot vanish twice
            return;

        empty = true;
        vanishSize = size;
        vanishColor = color.cpy();
        vanishLifetime = 1f;

        // The vanish distance is this measure (distance² + size³ * 20% size)
        // because it seems good enough. The more the distance, the more the
        // delay, but we decrease the delay depending on the cell size too or
        // it would be way too high
        Vector2 center = new Vector2(pos.x + size * 0.5f, pos.y + 0.5f);
        float vanishDist = Vector2.dst2(
                vanishFrom.x, vanishFrom.y, center.x, center.y) / (size * size * size * size * 0.2f);

        // Negative time = delay, + 0.4*lifetime because elastic interpolation has that delay
        vanishElapsed = vanishLifetime * 0.4f - vanishDist;

        color = Color.WHITE;
    }
}
