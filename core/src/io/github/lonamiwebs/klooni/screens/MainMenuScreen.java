package io.github.lonamiwebs.klooni.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

import io.github.lonamiwebs.klooni.Klooni;

public class MainMenuScreen implements Screen {
    private Klooni game;

    Skin skin;
    Stage stage;
    SpriteBatch batch;

    public MainMenuScreen(Klooni aGame) {
        game = aGame;

        batch = new SpriteBatch();
        stage = new Stage();
        skin = new Skin(Gdx.files.internal("skin/uiskin.json"));

        Table table = new Table();
        table.setFillParent(true);
        stage.addActor(table);

        skin.add("button_up", new NinePatch(new Texture(
                Gdx.files.internal("ui/button_up.png")), 28, 28, 28, 28));

        skin.add("button_down", new NinePatch(new Texture(
                Gdx.files.internal("ui/button_down.png")), 28, 28, 28, 28));

        skin.add("play_texture", new Texture(Gdx.files.internal("ui/play.png")));
        skin.add("star_texture", new Texture(Gdx.files.internal("ui/star.png")));
        skin.add("stats_texture", new Texture(Gdx.files.internal("ui/stats.png")));
        skin.add("palette_texture", new Texture(Gdx.files.internal("ui/palette.png")));

        // Play button
        ImageButton.ImageButtonStyle playStyle = new ImageButton.ImageButtonStyle(
                skin.newDrawable("button_up", Color.GREEN), skin.newDrawable("button_down", Color.GREEN),
                null, skin.getDrawable("play_texture"), null, null);

        final ImageButton playButton = new ImageButton(playStyle);
        table.add(playButton).colspan(3).fill().space(16);

        playButton.addListener(new ChangeListener() {
            public void changed (ChangeEvent event, Actor actor) {
                game.setScreen(new GameScreen(game));
                dispose();
            }
        });

        table.row();

        // Star button (on GitHub)
        ImageButton.ImageButtonStyle starStyle = new ImageButton.ImageButtonStyle(
                skin.newDrawable("button_up", Color.YELLOW), skin.newDrawable("button_down", Color.YELLOW),
                null, skin.getDrawable("star_texture"), null, null);

        final ImageButton starButton = new ImageButton(starStyle);
        table.add(starButton).space(16);

        // Stats button (high scores)
        ImageButton.ImageButtonStyle statsStyle = new ImageButton.ImageButtonStyle(
                skin.newDrawable("button_up", Color.BLUE), skin.newDrawable("button_down", Color.BLUE),
                null, skin.getDrawable("stats_texture"), null, null);

        final ImageButton statsButton = new ImageButton(statsStyle);
        table.add(statsButton).space(16);

        // Palette button (buy colors)
        ImageButton.ImageButtonStyle paletteStyle = new ImageButton.ImageButtonStyle(
                skin.newDrawable("button_up", Color.FIREBRICK), skin.newDrawable("button_down", Color.FIREBRICK),
                null, skin.getDrawable("palette_texture"), null, null);

        final ImageButton paletteButton = new ImageButton(paletteStyle);
        table.add(paletteButton).space(16);
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
    }

    private static final float minDelta = 1/30f;

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.9f, 0.9f, 0.7f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act(Math.min(Gdx.graphics.getDeltaTime(), minDelta));
        stage.draw();
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
        skin.dispose();
    }
}
