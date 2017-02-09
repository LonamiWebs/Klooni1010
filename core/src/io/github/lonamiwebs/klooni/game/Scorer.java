package io.github.lonamiwebs.klooni.game;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import io.github.lonamiwebs.klooni.Klooni;
import io.github.lonamiwebs.klooni.serializer.BinSerializable;

// Used to keep track of the current and maximum
// score, and to also display it on the screen.
// The maximum score is NOT saved automatically.
public class Scorer extends BaseScorer implements BinSerializable {

    //region Members

    private int currentScore, highScore;

    // To interpolate between shown score -> real score
    private float shownScore;

    //endregion

    //region Constructor

    // The board size is required when calculating the score
    public Scorer(final Klooni game, GameLayout layout) {
        super(game, layout, Klooni.getMaxScore());

        currentScore = 0;
        highScore = Klooni.getMaxScore();
    }

    //endregion

    //region Private methods

    @Override
    protected void addScore(int score) {
        currentScore += score;
    }

    //endregion

    //region Public methods

    public int getCurrentScore() {
        return currentScore;
    }

    public void saveScore() {
        if (isNewRecord()) {
            Klooni.setMaxScore(currentScore);
        }
    }

    @Override
    protected boolean isNewRecord() {
        return currentScore > highScore;
    }

    @Override
    public boolean isGameOver() {
        return false;
    }

    public void draw(SpriteBatch batch) {
        int roundShown = MathUtils.round(shownScore);
        if (roundShown != currentScore) {
            shownScore = Interpolation.linear.apply(shownScore, currentScore, 0.1f);
            leftLabel.setText(Integer.toString(MathUtils.round(shownScore)));
        }
        super.draw(batch);
    }

    //endregion

    //region Serialization

    @Override
    public void write(DataOutputStream out) throws IOException {
        // currentScore, highScore
        out.writeInt(currentScore);
        out.writeInt(highScore);
    }

    @Override
    public void read(DataInputStream in) throws IOException {
        currentScore = in.readInt();
        highScore = in.readInt();
    }

    //endregion
}
