package io.github.lonamiwebs.klooni.game;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.TimeUtils;

import io.github.lonamiwebs.klooni.Klooni;

public class TimeScorer extends BaseScorer {

    //region Members

    private final long startTime;

    // Indicates where we would die in time. Score adds to this, so we take
    // longer to die. To get the "score" we simply calculate `deadTime - startTime`
    private long deadTime;

    private static final long START_TIME = 20 * 1000000000L;

    //endregion

    //region Constructor

    // The board size is required when calculating the score
    public TimeScorer(final Klooni game, GameLayout layout) {
        super(game, layout, Klooni.getMaxTimeScore());

        startTime = TimeUtils.nanoTime();
        deadTime = startTime + START_TIME;
    }

    //endregion

    //region Private methods

    @Override
    protected void addScore(int score) {
        deadTime += scoreToNanos(score);
    }

    private int nanosToSeconds(long nano) {
        return MathUtils.ceil((float)(nano * 1e-09));
    }

    private long scoreToNanos(int score) {
        // 1s/4p seems fair enough
        return (long)((score / 4.0) * 1e+09);
    }

    @Override
    public boolean isGameOver() {
        return TimeUtils.nanoTime() > deadTime;
    }

    //endregion

    //region Public methods

    @Override
    public int getCurrentScore() {
        return nanosToSeconds(deadTime - startTime);
    }

    @Override
    public void saveScore() {
        // TODO Save high time score
    }

    @Override
    protected boolean isNewRecord() {
        // TODO Return true if it is a new record
        return false;
    }

    @Override
    public void draw(SpriteBatch batch) {
        int timeLeft = Math.max(nanosToSeconds(deadTime - TimeUtils.nanoTime()), 0);
        leftLabel.setText(Integer.toString(timeLeft));

        super.draw(batch);
    }

    //endregion
}
