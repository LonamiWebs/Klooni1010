package io.github.lonamiwebs.klooni.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public class PieceHolder {

    private final Piece[] pieces;
    private final Rectangle[] originalPositions; // Needed after a piece is dropped

    private final int count;

    private int heldPiece;

    final Rectangle area;

    // The size the cells will adopt once picked
    private final float pickedCellSize;

    public static final int NO_DROP = 0;
    public static final int NORMAL_DROP = 1;
    public static final int ON_BOARD_DROP = 2;

    public PieceHolder(final GameLayout layout, final int pieceCount, final float pickedCellSize) {
        count = pieceCount;
        pieces = new Piece[count];
        originalPositions = new Rectangle[count];

        heldPiece = -1;
        this.pickedCellSize = pickedCellSize;

        area = new Rectangle();
        layout.update(this);

        // takeMore depends on the layout to be ready
        // TODO So, how would pieces handle a layout update?
        takeMore();
    }

    void takeMore() {
        float perPieceWidth = area.width / count;
        for (int i = 0; i < count; i++) {
            pieces[i] = Piece.random();

            // Set the absolute position on screen and the cells' cellSize
            // Also clamp the cell size to be the picked * 2 as maximum, or
            // it would be too big in some cases.
            pieces[i].pos.set(area.x + i * perPieceWidth, area.y);
            pieces[i].cellSize = Math.min(Math.min(
                    perPieceWidth / pieces[i].cellCols,
                    area.height / pieces[i].cellRows), pickedCellSize * 2f);

            // Center the piece on the X and Y axes. For this we see how
            // much up we can go, this is, (area.height - piece.height) / 2
            Rectangle rectangle = pieces[i].getRectangle();
            pieces[i].pos.y += (area.height - rectangle.height) * 0.5f;
            pieces[i].pos.x += (perPieceWidth - rectangle.width) * 0.5f;

            originalPositions[i] = new Rectangle(
                    pieces[i].pos.x, pieces[i].pos.y,
                    pieces[i].cellSize, pieces[i].cellSize);
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

    // Returns one of the following: NO_DROP, NORMAL_DROP, ON_BOARD_DROP
    public int dropPiece(Board board) {
        if (heldPiece > -1) {
            boolean put = board.putScreenPiece(pieces[heldPiece]);
            if (put)
                pieces[heldPiece] = null;

            heldPiece = -1;
            if (handFinished())
                takeMore();

            return put ? ON_BOARD_DROP : NORMAL_DROP;
        } else
            return NO_DROP;
    }

    public void update() {
        Piece piece;
        if (heldPiece > -1) {
            piece = pieces[heldPiece];

            Vector2 mouse = new Vector2(
                    Gdx.input.getX(),
                    Gdx.graphics.getHeight() - Gdx.input.getY()); // Y axis is inverted

            // Center the piece
            mouse.sub(piece.getRectangle().width / 2, piece.getRectangle().height / 2);

            piece.pos.lerp(mouse, 0.4f);
            piece.cellSize = Interpolation.linear.apply(piece.cellSize, pickedCellSize, 0.4f);
        }

        // Return the pieces to their original position
        // TODO This seems somewhat expensive, can't it be done any better?
        Rectangle original;
        for (int i = 0; i < count; i++) {
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

    public void draw(SpriteBatch batch, NinePatch patch) {
        for (int i = 0; i < count; i++) {
            if (pieces[i] != null) {
                pieces[i].draw(batch, patch);
            }
        }
    }
}
