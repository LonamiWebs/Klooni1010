package io.github.lonamiwebs.klooni.screens;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;

import io.github.lonamiwebs.klooni.Klooni;

public class CustomizeScreen implements Screen {
    private Klooni game;

    private Stage stage;

    public CustomizeScreen(Klooni aGame, final Screen lastScreen) {
        game = aGame;
        stage = new Stage();

        Table table = new Table();
        table.setFillParent(true);
        stage.addActor(table);

        HorizontalGroup optionsGroup = new HorizontalGroup();
        optionsGroup.space(12);

        // Back to the previous screen
        ImageButton.ImageButtonStyle backStyle = new ImageButton.ImageButtonStyle(
                game.skin.newDrawable("button_up", Color.GOLD),
                game.skin.newDrawable("button_down", Color.GOLD),
                null, game.skin.getDrawable("back_texture"), null, null);

        final ImageButton backButton = new ImageButton(backStyle);
        optionsGroup.addActor(backButton);
        backButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.setScreen(lastScreen);
                dispose();
            }
        });

        // Turn sound on/off
        Drawable soundDrawable = game.skin.getDrawable(
                Klooni.soundsEnabled() ? "sound_on_texture" : "sound_off_texture");

        ImageButton.ImageButtonStyle soundStyle = new ImageButton.ImageButtonStyle(
                game.skin.newDrawable("button_up", Color.LIME),
                game.skin.newDrawable("button_down", Color.LIME),
                null, soundDrawable, null, null);

        final ImageButton soundButton = new ImageButton(soundStyle);
        optionsGroup.addActor(soundButton);
        soundButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Klooni.toggleSound();
                soundButton.getStyle().imageUp = game.skin.getDrawable(
                        Klooni.soundsEnabled() ? "sound_on_texture" : "sound_off_texture");
            }
        });

        // Issues
        ImageButton.ImageButtonStyle issuesStyle = new ImageButton.ImageButtonStyle(
                game.skin.newDrawable("button_up", Color.FIREBRICK),
                game.skin.newDrawable("button_down", Color.FIREBRICK),
                null, game.skin.getDrawable("issues_texture"), null, null);

        final ImageButton issuesButton = new ImageButton(issuesStyle);
        optionsGroup.addActor(issuesButton);
        issuesButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Gdx.net.openURI("https://github.com/LonamiWeb/Klooni1010/issues");
            }
        });

        // Website
        ImageButton.ImageButtonStyle webStyle = new ImageButton.ImageButtonStyle(
                game.skin.newDrawable("button_up", new Color(0x6E99FFFF)),
                game.skin.newDrawable("button_down", new Color(0x6E99FFFF)),
                null, game.skin.getDrawable("web_texture"), null, null);

        final ImageButton webButton = new ImageButton(webStyle);
        optionsGroup.addActor(webButton);
        webButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Gdx.net.openURI("https://lonamiwebs.github.io");
            }
        });

        table.add(new ScrollPane(optionsGroup)).pad(20, 4, 12, 4);
        table.row();

        VerticalGroup themesGroup = new VerticalGroup();
        themesGroup.space(8);

        table.add(themesGroup).expandY();
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
    }

    private static final float minDelta = 1/30f;

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.7f, 0.9f, 0.9f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act(Math.min(Gdx.graphics.getDeltaTime(), minDelta));
        stage.draw();

        if (Gdx.input.isKeyJustPressed(Input.Keys.BACK)) {
            game.setScreen(new MainMenuScreen(game));
            dispose();
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
