package io.github.lonamiwebs.klooni.actors;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;

import io.github.lonamiwebs.klooni.Theme;
import io.github.lonamiwebs.klooni.game.Cell;
import io.github.lonamiwebs.klooni.game.GameLayout;

public class ThemeCard extends Actor {

    private final ShapeRenderer shapeRenderer;

    public final Theme theme;

    public ThemeCard(final GameLayout layout, final Theme theme) {
        shapeRenderer = new ShapeRenderer(20);
        this.theme = theme;
        layout.update(this);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        batch.end();

        final float x = getX(), y = getY();

        Vector2 pos = localToStageCoordinates(new Vector2(x, y));

        // TODO Something is wrong with this code, shape renderer fills one yes one no if multiple themes
        Gdx.gl.glEnable(GL20.GL_BLEND);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(0.5f, 0.5f, 0.5f, 0.3f);
        shapeRenderer.rect(pos.x, pos.y, getWidth(), getHeight());
        shapeRenderer.end();
        Gdx.gl.glDisable(GL20.GL_BLEND);

        batch.begin();

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
