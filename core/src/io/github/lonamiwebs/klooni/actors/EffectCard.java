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
import com.badlogic.gdx.math.Rectangle;

import io.github.lonamiwebs.klooni.Klooni;
import io.github.lonamiwebs.klooni.Theme;
import io.github.lonamiwebs.klooni.game.Board;
import io.github.lonamiwebs.klooni.game.GameLayout;
import io.github.lonamiwebs.klooni.game.Piece;
import io.github.lonamiwebs.klooni.interfaces.IEffectFactory;

// Card-like actor used to display information about a given theme
public class EffectCard extends ShopCard {

    //region Members

    private final IEffectFactory effect;
    private final Board board;

    // We want to create an effect from the beginning
    private boolean needCreateEffect = true;

    private final Texture background;

    //endregion

    //region Constructor

    public EffectCard(final Klooni game, final GameLayout layout, final IEffectFactory effect) {
        super(game, layout, effect.getDisplay(), Klooni.theme.background);
        background = Theme.getBlankTexture();
        this.effect = effect;

        // Let the board have room for 3 cells, so cellSize * 3
        board = new Board(new Rectangle(0, 0, cellSize * 3, cellSize * 3), 3);

        setRandomPiece();
        usedItemUpdated();
    }

    private void setRandomPiece() {
        while (true) {
            final Piece piece = Piece.random();
            if (piece.cellCols > 3 || piece.cellRows > 3)
                continue;

            // Try to center it (max size is 3, so center is the second grid bit unless max size)
            int x = piece.cellCols == 3 ? 0 : 1;
            int y = piece.cellRows == 3 ? 0 : 1;
            if (board.putPiece(piece, x, y))
                break; // Should not fail, but if it does, don't break
        }
    }

    //endregion

    //region Public methods

    @Override
    public void draw(Batch batch, float parentAlpha) {
        final float x = getX(), y = getY();

        batch.setColor(Klooni.theme.background);
        batch.draw(background, x, y, getWidth(), getHeight());

        // Avoid drawing on the borders by adding +1 cell padding
        board.pos.set(x + cellSize * 1, y + cellSize * 1);

        // Draw only if effects are done, i.e. not showcasing
        if (board.effectsDone())
            board.draw(batch);

        super.draw(batch, parentAlpha);
    }

    @Override
    public boolean showcase(Batch batch, float yDisplacement) {
        board.pos.y += yDisplacement;

        // If no effect is running
        if (board.effectsDone()) {
            // And we want to create a new one
            if (needCreateEffect) {
                // Clear at cells[1][1], the center one
                board.clearAll(1, 1, effect);
                needCreateEffect = false;
            } else {
                // Otherwise, the previous effect finished, so return false because we're done
                // We also want to draw the next time so set the flag to true
                setRandomPiece();
                needCreateEffect = true;
                return false;
            }
        }

        board.draw(batch);
        return true;
    }

    @Override
    public void usedItemUpdated() {
        if (game.effect.getName().equals(effect.getName()))
            priceLabel.setText("currently used");
        else if (Klooni.isEffectBought(effect))
            priceLabel.setText("bought");
        else
            priceLabel.setText("buy for " + effect.getPrice());
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
        return game.effect.getName().equals(effect.getName());
    }

    @Override
    public float getPrice() {
        return effect.getPrice();
    }

    @Override
    public void performBuy() {
        Klooni.buyEffect(effect);
        use();
    }

    //endregion
}
