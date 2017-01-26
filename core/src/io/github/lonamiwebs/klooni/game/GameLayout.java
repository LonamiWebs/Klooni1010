package io.github.lonamiwebs.klooni.game;


import com.badlogic.gdx.Gdx;

// Helper class to calculate the size of each element
//
// TODO In a future, perhaps this could handle landscape mode differently
// For example, the boardHeight on the left and the piece holder on the right
public class GameLayout {

    // Widths
    private float screenWidth, marginWidth;

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

        // Heights
        logoHeight = screenHeight * 0.10f;
        scoreHeight = screenHeight * 0.15f;
        boardHeight = screenHeight * 0.50f;
        pieceHolderHeight = screenHeight * 0.25f;
    }

    // Note that we're now using Y-up coordinates
    void update(Board board) {
        // We can't leave our area, so pick the minimum between available
        // height and width to determine an appropriated cell size
        float availableWidth = screenWidth - marginWidth * 2f;
        float boardSize = Math.min(availableWidth, boardHeight);
        board.cellSize = boardSize / board.cellCount;

        // Now that we know the board size, we can center the board on the screen
        board.pos.set(screenWidth * 0.5f - boardSize * 0.5f, pieceHolderHeight);
    }

    void update(PieceHolder holder) {
        float availableWidth = screenWidth - marginWidth * 2f;
        holder.area.set(
                marginWidth, 0f,
                availableWidth, pieceHolderHeight);
    }
}
