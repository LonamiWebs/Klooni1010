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
package io.github.lonamiwebs.klooni;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.PixmapIO;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.BufferUtils;
import com.badlogic.gdx.utils.ScreenUtils;

import java.io.File;

public abstract class ShareChallenge {

    // Meant to return the file path to which the image will be saved
    // On some platforms it might be as simple as Gdx.files.local().file()
    abstract File getShareImageFilePath();

    // Meant to share the saved screenshot at getShareImageFilePath()
    public abstract void shareScreenshot(final boolean saveResult);

    // Saves the "Challenge me" shareable image to getShareImageFilePath()
    public boolean saveChallengeImage(final int score, final boolean timeMode) {
        final File saveAt = getShareImageFilePath();
        if (!saveAt.getParentFile().isDirectory())
            if (!saveAt.mkdirs())
                return false;

        final FileHandle output = new FileHandle(saveAt);

        final Texture shareBase = new Texture(Gdx.files.internal("share.png"));
        final int width = shareBase.getWidth();
        final int height = shareBase.getHeight();

        final FrameBuffer frameBuffer = new FrameBuffer(Pixmap.Format.RGB888, width, height, false);
        frameBuffer.begin();

        // Render the base share texture
        final SpriteBatch batch = new SpriteBatch();
        final Matrix4 matrix = new Matrix4();
        matrix.setToOrtho2D(0, 0, width, height);
        batch.setProjectionMatrix(matrix);

        Gdx.gl.glClearColor(Color.GOLD.r, Color.GOLD.g, Color.GOLD.b, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.begin();
        batch.draw(shareBase, 0, 0);

        // Render the achieved score
        final Label.LabelStyle style = new Label.LabelStyle();
        style.font = new BitmapFont(Gdx.files.internal("font/x1.0/geosans-light64.fnt"));
        Label label = new Label("just scored " + score + " on", style);
        label.setColor(Color.BLACK);
        label.setPosition(40, 500);
        label.draw(batch, 1);

        label.setText("try to beat me if you can");
        label.setPosition(40, 40);
        label.draw(batch, 1);

        if (timeMode) {
            Texture timeModeTexture = new Texture("ui/x1.5/stopwatch.png");
            batch.setColor(Color.BLACK);
            batch.draw(timeModeTexture, 200, 340);
        }

        batch.end();

        // Get the framebuffer pixels and write them to a local file
        final byte[] pixels = ScreenUtils.getFrameBufferPixels(0, 0, width, height, true);

        final Pixmap pixmap = new Pixmap(width, height, Pixmap.Format.RGBA8888);

        BufferUtils.copy(pixels, 0, pixmap.getPixels(), pixels.length);
        PixmapIO.writePNG(output, pixmap);

        // Dispose everything
        pixmap.dispose();
        shareBase.dispose();
        batch.dispose();
        frameBuffer.end();

        return true;
    }
}
