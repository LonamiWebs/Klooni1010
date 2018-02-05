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

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import io.github.lonamiwebs.klooni.interfaces.IEffect;
import io.github.lonamiwebs.klooni.interfaces.IEffectFactory;
import io.github.lonamiwebs.klooni.serializer.BinSerializable;

// Represents the on screen board, with all the put cells
// and functions to determine when it is game over given a PieceHolder
public class Board implements BinSerializable {

    //region Members

    public final int cellCount;
    public float cellSize;
    private Cell[][] cells;
    private final Array<IEffect> effects = new Array<IEffect>(); // Particle effects once they vanish

    public final Vector2 pos = new Vector2();

    // Used to animate cleared cells vanishing
    private final Vector2 lastPutPiecePos = new Vector2();

    //endregion

    //region Constructor

    public Board(final GameLayout layout, int cellCount) {
        this.cellCount = cellCount;

        // Cell size depends on the layout to be updated first
        layout.update(this);
        createCells();
    }

    public Board(final Rectangle area, int cellCount) {
        this.cellCount = cellCount;

        // Cell size depends on the layout to be updated first
        pos.set(area.x, area.y);
        cellSize = Math.min(area.height, area.width) / cellCount;
        createCells();
    }

    private void createCells() {
        cells = new Cell[this.cellCount][this.cellCount];
        for (int i = 0; i < this.cellCount; ++i) {
            for (int j = 0; j < this.cellCount; ++j) {
                cells[i][j] = new Cell(j * cellSize, i * cellSize, cellSize);
            }
        }
    }

    //endregion

    //region Private methods

    // True if the given cell coordinates are inside the bounds of the board
    private boolean inBounds(int x, int y) {
        return x >= 0 && x < cellCount && y >= 0 && y < cellCount;
    }

    // True if the given piece at the given coordinates is not outside the bounds of the board
    private boolean inBounds(Piece piece, int x, int y) {
        return inBounds(x, y) && inBounds(x + piece.cellCols - 1, y + piece.cellRows - 1);
    }

    // This only tests for the piece on the given coordinates, not the whole board
    private boolean canPutPiece(Piece piece, int x, int y) {
        if (!inBounds(piece, x, y))
            return false;

        for (int i = 0; i < piece.cellRows; ++i)
            for (int j = 0; j < piece.cellCols; ++j)
                if (!cells[y + i][x + j].isEmpty() && piece.filled(i, j))
                    return false;

        return true;
    }

    // Returns true iff the piece was put on the board
    public boolean putPiece(Piece piece, int x, int y) {
        if (!canPutPiece(piece, x, y))
            return false;

        lastPutPiecePos.set(piece.calculateGravityCenter());
        for (int i = 0; i < piece.cellRows; ++i)
            for (int j = 0; j < piece.cellCols; ++j)
                if (piece.filled(i, j))
                    cells[y + i][x + j].set(piece.colorIndex);

        return true;
    }

    //endregion

    //region Public methods

    public void draw(final Batch batch) {
        batch.setTransformMatrix(batch.getTransformMatrix().translate(pos.x, pos.y, 0));

        for (int i = 0; i < cellCount; ++i)
            for (int j = 0; j < cellCount; ++j)
                cells[i][j].draw(batch);

        for (int i = effects.size; i-- != 0; ) {
            effects.get(i).draw(batch);
            if (effects.get(i).isDone())
                effects.removeIndex(i);
        }

        batch.setTransformMatrix(batch.getTransformMatrix().translate(-pos.x, -pos.y, 0));
    }

    public boolean canPutPiece(Piece piece) {
        for (int i = 0; i < cellCount; ++i)
            for (int j = 0; j < cellCount; ++j)
                if (canPutPiece(piece, j, i))
                    return true;

        return false;
    }

