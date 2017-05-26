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

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Array;

import java.util.Iterator;

import io.github.lonamiwebs.klooni.Klooni;

public class BonusParticleHandler {

    private final Array<BonusParticle> particles;
    private final Label.LabelStyle labelStyle;

    public BonusParticleHandler(final Klooni game) {
        labelStyle = new Label.LabelStyle();
        labelStyle.font = game.skin.getFont("font_bonus");
        particles = new Array<BonusParticle>();
    }

    public void addBonus(final Vector2 pos, final int score) {
        particles.add(new BonusParticle(pos, score, labelStyle));
    }

    public void run(final Batch batch) {
        BonusParticle particle;
        Iterator<BonusParticle> iterator = particles.iterator();
        while (iterator.hasNext()) {
            particle = iterator.next();
            particle.run(batch);
            if (particle.done())
                iterator.remove();
        }
    }
}
