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
package io.github.lonamiwebs.klooni.game;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.TimeUtils;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import io.github.lonamiwebs.klooni.Klooni;
import io.github.lonamiwebs.klooni.serializer.BinSerializable;

public class TimeScorer extends BaseScorer implements BinSerializable {

    //region Members

    private final Label timeLeftLabel;

    private long startTime;
    private int highScore;

    // Indicates where we would die in time. Score adds to this, so we take
    // longer to die. To get the "score" we simply calculate `deadTime - startTime`
    private long deadTime;

    // We need to know when the game was paused to "stop" counting
    private long pauseTime;
    private int pausedTimeLeft;

    //endregion

    //region Static variables

    private static final long START_TIME = 30 * 1000000000L;

    // 2 seconds every 10 points: (2/10)*10^9 to get the nanoseconds
    private static final double SCORE_TO_NANOS = 0.2e+09d;
    private static final double NANOS_TO_SECONDS = 1e-09d;

    //endregion

    //region Constructor

    // The board size is required when calculating the score
    public TimeScorer(final Klooni game, GameLayout layout) {
        super(game, layout, Klooni.getMaxTimeScore());
        highScore = Klooni.getMaxTimeScore();

        Label.LabelStyle labelStyle = new Label.LabelStyle();
        labelStyle.font = game.skin.getFont("font");
        timeLeftLabel = new Label("", labelStyle);
        timeLeftLabel.setAlignment(Align.center);
        layout.updateTimeLeftLabel(timeLeftLabel);

        startTime = TimeUtils.nanoTime();
        deadTime = startTime + START_TIME;

        pausedTimeLeft = -1;
    }

    //endregion

    //region Private methods

    private int nanosToSeconds(long nano) {
        return MathUtils.ceil((float) (nano * NANOS_TO_SECONDS));
    }

    private long scoreToNanos(int score) {
        return (long) (score * SCORE_TO_NANOS);
    }

    private int getTimeLeft() {
        return Math.max(nanosToSeconds(deadTime - TimeUtils.nanoTime()), 0);
    }

    //endregion

    //region Public methods

    @Override
    public int addBoardScore(int stripsCleared, int boardSize) {
        // Only clearing strips adds extra time
        long extraTime = scoreToNanos(calculateClearScore(stripsCleared, boardSize));
        deadTime += extraTime;
        super.addBoardScore(stripsCleared, boardSize);

        return nanosToSeconds(extraTime);
    }

    @Override
    public boolean isGameOver() {
        return TimeUtils.nanoTime() > deadTime;
    }

    @Override
    public String gameOverReason() {
        return "time is up";
    }

    @Override
    public void saveScore() {
        if (isNewRecord()) {
            Klooni.setMaxTimeScore(getCurrentScore());
        }
    }

    @Override
    protected boolean isNewRecord() {
        return getCurrentScore() > highScore;
    }

    @Override
    public void pause() {
        pauseTime = TimeUtils.nanoTime();
        pausedTimeLeft = getTimeLeft();
    }

    @Override
    public void resume() {
        if (pauseTime != 0L) {
            long difference = TimeUtils.nanoTime() - pauseTime;
            startTime += difference;
            deadTime += difference;

            pauseTime = 0L;
            pausedTimeLeft = -1;
        }
    }

    @Override
    public void draw(SpriteBatch batch) {
        super.draw(batch);

        int timeLeft = pausedTimeLeft < 0 ? getTimeLeft() : pausedTimeLeft;
        timeLeftLabel.setText(Integer.toString(timeLeft));
        timeLeftLabel.setColor(Klooni.theme.currentScore);
        timeLeftLabel.draw(batch, 1f);
    }

    //endregion

    //region Serialization

    @Override
    public void write(DataOutputStream out) throws IOException {
        // current/dead offset ("how long until we die"), highScore
        out.writeLong(TimeUtils.nanoTime() - startTime);
        out.writeInt(highScore);
    }

    @Override
    public void read(DataInputStream in) throws IOException {
        // We need to use the offset, since the start time
        // is different and we couldn't save absolute values
        long deadOffset = in.readLong();
        deadTime = startTime + deadOffset;
        highScore = in.readInt();
    }

    //endregion
}
