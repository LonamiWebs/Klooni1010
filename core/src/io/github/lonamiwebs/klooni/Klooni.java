package io.github.lonamiwebs.klooni;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

import io.github.lonamiwebs.klooni.screens.MainMenuScreen;

public class Klooni extends Game {

    public static Skin skin;

    @Override
    public void create() {
        //skin = new Skin(Gdx.files.internal("skin/craftacular-ui.json"));
        setScreen(new MainMenuScreen(this));
    }

    @Override
    public void render() {
        super.render();
    }

    @Override
    public void dispose() {
        super.dispose();
    }
}
