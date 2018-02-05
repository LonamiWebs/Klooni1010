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
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

import io.github.lonamiwebs.klooni.game.Cell;
import io.github.lonamiwebs.klooni.interfaces.IEffect;
import io.github.lonamiwebs.klooni.interfaces.IEffectFactory;


public class EvaporateEffectFactory implements IEffectFactory {
    @Override
    public String getName() {
        return "evaporate";
    }

    @Override
    public String getDisplay() {
        return "Evaporate";
    }

    @Override
    public int getPrice() {
        return 200;
    }

    @Override
    public IEffect create(Cell deadCell, Vector2 culprit) {
        IEffect effect = new EvaporateEffect();
        effect.setInfo(deadCell, culprit);
        return effect;
    }


    private class EvaporateEffect implements IEffect {
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

        EvaporateEffect() {
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
        public void draw(Batch batch) {
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

}
