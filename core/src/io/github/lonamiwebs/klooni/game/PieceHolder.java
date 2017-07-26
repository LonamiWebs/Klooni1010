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

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import io.github.lonamiwebs.klooni.Klooni;
import io.github.lonamiwebs.klooni.serializer.BinSerializable;

// A holder of pieces that can be drawn on screen.
// Pieces can be picked up from it and dropped on a board.
public class PieceHolder implements BinSerializable {

    //region Members

    final Rectangle area;
    private final Piece[] pieces;

    private final Sound pieceDropSound;
    private final Sound invalidPieceDropSound;
    private final Sound takePiecesSound;

    // Count of pieces to be shown
    private final int count;

    // Currently held piece index (picked by the user)
    private int heldPiece;
    public boolean enabled;

    // Needed after a piece is dropped, so it can go back
    private final Rectangle[] originalPositions;

    // The size the cells will adopt once picked
    private final float pickedCellSize;

    // Every piece holder belongs to a specific board
    private final Board board;

    //endregion

    //region Static members

    private static final float DRAG_SPEED = 0.5f; // Interpolation value ((pos -> new) / frame)

    //endregion

    //region Constructor

    public PieceHolder(final GameLayout layout, final Board board,
                       final int pieceCount, final float pickedCellSize) {
        this.board = board;
        enabled = true;
        count = pieceCount;
        pieces = new Piece[count];
        originalPositions = new Rectangle[count];

        pieceDropSound = Gdx.audio.newSound(Gdx.files.internal("sound/piece_drop.mp3"));
        invalidPieceDropSound = Gdx.audio.newSound(Gdx.files.internal("sound/invalid_drop.mp3"));
        takePiecesSound = Gdx.audio.newSound(Gdx.files.internal("sound/take_pieces.mp3"));

        heldPiece = -1;
        this.pickedCellSize = pickedCellSize;

        area = new Rectangle();
        layout.update(this);

        // takeMore depends on the layout to be ready
        // TODO So, how would pieces handle a layout update?
        takeMore();
    }

    //endregion

    //region Private methods

    // Determines whether all the pieces have been put (and the "hand" is finished)
    private boolean handFinished() {
        for (int i = 0; i < count; ++i)
            if (pieces[i] != null)
                return false;

        return true;
    }

    // Takes a new set of pieces. Should be called when there are no more piece left
    private void takeMore() {
        for (int i = 0; i < count; ++i)
            pieces[i] = Piece.random();
        updatePiecesStartLocation();

        if (Klooni.soundsEnabled()) {
            // Random pitch so it's not always the same sound
            takePiecesSound.play(1, MathUtils.random(0.8f, 1.2f), 0);
        }
    }

    private void updatePiecesStartLocation() {
        float perPieceWidth = area.width / count;
        Piece piece;
        for (int i = 0; i < count; ++i) {
            piece = pieces[i];
            if (piece == null)
                continue;

            // Set the absolute position on screen and the cells' cellSize
            // Also clamp the cell size to be the picked size as maximum, or
            // it would be too big in some cases.
            piece.pos.set(area.x + i * perPieceWidth, area.y);
            piece.cellSize = Math.min(Math.min(
                    perPieceWidth / piece.cellCols,
                    area.height / piece.cellRows), pickedCellSize);

            // Center the piece on the X and Y axes. For this we see how
            // much up we can go, this is, (area.height - piece.height) / 2
            Rectangle rectangle = piece.getRectangle();
            piece.pos.y += (area.height - rectangle.height) * 0.5f;
            piece.pos.x += (perPieceWidth - rectangle.width) * 0.5f;

            originalPositions[i] = new Rectangle(
                    piece.pos.x, piece.pos.y,
                    piece.cellSize, piece.cellSize);

            // Now that we have the original positions, reset the size so it animates and grows
            piece.cellSize = 0f;
        }
    }

    //endregion

    //region Public methods

    // Picks the piece below the finger/mouse, returning true if any was picked
    public boolean pickPiece() {
        Vector2 mouse = new Vector2(
                Gdx.input.getX(),
                Gdx.graphics.getHeight() - Gdx.input.getY()); // Y axis is inverted

        final float perPieceWidth = area.width / count;
        for (int i = 0; i < count; ++i) {
            if (pieces[i] != null) {
                Rectangle maxPieceArea = new Rectangle(
                        area.x + i * perPieceWidth, area.y, perPieceWidth, area.height);

                if (maxPieceArea.contains(mouse)) {
                    heldPiece = i;
                    return true;
                }
            }
        }

        heldPiece = -1;
        return false;
    }

    public Array<Piece> getAvailablePieces() {
        Array<Piece> result = new Array<Piece>(count);
        for (int i = 0; i < count; ++i)
            if (pieces[i] != null)
                result.add(pieces[i]);

        return result;
    }

