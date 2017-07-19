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
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;

import io.github.lonamiwebs.klooni.Effect;
import io.github.lonamiwebs.klooni.Klooni;
import io.github.lonamiwebs.klooni.Theme;
import io.github.lonamiwebs.klooni.game.Cell;
import io.github.lonamiwebs.klooni.game.GameLayout;

// Card-like actor used to display information about a given theme
public class EffectCard extends Actor {

    //region Members

    private final Klooni game;
    public final Effect effect;
    private final Texture background;
    private Color color;

    private final Label nameLabel;
    private final Label priceLabel;

    public final Rectangle nameBounds;
    public final Rectangle priceBounds;

    public float cellSize;

    //endregion

    //region Constructor

    public EffectCard(final Klooni game, final GameLayout layout, final Effect effect) {
        this.game = game;
        this.effect = effect;
        background = Theme.getBlankTexture();

        Label.LabelStyle labelStyle = new Label.LabelStyle();
        labelStyle.font = game.skin.getFont("font_small");

        priceLabel = new Label("", labelStyle);
        nameLabel = new Label(effect.getDisplay(), labelStyle);

        Color labelColor = Theme.shouldUseWhite(Klooni.theme.background) ? Color.WHITE : Color.BLACK;
        priceLabel.setColor(labelColor);
        nameLabel.setColor(labelColor);

        priceBounds = new Rectangle();
        nameBounds = new Rectangle();

        layout.update(this);
        usedEffectUpdated();

        color = Klooni.theme.getRandomCellColor();
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

        nameLabel.setBounds(x + nameBounds.x, y + nameBounds.y, nameBounds.width, nameBounds.height);
        nameLabel.draw(batch, parentAlpha);

        priceLabel.setBounds(x + priceBounds.x, y + priceBounds.y, priceBounds.width, priceBounds.height);
        priceLabel.draw(batch, parentAlpha);
    }

    public void usedEffectUpdated() {
        if (game.effect.name.equals(effect.name))
            priceLabel.setText("currently used");
        else if (Klooni.isEffectBought(effect))
            priceLabel.setText("bought");
        else
            priceLabel.setText("buy for "+effect.price);
    }

    public void use() {
        game.updateEffect(effect);
        usedEffectUpdated();
    }

    public boolean isUsed() {
        return game.effect.equals(effect.name);
    }

    void performBuy() {
        Klooni.buyEffect(effect);
        use();
    }

    //endregion
}
