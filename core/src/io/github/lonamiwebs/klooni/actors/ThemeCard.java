package io.github.lonamiwebs.klooni.actors;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;

import io.github.lonamiwebs.klooni.Klooni;
import io.github.lonamiwebs.klooni.Theme;
import io.github.lonamiwebs.klooni.game.Cell;
import io.github.lonamiwebs.klooni.game.GameLayout;

// Card-like actor used to display information about a given theme
public class ThemeCard extends Actor {

    //region Members

    public final Theme theme;
    private final Texture background;

    private final Label nameLabel;
    private final Label priceLabel;

    public final Rectangle nameBounds;
    public final Rectangle priceBounds;

    public float cellSize;

    private final static int colorsUsed[][] = {
            {0, 7, 7},
            {8, 7, 3},
            {8, 8, 3}
    };

    //endregion

    //region Constructor

    public ThemeCard(final Klooni game, final GameLayout layout, final Theme theme) {
        this.theme = theme;
        background = Theme.getBlankTexture();

        Label.LabelStyle labelStyle = new Label.LabelStyle();
        labelStyle.font = game.skin.getFont("font_small");

        priceLabel = new Label("", labelStyle);
        nameLabel = new Label(theme.getDisplay(), labelStyle);

        Color labelColor = Theme.shouldUseWhite(theme.background) ? Color.WHITE : Color.BLACK;
        priceLabel.setColor(labelColor);
        nameLabel.setColor(labelColor);

        priceBounds = new Rectangle();
        nameBounds = new Rectangle();

        layout.update(this);
        usedThemeUpdated();
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

        nameLabel.setBounds(x + nameBounds.x, y + nameBounds.y, nameBounds.width, nameBounds.height);
        nameLabel.draw(batch, parentAlpha);

        priceLabel.setBounds(x + priceBounds.x, y + priceBounds.y, priceBounds.width, priceBounds.height);
        priceLabel.draw(batch, parentAlpha);
    }

    public void usedThemeUpdated() {
        if (Klooni.theme.getName().equals(theme.getName()))
            priceLabel.setText("currently used");
        else if (Klooni.isThemeBought(theme))
            priceLabel.setText("bought");
        else
            priceLabel.setText("buy for "+theme.getPrice());
    }

    public void use() {
        Klooni.updateTheme(theme);
        usedThemeUpdated();
    }

    public boolean isUsed() {
        return Klooni.theme.getName().equals(theme.getName());
    }

    void performBuy() {
        Klooni.buyTheme(theme);
        use();
    }

    //endregion
}
