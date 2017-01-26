package io.github.lonamiwebs.klooni.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

public class Board {

    Cell[][] cells;
    private final int count; // Cell count
    public final int cellSize; // Size per cell

    final Vector2 pos;

    public NinePatch cellPatch;

    public Board(float x, float y, int cellCount, int cellSize, boolean center) {
        pos = new Vector2(x, y);
        count = cellCount;
        this.cellSize = cellSize;

        cells = new Cell[count][count];
        for (int i = 0; i < count; i++) {
            for (int j = 0; j < count; j++) {
                cells[i][j] = new Cell();
            }
        }

        cellPatch = new NinePatch(
                new Texture(Gdx.files.internal("ui/cells/basic.png")), 4, 4, 4, 4);

        if (center) {
            float half = getPxSize() / 2;
            pos.sub(half, half);
        }
    }

    public int getPxSize() {
        return count * cellSize;
    }

    private boolean inBounds(int x, int y) {
        return x >= 0 && x < count && y >= 0 && y < count;
    }

    private boolean inBounds(Piece piece, int x, int y) {
        return inBounds(x, y) && inBounds(x + piece.cellCols - 1, y + piece.cellRows - 1);
    }

    private boolean canPutPiece(Piece piece, int x, int y) {
        if (!inBounds(piece, x, y))
            return false;

        for (int i = 0; i < piece.cellRows; i++)
            for (int j = 0; j < piece.cellCols; j++)
                if (!cells[y+i][x+j].isEmpty() && piece.filled(i, j))
                    return false;

        return true;
    }

    public boolean putScreenPiece(Piece piece) {
        // Get the local piece coordinates
        // TODO Works weird, it puts the piece like one too low…
        Vector2 local = piece.pos.cpy().sub(pos);
        int x = MathUtils.round(local.x / piece.cellSize);
        int y = MathUtils.round(local.y / piece.cellSize);
        return putPiece(piece, x, y);
    }

    public int clearComplete() {
        // This will clear both complete rows and columns, all at once.
        // The reason why we can't check first rows and then columns
        // (or vice versa) is because the following case (* filled, _ empty):
        //
        // 4x4 board    piece
        // _ _ * *      * *
        // _ * * *      *
        // * * _ _
        // * * _ _
        //
        // If the piece is put on the top left corner, all the cells will be cleared.
        // If we first cleared the columns, then the rows wouldn't have been cleared.
        int clearCount = 0;
        boolean[] clearedRows = new boolean[count];
        boolean[] clearedCols = new boolean[count];

        // Analyze rows and columns that will be cleared
        for (int i = 0; i < count; i++) {
            clearedRows[i] = true;
            for (int j = 0; j < count; j++) {
                if (cells[i][j].isEmpty()) {
                    clearedRows[i] = false;
                    break;
                }
            }
            if (clearedRows[i])
                clearCount++;
        }
        for (int j = 0; j < count; j++) {
            clearedCols[j] = true;
            for (int i = 0; i < count; i++) {
                if (cells[i][j].isEmpty()) {
                    clearedCols[j] = false;
                    break;
                }
            }
            if (clearedCols[j])
                clearCount++;
        }
        if (clearCount > 0) {
            // Do clear those rows and columns
            for (int i = 0; i < count; i++) {
                if (clearedRows[i]) {
                    for (int j = 0; j < count; j++) {
                        cells[i][j].setEmpty();
                    }
                }
            }
            for (int j = 0; j < count; j++) {
                if (clearedCols[j]) {
                    for (int i = 0; i < count; i++) {
                        cells[i][j].setEmpty();
                    }
                }
            }
        }

        return clearCount;
    }

    public boolean putPiece(Piece piece, int x, int y) {
        if (!canPutPiece(piece, x, y))
            return false;

        for (int i = 0; i < piece.cellRows; i++) {
            for (int j = 0; j < piece.cellCols; j++) {
                if (piece.filled(i, j)) {
                    cells[y+i][x+j].set(piece.color);
                }
            }
        }

        return true;
    }

    public void draw(SpriteBatch batch) {
        for (int i = 0; i < count; i++) {
            for (int j = 0; j < count; j++) {
                cells[i][j].draw(batch, cellPatch,
                        pos.x + j * cellSize, pos.y + i * cellSize, cellSize);
            }
        }
    }
}
