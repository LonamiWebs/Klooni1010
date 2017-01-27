package io.github.lonamiwebs.klooni.game;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

// Pieces can be L shaped and be rotated 0 to 3 times to make it random
// Maximum cellSize = 4
public class Piece {

    private final static int[] colors = {
            0x7988bfff, 0x98dc53ff, 0x4cd4aeff,             // Squares
            0xfec63dff, 0xec9548ff, 0xe66a82ff, 0xda6554ff, // Lines
            0x57cb84ff, 0x5abee2ff                          // L's
    };

    final Vector2 pos;
    float cellSize = 10f; // Default

    final int cellCols, cellRows;
    private boolean shape[][];

    final Color color;

    private Piece(int cols, int rows, boolean swapSize, int colorIndex) {
        color = new Color(colors[colorIndex]);

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

    private Piece(int lSize, int rotateCount, int colorIndex) {
        color = new Color(colors[colorIndex]);

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

    boolean filled(int i, int j) {
        return shape[i][j];
    }

    public static Piece random() {
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

    Rectangle getRectangle() {
        return new Rectangle(pos.x, pos.y, cellCols * cellSize, cellRows * cellSize);
    }

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

    void draw(SpriteBatch batch, NinePatch patch) {
        for (int i = 0; i < cellRows; i++) {
            for (int j = 0; j < cellCols; j++) {
                if (shape[i][j]) {
                    Cell.draw(color, batch, patch,
                            pos.x + j * cellSize, pos.y + i * cellSize, cellSize);
                }
            }
        }
    }
}
