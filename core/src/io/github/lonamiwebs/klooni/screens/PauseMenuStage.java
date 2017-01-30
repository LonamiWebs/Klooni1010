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
import io.github.lonamiwebs.klooni.actors.Band;
import io.github.lonamiwebs.klooni.game.GameLayout;
import io.github.lonamiwebs.klooni.game.Scorer;

public class PauseMenuStage extends Stage {

    private InputProcessor lastInputProcessor;
    private boolean shown;
    private boolean hiding;

    private final Band band;
    private final Scorer scorer;

    public PauseMenuStage(final GameLayout layout, final Klooni game, final Scorer aScorer) {
        scorer = aScorer;

        Table table = new Table();
        table.setFillParent(true);
        addActor(table);

        // Current and maximum score band.
        // Do not add it to the table not to over-complicate things.
        band = new Band(layout, scorer, Color.SKY);
        addActor(band);

        // Home screen button
        final ImageButton homeButton = new ImageButton(Klooni.theme.getStyle(game.skin, 3, "home_texture"));
        table.add(homeButton).space(16);

        homeButton.addListener(new ChangeListener() {
            public void changed (ChangeEvent event, Actor actor) {
                game.setScreen(new MainMenuScreen(game));
                dispose();
            }
        });

        // Replay button
        final ImageButton replayButton = new ImageButton(Klooni.theme.getStyle(game.skin, 0, "replay_texture"));
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
        final ImageButton paletteButton = new ImageButton(Klooni.theme.getStyle(game.skin, 1, "palette_texture"));
        table.add(paletteButton).space(16);

        paletteButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.setScreen(new CustomizeScreen(game, game.getScreen()));
                // Don't dispose because then it needs to take us to the previous screen
            }
        });

        // Continue playing OR share (if game over) button
        // TODO Enable both actions for this button
        final ImageButton playButton = new ImageButton(Klooni.theme.getStyle(game.skin, 2, "play_texture"));
        table.add(playButton).space(16);

        playButton.addListener(new ChangeListener() {
            public void changed (ChangeEvent event, Actor actor) {
                hide();
            }
        });
    }

    void show(final boolean gameOver) {
        scorer.saveScore();

        lastInputProcessor = Gdx.input.getInputProcessor();
        Gdx.input.setInputProcessor(this);
        shown = true;
        hiding = false;

        if (gameOver)
            band.setGameOver();

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
        if (keyCode == Input.Keys.P || keyCode == Input.Keys.BACK) // Pause
            hide();

        return super.keyUp(keyCode);
    }
}
