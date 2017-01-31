package io.github.lonamiwebs.klooni.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Align;

import io.github.lonamiwebs.klooni.Klooni;

// Used to keep track of the current and maximum
// score, and to also display it on the screen.
// The maximum score is NOT saved automatically.
public class Scorer {

    //region Members

    private int currentScore, maxScore;

    final Label currentScoreLabel;
    final Label maxScoreLabel;

    final Texture cupTexture;
    final Rectangle cupArea;

    // If the currentScore beat the maxScore, then we have a new record
    private boolean newRecord;

    // To interpolate between shown score -> real score
    private float shownScore;

    //endregion

    //region Constructor

    // The board size is required when calculating the score
    public Scorer(final Klooni game, GameLayout layout) {
        currentScore = 0;
        maxScore = Klooni.getMaxScore();

        cupTexture = new Texture(Gdx.files.internal("ui/cup.png"));
        cupArea = new Rectangle();

        Label.LabelStyle labelStyle = new Label.LabelStyle();
        labelStyle.font = game.skin.getFont("font");

        currentScoreLabel = new Label("0", labelStyle);
        currentScoreLabel.setColor(Color.GOLD);
        currentScoreLabel.setAlignment(Align.right);

        maxScoreLabel = new Label(Integer.toString(maxScore), labelStyle);
        maxScoreLabel.setColor(new Color(0x65D681FF));

        layout.update(this);
    }

    //endregion

    //region Private methods

    private void addScore(int score) {
        currentScore += score;
        newRecord = currentScore > maxScore;
    }

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
    public void addPieceScore(int areaPut) {
        addScore(areaPut);
    }

    // Adds the score given by the board, this is, the count of cleared strips
    public void addBoardScore(int stripsCleared, int boardSize) {
        addScore(calculateClearScore(stripsCleared, boardSize));
    }

    public int getCurrentScore() {
        return currentScore;
    }

    public void saveScore() {
        if (newRecord) {
            Klooni.setMaxScore(currentScore);
        }
    }

    public void draw(SpriteBatch batch) {
        int roundShown = MathUtils.round(shownScore);
        if (roundShown != currentScore) {
            shownScore = Interpolation.linear.apply(shownScore, currentScore, 0.1f);
            currentScoreLabel.setText(Integer.toString(MathUtils.round(shownScore)));
        }

        batch.setColor(Color.GOLD);
        batch.draw(cupTexture, cupArea.x, cupArea.y, cupArea.width, cupArea.height);
        currentScoreLabel.draw(batch, 1f);
        maxScoreLabel.draw(batch, 1f);
    }

    //endregion
}
