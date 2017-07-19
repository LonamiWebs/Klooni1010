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
package io.github.lonamiwebs.klooni.game;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import io.github.lonamiwebs.klooni.Klooni;
import io.github.lonamiwebs.klooni.serializer.BinSerializable;

// Represents a single cell, with a position, size and color.
// Instances will use the cell texture provided by the currently used skin.
public class Cell implements BinSerializable {

    //region Members

    // Negative index indicates that the cell is empty
    private int colorIndex;

    public final Vector2 pos;
    public final float size;

    //endregion

    //region Constructor

    Cell(float x, float y, float cellSize) {
        pos = new Vector2(x, y);
        size = cellSize;

        colorIndex = -1;
    }

    //endregion

    //region Package local methods

    // Sets the cell to be non-empty and of the specified color index
    public void set(int ci) {
        colorIndex = ci;
    }

    public void draw(Batch batch) {
        // Always query the color to the theme, because it might have changed
        draw(Klooni.theme.getCellColor(colorIndex), batch, pos.x, pos.y, size);
    }

    public Color getColorCopy() {
        return Klooni.theme.getCellColor(colorIndex).cpy();
    }

    boolean isEmpty() {
        return colorIndex < 0;
    }

    //endregion

    //region Static methods

    // Default texture (don't call overloaded version to avoid overhead)
    public static void draw(final Color color, final Batch batch,
                            final float x, final float y, final float size) {
        batch.setColor(color);
        batch.draw(Klooni.theme.cellTexture, x, y, size, size);
    }

    // Custom texture
    public static void draw(final Texture texture, final Color color, final Batch batch,
                            final float x, final float y, final float size) {
        batch.setColor(color);
        batch.draw(texture, x, y, size, size);
    }

    //endregion

    //region Serialization

    @Override
    public void write(DataOutputStream out) throws IOException {
        // Only the color index is saved
        out.writeInt(colorIndex);
    }

    @Override
    public void read(DataInputStream in) throws IOException {
        colorIndex = in.readInt();
    }

    //endregion
}
