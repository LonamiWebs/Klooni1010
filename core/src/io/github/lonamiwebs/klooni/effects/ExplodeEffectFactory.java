/*
    1010! Klooni, a free customizable puzzle game for Android and Desktop
    Copyright (C) 2017  Lonami Exo | LonamiWebs

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/
package io.github.lonamiwebs.klooni.effects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

import io.github.lonamiwebs.klooni.game.Cell;
import io.github.lonamiwebs.klooni.interfaces.IEffect;
import io.github.lonamiwebs.klooni.interfaces.IEffectFactory;


public class ExplodeEffectFactory implements IEffectFactory {
    @Override
    public String getName() {
        return "explode";
    }

    @Override
    public String getDisplay() {
        return "Explode";
    }

    @Override
    public int getPrice() {
        return 200;
    }

    @Override
    public IEffect create(Cell deadCell, Vector2 culprit) {
        IEffect effect = new ExplodeEffect();
        effect.setInfo(deadCell, culprit);
        return effect;
    }


    private class ExplodeEffect implements IEffect {
        private Color color;
        boolean dead;

        private final static float EXPLOSION_X_RANGE = 0.25f;
        private final static float EXPLOSION_Y_RANGE = 0.30f;
        private final static float GRAVITY_PERCENTAGE = -0.60f;

        private class Shard {
            final Vector2 pos, vel, acc;
            final float size;

            Shard(final Vector2 pos, final float size) {
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
        }

        private Shard[] shards;

        @Override
        public void setInfo(Cell deadCell, Vector2 culprit) {
            color = deadCell.getColorCopy();

            shards = new Shard[MathUtils.random(4, 6)];
            for (int i = 0; i != shards.length; ++i)
                shards[i] = new Shard(deadCell.pos, deadCell.size);
        }

        @Override
        public void draw(Batch batch) {
            dead = true; // assume we're death
            final Vector3 translation = batch.getTransformMatrix().getTranslation(new Vector3());
            for (int i = shards.length; i-- != 0; ) {
                shards[i].draw(batch, Gdx.graphics.getDeltaTime());
                dead &= translation.y + shards[i].pos.y + shards[i].size < 0; // all must be dead
            }
        }

        @Override
        public boolean isDone() {
            return dead;
        }
    }

}
