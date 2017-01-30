package io.github.lonamiwebs.klooni.screens;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

import io.github.lonamiwebs.klooni.Klooni;
import io.github.lonamiwebs.klooni.Theme;
import io.github.lonamiwebs.klooni.actors.SoftButton;
import io.github.lonamiwebs.klooni.actors.ThemeCard;
import io.github.lonamiwebs.klooni.game.GameLayout;

public class CustomizeScreen implements Screen {
    private Klooni game;

    private Stage stage;

    public CustomizeScreen(Klooni aGame, final Screen lastScreen) {
        final GameLayout layout = new GameLayout();

        game = aGame;
        stage = new Stage();

        Table table = new Table();
        table.setFillParent(true);
        stage.addActor(table);

        HorizontalGroup optionsGroup = new HorizontalGroup();
        optionsGroup.space(12);

        // Back to the previous screen
        final ImageButton backButton = new SoftButton(1, "back_texture");
        backButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.setScreen(lastScreen);
                dispose();
            }
        });
        optionsGroup.addActor(backButton);

        // Turn sound on/off
        final ImageButton soundButton = new SoftButton(
                0, Klooni.soundsEnabled() ? "sound_on_texture" : "sound_off_texture");

        soundButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Klooni.toggleSound();
                soundButton.getStyle().imageUp = game.skin.getDrawable(
                        Klooni.soundsEnabled() ? "sound_on_texture" : "sound_off_texture");
            }
        });
        optionsGroup.addActor(soundButton);

        // Issues
        final ImageButton issuesButton = new SoftButton(3, "issues_texture");
        issuesButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Gdx.net.openURI("https://github.com/LonamiWeb/Klooni1010/issues");
            }
        });
        optionsGroup.addActor(issuesButton);

        // Website
        final ImageButton webButton = new SoftButton(2, "web_texture");
        webButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Gdx.net.openURI("https://lonamiwebs.github.io");
            }
        });
        optionsGroup.addActor(webButton);

        table.add(new ScrollPane(optionsGroup)).pad(20, 4, 12, 4);
        table.row();

        VerticalGroup themesGroup = new VerticalGroup();
        for (Theme theme : Theme.getThemes()) {
            final ThemeCard card = new ThemeCard(layout, theme);
            card.addListener(new InputListener() {
                @Override
                public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                    Klooni.updateTheme(card.theme);
                    return true;
                }
            });
            themesGroup.addActor(card);
        }

        themesGroup.space(8);
        table.add(new ScrollPane(themesGroup)).expandY();
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
