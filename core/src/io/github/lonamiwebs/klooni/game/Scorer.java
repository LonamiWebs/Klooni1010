package io.github.lonamiwebs.klooni.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Align;

public class Scorer {

    private final Preferences prefs;
    private int currentScore, maxScore;
    private boolean newRecord;

    private float shownScore; // To interpolate between shown score -> real score
    private final int boardSize;

    final Label currentScoreLabel;
    final Label maxScoreLabel;

    final Texture cupTexture;
    final Rectangle cupArea;

    public Scorer(GameLayout layout, int boardSize) {
        prefs = Gdx.app.getPreferences("io.github.lonamiwebs.klooni.game");

        currentScore = 0;
        maxScore = prefs.getInteger("maxScore", 0);
        this.boardSize = boardSize;

        cupTexture = new Texture(Gdx.files.internal("ui/cup.png"));
        cupArea = new Rectangle();

        Label.LabelStyle scoreStyle = new Label.LabelStyle();
        scoreStyle.font = new BitmapFont(Gdx.files.internal("font/geosans-light.fnt"));

        currentScoreLabel = new Label("0", scoreStyle);
        currentScoreLabel.setColor(Color.GOLD);
        currentScoreLabel.setAlignment(Align.right);

        maxScoreLabel = new Label(Integer.toString(maxScore), scoreStyle);
        maxScoreLabel.setColor(new Color(0x65D681FF));

        layout.update(this);
    }

    public void addPieceScore(int areaPut) {
        addScore(areaPut);
    }

    public void addBoardScore(int stripsCleared) {
        addScore(calculateClearScore(stripsCleared));
    }

    private void addScore(int score) {
        currentScore += score;
        newRecord = currentScore > maxScore;
    }

    public int getCurrentScore() {
        return currentScore;
    }

    public void saveScore() {
        if (newRecord) {
            prefs.putInteger("maxScore", currentScore);
            prefs.flush();
        }
    }

    int calculateClearScore(int stripsCleared) {
        // The original game seems to work as follows:
        // If < 1 were cleared, score = 0
        // If = 1  was cleared, score = cells cleared
        // If > 1 were cleared, score = cells cleared + score(cleared - 1)
        if (stripsCleared < 1) return 0;
        if (stripsCleared == 1) return boardSize;
        else return boardSize * stripsCleared + calculateClearScore(stripsCleared - 1);
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
}
