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
package io.github.lonamiwebs.klooni.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.Label;

import io.github.lonamiwebs.klooni.Klooni;

class BonusParticle {

    private final Label label;
    private float lifetime;

    private final static float SPEED = 1f;

    BonusParticle(final Vector2 pos, final int score, final Label.LabelStyle style) {
        label = new Label("+" + score, style);
        label.setBounds(pos.x, pos.y, 0, 0);
    }

    void run(final Batch batch) {
        // Update
        lifetime += SPEED * Gdx.graphics.getDeltaTime();
        if (lifetime > 1f)
            lifetime = 1f;

        // Render
        label.setColor(Klooni.theme.bonus);
        label.setFontScale(Interpolation.elasticOut.apply(0f, 1f, lifetime));
        float opacity = Interpolation.linear.apply(1f, 0f, lifetime);
        label.draw(batch, opacity);
    }

    boolean done() {
        return lifetime >= 1f;
    }
}
