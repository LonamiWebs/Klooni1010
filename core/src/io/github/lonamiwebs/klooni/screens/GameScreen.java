package io.github.lonamiwebs.klooni.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;

import io.github.lonamiwebs.klooni.Klooni;
import io.github.lonamiwebs.klooni.game.Board;
import io.github.lonamiwebs.klooni.game.Piece;
import io.github.lonamiwebs.klooni.game.PieceHolder;

public class GameScreen implements Screen, InputProcessor {

    private Klooni game;
    private Board board;
    private PieceHolder holder;

    private SpriteBatch batch;

    private final Color clearColor;
    private int score;

    public GameScreen(Klooni aGame) {
        game = aGame;
        score = 0;
        clearColor = new Color(0.9f, 0.9f, 0.7f, 1f);

        // Board(x, y, cell cellCount, cell size, center)
        board = new Board(
                Gdx.graphics.getWidth() / 2,
                Gdx.graphics.getHeight() * 3 / 4,
                10, 20, true);

        // PieceHolder(pieces, x, y, w, h)
        int holderWidth = Gdx.graphics.getWidth() / 2;
        holder = new PieceHolder(3,
                Gdx.graphics.getWidth() / 2 - holderWidth / 2, Gdx.graphics.getHeight() / 4,
                Gdx.graphics.getWidth() / 2, 80);

        batch = new SpriteBatch();

        // Fill some random pieces
        for (int i = 0; i < 10; i++) {
            board.putPiece(Piece.random(), MathUtils.random(10), MathUtils.random(10));
        }
    }

    int calculateClearScore(int cleared) {
        // The original game seems to work as follows:
        // If < 1 were cleared, score = 0
        // If = 1  was cleared, score = cells cleared
        // If > 1 were cleared, score = cells cleared + score(cleared - 1)
        if (cleared < 1) return 0;
        if (cleared == 1) return board.cellCount;
        else return board.cellCount * cleared + calculateClearScore(cleared - 1);
    }

    boolean isGameOver() {
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
        board.draw(batch);

        holder.update(board.cellSize);
        holder.draw(batch, board.cellPatch);

        batch.end();
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

    }

    //endregion

    //region Input

    @Override
    public boolean keyDown(int keycode) {
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
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
            int cleared = board.clearComplete();
            score += area + calculateClearScore(cleared);

            if (isGameOver()) {
                clearColor.set(0.4f, 0.1f, 0.1f, 1f);
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
