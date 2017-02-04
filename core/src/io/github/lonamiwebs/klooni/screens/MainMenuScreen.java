package io.github.lonamiwebs.klooni.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

import io.github.lonamiwebs.klooni.Klooni;
import io.github.lonamiwebs.klooni.actors.SoftButton;

// Main menu screen, presenting some options (play, customize…)
public class MainMenuScreen extends InputListener implements Screen {

    //region Members

    private final Klooni game;
    private final Stage stage;

    //endregion

    //region Static members

    // As the examples show on the LibGdx wiki
    private static final float minDelta = 1/30f;

    //endregion

    //region Constructor

    public MainMenuScreen(Klooni game) {
        this.game = game;

        stage = new Stage();

        Table table = new Table();
        table.setFillParent(true);
        stage.addActor(table);

        // Play button
        final SoftButton playButton = new SoftButton(0, "play_texture");
        playButton.addListener(new ChangeListener() {
            public void changed (ChangeEvent event, Actor actor) {
                MainMenuScreen.this.game.setScreen(
                        new GameScreen(MainMenuScreen.this.game, GameScreen.GAME_MODE_SCORE));
                dispose();
            }
        });
        table.add(playButton).colspan(3).fill().space(16);

        table.row();

        // Star button (on GitHub)
        final SoftButton starButton = new SoftButton(1, "star_texture");
        starButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Gdx.net.openURI("https://github.com/LonamiWebs/Klooni1010");
            }
        });
        table.add(starButton).space(16);

        // Stats button (high scores)
        final SoftButton statsButton = new SoftButton(2, "stats_texture");
        // TODO For testing purposes, open the time mode
        statsButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                MainMenuScreen.this.game.setScreen(
                        new GameScreen(MainMenuScreen.this.game, GameScreen.GAME_MODE_TIME));
                dispose();
            }
        });
        table.add(statsButton).space(16);

        // Palette button (buy colors)
        final SoftButton paletteButton = new SoftButton(3, "palette_texture");
        paletteButton.addListener(new ChangeListener() {
            public void changed (ChangeEvent event, Actor actor) {
                MainMenuScreen.this.game.setScreen(new CustomizeScreen(MainMenuScreen.this.game, MainMenuScreen.this.game.getScreen()));
                // Don't dispose because then it needs to take us to the previous screen
            }
        });
        table.add(paletteButton).space(16);
    }

    //endregion

    //region Screen

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void render(float delta) {
        Klooni.theme.glClearBackground();
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act(Math.min(Gdx.graphics.getDeltaTime(), minDelta));
        stage.draw();

        if (Gdx.input.isKeyJustPressed(Input.Keys.BACK)) {
            Gdx.app.exit();
        }
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void dispose() {
        stage.dispose();
    }

    //endregion

    //region Unused methods

    @Override
    public void pause() { }

    @Override
    public void resume() { }

    @Override
    public void hide() { }

    //endregion
}
