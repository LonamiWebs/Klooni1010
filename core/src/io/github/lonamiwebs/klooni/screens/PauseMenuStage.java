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
import io.github.lonamiwebs.klooni.actors.SoftButton;
import io.github.lonamiwebs.klooni.game.GameLayout;
import io.github.lonamiwebs.klooni.game.Scorer;

// The pause stage is not a whole screen but rather a menu
// which can be overlaid on top of another screen
class PauseMenuStage extends Stage {

    //region Members

    private InputProcessor lastInputProcessor;
    private boolean shown;
    private boolean hiding;

    private final ShapeRenderer shapeRenderer;

    private final Band band;
    private final Scorer scorer;

    //endregion

    //region Constructor

    // We need the score to save the maximum score if a new record was beaten
    PauseMenuStage(final GameLayout layout, final Klooni game, final Scorer scorer) {
        this.scorer = scorer;

        shapeRenderer = new ShapeRenderer(20); // 20 vertex seems to be enough for a rectangle

        Table table = new Table();
        table.setFillParent(true);
        addActor(table);

        // Current and maximum score band.
        // Do not add it to the table not to over-complicate things.
        band = new Band(layout, this.scorer, Color.SKY);
        addActor(band);

        // Home screen button
        final ImageButton homeButton = new SoftButton(3, "home_texture");
        table.add(homeButton).space(16);

        homeButton.addListener(new ChangeListener() {
            public void changed (ChangeEvent event, Actor actor) {
                game.setScreen(new MainMenuScreen(game));
                dispose();
            }
        });

        // Replay button
        final ImageButton replayButton = new SoftButton(0, "replay_texture");
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
        final ImageButton paletteButton = new SoftButton(1, "palette_texture");
        table.add(paletteButton).space(16);

        paletteButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.setScreen(new CustomizeScreen(game, game.getScreen()));
                // Don't dispose because then it needs to take us to the previous screen
            }
        });

        // Continue playing OR share (if game over) button
        // TODO Enable both actions for this button? Or leave play?
        final ImageButton playButton = new SoftButton(2, "play_texture");
        table.add(playButton).space(16);

        playButton.addListener(new ChangeListener() {
            public void changed (ChangeEvent event, Actor actor) {
                hide();
            }
        });
    }

    //endregion

    //region Private methods

    // Hides the pause menu, setting back the previous input processor
    private void hide() {
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

    //endregion

    //region Package local methods

    // Shows the pause menu, indicating whether it's game over or not
    void show(final boolean gameOver) {
        scorer.saveScore();

        // Save the last input processor so then we can return the handle to it
        lastInputProcessor = Gdx.input.getInputProcessor();
        Gdx.input.setInputProcessor(this);
        shown = true;
        hiding = false;

        if (gameOver)
            band.setGameOver();

        addAction(Actions.moveTo(0, Gdx.graphics.getHeight()));
        addAction(Actions.moveTo(0, 0, 0.75f, Interpolation.swingOut));
    }

    boolean isShown() {
        return shown;
    }

    boolean isHiding() {
        return hiding;
    }

    //endregion

    //region Public methods

    @Override
    public void draw() {
        if (shown) {
            // Draw an overlay rectangle with not all the opacity
            // This is the only place where ShapeRenderer is OK because the batch hasn't started
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

    //endregion
}
