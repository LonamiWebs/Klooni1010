package io.github.lonamiwebs.klooni.actors;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Align;

import io.github.lonamiwebs.klooni.game.GameLayout;
import io.github.lonamiwebs.klooni.game.Scorer;

// Score and pause menu band actually
public class Band extends Actor {

    private final Scorer scorer;
    private final ShapeRenderer shapeRenderer; // To draw the horizontal "Band"
    private final Color bandColor;

    public final Rectangle scoreBounds;
    public final Rectangle infoBounds;

    public final Label infoLabel;
    public final Label scoreLabel;

    public Band(final GameLayout layout, final Scorer aScorer, final Color aBandColor) {
        scorer = aScorer;
        bandColor = aBandColor;
        shapeRenderer = new ShapeRenderer(20); // Only 20 vertex are required, maybe less

        Label.LabelStyle scoreStyle = new Label.LabelStyle();
        scoreStyle.font = new BitmapFont(Gdx.files.internal("font/geosans-light.fnt"));

        scoreLabel = new Label("", scoreStyle);
        scoreLabel.setAlignment(Align.center);
        infoLabel = new Label("pause menu", scoreStyle);
        infoLabel.setAlignment(Align.center);

        scoreBounds = new Rectangle();
        infoBounds = new Rectangle();
        layout.update(this);
    }

    public void setGameOver() {
        infoLabel.setText("no moves left");
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        // We need to end (thus flush) the batch or things will mess up!
        batch.end();

        float w = getWidth();
        float h = getHeight();

        // TODO This is not the best way to apply the transformation, but, oh well
        float x = getParent().getX();
        float y = getParent().getY();
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(bandColor);
        shapeRenderer.rect(x + getX(), y + getY(), w, h);
        shapeRenderer.end();
        batch.begin();

        scoreLabel.setBounds(x + scoreBounds.x, y + scoreBounds.y, scoreBounds.width, scoreBounds.height);
        scoreLabel.setText(Integer.toString(scorer.getCurrentScore()));
        scoreLabel.draw(batch, parentAlpha);

        infoLabel.setBounds(x + infoBounds.x, y + infoBounds.y, infoBounds.width, infoBounds.height);
        infoLabel.draw(batch, parentAlpha);
    }
}
