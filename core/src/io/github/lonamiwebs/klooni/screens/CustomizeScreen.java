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
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.SnapshotArray;

import io.github.lonamiwebs.klooni.Klooni;
import io.github.lonamiwebs.klooni.Theme;
import io.github.lonamiwebs.klooni.actors.EffectCard;
import io.github.lonamiwebs.klooni.actors.MoneyBuyBand;
import io.github.lonamiwebs.klooni.actors.ShopCard;
import io.github.lonamiwebs.klooni.actors.SoftButton;
import io.github.lonamiwebs.klooni.actors.ThemeCard;
import io.github.lonamiwebs.klooni.game.GameLayout;
import io.github.lonamiwebs.klooni.interfaces.IEffectFactory;

// Screen where the user can customize the look and feel of the game
class CustomizeScreen implements Screen {

    //region Members

    private final Klooni game;
    private final Stage stage;

    private final Screen lastScreen;

    private final Table table;
    private final SoftButton toggleShopButton;
    private final VerticalGroup shopGroup; // Showing available themes or effects
    private final ScrollPane shopScroll;
    private final MoneyBuyBand buyBand;

    private boolean showingEffectsShop;
    private int showcaseIndex;

    private float shopDragStartX, shopDragStartY;

    //endregion

    //region Static members

    // As the examples show on the LibGdx wiki
    private static final float MIN_DELTA = 1 / 30f;
    private static final float DRAG_LIMIT_SQ = 20 * 20;

    //endregion

    //region Constructor

    CustomizeScreen(Klooni game, final Screen lastScreen) {
        this.game = game;
        this.lastScreen = lastScreen;
        stage = new Stage();

        table = new Table();
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

                buyBand.setTempText("sound " + (enabled ? "on" : "off"));
            }
        });
        optionsGroup.addActor(soundButton);

        // Toggle the current shop (themes or effects)
        toggleShopButton = new SoftButton(2, "effects_texture");
        toggleShopButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                showingEffectsShop = !showingEffectsShop;
                if (showingEffectsShop) {
                    toggleShopButton.updateImage("palette_texture");
                } else {
                    toggleShopButton.updateImage("effects_texture");
                }
                loadShop();
            }
        });
        optionsGroup.addActor(toggleShopButton);

        // Snap to grid on/off
        final SoftButton snapButton = new SoftButton(
                1, Klooni.shouldSnapToGrid() ? "snap_on_texture" : "snap_off_texture");

        snapButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                final boolean shouldSnap = Klooni.toggleSnapToGrid();
                snapButton.image = CustomizeScreen.this.game.skin.getDrawable(
                        shouldSnap ? "snap_on_texture" : "snap_off_texture");

                buyBand.setTempText("snap to grid " + (shouldSnap ? "on" : "off"));
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

        buyBand = new MoneyBuyBand(game);
        table.row();

        // Load all the available themes as the default "shop"
        shopGroup = new VerticalGroup();
        shopScroll = new ScrollPane(shopGroup);
        table.add(shopScroll).expand().fill();
        loadShop();

        // Show the current money row
        table.row();
        table.add(buyBand).expandX().fillX();
    }

    //endregion

    //region Private methods

    private void goBack() {
        CustomizeScreen.this.game.transitionTo(lastScreen);
    }

    private void loadShop() {
        showcaseIndex = 0; // Reset the index

        final GameLayout layout = new GameLayout();
        shopGroup.clear();

        if (showingEffectsShop)
            for (IEffectFactory effect : Klooni.EFFECTS)
                addCard(new EffectCard(game, layout, effect));

        else // showingThemesShop
            for (Theme theme : Theme.getThemes())
                addCard(new ThemeCard(game, layout, theme));

        // Scroll to the currently selected item
        table.layout();
        for (Actor a : shopGroup.getChildren()) {
            ShopCard c = (ShopCard) a;
            if (c.isUsed()) {
                shopScroll.scrollTo(
                        c.getX(), c.getY() + c.getHeight(),
                        c.getWidth(), c.getHeight());
                break;
            }
            c.usedItemUpdated();
        }
    }

    private void addCard(final ShopCard card) {
        card.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                shopDragStartX = x;
                shopDragStartY = y;
                return true;
            }

            // We could actually rely on touchDragged not being called,
            // but perhaps it would be hard for some people not to move
            // their fingers even the slightest bit, so we use a custom
            // drag limit

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                x -= shopDragStartX;
                y -= shopDragStartY;
                float distSq = x * x + y * y;
                if (distSq < DRAG_LIMIT_SQ) {
                    if (card.isBought())
                        card.use();
                    else
                        buyBand.askBuy(card);

                    for (Actor a : shopGroup.getChildren()) {
                        ((ShopCard) a).usedItemUpdated();
                    }
                }
            }
        });

        shopGroup.addActor(card);
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

        // After everything is drawn, showcase the current shop item
        SnapshotArray<Actor> children = shopGroup.getChildren();
        if (children.size > 0) {
            final ShopCard card = (ShopCard) children.get(showcaseIndex);

            final Batch batch = stage.getBatch();
            batch.begin();
            // For some really strange reason, we need to displace the particle effect
            // by "buyBand.height", or it will render exactly that height below where
            // it should.
            // TODO Fix this - maybe use the same project matrix as stage.draw()?
            // batch.setProjectionMatrix(stage.getViewport().getCamera().combined)
            if (!card.showcase(batch, buyBand.getHeight())) {
                showcaseIndex = (showcaseIndex + 1) % children.size;
            }
            batch.end();
        }

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
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void hide() {
    }

    //endregion
}
