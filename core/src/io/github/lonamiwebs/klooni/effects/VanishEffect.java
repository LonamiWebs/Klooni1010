package io.github.lonamiwebs.klooni.effects;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

import io.github.lonamiwebs.klooni.game.Cell;

public class VanishEffect implements IEffect {
    private Cell cell;

    private Color vanishColor;
    private float vanishSize;
    private float vanishElapsed;
    private float vanishLifetime;

    private final static float MINIMUM_SIZE = 0.3f;

    public VanishEffect() {
        vanishElapsed = Float.POSITIVE_INFINITY;
    }

    @Override
    public void setInfo(Cell deadCell, Vector2 culprit) {
        cell = deadCell;

        vanishSize = cell.size;
        vanishColor = cell.getColorCopy();
        vanishLifetime = 1f;

        // The vanish distance is this measure (distance² + size³ * 20% size)
        // because it seems good enough. The more the distance, the more the
        // delay, but we decrease the delay depending on the cell size too or
        // it would be way too high
        Vector2 center = new Vector2(cell.pos.x + cell.size * 0.5f, cell.pos.y + 0.5f);
        float vanishDist = Vector2.dst2(
                culprit.x, culprit.y, center.x, center.y) / ((float)Math.pow(cell.size, 4.0f) * 0.2f);

        // Negative time = delay, + 0.4*lifetime because elastic interpolation has that delay
        vanishElapsed = vanishLifetime * 0.4f - vanishDist;
    }

    @Override
    public void draw(Batch batch) {
        vanishElapsed += Gdx.graphics.getDeltaTime();

        // vanishElapsed might be < 0 (delay), so clamp to 0
        float progress = Math.min(1f,
                Math.max(vanishElapsed, 0f) / vanishLifetime);

        // If one were to plot the elasticIn function, they would see that the slope increases
        // a lot towards the end- a linear interpolation between the last size + the desired
        // size at 20% seems to look a lot better.
        vanishSize = MathUtils.lerp(
                vanishSize,
                Interpolation.elasticIn.apply(cell.size, 0, progress),
                0.2f
        );

        float centerOffset = cell.size * 0.5f - vanishSize * 0.5f;
        Cell.draw(vanishColor, batch, cell.pos.x + centerOffset, cell.pos.y + centerOffset, vanishSize);
    }

    @Override
    public boolean isDone() {
        return vanishSize < MINIMUM_SIZE;
    }
}
