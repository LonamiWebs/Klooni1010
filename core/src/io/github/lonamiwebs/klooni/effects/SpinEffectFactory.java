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
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;

import io.github.lonamiwebs.klooni.game.Cell;
import io.github.lonamiwebs.klooni.interfaces.IEffect;
import io.github.lonamiwebs.klooni.interfaces.IEffectFactory;


public class SpinEffectFactory implements IEffectFactory {
    @Override
    public String getName() {
        return "spin";
    }

    @Override
    public String getDisplay() {
        return "Spin";
    }

    @Override
    public int getPrice() {
        return 200;
    }

    @Override
    public IEffect create(Cell deadCell, Vector2 culprit) {
        IEffect effect = new SpinEffect();
        effect.setInfo(deadCell, culprit);
        return effect;
    }


    private class SpinEffect implements IEffect {
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
                    +0.5f * (size - currentSize) // the smaller, the more we need to "push" to center
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

}
