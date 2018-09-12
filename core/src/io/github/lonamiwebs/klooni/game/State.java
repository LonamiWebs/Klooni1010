package io.github.lonamiwebs.klooni.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

/*
    State is an object designed to help validating randomly-generated blocks. Every time a block is
    inserted into the board, the game's state changes. Different permutation of blocks and different
    position creates new states. Thus to validate a set of generated blocks, we have to check all
    possible states. Instead of checking them directly on the board, we create a special class for
    validation.
 */

public class State {

    // region Members

    private boolean[][] state;
    private final int cellCount;
    private int emptySpace = 0;
    private static int[] unfitBlock = new int[6];
    private final static int [][] CHECKER = {{0,1,2},{0,2,1},{1,0,2},{1,2,0},{2,0,1},{2,1,0}};
    private final static Vector2 ORIGIN = new Vector2(0, -1);

    // endregion

    //region Constructors

    private State(Board board) {
        this.cellCount = board.cellCount;
        this.state = new boolean[cellCount][cellCount];
        for (int i = 0; i < board.cellCount; ++i)
            for(int j = 0; j < board.cellCount; ++j)
                if (board.isEmpty(i, j))
                    emptySpace += 1;
                else
                    this.state[i][j] = true;
        clearComplete();
    }

    private State(State toClone) {
        this.cellCount = toClone.state.length;
        this.state = new boolean[cellCount][cellCount];
        for (int i = 0; i < cellCount; ++i)
            for(int j = 0; j < cellCount; ++j)
                if (toClone.state[i][j])
                    this.state[i][j] = true;
                else
                    emptySpace += 1;
    }

    // endregion

    //region Check piece

    private static String getUnfitBlock() {
        StringBuilder sb = new StringBuilder();
        for (int i : unfitBlock) {
            sb.append(i);
            sb.append(" ");
        }
        return sb.toString();
    }

    // True if the given cell coordinates are inside the bounds of the board
    private boolean inBounds(int x, int y) {
        return x >= 0 && x < cellCount && y >= 0 && y < cellCount;
    }

    // True if the given piece at the given coordinates is not outside the bounds of the board
    private boolean inBounds(Piece piece, int x, int y) {
        return inBounds(x, y) && inBounds(x + piece.cellCols - 1, y + piece.cellRows - 1);
    }

    // Given coordinates as the starting point, return true if a piece fits in said coordinates
    private boolean canPutPiece(Piece piece, int x, int y) {
        if (!inBounds(piece, x, y))
            return false;

        for (int i = 0; i < piece.cellRows; ++i)
            for (int j = 0; j < piece.cellCols; ++j)
                if (state[y + i][x + j] && piece.filled(i, j)) {
                    return false;
                }

        return true;
    }

    // Given Vector2 as last checked coordinates, return a new vector where the piece fits,
    // else the same vector if there are no longer any place in board to fit the piece
    private Vector2 canPutPiece(Piece piece, Vector2 origin) {
        for (int j = (int) (origin.x + 1); j < cellCount; ++j)
            if (canPutPiece(piece, j, (int) origin.y))
                return new Vector2(j, origin.y);

        for (int i = (int) (origin.y + 1); i < cellCount; ++i)
            for (int j = 0; j < cellCount; ++j)
                if (canPutPiece(piece, j, i))
                    return new Vector2(j, i);

        return origin;
    }

