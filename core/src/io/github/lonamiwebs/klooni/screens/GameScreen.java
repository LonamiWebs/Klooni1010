package io.github.lonamiwebs.klooni.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import io.github.lonamiwebs.klooni.Klooni;
import io.github.lonamiwebs.klooni.game.Board;
import io.github.lonamiwebs.klooni.game.GameLayout;
import io.github.lonamiwebs.klooni.game.Piece;
import io.github.lonamiwebs.klooni.game.PieceHolder;
import io.github.lonamiwebs.klooni.game.Scorer;

public class GameScreen implements Screen, InputProcessor {

    private final Scorer scorer;
    private Board board;
    private PieceHolder holder;

    private final GameLayout layout;

    private SpriteBatch batch;

    private final Color clearColor;

    private final PauseMenuStage pauseMenu;

    GameScreen(final Klooni game) {
        clearColor = new Color(0.9f, 0.9f, 0.7f, 1f);
        batch = new SpriteBatch();

        layout = new GameLayout();

        scorer = new Scorer(layout, 10);
        board = new Board(layout, 10);
        holder = new PieceHolder(layout, 3);

        pauseMenu = new PauseMenuStage(layout, game, scorer);
    }

    private boolean isGameOver() {
        for (Piece piece : holder.getAvailablePieces()) {
            if (board.canPutPiece(piece)) {
                return false;
            }
        }
        return true;
    }

    //region Screen

    @Override
    public void show() {
        Gdx.input.setInputProcessor(this);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(clearColor.r, clearColor.g, clearColor.b, clearColor.a);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.begin();

        scorer.draw(batch);
        board.draw(batch);
        holder.update(board.cellSize);
        holder.draw(batch, board.cellPatch);

        batch.end();

        if (pauseMenu.isShown() || pauseMenu.isHiding()) {
            pauseMenu.act(delta);
            pauseMenu.draw();
        }
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        pauseMenu.dispose();
    }

    //endregion

    //region Input

    @Override
    public boolean keyDown(int keycode) {
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        if (keycode == Input.Keys.P) // Pause
            pauseMenu.show(false);

        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        return holder.pickPiece();
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        int area = holder.calculateHeldPieceArea();
        if (holder.dropPiece(board)) {
            scorer.addPieceScore(area);
            scorer.addBoardScore(board.clearComplete());

            // After the piece was put, check if it's game over
            if (isGameOver()) {
                pauseMenu.show(true);
            }
            return true;
        } else {
            return false;
        }
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
