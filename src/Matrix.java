import java.util.ArrayList;

/**
 * Write a description of class Matrix here.
 *
 * @author Henry (me@hnry.us)
 * @version (a version number or a date)
 */
public class Matrix extends Board {

    final static char RED = 'R';
    final static char YELLOW = 'Y';
    final static char MOVEDFIRST = 'A';
    final static char MOVEDSECOND = 'V';
    final static char PLAYABLE = 'P';
    final static char DRAW = 'D';
    final static char NOTWON = 'N';
    protected static char myChar;
    protected static char lookingFor;

    /**
     * Default constructor creates a matrix for a 7x6 board.
     */
    public Matrix() {
        numberOfRows = 6;
        numberOfColumns = 7;
        board = new char[numberOfRows][numberOfColumns];
    }

    /**
     * Creates a matrix, size defined by the parameters sent by the calling class.
     *
     * @param passedRows    Number of rows for the matrix to be created
     * @param passedColumns Number of columns for the matrix to be created
     */

    public Matrix(int passedRows, int passedColumns) {
        numberOfRows = passedRows;
        numberOfColumns = passedColumns;
        board = new char[numberOfRows][numberOfColumns];
    }

    /**
     * Creates a matrix by replicating another matrix's board.
     *
     * @param father Matrix to be copied.
     */

    public Matrix(Matrix father) {
        numberOfRows = father.getRowCount();
        numberOfColumns = father.getColumnCount();
        board = new char[numberOfRows][numberOfColumns];
        copy(father.getBoard());
    }

    /**
     * Receives a board and creates a Matrix object from it.
     *
     * @param boardMatrix received to be turned into a Matrix object.
     */

    public Matrix(char[][] boardMatrix) {
        numberOfRows = boardMatrix.length;
        numberOfColumns = boardMatrix[0].length;
        board = new char[numberOfRows][numberOfColumns];
        copy(boardMatrix);
    }

    /**
     * Initializes all the slots in a Matrix's board to BLANK.
     */

    public void fill() {
        for (int i = 0; i < getColumnCount(); i++) {
            for (int j = 0; j < getRowCount(); j++) {
                board[j][i] = BLANK;
            }
        }
    }


    /**
     * Scans the board looking for the currently playable positions.
     *
     * @return A list of all the possible moves (sons of a Matrix) based on the current board.
     */
    public ArrayList<Problem> getPlayables() //getLowestEmptyIndex
    {
        ArrayList<Problem> sons = new ArrayList<>();
        for (int x = 0; x < board[0].length; x++) {
            int lowestBlank = -1;
            for (int y = 0; y < board.length; y++) {
                if (board[y][x] == BLANK) {
                    lowestBlank = y;
                }
            }
            if (lowestBlank != -1) {
                Problem son = new Problem(lowestBlank, x);
                sons.add(son);
            }
        }
        return sons;
    }


    /**
     * Modify a single position in the current Matrix's board.
     *
     * @param row    value of the position to be modified.
     * @param column value of the position to be modified.
     * @param move   by player to be inserted in the board.
     */
    //@TODO: Validate that the move received is the tag of the player to move.
    public void modify(int row, int column, char move) {
        if (move == RED || move == YELLOW) {
            if (row < board.length && row >= 0) {
                if (column < board[0].length && column >= 0) {
                    board[row][column] = move;
                }
            }
        }
    }

    /**
     * Inserts a move in a Matrix's board based on the coordinates stored in the Problem object.
     *
     * @param seed Holds the coordinates of the position to be taken.
     * @param turn The tag to be inserted in the Matrix's board.
     */

    public void plantTheSeed(Problem seed, char turn) {
        int row = seed.getRow();
        int column = seed.getColumn();

        if (turn == MOVEDFIRST || turn == MOVEDSECOND) {
            if (row < board.length && row >= 0) {
                if (column < board[0].length && column >= 0) {
                    board[row][column] = turn;
                    revertPlayables();
                }
            }
        }
    }

    /**
     * Reestablishes the BLANK tags for an accurate return
     * of the getCountOfBlanks method.
     */

    public void revertPlayables() {
        for (int i = 0; i < getColumnCount(); i++) {
            for (int j = 0; j < getRowCount(); j++) {
                if (board[j][i] == PLAYABLE) {
                    board[j][i] = BLANK;
                }
            }
        }
    }

    /**
     * Compares the board to another Matrix's board.
     *
     * @param check The Matrix object to be matched against.
     * @return Whether the board's match.
     */

    public boolean match(Matrix check) {
        boolean match = true;
        char[][] local = check.board;

        for (int i = 0; i < getColumnCount(); i++) {
            for (int j = 0; j < getRowCount(); j++) {
                if (local[j][i] != board[j][i]) {
                    match = false;
                }
            }
        }

        return match;
    }


    /**
     * Check if the game has been won.
     *
     * @return Any return other than NOTWON signifies that the game has been won.
     */
    public char gameWon() {
        for (int i = 0; i < getColumnCount(); i++) {
            for (int j = 0; j < getRowCount(); j++) {
                if (board[j][i] != BLANK) {
                    if (j + 3 < getRowCount()) {
                        if (board[j][i] == board[j + 1][i] && board[j][i] == board[j + 2][i] && board[j][i] == board[j + 3][i]) {
                            return board[j][i];
                        }
                    }
                    if (i + 3 < getColumnCount()) {
                        if (board[j][i] == board[j][i + 1] && board[j][i] == board[j][i + 2] && board[j][i] == board[j][i + 3]) {
                            return board[j][i];
                        }
                    }
                    if (i + 3 < getColumnCount() && j + 3 < getRowCount()) {
                        if (board[j][i] == board[j + 1][i + 1] && board[j][i] == board[j + 2][i + 2] && board[j][i] == board[j + 3][i + 3]) {
                            return board[j][i];
                        }
                    }
                    if (i > 2 && j + 3 < getRowCount()) {
                        if (board[j][i] == board[j + 1][i - 1] && board[j][i] == board[j + 2][i - 2] && board[j][i] == board[j + 3][i - 3]) {
                            return board[j][i];
                        }
                    }
                }
            }
        }

        return NOTWON;
    }


    /**
     * Vicky identifies herself to the Matrix class.
     *
     * @param redAgent true if Vicky plays red
     */
    public void identifyPlayer(boolean redAgent) {
        if (redAgent) {
            myChar = RED;
            lookingFor = YELLOW;
        } else {
            myChar = YELLOW;
            lookingFor = RED;
        }
    }

    /**
     * Support method to...
     *
     * @return the agent's char.
     */
    public char getMyChar() {
        return myChar;
    }

    /**
     * Support method to...
     *
     * @return the opponent's tag
     */
    public char getLookingFor() {
        return lookingFor;
    }


}
