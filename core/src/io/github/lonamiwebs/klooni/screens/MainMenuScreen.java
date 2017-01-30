package io.github.lonamiwebs.klooni.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

import io.github.lonamiwebs.klooni.Klooni;
import io.github.lonamiwebs.klooni.actors.SoftButton;

public class MainMenuScreen extends InputListener implements Screen {
    private Klooni game;

    Stage stage;
    SpriteBatch batch;

    public MainMenuScreen(Klooni aGame) {
        game = aGame;

        batch = new SpriteBatch();
        stage = new Stage();

        Table table = new Table();
        table.setFillParent(true);
        stage.addActor(table);

        // Play button
        final ImageButton playButton = new SoftButton(0, "play_texture");
        playButton.addListener(new ChangeListener() {
            public void changed (ChangeEvent event, Actor actor) {
                game.setScreen(new GameScreen(game));
                dispose();
            }
        });
        table.add(playButton).colspan(3).fill().space(16);

        table.row();

        // Star button (on GitHub)
        final ImageButton starButton = new SoftButton(1, "star_texture");
        table.add(starButton).space(16);

        // Stats button (high scores)
        final ImageButton statsButton = new SoftButton(2, "stats_texture");
        table.add(statsButton).space(16);

        // Palette button (buy colors)
        final ImageButton paletteButton = new SoftButton(3, "palette_texture");
        paletteButton.addListener(new ChangeListener() {
            public void changed (ChangeEvent event, Actor actor) {
                game.setScreen(new CustomizeScreen(game, game.getScreen()));
                // Don't dispose because then it needs to take us to the previous screen
            }
        });
        table.add(paletteButton).space(16);
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
    }

    private static final float minDelta = 1/30f;

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
        stage.dispose();
    }
}
