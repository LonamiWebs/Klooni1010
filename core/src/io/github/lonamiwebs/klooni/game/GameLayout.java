package io.github.lonamiwebs.klooni.game;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Rectangle;

import io.github.lonamiwebs.klooni.actors.Band;

// Helper class to calculate the size of each element
//
// TODO In a future, perhaps this could handle landscape mode differently
// For example, the boardHeight on the left and the piece holder on the right
public class GameLayout {

    // Widths
    private float screenWidth, marginWidth, availableWidth;

    // Heights
    private float screenHeight, logoHeight, scoreHeight, boardHeight, pieceHolderHeight;

    public GameLayout() {
        calculate();
    }

    private void calculate() {
        screenWidth = Gdx.graphics.getWidth();
        screenHeight = Gdx.graphics.getHeight();

        // Widths
        marginWidth = screenWidth * 0.05f;
        availableWidth = screenWidth - marginWidth * 2f;

        // Heights
        logoHeight = screenHeight * 0.10f;
        scoreHeight = screenHeight * 0.15f;
        boardHeight = screenHeight * 0.50f;
        pieceHolderHeight = screenHeight * 0.25f;
    }

    // Note that we're now using Y-up coordinates
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

        scorer.maxScoreLabel.setBounds(
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
}
