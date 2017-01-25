package io.github.lonamiwebs.klooni.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
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

    public GameScreen(Klooni aGame) {
        game = aGame;
        board = new Board(10, 20);

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

    //region Screen

    @Override
    public void show() {
        Gdx.input.setInputProcessor(this);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.9f, 0.9f, 0.7f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.begin();
        board.draw(batch,
                Gdx.graphics.getWidth() / 2 - board.getPxSize() / 2,
                Gdx.graphics.getHeight() * 3 / 4 - board.getPxSize() / 2);

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
        return holder.dropPiece();
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
