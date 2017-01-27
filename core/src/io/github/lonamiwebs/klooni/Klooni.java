package io.github.lonamiwebs.klooni;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

import io.github.lonamiwebs.klooni.screens.MainMenuScreen;

public class Klooni extends Game {

    public Skin skin;

    @Override
    public void create() {
        // TODO Better way to have this skin somewhere
        // Gotta create that darn .json…!
        skin = new Skin(Gdx.files.internal("skin/uiskin.json"));

        skin.add("button_up", new NinePatch(new Texture(
                Gdx.files.internal("ui/button_up.png")), 28, 28, 28, 28));

        skin.add("button_down", new NinePatch(new Texture(
                Gdx.files.internal("ui/button_down.png")), 28, 28, 28, 28));

        skin.add("play_texture", new Texture(Gdx.files.internal("ui/play.png")));
        skin.add("star_texture", new Texture(Gdx.files.internal("ui/star.png")));
        skin.add("stats_texture", new Texture(Gdx.files.internal("ui/stats.png")));
        skin.add("palette_texture", new Texture(Gdx.files.internal("ui/palette.png")));
        skin.add("home_texture", new Texture(Gdx.files.internal("ui/home.png")));
        skin.add("replay_texture", new Texture(Gdx.files.internal("ui/replay.png")));
        skin.add("share_texture", new Texture(Gdx.files.internal("ui/share.png")));

        setScreen(new MainMenuScreen(this));
    }

    @Override
    public void render() {
        super.render();
    }

    @Override
    public void dispose() {
        super.dispose();
        skin.dispose();
    }
}
