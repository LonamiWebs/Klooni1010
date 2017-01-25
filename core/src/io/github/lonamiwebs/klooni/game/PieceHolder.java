package io.github.lonamiwebs.klooni.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;

public class PieceHolder {

    final Piece[] pieces;
    final int count;

    int heldPiece;

    final Vector2 pos;
    final int width, height;

    public PieceHolder(int pieceCount, float x, float y, int holderWidth, int holderHeight) {
        count = pieceCount;
        pieces = new Piece[count];

        heldPiece = -1;
        pos = new Vector2(x, y);
        width = holderWidth;
        height = holderHeight;

        takeMore();
    }

    void takeMore() {
        int perPieceSize = width / 3;
        for (int i = 0; i < count; i++) {
            pieces[i] = Piece.random();

            // Set the local position and the cell cellSize
            pieces[i].pos.set(pos.x + i * perPieceSize, pos.y);
            pieces[i].cellSize = Math.min(
                    perPieceSize / pieces[i].cellCols,
                    height / pieces[i].celRows);
        }
    }

    // Pick the piece below the finger/mouse
    public boolean pickPiece() {
        Vector2 mouse = new Vector2(
                Gdx.input.getX(),
                Gdx.graphics.getHeight() - Gdx.input.getY()); // Y axis is inverted

        for (int i = 0; i < count; i++) {
            if (pieces[i].getRectangle().contains(mouse)) {
                heldPiece = i;
                return true;
            }
        }

        heldPiece = -1;
        return false;
    }

    public boolean dropPiece(Board board) {
        if (heldPiece > -1) {
            if (board.putScreenPiece(pieces[heldPiece])) {
                // TODO Remove the piece
            }
            heldPiece = -1;
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
