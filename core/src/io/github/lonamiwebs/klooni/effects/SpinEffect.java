package io.github.lonamiwebs.klooni.effects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;

import io.github.lonamiwebs.klooni.game.Cell;
import io.github.lonamiwebs.klooni.interfaces.IEffect;

class SpinEffect implements IEffect {
    private float age;
    private Vector2 pos;
    private float size;
    private Color color;

    private static final float LIFETIME = 2.0f;
    private static final float INV_LIFETIME = 1.0f / LIFETIME;

    private static final float TOTAL_ROTATION = 600;

    @Override
    public void setInfo(Cell deadCell, Vector2 culprit) {
        age = 0;
        pos = deadCell.pos.cpy();
        size = deadCell.size;
        color = deadCell.getColorCopy();
    }

    @Override
    public void draw(Batch batch) {
        age += Gdx.graphics.getDeltaTime();

        final float progress = age * INV_LIFETIME;
        final float currentSize = Interpolation.pow2In.apply(size, 0, progress);
        final float currentRotation = Interpolation.sine.apply(0, TOTAL_ROTATION, progress);

        final Matrix4 original = batch.getTransformMatrix().cpy();
        final Matrix4 rotated = batch.getTransformMatrix();

        final float disp =
                + 0.5f * (size - currentSize) // the smaller, the more we need to "push" to center
                + currentSize * 0.5f; // center the cell for rotation

        rotated.translate(pos.x + disp, pos.y + disp, 0);
        rotated.rotate(0, 0, 1, currentRotation);
        rotated.translate(currentSize * -0.5f, currentSize * -0.5f, 0); // revert centering for rotation

        batch.setTransformMatrix(rotated);
        Cell.draw(color, batch, 0, 0, currentSize);
        batch.setTransformMatrix(original);
    }

    @Override
    public boolean isDone() {
        return age > LIFETIME;
    }
}
