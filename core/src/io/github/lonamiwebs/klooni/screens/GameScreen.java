/*
    1010! Klooni, a free customizable puzzle game for Android and Desktop
    Copyright (C) 2017  Lonami Exo | LonamiWebs

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/
package io.github.lonamiwebs.klooni.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import io.github.lonamiwebs.klooni.Klooni;
import io.github.lonamiwebs.klooni.game.BaseScorer;
import io.github.lonamiwebs.klooni.game.Board;
import io.github.lonamiwebs.klooni.game.BonusParticleHandler;
import io.github.lonamiwebs.klooni.game.GameLayout;
import io.github.lonamiwebs.klooni.game.Piece;
import io.github.lonamiwebs.klooni.game.PieceHolder;
import io.github.lonamiwebs.klooni.game.Scorer;
import io.github.lonamiwebs.klooni.game.TimeScorer;
import io.github.lonamiwebs.klooni.serializer.BinSerializable;
import io.github.lonamiwebs.klooni.serializer.BinSerializer;

// Main game screen. Here the board, piece holder and score are shown
class GameScreen implements Screen, InputProcessor, BinSerializable {

    //region Members

    private final Klooni game;
    private final BaseScorer scorer;
    private final BonusParticleHandler bonusParticleHandler;

    private final Board board;
    private final PieceHolder holder;

    private final SpriteBatch batch;
    private final Sound gameOverSound;

    private final PauseMenuStage pauseMenu;

    // TODO Perhaps make an abstract base class for the game screen and game modes
    // by implementing different "isGameOver" etc. logic instead using an integer?
    private final int gameMode;

    private boolean gameOverDone;

    // The last score that was saved when adding the money.
    // We use this so we don't add the same old score to the money twice,
    // but rather subtract it from the current score and then update it
    // with the current score to get the "increase" of money score.
    private int savedMoneyScore;

    //endregion

    //region Static members

    private final static int BOARD_SIZE = 10;
    private final static int HOLDER_PIECE_COUNT = 3;

    final static int GAME_MODE_SCORE = 0;
    final static int GAME_MODE_TIME = 1;

    private final static String SAVE_DAT_FILENAME = ".klooni.sav";

    //endregion

    //region Constructor

    // Load any previously saved file by default
    GameScreen(final Klooni game, final int gameMode) {
        this(game, gameMode, true);
    }

    GameScreen(final Klooni game, final int gameMode, final boolean loadSave) {
        batch = new SpriteBatch();
        this.game = game;
        this.gameMode = gameMode;

        final GameLayout layout = new GameLayout();
        switch (gameMode) {
            case GAME_MODE_SCORE:
                scorer = new Scorer(game, layout);
                break;
            case GAME_MODE_TIME:
                scorer = new TimeScorer(game, layout);
                break;
            default:
                throw new RuntimeException("Unknown game mode given: " + gameMode);
        }

        board = new Board(layout, BOARD_SIZE);
        holder = new PieceHolder(layout, board, HOLDER_PIECE_COUNT, board.cellSize);
        pauseMenu = new PauseMenuStage(layout, game, scorer, gameMode);
        bonusParticleHandler = new BonusParticleHandler(game);

        gameOverSound = Gdx.audio.newSound(Gdx.files.internal("sound/game_over.mp3"));

        if (gameMode == GAME_MODE_SCORE) {
            if (loadSave) {
                // The user might have a previous game. If this is the case, load it
                tryLoad();
            } else {
                // Ensure that there is no old save, we don't want to load it, thus delete it
                deleteSave();
            }
        }
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

    private void doGameOver(final String gameOverReason) {
        if (!gameOverDone) {
            gameOverDone = true;

            saveMoney();
            holder.enabled = false;
            pauseMenu.showGameOver(gameOverReason, scorer instanceof TimeScorer);
            if (Klooni.soundsEnabled())
                gameOverSound.play();

            // The user should not be able to return to the game if its game over
            if (gameMode == GAME_MODE_SCORE)
                deleteSave();
        }
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

    // Save the state, the user might leave the game in any of the following 2 methods
    private void showPauseMenu() {
        saveMoney();
        pauseMenu.show();
        save();
    }

    @Override
    public void pause() {
        save();
    }

    @Override
    public void render(float delta) {
        Klooni.theme.glClearBackground();
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if (scorer.isGameOver() && !pauseMenu.isShown()) {
            // TODO A bit hardcoded (timeOver = scorer instanceof TimeScorer)
            // Perhaps have a better mode to pass the required texture to overlay
            doGameOver(scorer.gameOverReason());
        }

        batch.begin();

        scorer.draw(batch);
        board.draw(batch);
        holder.update();
        holder.draw(batch);
        bonusParticleHandler.run(batch);

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
            showPauseMenu();

        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        return holder.pickPiece();
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        PieceHolder.DropResult result = holder.dropPiece();
        if (!result.dropped)
            return false;

        if (result.onBoard) {
            scorer.addPieceScore(result.area);
            int bonus = scorer.addBoardScore(board.clearComplete(game.effect), board.cellCount);
            if (bonus > 0) {
                bonusParticleHandler.addBonus(result.pieceCenter, bonus);
                if (Klooni.soundsEnabled()) {
                    game.playEffectSound();
                }
            }

            // After the piece was put, check if it's game over
            if (isGameOver()) {
                doGameOver("no moves left");
            }
        }
        return true;
    }

    //endregion

    //region Unused methods

    @Override
    public void resize(int width, int height) {
    }

    @Override
    public void resume() {
    }

    @Override
    public void hide() { /* Hide can only be called if the menu was shown. Place logic there. */ }

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

    //region Saving and loading

    private void saveMoney() {
        // Calculate new money since the previous saving
        int nowScore = scorer.getCurrentScore();
        int newMoneyScore = nowScore - savedMoneyScore;
        savedMoneyScore = nowScore;
        Klooni.addMoneyFromScore(newMoneyScore);
    }

    private void save() {
        // Only save if the game is not over and the game mode is not the time mode. It
        // makes no sense to save the time game mode since it's supposed to be something quick.
        // Don't save either if the score is 0, which means the player did nothing.
        if (gameOverDone || gameMode != GAME_MODE_SCORE || scorer.getCurrentScore() == 0)
            return;

        final FileHandle handle = Gdx.files.local(SAVE_DAT_FILENAME);
        try {
            BinSerializer.serialize(this, handle.write(false));
        } catch (IOException e) {
            // Should never happen but what else could be done if the game wasn't saved?
            e.printStackTrace();
        }
    }

    private void deleteSave() {
        final FileHandle handle = Gdx.files.local(SAVE_DAT_FILENAME);
        if (handle.exists())
            handle.delete();
    }

    static boolean hasSavedData() {
        return Gdx.files.local(SAVE_DAT_FILENAME).exists();
    }

    private boolean tryLoad() {
        final FileHandle handle = Gdx.files.local(SAVE_DAT_FILENAME);
        if (handle.exists()) {
            try {
                BinSerializer.deserialize(this, handle.read());
                // No cheating! We need to load the previous money
                // or it would seem like we earned it on this game
                savedMoneyScore = scorer.getCurrentScore();

                // After it's been loaded, delete the save file
                deleteSave();
                return true;
            } catch (IOException ignored) {
            }
        }
        return false;
    }

    @Override
    public void write(DataOutputStream out) throws IOException {
        // gameMode, board, holder, scorer
        out.writeInt(gameMode);
        board.write(out);
        holder.write(out);
        scorer.write(out);
    }

    @Override
    public void read(DataInputStream in) throws IOException {
        int savedGameMode = in.readInt();
        if (savedGameMode != gameMode)
            throw new IOException("A different game mode was saved. Cannot load the save data.");

        board.read(in);
        holder.read(in);
        scorer.read(in);
    }

    //endregion
}
