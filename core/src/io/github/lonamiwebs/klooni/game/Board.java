package io.github.lonamiwebs.klooni.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class Board {

    Cell[][] cells;
    private final int count; // Cell count
    private final int size; // Size per cell

    private NinePatch cellPatch;

    public Board(int boardSize, int cellSize) {
        count = boardSize;
        size = cellSize;

        cells = new Cell[count][count];
        for (int i = 0; i < count; i++) {
            for (int j = 0; j < count; j++) {
                cells[i][j] = new Cell();
            }
        }

        cellPatch = new NinePatch(
                new Texture(Gdx.files.internal("ui/cells/basic.png")), 4, 4, 4, 4);
    }

    private boolean inBounds(int x, int y) {
        return x >= 0 && x < count && y >= 0 && y < count;
    }

    private boolean inBounds(Piece piece, int x, int y) {
        return inBounds(x, y) && inBounds(x + piece.width, y + piece.height - 1);
    }

    private boolean canPutPiece(Piece piece, int x, int y) {
        if (!inBounds(piece, x, y))
            return false;

        for (int i = 0; i < piece.height; i++)
            for (int j = 0; j < piece.width; j++)
                if (!cells[y+i][x+j].isEmpty() && piece.filled(i, j))
                    return false;

        return true;
    }

    public boolean putPiece(Piece piece, int x, int y) {
        if (!canPutPiece(piece, x, y))
            return false;

        for (int i = 0; i < piece.height; i++)
            for (int j = 0; j < piece.width; j++)
                cells[y+i][x+j].set(piece.color);

        return true;
    }

    public void draw(SpriteBatch batch) {
        for (int i = 0; i < count; i++)
            for (int j = 0; j < count; j++)
                cells[i][j].draw(batch, cellPatch, j * size, i * size, size);
    }
}
