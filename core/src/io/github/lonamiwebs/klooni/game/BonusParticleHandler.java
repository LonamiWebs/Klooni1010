package io.github.lonamiwebs.klooni.game;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Array;

import java.util.Iterator;

class BonusParticleHandler {

    private final Array<BonusParticle> particles;
    private final Label.LabelStyle labelStyle;

    BonusParticleHandler(final Label.LabelStyle style) {
        labelStyle = style;
        particles = new Array<BonusParticle>();
    }

    void addBonus(final Vector2 pos, final int score) {
        particles.add(new BonusParticle(pos, score, labelStyle));
    }

    void run(final Batch batch) {
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
