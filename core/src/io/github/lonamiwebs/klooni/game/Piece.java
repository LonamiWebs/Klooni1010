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
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import io.github.lonamiwebs.klooni.Klooni;

// Represents a piece with an arbitrary shape, which
// can be either rectangles (squares too) or L shaped
// with any rotation.
public class Piece {

    //region Members

    final Vector2 pos;
    public final int colorIndex;
    private final int rotation;

    public final int cellCols, cellRows;
    private final boolean shape[][];

    // Default arbitrary value
    float cellSize = 10f;

    //endregion

    //region Constructors

    // Rectangle-shaped constructor
    //
    // If swapSize is true, the rows and columns will be swapped.
    // colorIndex represents a random index that will be used
    // to determine the color of this piece when drawn on the screen.
    private Piece(int cols, int rows, int rotateSizeBy, int colorIndex) {
        this.colorIndex = colorIndex;

        pos = new Vector2();
        rotation = rotateSizeBy % 2;
        cellCols = rotation == 1 ? rows : cols;
        cellRows = rotation == 1 ? cols : rows;

        shape = new boolean[cellRows][cellCols];
        for (int i = 0; i < cellRows; ++i) {
            for (int j = 0; j < cellCols; ++j) {
                shape[i][j] = true;
            }
        }
    }

    // L-shaped constructor
    private Piece(int lSize, int rotateCount, int colorIndex) {
        this.colorIndex = colorIndex;

        pos = new Vector2();
        cellCols = cellRows = lSize;
        shape = new boolean[lSize][lSize];

        rotation = rotateCount % 4;
        switch (rotation) {
            case 0: // ┌
                for (int j = 0; j < lSize; ++j)
                    shape[0][j] = true;
                for (int i = 0; i < lSize; ++i)
                    shape[i][0] = true;
                break;
            case 1: // ┐
                for (int j = 0; j < lSize; ++j)
                    shape[0][j] = true;
                for (int i = 0; i < lSize; ++i)
                    shape[i][lSize - 1] = true;
                break;
            case 2: // ┘
                for (int j = 0; j < lSize; ++j)
                    shape[lSize - 1][j] = true;
                for (int i = 0; i < lSize; ++i)
                    shape[i][lSize - 1] = true;
                break;
            case 3: // └
                for (int j = 0; j < lSize; ++j)
                    shape[lSize - 1][j] = true;
                for (int i = 0; i < lSize; ++i)
                    shape[i][0] = true;
                break;
        }
    }

    //endregion

    //region Static methods

    // Generates a random piece with always the same color for the generated shape
    public static Piece random() {
        // 9 pieces [0…8]; 4 possible rotations [0…3]
        return fromIndex(MathUtils.random(8), MathUtils.random(4));
    }

    private static Piece fromIndex(int colorIndex, int rotateCount) {
        switch (colorIndex) {
            // Squares
            case 0:
                return new Piece(1, 1, 0, colorIndex);
            case 1:
                return new Piece(2, 2, 0, colorIndex);
            case 2:
                return new Piece(3, 3, 0, colorIndex);

            // Lines
            case 3:
                return new Piece(1, 2, rotateCount, colorIndex);
            case 4:
                return new Piece(1, 3, rotateCount, colorIndex);
            case 5:
                return new Piece(1, 4, rotateCount, colorIndex);
            case 6:
                return new Piece(1, 5, rotateCount, colorIndex);

            // L's
            case 7:
                return new Piece(2, rotateCount, colorIndex);
            case 8:
                return new Piece(3, rotateCount, colorIndex);
        }
        throw new RuntimeException("Random function is broken.");
    }

    //endregion

    //region Package local methods

    void draw(SpriteBatch batch) {
        final Color c = Klooni.theme.getCellColor(colorIndex);
        for (int i = 0; i < cellRows; ++i)
            for (int j = 0; j < cellCols; ++j)
                if (shape[i][j])
                    Cell.draw(c, batch, pos.x + j * cellSize, pos.y + i * cellSize, cellSize);
    }

    // Calculates the rectangle of the piece with screen coordinates
    Rectangle getRectangle() {
        return new Rectangle(pos.x, pos.y, cellCols * cellSize, cellRows * cellSize);
    }

    // Determines whether the shape is filled on the given row and column
    boolean filled(int i, int j) {
        return shape[i][j];
    }

    // Calculates the area occupied by the shape
    int calculateArea() {
        int area = 0;
        for (int i = 0; i < cellRows; ++i) {
            for (int j = 0; j < cellCols; ++j) {
                if (shape[i][j]) {
                    area++;
                }
            }
        }
        return area;
    }

    // Calculates the gravity center of the piece shape
    Vector2 calculateGravityCenter() {
        int filledCount = 0;
        Vector2 result = new Vector2();
        for (int i = 0; i < cellRows; ++i) {
            for (int j = 0; j < cellCols; ++j) {
                if (shape[i][j]) {
                    filledCount++;
                    result.add(
                            pos.x + j * cellSize - cellSize * 0.5f,
                            pos.y + i * cellSize - cellSize * 0.5f);
                }
            }
        }
        return result.scl(1f / filledCount);
    }

    //endregion

    //region Serialization

    void write(DataOutputStream out) throws IOException {
        // colorIndex, rotation
        out.writeInt(colorIndex);
        out.writeInt(rotation);
    }

    static Piece read(DataInputStream in) throws IOException {
        return fromIndex(in.readInt(), in.readInt());
    }

    //endregion
}
