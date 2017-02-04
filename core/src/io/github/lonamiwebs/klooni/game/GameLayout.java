package io.github.lonamiwebs.klooni.game;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Rectangle;

import io.github.lonamiwebs.klooni.actors.Band;
import io.github.lonamiwebs.klooni.actors.ThemeCard;

// Helper class to calculate the size of each element
//
// TODO In a future, perhaps this could handle landscape mode differently
// For example, the boardHeight on the left and the piece holder on the right
public class GameLayout {

    //region Members

    private float screenWidth, marginWidth, availableWidth;
    private float scoreHeight, boardHeight, pieceHolderHeight, themeCardHeight;

    //endregion

    //region Constructor

    public GameLayout() {
        calculate();
    }

    //endregion

    //region Private methods

    private void calculate() {
        screenWidth = Gdx.graphics.getWidth();
        float screenHeight = Gdx.graphics.getHeight();

        // Widths
        marginWidth = screenWidth * 0.05f;
        availableWidth = screenWidth - marginWidth * 2f;

        // Heights
        // logoHeight = screenHeight * 0.10f; // Unused
        scoreHeight = screenHeight * 0.15f;
        boardHeight = screenHeight * 0.50f;
        pieceHolderHeight = screenHeight * 0.25f;

        themeCardHeight = screenHeight * 0.15f;
    }

    //endregion

    //region Update layout methods

    // These methods take any of the custom objects used in the game
    // and positions them accordingly on the screen, by using relative
    // coordinates. Since these objects are not actors and we cannot
    // add them to a table (and would probably be harder), this approach
    // was used. Note that all these are using Y-up coordinates.
    void update(Scorer scorer) {
        float cupSize = Math.min(scoreHeight, scorer.cupTexture.getHeight());
        final Rectangle area = new Rectangle(
                marginWidth, pieceHolderHeight + boardHeight,
                availableWidth, scoreHeight);

        scorer.cupArea.set(
                area.x + area.width * 0.5f - cupSize * 0.5f, area.y,
                cupSize, cupSize);

        scorer.currentScoreLabel.setBounds(
                area.x, area.y,
                area.width * 0.5f - cupSize * 0.5f, area.height);

        scorer.highScoreLabel.setBounds(
                area.x + area.width * 0.5f + cupSize * 0.5f, area.y,
                area.width * 0.5f - cupSize * 0.5f, area.height);
    }

    void update(TimeScorer scorer) {
        float cupSize = Math.min(scoreHeight, scorer.cupTexture.getHeight());
        final Rectangle area = new Rectangle(
                marginWidth, pieceHolderHeight + boardHeight,
                availableWidth, scoreHeight);

        scorer.cupArea.set(
                area.x + area.width * 0.5f - cupSize * 0.5f, area.y,
                cupSize, cupSize);

        scorer.timeLeftLabel.setBounds(
                area.x, area.y,
                area.width * 0.5f - cupSize * 0.5f, area.height);

        scorer.highTimeLabel.setBounds(
                area.x + area.width * 0.5f + cupSize * 0.5f, area.y,
                area.width * 0.5f - cupSize * 0.5f, area.height);
    }

    void update(Board board) {
        // We can't leave our area, so pick the minimum between available
        // height and width to determine an appropriated cell size
        float boardSize = Math.min(availableWidth, boardHeight);
        board.cellSize = boardSize / board.cellCount;

        // Now that we know the board size, we can center the board on the screen
        board.pos.set(
                screenWidth * 0.5f - boardSize * 0.5f, pieceHolderHeight);
    }

    void update(PieceHolder holder) {
        holder.area.set(
                marginWidth, 0f,
                availableWidth, pieceHolderHeight);
    }

    public void update(Band band) {
        final Rectangle area = new Rectangle(
                0, pieceHolderHeight + boardHeight,
                screenWidth, scoreHeight);

        band.setBounds(area.x, area.y, area.width, area.height);
        // Let the band have the following shape:
        // 10% (100) padding
        // 35% (90%) score label
        // 10% (55%) padding
        // 35% (45%) info label
        // 10% (10%) padding
        band.scoreBounds.set(area.x, area.y + area.height * 0.55f, area.width, area.height * 0.35f);
        band.infoBounds.set(area.x, area.y + area.height * 0.10f, area.width, area.height * 0.35f);
    }

    public void update(ThemeCard card) {
        card.setSize(availableWidth - marginWidth, themeCardHeight);
        card.cellSize = themeCardHeight * 0.2f;

        // X offset from the cells (5 cells = themeCardHeight)
        card.nameBounds.set(
                themeCardHeight, card.cellSize,
                availableWidth - themeCardHeight, themeCardHeight);

        card.priceBounds.set(
                themeCardHeight, -card.cellSize,
                availableWidth - themeCardHeight, themeCardHeight);
    }

    //endregion
}