    // If no piece is currently being held, the area will be 0
    private int calculateHeldPieceArea() {
        return heldPiece > -1 ? pieces[heldPiece].calculateArea() : 0;
    }

    private Vector2 calculateHeldPieceCenter() {
        return heldPiece > -1 ? pieces[heldPiece].calculateGravityCenter() : null;
    }

    // Tries to drop the piece on the given board. As a result, it
    // returns one of the following: NO_DROP, NORMAL_DROP, ON_BOARD_DROP
    public DropResult dropPiece() {
        DropResult result;

        if (heldPiece > -1) {
            boolean put;
            put = enabled && board.putScreenPiece(pieces[heldPiece]);
            if (put) {
                if (Klooni.soundsEnabled()) {
                    // The larger the piece size, the smaller the pitch
                    // Considering 10 cells to be the largest, 1.1 highest pitch, 0.7 lowest
                    float pitch = 1.104f - pieces[heldPiece].calculateArea() * 0.04f;
                    pieceDropSound.play(1, pitch, 0);
                }

                result = new DropResult(calculateHeldPieceArea(), calculateHeldPieceCenter());
                pieces[heldPiece] = null;
            } else {
                if (Klooni.soundsEnabled())
                    invalidPieceDropSound.play();

                result = new DropResult(true);
            }

            heldPiece = -1;
            if (handFinished())
                takeMore();
        } else
            result = new DropResult(false);

        return result;
    }

    // Updates the state of the piece holder (and the held piece)
    public void update() {
        Piece piece;
        if (heldPiece > -1) {
            piece = pieces[heldPiece];

            Vector2 mouse = new Vector2(
                    Gdx.input.getX(),
                    Gdx.graphics.getHeight() - Gdx.input.getY()); // Y axis is inverted

            if (Klooni.onDesktop) { //FIXME(oliver): This is a bad assumption to make. There are desktops with touch input and non-desktops with mouse input.
                // Center the piece to the mouse
                mouse.sub(piece.getRectangle().width * 0.5f, piece.getRectangle().height * 0.5f);
            } else {
                // Center the new piece position horizontally
                // and push it up by it's a cell (arbitrary) vertically, thus
                // avoiding to cover it with the finger (issue on Android devices)
                mouse.sub(piece.getRectangle().width * 0.5f, -pickedCellSize);
            }
            if (Klooni.shouldSnapToGrid())
                mouse.set(board.snapToGrid(piece, mouse));

            piece.pos.lerp(mouse, DRAG_SPEED);
            piece.cellSize = Interpolation.linear.apply(piece.cellSize, pickedCellSize, DRAG_SPEED);
        }

        // Return the pieces to their original position
        // TODO This seems somewhat expensive, can't it be done any better?
        Rectangle original;
        for (int i = 0; i < count; ++i) {
            if (i == heldPiece)
                continue;

            piece = pieces[i];
            if (piece == null)
                continue;

            original = originalPositions[i];
            piece.pos.lerp(new Vector2(original.x, original.y), 0.3f);
            piece.cellSize = Interpolation.linear.apply(piece.cellSize, original.width, 0.3f);
        }
    }

    public void draw(SpriteBatch batch) {
        for (int i = 0; i < count; ++i) {
            if (pieces[i] != null) {
                pieces[i].draw(batch);
            }
        }
    }

    //endregion

    //region Serialization

    @Override
    public void write(DataOutputStream out) throws IOException {
        // Piece count, false if piece == null, true + piece if piece != null
        out.writeInt(count);
        for (int i = 0; i < count; ++i) {
            if (pieces[i] == null) {
                out.writeBoolean(false);
            } else {
                out.writeBoolean(true);
                pieces[i].write(out);
            }
        }
    }

    @Override
    public void read(DataInputStream in) throws IOException {
        // If the saved piece count does not match the current piece count,
        // then an IOException is thrown since the data saved was invalid
        final int savedPieceCount = in.readInt();
        if (savedPieceCount != count)
            throw new IOException("Invalid piece count saved.");

        for (int i = 0; i < count; i++)
            pieces[i] = in.readBoolean() ? Piece.read(in) : null;
        updatePiecesStartLocation();
    }

    //endregion

    //region Sub-classes

    public class DropResult {

        public final boolean dropped;
        public final boolean onBoard;

        public final int area;
        public final Vector2 pieceCenter;

        DropResult(final boolean dropped) {
            this.dropped = dropped;
            onBoard = false;
            area = 0;
            pieceCenter = null;
        }

        DropResult(final int area, final Vector2 pieceCenter) {
            dropped = onBoard = true;
            this.area = area;
            this.pieceCenter = pieceCenter;
        }
    }

    //endregion
}