    // Check every possible state from a set of blocks and an initial state.
    // Immediately return true if found one fitting occurrence.
    // Do not try to optimize. This code's already working. Leave it be.
    private static boolean checkPermute(Piece[] holder, State state) {
        Vector2 temp1, temp2, temp3, pos1 = ORIGIN, pos2 = ORIGIN;
        for (int i = 0; i < 6; i++) {
            unfitBlock[i] = -1;
            while(true) {
                State state1 = new State(state);
                temp1 = state.canPutPiece(holder[CHECKER[i][0]], pos1);
                if (temp1.epsilonEquals(pos1, MathUtils.FLOAT_ROUNDING_ERROR)) {
                    if (unfitBlock[i] < 0)
                        unfitBlock[i] = 0;
                    break;
                }
                state1.putPiece(holder[CHECKER[i][0]], temp1);
                while (true) {
                    State state2 = new State(state1);
                    temp2 = state1.canPutPiece(holder[CHECKER[i][1]], pos2);
                    if (temp2.epsilonEquals(pos2, MathUtils.FLOAT_ROUNDING_ERROR)) {
                        if (unfitBlock[i] < 1)
                            unfitBlock[i] = 1;
                        break;
                    }
                    state2.putPiece(holder[CHECKER[i][1]], temp2);
                    temp3 = state2.canPutPiece(holder[CHECKER[i][2]], ORIGIN);
                    if (!temp3.epsilonEquals(ORIGIN, MathUtils.FLOAT_ROUNDING_ERROR)) {
                        state2.putPiece(holder[CHECKER[i][2]], temp3);
                        Gdx.app.log("Check permute", "Piece " +
                                holder[CHECKER[i][0]].colorIndex + " at " + temp1.toString());
                        Gdx.app.log("Check permute", "Piece " +
                                holder[CHECKER[i][1]].colorIndex + " at " + temp2.toString());
                        Gdx.app.log("Check permute", "Piece " +
                                holder[CHECKER[i][2]].colorIndex + " at" + temp3.toString());
                        return true;
                    }
                    else
                        unfitBlock[i] = 2;
                    pos2 = new Vector2(temp2);
                }
                pos1 = new Vector2(temp1);
            }
        }
        Gdx.app.log("Check permute", getUnfitBlock());
        return false;
    }

    // Change unfit block that cause most problem
    private static Piece[] changeBlock(Piece[] holder, State state) {
        int[] weight = new int[3];
        int max = 0;
        for (int i = 0; i < 6; i++) {
            int temp = CHECKER[i][unfitBlock[i]];
            weight[temp] += (unfitBlock[i] + 1) * 5;
        }

        for (int i : weight) {
            if (i > max)
                max = i;
        }

        Gdx.app.log("Change block", holder[0].colorIndex + ", " +
                holder[1].colorIndex + ", " + holder[2].colorIndex);
        Gdx.app.log("Change block", weight[0] + ", " +
                weight[1] + ", " + weight[2]);
        for (int i = 0; i < 3; i++) {
            if (weight[i] == max) {
                int previous = holder[i].colorIndex;
                Gdx.app.log("Change block", "Changing block");
                holder[i] = Piece.random();
                if (checkPermute(holder, state)) {
                    Gdx.app.log("Change block", "Piece " + previous +
                            " to Piece " + holder[i].colorIndex);
                    return holder;
                }
            }
        }
        return changeBlock(holder, state);
    }

    public static Piece[] validateBlock(Piece[] holder, Board board) {
        long invocationTime = System.nanoTime();
        State initialState = new State(board);
        Gdx.app.log("Validation", "Board contains " + initialState.emptySpace +
                " empty spaces.");
        if (checkPermute(holder, initialState)) {
            Gdx.app.log("Validation", "Validation ends, takes " +
                    (System.nanoTime() - invocationTime) + " ns!");
            return holder;
        }
        else {
            Gdx.app.log("Validation", "Validation ends, takes " +
                    (System.nanoTime() - invocationTime) + " ns with changeBlock.");
            return changeBlock(holder, initialState);
        }
    }

    // endregion

    // region Set piece

    private void clearComplete() {
        int clearCount = 0;
        boolean[] clearedRows = new boolean[cellCount];
        boolean[] clearedCols = new boolean[cellCount];

        // Analyze rows and columns that will be cleared
        for (int i = 0; i < cellCount; ++i) {
            clearedRows[i] = true;
            for (int j = 0; j < cellCount; ++j) {
                if (!state[i][j]) {
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
                if (!state[i][j]) {
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
                        state[i][j] = false;
                    }
                }
            }

            for (int j = 0; j < cellCount; ++j) {
                if (clearedCols[j]) {
                    for (int i = 0; i < cellCount; ++i) {
                        state[i][j] = false;
                    }
                }
            }
        }
    }

    private void putPiece(Piece piece, Vector2 vec) {
        for (int i = 0; i < piece.cellRows; ++i)
            for (int j = 0; j < piece.cellCols; ++j)
                if (piece.filled(i, j))
                    state[(int) (vec.y + i)][(int) (vec.x + j)] = true;
        clearComplete();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("State:\n");
        for (int i = cellCount - 1 ; i > -1; --i) {
            for (int j = 0; j < cellCount; ++j) {
                if (state[i][j]) {
                    sb.append(1);
                } else {
                    sb.append(0);
                }
            }
            sb.append("\n");
        }
        return sb.toString();
    }

    // endregion
}
