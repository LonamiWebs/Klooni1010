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
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

import io.github.lonamiwebs.klooni.Klooni;
import io.github.lonamiwebs.klooni.Theme;
import io.github.lonamiwebs.klooni.actors.MoneyBuyBand;
import io.github.lonamiwebs.klooni.actors.SoftButton;
import io.github.lonamiwebs.klooni.actors.ThemeCard;
import io.github.lonamiwebs.klooni.game.GameLayout;

// Screen where the user can customize the look and feel of the game
class CustomizeScreen implements Screen {

    //region Members

    private Klooni game;
    private Stage stage;

    private final Screen lastScreen;

    private float themeDragStartX, themeDragStartY;

    //endregion

    //region Static members

    // As the examples show on the LibGdx wiki
    private static final float MIN_DELTA = 1/30f;
    private static final float DRAG_LIMIT_SQ = 5*5;

    //endregion

    //region Constructor

    CustomizeScreen(Klooni game, final Screen lastScreen) {
        final GameLayout layout = new GameLayout();

        this.game = game;
        this.lastScreen = lastScreen;
        stage = new Stage();

        Table table = new Table();
        table.setFillParent(true);
        stage.addActor(table);

        HorizontalGroup optionsGroup = new HorizontalGroup();
        optionsGroup.space(12);

        // Back to the previous screen
        final SoftButton backButton = new SoftButton(1, "back_texture");
        backButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                goBack();
            }
        });
        optionsGroup.addActor(backButton);

        // Turn sound on/off
        final SoftButton soundButton = new SoftButton(
                0, Klooni.soundsEnabled() ? "sound_on_texture" : "sound_off_texture");

        soundButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                final boolean enabled = Klooni.toggleSound();
                soundButton.image = CustomizeScreen.this.game.skin.getDrawable(
                        enabled ? "sound_on_texture" : "sound_off_texture");
            }
        });
        optionsGroup.addActor(soundButton);

        // Snap to grid on/off
        final SoftButton snapButton = new SoftButton(
                2, Klooni.shouldSnapToGrid() ? "snap_on_texture" : "snap_off_texture");

        snapButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                final boolean shouldSnap = Klooni.toggleSnapToGrid();
                snapButton.image = CustomizeScreen.this.game.skin.getDrawable(
                        shouldSnap ? "snap_on_texture" : "snap_off_texture");
            }
        });
        optionsGroup.addActor(snapButton);

        // Issues
        final SoftButton issuesButton = new SoftButton(3, "issues_texture");
        issuesButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Gdx.net.openURI("https://github.com/LonamiWebs/Klooni1010/issues");
            }
        });
        optionsGroup.addActor(issuesButton);

        // Website
        final SoftButton webButton = new SoftButton(2, "web_texture");
        webButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Gdx.net.openURI("https://lonamiwebs.github.io");
            }
        });
        optionsGroup.addActor(webButton);

        // Use the same height as the buttons (for instance, the back button)
        table.add(new ScrollPane(optionsGroup))
                .pad(20, 4, 12, 4).height(backButton.getHeight());

        // Load all the available themes
        final MoneyBuyBand buyBand = new MoneyBuyBand(game);

        table.row();
        final VerticalGroup themesGroup = new VerticalGroup();
        for (Theme theme : Theme.getThemes()) {
            final ThemeCard card = new ThemeCard(game, layout, theme);
            card.addListener(new InputListener() {
                @Override
                public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                    themeDragStartX = x;
                    themeDragStartY = y;
                    return true;
                }

                // We could actually rely on touchDragged not being called,
                // but perhaps it would be hard for some people not to move
                // their fingers even the slightest bit, so we use a custom
                // drag limit

                @Override
                public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                    x -= themeDragStartX;
                    y -= themeDragStartY;
                    float distSq = x * x + y * y;
                    if (distSq < DRAG_LIMIT_SQ) {
                        if (Klooni.isThemeBought(card.theme))
                            card.use();
                        else
                            buyBand.askBuy(card);

                        for (Actor a : themesGroup.getChildren()) {
                            ThemeCard c = (ThemeCard)a;
                            c.usedThemeUpdated();
                        }
                    }
                }
            });
            themesGroup.addActor(card);
        }

        final ScrollPane themesScroll = new ScrollPane(themesGroup);
        table.add(themesScroll).expand().fill();

        // Show the current money row
        table.row();
        table.add(buyBand).expandX().fillX();

        // Scroll to the currently selected theme
        table.layout();
        for (Actor a : themesGroup.getChildren()) {
            ThemeCard c = (ThemeCard)a;
            if (c.isUsed()) {
                themesScroll.scrollTo(
                        c.getX(), c.getY() + c.getHeight(),
                        c.getWidth(), c.getHeight());
                break;
            }
            c.usedThemeUpdated();
        }
    }

    //endregion

    //region Private methods

    private void goBack() {
        CustomizeScreen.this.game.transitionTo(lastScreen);
    }

    //endregion

    //region Public methods

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void render(float delta) {
        Klooni.theme.glClearBackground();
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act(Math.min(Gdx.graphics.getDeltaTime(), MIN_DELTA));
        stage.draw();

        if (Gdx.input.isKeyJustPressed(Input.Keys.BACK)) {
            goBack();
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

    //region Empty methods

    @Override
    public void pause() { }

    @Override
    public void resume() { }

    @Override
    public void hide() { }

    //endregion
}
