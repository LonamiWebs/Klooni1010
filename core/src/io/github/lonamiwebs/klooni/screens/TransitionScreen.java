package io.github.lonamiwebs.klooni.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;

import io.github.lonamiwebs.klooni.Klooni;

public class TransitionScreen implements Screen {

    //region Members

    // Rendering
    private FrameBuffer frameBuffer;
    private TextureRegion bufferTexture;
    private final SpriteBatch spriteBatch;
    private float fadedElapsed;
    private boolean fadingOut;
    private int width, height;

    // From, to, and game to change the screen after the transition finishes
    private final Screen fromScreen, toScreen;
    private final Klooni game;

    // Should the previous screen be disposed afterwards? Not desirable
    // if it was stored somewhere else, for example, to return to it later
    private final boolean disposeAfter;

    //endregion

    //region Static variables

    // Time it takes to fade out and in, 0.15s (0.3s total)
    private static final float FADE_INVERSE_DELAY = 1f / 0.15f;

    //endregion

    //region Constructor

    public TransitionScreen(Klooni game, Screen from, Screen to, boolean disposeAfter) {
        this.disposeAfter = disposeAfter;
        this.game = game;
        fromScreen = from;
        toScreen = to;

        spriteBatch = new SpriteBatch();
    }

    //endregion

    //region Rendering

    @Override
    public void show() {
        fadedElapsed = 0f;
        fadingOut = true;
    }

    @Override
    public void render(float delta) {
        // Black background since we're fading to black
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Render on another buffer so then we can set its opacity. This
        // second buffer also would allow us to do more stuff, since then
        // we can use a texture, which we could move across the screen.
        frameBuffer.begin();

        float opacity;
        if (fadingOut) {
            fromScreen.render(delta);
            opacity = 1 - Math.min(fadedElapsed * FADE_INVERSE_DELAY, 1);
            if (opacity == 0) {
                fadedElapsed = 0;
                fadingOut = false;
            }
        }
        else {
            toScreen.render(delta);
            opacity = Math.min(fadedElapsed * FADE_INVERSE_DELAY, 1);
        }

        frameBuffer.end();

        // Render the faded texture
        spriteBatch.begin();
        spriteBatch.setColor(1, 1, 1, opacity);
        spriteBatch.draw(bufferTexture, 0, 0, width, height);
        spriteBatch.end();
        fadedElapsed += delta;

        // We might have finished fading if the opacity is full
        if (opacity == 1 && !fadingOut) {
            game.setScreen(toScreen);
            dispose();
        }
    }

    @Override
    public void resize(int width, int height) {
        this.width = width;
        this.height = height;
        if (frameBuffer != null)
            frameBuffer.dispose();

        frameBuffer = new FrameBuffer(Pixmap.Format.RGB565, width, height, false);
        bufferTexture = new TextureRegion(frameBuffer.getColorBufferTexture());
        bufferTexture.flip(false, true);
    }

    //endregion

    //region Disposing

    @Override
    public void dispose() {
        frameBuffer.dispose();
        if (disposeAfter)
            fromScreen.dispose();
    }

    //endregion

    //region Unused methods

    @Override
    public void pause() { }

    @Override
    public void resume() { }

    @Override
    public void hide() { }

    //endregion
}
