package io.github.lonamiwebs.klooni.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import io.github.lonamiwebs.klooni.Klooni;
import io.github.lonamiwebs.klooni.game.Board;
import io.github.lonamiwebs.klooni.game.GameLayout;
import io.github.lonamiwebs.klooni.game.Piece;
import io.github.lonamiwebs.klooni.game.PieceHolder;
import io.github.lonamiwebs.klooni.game.Scorer;

// Main game screen. Here the board, piece holder and score are shown
class GameScreen implements Screen, InputProcessor {

    //region Members

    private final Scorer scorer;
    private final Board board;
    private final PieceHolder holder;

    private final SpriteBatch batch;
    private final Sound gameOverSound;

    private final PauseMenuStage pauseMenu;

    //endregion

    //region Static members

    private final static int BOARD_SIZE = 10;
    private final static int HOLDER_PIECE_COUNT = 3;

    //endregion

    //region Constructor

    GameScreen(final Klooni game) {
        batch = new SpriteBatch();

        final GameLayout layout = new GameLayout();
        scorer = new Scorer(layout);
        board = new Board(layout, BOARD_SIZE);
        holder = new PieceHolder(layout, HOLDER_PIECE_COUNT, board.cellSize);
        pauseMenu = new PauseMenuStage(layout, game, scorer);

        gameOverSound = Gdx.audio.newSound(Gdx.files.internal("sound/game_over.mp3"));
    }

    //endregion

    //region Private methods

    // If no piece can be put, then it is considered to be game over
    private boolean isGameOver() {
        for (Piece piece : holder.getAvailablePieces())
            if (board.canPutPiece(piece))
                return false;

        return true;
    }

    //endregion

    //region Screen

    @Override
    public void show() {
        if (pauseMenu.isShown()) // Will happen if we go to the customize menu
            Gdx.input.setInputProcessor(pauseMenu);
        else
            Gdx.input.setInputProcessor(this);
    }

    @Override
    public void render(float delta) {
        Klooni.theme.glClearBackground();
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.begin();

        scorer.draw(batch);
        board.draw(batch);
        holder.update();
        holder.draw(batch);

        batch.end();

        if (pauseMenu.isShown() || pauseMenu.isHiding()) {
            pauseMenu.act(delta);
            pauseMenu.draw();
        }
    }

    @Override
    public void dispose() {
        pauseMenu.dispose();
    }

    //endregion

    //region Input

    @Override
    public boolean keyUp(int keycode) {
        if (keycode == Input.Keys.P || keycode == Input.Keys.BACK) // Pause
            pauseMenu.show(false);

        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        return holder.pickPiece();
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        int area = holder.calculateHeldPieceArea();
        int action = holder.dropPiece(board);
        if (action == PieceHolder.NO_DROP)
            return false;

        if (action == PieceHolder.ON_BOARD_DROP) {
            scorer.addPieceScore(area);
            scorer.addBoardScore(board.clearComplete(), board.cellCount);

            // After the piece was put, check if it's game over
            if (isGameOver()) {
                pauseMenu.show(true);
                if (Klooni.soundsEnabled())
                    gameOverSound.play();
            }
        }
        return true;
    }

    //endregion

    //region Unused methods

    @Override
    public void resize(int width, int height) { }

    @Override
    public void pause() { }

    @Override
    public void resume() { }

    @Override
    public void hide() { }

    @Override
    public boolean keyDown(int keycode) {
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        return false;
    }

    //endregion
}