    public boolean putScreenPiece(final Piece piece) {
        // Convert the on screen coordinates of the piece to the local-board-space coordinates
        // This is done by subtracting the piece coordinates from the board coordinates
        Vector2 local = piece.pos.cpy().sub(pos);
        int x = MathUtils.round(local.x / piece.cellSize);
        int y = MathUtils.round(local.y / piece.cellSize);
        return putPiece(piece, x, y);
    }

    Vector2 snapToGrid(final Piece piece, final Vector2 position) {
        // Snaps the given position (e.g. mouse) to the grid,
        // assuming piece wants to be put at the specified position.
        // If the piece was not on the grid, the original position is returned
        //
        // Logic to determine the x and y is a copy-paste from putScreenPiece
        final Vector2 local = position.cpy().sub(pos);
        int x = MathUtils.round(local.x / piece.cellSize);
        int y = MathUtils.round(local.y / piece.cellSize);
        if (canPutPiece(piece, x, y))
            return new Vector2(pos.x + x * piece.cellSize, pos.y + y * piece.cellSize);
        else
            return position;
    }

    // This will clear both complete rows and columns, all at once.
    // The reason why we can't check first rows and then columns
    // (or vice versa) is because the following case (* filled, _ empty):
    //
    // 4x4 boardHeight    piece
    // _ _ * *      * *
    // _ * * *      *
    // * * _ _
    // * * _ _
    //
    // If the piece is put on the top left corner, all the cells will be cleared.
    // If we first cleared the columns, then the rows wouldn't have been cleared.
    public int clearComplete(final IEffectFactory effect) {
        int clearCount = 0;
        boolean[] clearedRows = new boolean[cellCount];
        boolean[] clearedCols = new boolean[cellCount];

        // Analyze rows and columns that will be cleared
        for (int i = 0; i < cellCount; ++i) {
            clearedRows[i] = true;
            for (int j = 0; j < cellCount; ++j) {
                if (cells[i][j].isEmpty()) {
                    clearedRows[i] = false;
                    break;
                }
            }
            if (clearedRows[i])
                clearCount++;
        }
        for (int j = 0; j < cellCount; ++j) {
            clearedCols[j] = true;
            for (int i = 0; i < cellCount; ++i) {
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
            for (int i = 0; i < cellCount; ++i) {
                if (clearedRows[i]) {
                    for (int j = 0; j < cellCount; ++j) {
                        effects.add(effect.create(cells[i][j], lastPutPiecePos));
                        cells[i][j].set(-1);
                    }
                }
            }

            for (int j = 0; j < cellCount; ++j) {
                if (clearedCols[j]) {
                    for (int i = 0; i < cellCount; ++i) {
                        effects.add(effect.create(cells[i][j], lastPutPiecePos));
                        cells[i][j].set(-1);
                    }
                }
            }
        }

        return clearCount;
    }

    public void clearAll(final int clearFromX, final int clearFromY, final IEffectFactory effect) {
        final Vector2 culprit = cells[clearFromY][clearFromX].pos;

        for (int i = 0; i < cellCount; ++i) {
            for (int j = 0; j < cellCount; ++j) {
                if (!cells[i][j].isEmpty()) {
                    effects.add(effect.create(cells[i][j], culprit));
                    cells[i][j].set(-1);
                }
            }
        }
    }

    public boolean effectsDone() {
        return effects.size == 0;
    }

    //endregion

    //region Serialization

    @Override
    public void write(DataOutputStream out) throws IOException {
        // Cell count, cells in row-major order
        out.writeInt(cellCount);
        for (int i = 0; i < cellCount; ++i)
            for (int j = 0; j < cellCount; ++j)
                cells[i][j].write(out);
    }

    @Override
    public void read(DataInputStream in) throws IOException {
        // If the saved cell count does not match the current cell count,
        // then an IOException is thrown since the data saved was invalid
        final int savedCellCount = in.readInt();
        if (savedCellCount != cellCount)
            throw new IOException("Invalid cellCount saved.");

        for (int i = 0; i < cellCount; ++i)
            for (int j = 0; j < cellCount; ++j)
                cells[i][j].read(in);
    }

    //endregion
}
