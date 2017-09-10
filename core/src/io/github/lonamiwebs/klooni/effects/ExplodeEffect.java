package io.github.lonamiwebs.klooni.effects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;

import io.github.lonamiwebs.klooni.game.Cell;

public class ExplodeEffect implements IEffect {
    private float age;
    private Vector2 pos;
    private float size;
    private Color color;

    private final static float EXPLOSION_X_RANGE = 0.25f;
    private final static float EXPLOSION_Y_RANGE = 0.30f;
    private final static float GRAVITY_PERCENTAGE = -0.60f;

    class Shard {
        final Vector2 pos, vel, acc;
        final float size;

        public Shard(final Vector2 pos, final float size) {
            final float xRange = Gdx.graphics.getWidth() * EXPLOSION_X_RANGE;
            final float yRange = Gdx.graphics.getHeight() * EXPLOSION_Y_RANGE;
            vel = new Vector2(MathUtils.random(-xRange, +xRange), MathUtils.random(-yRange * 0.2f, +yRange));
            acc = new Vector2(0f, Gdx.graphics.getHeight() * GRAVITY_PERCENTAGE);

            this.size = size * MathUtils.random(0.40f, 0.60f);
            this.pos = pos.cpy().add(this.size * 0.5f, this.size * 0.5f);
        }

        public void draw(final Batch batch, final float dt) {
            vel.add(acc.x * dt, acc.y * dt).scl(0.99f);
            pos.add(vel.x * dt, vel.y * dt);
            Cell.draw(color, batch, pos.x, pos.y, size);
        }

        public boolean isDead() {
            return pos.y - size < 0;
        }
    }

    private Shard[] shards;

    @Override
    public void setInfo(Cell deadCell, Vector2 culprit) {
        age = 0;
        color = deadCell.getColorCopy();

        shards = new Shard[MathUtils.random(4, 6)];
        for (int i = 0; i != shards.length; ++i)
            shards[i] = new Shard(deadCell.pos, deadCell.size);
    }

    @Override
    public void draw(Batch batch) {
        for (int i = 0; i != shards.length; ++i)
            shards[i].draw(batch, Gdx.graphics.getDeltaTime());
    }

    @Override
    public boolean isDone() {
        for (int i = 0; i != shards.length; ++i)
            if (!shards[i].isDead())
                return false;

        return true;
    }
}
