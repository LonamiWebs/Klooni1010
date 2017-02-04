package io.github.lonamiwebs.klooni.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.TimeUtils;

import io.github.lonamiwebs.klooni.Klooni;

public class TimeScorer {

    //region Members

    private final long startTime;

    // Maximum time alive, in seconds
    private int maxTimeScore;

    // Indicates where we would die in time. Score adds to this, so we take
    // longer to die. To get the "score" we simply calculate `deadTime - startTime`
    private long deadTime;

    final Label timeLeftLabel;
    final Label highTimeLabel;

    final Texture cupTexture;
    final Rectangle cupArea;

    private final Color cupColor;

    private static final long START_TIME = 20 * 1000000000L;

    //endregion

    //region Constructor

    // The board size is required when calculating the score
    public TimeScorer(final Klooni game, GameLayout layout) {
        startTime = TimeUtils.nanoTime();
        deadTime = startTime + START_TIME;

        cupTexture = new Texture(Gdx.files.internal("ui/cup.png"));
        cupColor = Klooni.theme.currentScore.cpy();
        cupArea = new Rectangle();

        Label.LabelStyle labelStyle = new Label.LabelStyle();
        labelStyle.font = game.skin.getFont("font");

        timeLeftLabel = new Label("0", labelStyle);
        timeLeftLabel.setColor(Klooni.theme.currentScore);
        timeLeftLabel.setAlignment(Align.right);

        highTimeLabel = new Label(Integer.toString(nanosToSeconds(maxTimeScore)), labelStyle);
        highTimeLabel.setColor(Klooni.theme.highScore);

        layout.update(this);
    }

    //endregion

    //region Private methods

    private void addScore(int score) {
        deadTime += scoreToNanos(score);
    }

    private int calculateClearScore(int stripsCleared, int boardSize) {
        if (stripsCleared < 1) return 0;
        if (stripsCleared == 1) return boardSize;
        else return boardSize * stripsCleared + calculateClearScore(stripsCleared - 1, boardSize);
    }

    private int nanosToSeconds(long nano) {
        return MathUtils.ceil((float)(nano * 1e-09));
    }

    private long scoreToNanos(int score) {
        // 1s/4p seems fair enough
        return (long)((score / 4.0) * 1e+09);
    }

    public boolean isGameOver() {
        return TimeUtils.nanoTime() > deadTime;
    }

    //endregion

    //region Public methods

    // Adds the score a given piece would give
    public void addPieceScore(int areaPut) {
        addScore(areaPut);
    }

    // Adds the score given by the board, this is, the count of cleared strips
    public void addBoardScore(int stripsCleared, int boardSize) {
        addScore(calculateClearScore(stripsCleared, boardSize));
    }

    public void draw(SpriteBatch batch) {
        int timeLeft = Math.max(nanosToSeconds(deadTime - TimeUtils.nanoTime()), 0);
        timeLeftLabel.setText(Integer.toString(timeLeft));

        // If we beat a new record, the cup color will linear interpolate to the high score color
        //cupColor.lerp(newRecord ? Klooni.theme.highScore : Klooni.theme.currentScore, 0.05f);
        batch.setColor(cupColor);
        batch.draw(cupTexture, cupArea.x, cupArea.y, cupArea.width, cupArea.height);

        timeLeftLabel.draw(batch, 1f);
        highTimeLabel.draw(batch, 1f);
    }

    //endregion
}
