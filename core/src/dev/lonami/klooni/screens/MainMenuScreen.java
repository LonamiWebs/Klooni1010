/*
    1010! Klooni, a free customizable puzzle game for Android and Desktop
    Copyright (C) 2017-2019  Lonami Exo @ lonami.dev

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
package dev.lonami.klooni.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ButtonGroup;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;
import com.badlogic.gdx.utils.Align;

import dev.lonami.klooni.Klooni;
import dev.lonami.klooni.SkinLoader;
import dev.lonami.klooni.Theme;
import dev.lonami.klooni.actors.SoftButton;

// Main menu screen, presenting some options (play, customize…)
public class MainMenuScreen extends InputListener implements Screen {

    //region Members

    private final Klooni game;
    private final Stage stage;
    //endregion

    //region Static members

    // As the examples show on the LibGdx wiki
    private static final float minDelta = 1 / 30f;
    final Texture cupTexture;
    final Image cupImage;
    final Label highScoreLabel;
    //endregion


    public MainMenuScreen(final Klooni game) {
        this.game = game;
        stage = new Stage();
        final Table table = new Table();
        int width = Gdx.graphics.getWidth();
        int height = Gdx.graphics.getHeight();
        table.setFillParent(true);
        stage.addActor(table);
        Label.LabelStyle labelStyle = new Label.LabelStyle();
        labelStyle.font = game.skin.getFont("font");
        Color colorScore = new Color(0xEBB53Eff);
        highScoreLabel = new Label(Integer.toString(Klooni.getMaxScore()), labelStyle);
        highScoreLabel.setColor(colorScore);
        cupTexture = SkinLoader.loadPng("cup.png");
        cupImage = new Image(cupTexture);
        Color color =  new Color(0xEBB53Eff);
        cupImage.setColor(color);
        table.add(cupImage).align(Align.top).colspan(2).space(0, 0, 16, 0).center();
        table.row();
        table.add(highScoreLabel).colspan(2).space(0, 0, height / 30, 0).center();
        table.row();
        // Play button
        final SoftButton playButton = new SoftButton(
                0, GameScreen.hasSavedData(10) ? "bg_10X10_s_texture" : "bg_10X10_texture");
        playButton.addListener(new ChangeListener() {
            public void changed(ChangeEvent event, Actor actor) {
                Klooni.setBoardSize(10);
                MainMenuScreen.this.game.transitionTo(
                        new GameScreen(MainMenuScreen.this.game, GameScreen.GAME_MODE_SCORE));
            }
        });
        table.add(playButton).minWidth((float) (width / 1.5)).colspan(2).fill().space(16);
        table.row();
        final SoftButton playButton15 = new SoftButton(
                1, GameScreen.hasSavedData(15) ? "bg_15X15_s_texture" : "bg_15X15_texture");
        playButton15.addListener(new ChangeListener() {
            public void changed(ChangeEvent event, Actor actor) {
                Klooni.setBoardSize(15);
                MainMenuScreen.this.game.transitionTo(
                        new GameScreen(MainMenuScreen.this.game, GameScreen.GAME_MODE_SCORE));
            }
        });
        table.add(playButton15).minWidth(width / 3).space(16);
        final SoftButton playButton20 = new SoftButton(
                2, GameScreen.hasSavedData(20) ? "bg_20X20_s_texture" : "bg_20X20_texture");
        playButton20.addListener(new ChangeListener() {
            public void changed(ChangeEvent event, Actor actor) {
                Klooni.setBoardSize(20);
                MainMenuScreen.this.game.transitionTo(
                        new GameScreen(MainMenuScreen.this.game, GameScreen.GAME_MODE_SCORE));
            }
        });
        table.add(playButton20).minWidth(width / 3).space(16);
        table.row();
        final SoftButton statsButton = new SoftButton(2, "stopwatch_texture");
        statsButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Klooni.setBoardSize(10);
                MainMenuScreen.this.game.transitionTo(
                        new GameScreen(MainMenuScreen.this.game, GameScreen.GAME_MODE_TIME));
            }
        });
        table.add(statsButton).minWidth((float) (width / 1.5)).colspan(2).fill().space(16);
        table.row();
        // Star button (on GitHub)
        final SoftButton starButton = new SoftButton(1, "star_texture");
        starButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.iActivityRequestHandler.inAppReview();
            }
        });
        table.add(starButton).minWidth(width / 3).space(16);
        final SoftButton paletteButton = new SoftButton(3, "palette_texture");
        paletteButton.addListener(new ChangeListener() {
            public void changed(ChangeEvent event, Actor actor) {
                MainMenuScreen.this.game.transitionTo(new CustomizeScreen(
                        MainMenuScreen.this.game, MainMenuScreen.this.game.getScreen()), false);

            }
        });
        table.add(paletteButton).minWidth(width / 3).space(16);
        table.row();
        if (game.iActivityRequestHandler.isAdAvaliable() & !Klooni.getIsRemove()) {
            final SoftButton adButton = new SoftButton(2, "bg_ad_texture");
            adButton.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    game.iActivityRequestHandler.removeAd(table, adButton);
                }
            });
            table.add(adButton).minWidth((float) (width / 1.5)).colspan(2).fill().space(16);
        }
    }


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
    public void pause() {
    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {
//        game.iActivityRequestHandler.hideBanner();
    }

    //endregion
}
