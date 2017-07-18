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
package io.github.lonamiwebs.klooni;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

import io.github.lonamiwebs.klooni.effects.EvaporateEffect;
import io.github.lonamiwebs.klooni.effects.IEffect;
import io.github.lonamiwebs.klooni.effects.VanishEffect;
import io.github.lonamiwebs.klooni.effects.WaterdropEffect;
import io.github.lonamiwebs.klooni.game.Cell;

public class Effect {

    //region Members

    private final int effectId;
    private final Sound effectSound;

    //endregion

    //region Constructor

    // This method will load the sound "sound/effect_{effectName}.mp3"
    public Effect(final String effectName) {
        effectId = effectNameToInt(effectName);
        effectSound = Gdx.audio.newSound(Gdx.files.internal("sound/effect_" + effectName + ".mp3"));
    }

    //endregion

    //region Public methods

    public void playSound() {
        effectSound.play(MathUtils.random(0.7f, 1f), MathUtils.random(0.8f, 1.2f), 0);
    }

    //endregion

    //region Static methods

    public static Effect[] getEffects() {
        // TODO Load effects
        return new Effect[] {
                new Effect("vanish"),
                new Effect("waterdrop"),
                new Effect("evaporate")
        };
    }

    //endregion

    //region Name <-> ID <-> IEffect

    // Effects used when clearing a row
    public IEffect create(final Cell deadCell, final Vector2 culprit) {
        final IEffect effect;
        switch (effectId) {
            default:
            case 0:
                effect = new VanishEffect();
                break;
            case 1:
                effect = new WaterdropEffect();
                break;
            case 2:
                effect = new EvaporateEffect();
                break;
        }
        effect.setInfo(deadCell, culprit);
        return effect;
    }

    private static int effectNameToInt(final String name) {
        // String comparision is more expensive compared to a single integer one,
        // and when creating instances of a lot of effects it's better if we can
        // save some processor cycles.
        if (name.equals("vanish")) return 0;
        if (name.equals("waterdrop")) return 1;
        if (name.equals("evaporate")) return 2;
        return -1;
    }

    //endregion

    //region Disposal

    void dispose() {
        effectSound.dispose();
    }

    //endregion
}
