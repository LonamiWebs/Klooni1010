package dev.lonami.klooni.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import dev.lonami.klooni.SkinLoader;

public class SplashScreen implements Screen {
    private SpriteBatch batch;
    private Texture ttrSplash;
 
    public SplashScreen() {
        super();
        batch = new SpriteBatch();
        ttrSplash = SkinLoader.loadPng("splash.png");
    }
 
    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(255, 255, 255, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
 
        batch.begin();
        batch.draw(ttrSplash, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        batch.end();
    }
 
    @Override
    public void hide() { }
 
    @Override
    public void pause() { }
 
    @Override
    public void resume() { }
    
    @Override
    public void show() { }
    
    @Override
    public void resize(int width, int height) { }
 
    @Override
    public void dispose() {
        ttrSplash.dispose();
        batch.dispose();
    }
}
