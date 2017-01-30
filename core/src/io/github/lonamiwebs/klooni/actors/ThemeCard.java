package io.github.lonamiwebs.klooni.actors;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;

import io.github.lonamiwebs.klooni.Theme;
import io.github.lonamiwebs.klooni.game.Cell;
import io.github.lonamiwebs.klooni.game.GameLayout;

public class ThemeCard extends Actor {

    public final Theme theme;
    private final Texture background;

    public ThemeCard(final GameLayout layout, final Theme theme) {
        // TODO We could also use white color and then batch.setColor(theme.background)
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(theme.background);
        pixmap.fill();
        background = new Texture(pixmap);
        pixmap.dispose();

        this.theme = theme;
        layout.update(this);

        setWidth(Gdx.graphics.getWidth());
        setScaleX(200);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        final float x = getX(), y = getY();

        batch.setColor(Color.WHITE);
        batch.draw(background, x, y, getWidth(), getHeight());
        // Consider 5 cells on the available size (1/5 height each)
        // Do not draw on the borders to add some padding, colors used:
        // 0 7 7
        // 8 7 3
        // 8 8 3
        float cellSize = getHeight() * 0.2f;
        Cell.draw(theme.getCellColor(0), batch, x + cellSize, y + cellSize, cellSize);
        Cell.draw(theme.getCellColor(7), batch, x + cellSize * 2, y + cellSize, cellSize);
        Cell.draw(theme.getCellColor(7), batch, x + cellSize * 3, y + cellSize, cellSize);

        Cell.draw(theme.getCellColor(8), batch, x + cellSize, y + cellSize * 2, cellSize);
        Cell.draw(theme.getCellColor(7), batch, x + cellSize * 2, y + cellSize * 2, cellSize);
        Cell.draw(theme.getCellColor(8), batch, x + cellSize * 3, y + cellSize * 2, cellSize);

        Cell.draw(theme.getCellColor(8), batch, x + cellSize, y + cellSize * 3, cellSize);
        Cell.draw(theme.getCellColor(8), batch, x + cellSize * 2, y + cellSize * 3, cellSize);
        Cell.draw(theme.getCellColor(3), batch, x + cellSize * 3, y + cellSize * 3, cellSize);
    }
}
