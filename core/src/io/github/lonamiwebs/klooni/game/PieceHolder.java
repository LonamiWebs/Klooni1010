package io.github.lonamiwebs.klooni.game;

import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class PieceHolder {

    final Piece[] pieces;
    final int count;
    public final int width, height;

    public PieceHolder(int pieceCount, int holderWidth, int holderHeight) {
        count = pieceCount;
        pieces = new Piece[count];

        width = holderWidth;
        height = holderHeight;

        takeMore();
    }

    void takeMore() {
        for (int i = 0; i < count; i++) {
            pieces[i] = Piece.random();
        }
    }

    // TODO Scale evenly, and when taking the piece, scale to match the on-board cells' size
    public void draw(SpriteBatch batch, NinePatch patch, int x, int y) {
        int perPieceSize = width / 3;

        for (int i = 0; i < count; i++) {
            if (pieces[i] != null) {
                // Pick the smallest value (either width/cell count, or height/cell count)
                int cellSize = Math.min(
                        perPieceSize / pieces[i].width,
                        height / pieces[i].height
                );

                pieces[i].draw(batch, patch, x + i * perPieceSize, y, cellSize);
            }
        }
    }
}
