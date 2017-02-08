package io.github.lonamiwebs.klooni.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Align;

import io.github.lonamiwebs.klooni.Klooni;

public abstract class BaseScorer {

    //region Members

    final Label leftLabel;
    final Label highScoreLabel;

    final Texture cupTexture;
    final Rectangle cupArea;

    private final Color cupColor;

    //endregion

    //region Constructor

    // The board size is required when calculating the score
    BaseScorer(final Klooni game, GameLayout layout, int highScore) {
        cupTexture = new Texture(Gdx.files.internal("ui/cup.png"));
        cupColor = Klooni.theme.currentScore.cpy();
        cupArea = new Rectangle();

        Label.LabelStyle labelStyle = new Label.LabelStyle();
        labelStyle.font = game.skin.getFont("font");

        leftLabel = new Label("0", labelStyle);
        leftLabel.setAlignment(Align.right);

        highScoreLabel = new Label(Integer.toString(highScore), labelStyle);

        layout.update(this);
    }

    //endregion

    //region Private methods

    protected abstract void addScore(int score);

    // The original game seems to work as follows:
    // If < 1 were cleared, score = 0
    // If = 1  was cleared, score = cells cleared
    // If > 1 were cleared, score = cells cleared + score(cleared - 1)
    private int calculateClearScore(int stripsCleared, int boardSize) {
        if (stripsCleared < 1) return 0;
        if (stripsCleared == 1) return boardSize;
        else return boardSize * stripsCleared + calculateClearScore(stripsCleared - 1, boardSize);
    }

    //endregion

    //region Public methods

    // Adds the score a given piece would give
    public final void addPieceScore(int areaPut) {
        addScore(areaPut);
    }

    // Adds the score given by the board, this is, the count of cleared strips
    public final void addBoardScore(int stripsCleared, int boardSize) {
        addScore(calculateClearScore(stripsCleared, boardSize));
    }

    public void pause() { }
    public void resume() { }

    abstract public boolean isGameOver();
    abstract protected boolean isNewRecord();

    abstract public int getCurrentScore();
    abstract public void saveScore();

    public void draw(SpriteBatch batch) {
        // If we beat a new record, the cup color will linear interpolate to the high score color
        cupColor.lerp(isNewRecord() ? Klooni.theme.highScore : Klooni.theme.currentScore, 0.05f);
        batch.setColor(cupColor);
        batch.draw(cupTexture, cupArea.x, cupArea.y, cupArea.width, cupArea.height);

        leftLabel.setColor(Klooni.theme.currentScore);
        leftLabel.draw(batch, 1f);

        highScoreLabel.setColor(Klooni.theme.highScore);
        highScoreLabel.draw(batch, 1f);
    }

    //endregion
}
