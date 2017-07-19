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
package io.github.lonamiwebs.klooni.actors;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;

import io.github.lonamiwebs.klooni.Effect;
import io.github.lonamiwebs.klooni.Klooni;
import io.github.lonamiwebs.klooni.Theme;
import io.github.lonamiwebs.klooni.game.Cell;
import io.github.lonamiwebs.klooni.game.GameLayout;

// Card-like actor used to display information about a given theme
public class EffectCard extends ShopCard {

    //region Members

    public final Effect effect;
    private final Texture background;
    private Color color;

    //endregion

    //region Constructor

    public EffectCard(final Klooni game, final GameLayout layout, final Effect effect) {
        super(game, layout, effect.getDisplay(), Klooni.theme.background);
        background = Theme.getBlankTexture();
        color = Klooni.theme.getRandomCellColor();

        this.effect = effect;
        usedItemUpdated();
    }

    //endregion

    //region Public methods

    @Override
    public void draw(Batch batch, float parentAlpha) {
        final float x = getX(), y = getY();

        batch.setColor(Klooni.theme.background);
        batch.draw(background, x, y, getWidth(), getHeight());

        // Avoid drawing on the borders by adding +1 cell padding +1 to center it
        // so it's becomes cellSize * 2
        Cell.draw(color, batch, x + cellSize * 2, y + cellSize * 2, cellSize);

        super.draw(batch, parentAlpha);
    }

    @Override
    public void usedItemUpdated() {
        if (game.effect.name.equals(effect.name))
            priceLabel.setText("currently used");
        else if (Klooni.isEffectBought(effect))
            priceLabel.setText("bought");
        else
            priceLabel.setText("buy for "+effect.price);
    }

    @Override
    public void use() {
        game.updateEffect(effect);
        usedItemUpdated();
    }

    @Override
    public boolean isBought() {
        return Klooni.isEffectBought(effect);
    }

    @Override
    public boolean isUsed() {
        return game.effect.equals(effect.name);
    }

    @Override
    public float getPrice() {
        return effect.price;
    }

    @Override
    public void performBuy() {
        Klooni.buyEffect(effect);
        use();
    }

    //endregion
}
