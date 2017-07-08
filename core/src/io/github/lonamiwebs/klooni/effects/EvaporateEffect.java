package io.github.lonamiwebs.klooni.effects;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

import io.github.lonamiwebs.klooni.game.Cell;

public class EvaporateEffect implements IEffect {
    private Vector2 pos;
    private float originalX;

    private float size;

    private Color vanishColor;
    private float vanishSize;
    private float vanishElapsed;
    private float driftMagnitude;
    private float randomOffset;

    private static final float UP_SPEED = 100.0f;
    private static final float LIFETIME = 3.0f;
    private static final float INV_LIFETIME = 1.0f / 3.0f;

    public EvaporateEffect() {
        vanishElapsed = Float.POSITIVE_INFINITY;
    }

    @Override
    public void setInfo(Cell deadCell, Vector2 culprit) {
        pos = deadCell.pos.cpy();
        originalX = pos.x;
        size = deadCell.size;

        vanishSize = deadCell.size;
        vanishColor = deadCell.getColorCopy();
        driftMagnitude = Gdx.graphics.getWidth() * 0.05f;
        vanishElapsed = 0;
        randomOffset = MathUtils.random(MathUtils.PI2);
    }

    @Override
    public void draw(SpriteBatch batch) {
        vanishElapsed += Gdx.graphics.getDeltaTime();

        // Update the size as we fade away
        final float progress = vanishElapsed * INV_LIFETIME;
        vanishSize = Interpolation.fade.apply(size, 0, progress);

        // Fade away depending on the time
        vanishColor.set(vanishColor.r, vanishColor.g, vanishColor.b, 1.0f - progress);

        // Ghostly fade upwards, by doing a lerp from our current position to the wavy one
        pos.x = MathUtils.lerp(
                pos.x,
                originalX + MathUtils.sin(randomOffset + vanishElapsed * 3f) * driftMagnitude,
                0.3f
        );
        pos.y += UP_SPEED * Gdx.graphics.getDeltaTime();

        Cell.draw(vanishColor, batch, pos.x, pos.y, vanishSize);
    }

    @Override
    public boolean isDone() {
        return vanishElapsed > LIFETIME;
    }
}
