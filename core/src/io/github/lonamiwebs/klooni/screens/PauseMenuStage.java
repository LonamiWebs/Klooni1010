package io.github.lonamiwebs.klooni.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.RunnableAction;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

import io.github.lonamiwebs.klooni.Klooni;

public class PauseMenuStage extends Stage {

    private InputProcessor lastInputProcessor;
    private boolean shown;
    private boolean hiding;

    public PauseMenuStage(final Klooni game) {

        Table table = new Table();
        table.setFillParent(true);
        addActor(table);

        // Home screen button
        ImageButton.ImageButtonStyle homeStyle = new ImageButton.ImageButtonStyle(
                game.skin.newDrawable("button_up", Color.FIREBRICK),
                game.skin.newDrawable("button_down", Color.FIREBRICK),
                null, game.skin.getDrawable("home_texture"), null, null);

        final ImageButton homeButton = new ImageButton(homeStyle);
        table.add(homeButton).space(16);

        homeButton.addListener(new ChangeListener() {
            public void changed (ChangeEvent event, Actor actor) {
                game.setScreen(new MainMenuScreen(game));
                dispose();
            }
        });

        // Replay button
        ImageButton.ImageButtonStyle replayStyle = new ImageButton.ImageButtonStyle(
                game.skin.newDrawable("button_up", Color.GREEN),
                game.skin.newDrawable("button_down", Color.GREEN),
                null, game.skin.getDrawable("replay_texture"), null, null);

        final ImageButton replayButton = new ImageButton(replayStyle);
        table.add(replayButton).space(16);

        replayButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.setScreen(new GameScreen(game));
                dispose();
            }
        });

        table.row();

        // Palette button (buy colors)
        ImageButton.ImageButtonStyle paletteStyle = new ImageButton.ImageButtonStyle(
                game.skin.newDrawable("button_up", Color.YELLOW),
                game.skin.newDrawable("button_down", Color.YELLOW),
                null, game.skin.getDrawable("palette_texture"), null, null);

        final ImageButton paletteButton = new ImageButton(paletteStyle);
        table.add(paletteButton).space(16);

        // Continue playing OR share (if game over) button
        // TODO Enable both actions for this button
        ImageButton.ImageButtonStyle playStyle = new ImageButton.ImageButtonStyle(
                game.skin.newDrawable("button_up", Color.BLUE),
                game.skin.newDrawable("button_down", Color.BLUE),
                null, game.skin.getDrawable("play_texture"), null, null);

        final ImageButton playButton = new ImageButton(playStyle);
        table.add(playButton).space(16);

        playButton.addListener(new ChangeListener() {
            public void changed (ChangeEvent event, Actor actor) {
                hide();
            }
        });
    }

    public void show() {
        lastInputProcessor = Gdx.input.getInputProcessor();
        Gdx.input.setInputProcessor(this);
        shown = true;
        hiding = false;

        addAction(Actions.moveTo(0, Gdx.graphics.getHeight()));
        addAction(Actions.moveTo(0, 0, 0.75f, Interpolation.swingOut));
    }

    public void hide() {
        shown = false;
        hiding = true;
        Gdx.input.setInputProcessor(lastInputProcessor);

        addAction(Actions.sequence(
                Actions.moveTo(0, Gdx.graphics.getHeight(), 0.5f, Interpolation.swingIn),
                new RunnableAction() {
                    @Override
                    public void run() {
                        hiding = false;
                    }
                }
        ));
    }

    public boolean isShown() {
        return shown;
    }

    public boolean isHiding() {
        return hiding;
    }

    @Override
    public void draw() {
        // Draw an overlay rectangle with not all the opacity
        if (shown) {
            ShapeRenderer shapeRenderer = new ShapeRenderer(20);

            Gdx.gl.glEnable(GL20.GL_BLEND);
            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            shapeRenderer.setColor(1f, 1f, 1f, 0.3f);
            shapeRenderer.rect(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
            shapeRenderer.end();
        }

        super.draw();
    }

    @Override
    public boolean keyUp(int keyCode) {
        if (keyCode == Input.Keys.P) // Pause
            hide();

        return super.keyUp(keyCode);
    }
}
