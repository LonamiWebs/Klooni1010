package io.github.lonamiwebs.klooni.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public class PieceHolder {

    final Piece[] pieces;
    final int count;

    int heldPiece;

    final Rectangle area;

    public PieceHolder(final GameLayout layout, final int pieceCount) {
        count = pieceCount;
        pieces = new Piece[count];
        heldPiece = -1;

        area = new Rectangle();
        layout.update(this);

        // takeMore depends on the layout to be ready
        // TODO So, how would pieces handle a layout update?
        takeMore();
    }

    void takeMore() {
        float perPieceSize = area.width / count;
        for (int i = 0; i < count; i++) {
            pieces[i] = Piece.random();

            // Set the absolute position on screen and the cells' cellSize
            pieces[i].pos.set(area.x + i * perPieceSize, area.y);
            pieces[i].cellSize = Math.min(
                    perPieceSize / pieces[i].cellCols,
                    area.height / pieces[i].cellRows);
        }
    }

    boolean handFinished() {
        for (int i = 0; i < count; i++)
            if (pieces[i] != null)
                return false;

        return true;
    }

    // Pick the piece below the finger/mouse
    public boolean pickPiece() {
        Vector2 mouse = new Vector2(
                Gdx.input.getX(),
                Gdx.graphics.getHeight() - Gdx.input.getY()); // Y axis is inverted

        for (int i = 0; i < count; i++) {
            if (pieces[i] != null && pieces[i].getRectangle().contains(mouse)) {
                heldPiece = i;
                return true;
            }
        }

        heldPiece = -1;
        return false;
    }

    public Array<Piece> getAvailablePieces() {
        Array<Piece> result = new Array<Piece>(count);
        for (int i = 0; i < count; i++) {
            if (pieces[i] != null) {
                result.add(pieces[i]);
            }
        }
        return result;
    }

    public int calculateHeldPieceArea() {
        if (heldPiece > -1) {
            return pieces[heldPiece].calculateArea();
        } else {
            return 0;
        }
    }

    public boolean dropPiece(Board board) {
        if (heldPiece > -1) {
            if (board.putScreenPiece(pieces[heldPiece])) {
                pieces[heldPiece] = null;
            }
            heldPiece = -1;
            if (handFinished())
                takeMore();

            return true;
        }
        return false;
    }

    public void update(float cellSizeOnBoard) {
        if (heldPiece > -1) {
            Piece piece = pieces[heldPiece];

            Vector2 mouse = new Vector2(
                    Gdx.input.getX(),
                    Gdx.graphics.getHeight() - Gdx.input.getY()); // Y axis is inverted

            // Center the piece
            mouse.sub(piece.getRectangle().width / 2, piece.getRectangle().height / 2);

            piece.pos.lerp(mouse, 0.4f);
            piece.cellSize = Interpolation.linear.apply(piece.cellSize, cellSizeOnBoard, 0.4f);
        }
    }

    public void draw(SpriteBatch batch, NinePatch patch) {
        for (int i = 0; i < count; i++) {
            if (pieces[i] != null) {
                pieces[i].draw(batch, patch);
            }
        }
    }
}
