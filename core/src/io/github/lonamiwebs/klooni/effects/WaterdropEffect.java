package io.github.lonamiwebs.klooni.effects;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

import io.github.lonamiwebs.klooni.SkinLoader;
import io.github.lonamiwebs.klooni.game.Cell;

public class WaterdropEffect implements IEffect {
    private Vector2 pos;

    private Color cellColor;
    private Color dropColor;
    private float cellSize;

    private final float fallAcceleration;
    private float fallSpeed;

    private static final float FALL_ACCELERATION = 500.0f;
    private static final float FALL_VARIATION = 50.0f;
    private static final float COLOR_SPEED = 7.5f;

    private static Texture dropTexture;

    static {
        dropTexture = SkinLoader.loadPng("cells/drop.png");
    }

    public WaterdropEffect() {
        fallAcceleration = FALL_ACCELERATION + MathUtils.random(-FALL_VARIATION, FALL_VARIATION);
    }

    @Override
    public void setInfo(Cell deadCell, Vector2 culprit) {
        pos = deadCell.pos.cpy();
        cellSize = deadCell.size;
        cellColor = deadCell.getColorCopy();
        dropColor = new Color(cellColor.r, cellColor.g, cellColor.b, 0.0f);
    }

    @Override
    public void draw(SpriteBatch batch) {
        final float dt = Gdx.graphics.getDeltaTime();
        fallSpeed += fallAcceleration * dt;
        pos.y -= fallSpeed * dt;

        cellColor.set(
                cellColor.r, cellColor.g, cellColor.b,
                Math.max(cellColor.a - COLOR_SPEED * dt, 0.0f)
        );
        dropColor.set(
                cellColor.r, cellColor.g, cellColor.b,
                Math.min(dropColor.a + COLOR_SPEED * dt, 1.0f)
        );

        Cell.draw(cellColor, batch, pos.x, pos.y, cellSize);
        Cell.draw(dropTexture, dropColor, batch, pos.x, pos.y, cellSize);
    }

    @Override
    public boolean isDone() {
        return pos.y + dropTexture.getHeight() < 0;
    }
}
