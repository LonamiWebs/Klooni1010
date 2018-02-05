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

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;

import io.github.lonamiwebs.klooni.Klooni;
import io.github.lonamiwebs.klooni.Theme;
import io.github.lonamiwebs.klooni.game.Cell;
import io.github.lonamiwebs.klooni.game.GameLayout;

// Card-like actor used to display information about a given theme
public class ThemeCard extends ShopCard {

    //region Members

    private final Theme theme;
    private final Texture background;

    private final static int colorsUsed[][] = {
            {0, 7, 7},
            {8, 7, 3},
            {8, 8, 3}
    };

    //endregion

    //region Constructor

    public ThemeCard(final Klooni game, final GameLayout layout, final Theme theme) {
        super(game, layout, theme.getDisplay(), theme.background);
        background = Theme.getBlankTexture();

        this.theme = theme;
        usedItemUpdated();
    }

    //endregion

    //region Public methods

    @Override
    public void draw(Batch batch, float parentAlpha) {
        final float x = getX(), y = getY();

        batch.setColor(theme.background);
        batch.draw(background, x, y, getWidth(), getHeight());

        // Avoid drawing on the borders by adding +1 cell padding
        for (int i = 0; i < colorsUsed.length; ++i) {
            for (int j = 0; j < colorsUsed[i].length; ++j) {
                Cell.draw(theme.cellTexture, theme.getCellColor(colorsUsed[i][j]), batch,
                        x + cellSize * (j + 1), y + cellSize * (i + 1), cellSize);
            }
        }

        super.draw(batch, parentAlpha);
    }

    @Override
    public void usedItemUpdated() {
        if (Klooni.theme.getName().equals(theme.getName()))
            priceLabel.setText("currently used");
        else if (Klooni.isThemeBought(theme))
            priceLabel.setText("bought");
        else
            priceLabel.setText("buy for " + theme.getPrice());
    }

    @Override
    public void use() {
        Klooni.updateTheme(theme);
        usedItemUpdated();
    }

    @Override
    public boolean isBought() {
        return Klooni.isThemeBought(theme);
    }

    @Override
    public boolean isUsed() {
        return Klooni.theme.getName().equals(theme.getName());
    }

    @Override
    public float getPrice() {
        return theme.getPrice();
    }

    @Override
    public void performBuy() {
        Klooni.buyTheme(theme);
        use();
    }

    //endregion
}
