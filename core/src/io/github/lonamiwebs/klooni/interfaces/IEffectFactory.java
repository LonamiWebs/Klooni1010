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
package io.github.lonamiwebs.klooni.interfaces;

import com.badlogic.gdx.math.Vector2;

import io.github.lonamiwebs.klooni.game.Cell;

/**
 * IEffectEfactory interface has to be implemented for each effect.
 *
 * It tells the name and the price of the effect and will create it, when needed.
 *
 * @see IEffect
 */
public interface IEffectFactory {
    public String getName();
    public String getDisplay();
    public int getPrice();
    public IEffect create(final Cell deadCell, final Vector2 culprit);
}
