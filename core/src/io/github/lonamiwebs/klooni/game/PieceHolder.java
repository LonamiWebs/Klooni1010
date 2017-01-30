package io.github.lonamiwebs.klooni.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

import io.github.lonamiwebs.klooni.Klooni;

// A holder of pieces that can be drawn on screen.
// Pieces can be picked up from it and dropped on a board.
public class PieceHolder {

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

    // Needed after a piece is dropped, so it can go back
    private final Rectangle[] originalPositions;

    // The size the cells will adopt once picked
    private final float pickedCellSize;

    //endregion

    //region Static members

    public static final int NO_DROP = 0;
    public static final int NORMAL_DROP = 1;
    public static final int ON_BOARD_DROP = 2;

    //endregion

    //region Constructor

    public PieceHolder(final GameLayout layout, final int pieceCount, final float pickedCellSize) {
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
        for (int i = 0; i < count; i++)
            if (pieces[i] != null)
                return false;

        return true;
    }

    // Takes a new set of pieces. Should be called when there are no more piece left
    private void takeMore() {
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

            // Now that we have the original positions, reset the size so it animates and grows
            pieces[i].cellSize = 0f;
        }
        if (Klooni.soundsEnabled()) {
            // Random pitch so it's not always the same sound
            takePiecesSound.setPitch(takePiecesSound.play(), MathUtils.random(0.8f, 1.2f));
        }
    }

    //endregion

    //region Public methods

    // Picks the piece below the finger/mouse, returning true if any was picked
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
        for (int i = 0; i < count; i++)
            if (pieces[i] != null)
                result.add(pieces[i]);

        return result;
    }

    // If no piece is currently being held, the area will be 0
    public int calculateHeldPieceArea() {
        return heldPiece > -1 ? pieces[heldPiece].calculateArea() : 0;
    }

    // Tries to drop the piece on the given board. As a result, it
    // returns one of the following: NO_DROP, NORMAL_DROP, ON_BOARD_DROP
    public int dropPiece(Board board) {
        if (heldPiece > -1) {
            boolean put = board.putScreenPiece(pieces[heldPiece]);
            if (put) {
                if (Klooni.soundsEnabled()) {
                    // The larger the piece size, the smaller the pitch
                    // Considering 10 cells to be the largest, 1.1 highest pitch, 0.7 lowest
                    float pitch = 1.104f - pieces[heldPiece].calculateArea() * 0.04f;
                    pieceDropSound.setPitch(pieceDropSound.play(), pitch);
                }
                pieces[heldPiece] = null;
            } else {
                if (Klooni.soundsEnabled())
                    invalidPieceDropSound.play();
            }

            heldPiece = -1;
            if (handFinished())
                takeMore();

            return put ? ON_BOARD_DROP : NORMAL_DROP;
        } else
            return NO_DROP;
    }

    // Updates the state of the piece holder (and the held piece)
    public void update() {
        Piece piece;
        if (heldPiece > -1) {
            piece = pieces[heldPiece];

            Vector2 mouse = new Vector2(
                    Gdx.input.getX(),
                    Gdx.graphics.getHeight() - Gdx.input.getY()); // Y axis is inverted

            // Center the new piece position
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

    public void draw(SpriteBatch batch) {
        for (int i = 0; i < count; i++) {
            if (pieces[i] != null) {
                pieces[i].draw(batch);
            }
        }
    }

    //endregion
}
