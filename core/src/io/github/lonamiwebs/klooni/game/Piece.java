package io.github.lonamiwebs.klooni.game;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

import io.github.lonamiwebs.klooni.Klooni;

// Represents a piece with an arbitrary shape, which
// can be either rectangles (squares too) or L shaped
// with any rotation.
public class Piece {

    //region Members

    final Vector2 pos;
    final Color color;

    final int cellCols, cellRows;
    private boolean shape[][];

    // Default arbitrary value
    float cellSize = 10f;

    //endregion

    //region Constructors

    // Rectangle-shaped constructor
    //
    // If swapSize is true, the rows and columns will be swapped.
    // colorIndex represents a random index that will be used
    // to determine the color of this piece when drawn on the screen.
    private Piece(int cols, int rows, boolean swapSize, int colorIndex) {
        color = Klooni.theme.getCellColor(colorIndex);

        pos = new Vector2();
        cellCols = swapSize ? rows : cols;
        cellRows = swapSize ? cols : rows;
        shape = new boolean[cellRows][cellCols];
        for (int i = 0; i < cellRows; i++) {
            for (int j = 0; j < cellCols; j++) {
                shape[i][j] = true;
            }
        }
    }

    // L-shaped constructor
    private Piece(int lSize, int rotateCount, int colorIndex) {
        color = Klooni.theme.getCellColor(colorIndex);

        pos = new Vector2();
        cellCols = cellRows = lSize;
        shape = new boolean[lSize][lSize];
        switch (rotateCount % 4) {
            case 0: // ┌
                for (int j = 0; j < lSize; j++)
                    shape[0][j] = true;
                for (int i = 0; i < lSize; i++)
                    shape[i][0] = true;
                break;
            case 1: // ┐
                for (int j = 0; j < lSize; j++)
                    shape[0][j] = true;
                for (int i = 0; i < lSize; i++)
                    shape[i][lSize-1] = true;
                break;
            case 2: // ┘
                for (int j = 0; j < lSize; j++)
                    shape[lSize-1][j] = true;
                for (int i = 0; i < lSize; i++)
                    shape[i][lSize-1] = true;
                break;
            case 3: // └
                for (int j = 0; j < lSize; j++)
                    shape[lSize-1][j] = true;
                for (int i = 0; i < lSize; i++)
                    shape[i][0] = true;
                break;
        }
    }

    //endregion

    //region Static methods

    // Generates a random piece with always the same color for the generated shape
    static Piece random() {
        int color = MathUtils.random(8); // 9 pieces
        switch (color) {
            // Squares
            case 0: return new Piece(1, 1, false, color);
            case 1: return new Piece(2, 2, false, color);
            case 2: return new Piece(3, 3, false, color);

            // Lines
            case 3: return new Piece(1, 2, MathUtils.randomBoolean(), color);
            case 4: return new Piece(1, 3, MathUtils.randomBoolean(), color);
            case 5: return new Piece(1, 4, MathUtils.randomBoolean(), color);
            case 6: return new Piece(1, 5, MathUtils.randomBoolean(), color);

            // L's
            case 7: return new Piece(2, MathUtils.random(3), color);
            case 8: return new Piece(3, MathUtils.random(3), color);
        }
        throw new RuntimeException("Random function is broken.");
    }

    //endregion

    //region Package local methods

    void draw(SpriteBatch batch) {
        for (int i = 0; i < cellRows; i++)
            for (int j = 0; j < cellCols; j++)
                if (shape[i][j])
                    Cell.draw(color, batch, pos.x + j * cellSize, pos.y + i * cellSize, cellSize);
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
        for (int i = 0; i < cellRows; i++) {
            for (int j = 0; j < cellCols; j++) {
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
        for (int i = 0; i < cellRows; i++) {
            for (int j = 0; j < cellCols; j++) {
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
}
