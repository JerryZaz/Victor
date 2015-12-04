import java.util.ArrayList;

/**
 * Write a description of class Matrix here.
 *
 * @author Henry (me@hnry.us)
 * @version (a version number or a date)
 */
public class Matrix {
    final static char BLANK = 'B';
    final static char RED = 'R';
    final static char YELLOW = 'Y';
    final static char MOVEDFIRST = 'A';
    final static char MOVEDSECOND = 'V';
    final static char PLAYABLE = 'P';
    final static char DRAW = 'D';
    final static char NOTWON = 'N';
    protected char myChar;
    protected char lookingFor;
    char[][] board;
    private int numberOfRows;
    private int numberOfColumns;
    private int countOfBlanks; //write a method to calculate this

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
     * Inserts the values of another board into this Matrix's board.
     *
     * @param toClone The board where the values will be extracted from.
     */

    public void copy(char[][] toClone) {
        for (int i = 0; i < getColumnCount(); i++) {
            for (int j = 0; j < getRowCount(); j++) {
                board[j][i] = toClone[j][i];
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
        ArrayList<Problem> sons = new ArrayList<Problem>();
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
     * This method might return different values depending on where the method is called,
     * as in the BoardMatrix class, not every unplayed space is tagged as blank.
     *
     * @return the count of spaces tagged as BLANK in the current Matrix's board.
     */
    public int getCountOfBlanks() {
        countOfBlanks = 0;
        for (int i = 0; i < getColumnCount(); i++) {
            for (int j = 0; j < getRowCount(); j++) {
                if (board[j][i] == BLANK) {
                    countOfBlanks++;
                }
            }
        }
        return countOfBlanks;
    }

    /**
     * Modify a single position in the current Matrix's board.
     *
     * @param row    value of the position to be modified.
     * @param column value of the position to be modified.
     * @param move   by player to be inserted in the board.
     * @TODO: Validate that the move received is the tag of the player to move.
     */
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
     * Check if the board is full.
     * @return true if the board is full, false otherwise.
     */
    public boolean boardFull() {
        for (int i = 0; i < getColumnCount(); i++) {
            for (int j = 0; j < getRowCount(); j++) {
                if (board[j][i] == BLANK) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Check if the game has been won.
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
     * Clears the board by tagging all positions as BLANK.
     */

    public void clear() {
        for (int i = 0; i < numberOfColumns; i++) {
            for (int j = 0; j < numberOfRows; j++) {
                board[j][i] = BLANK;
            }
        }
    }

    /**
     * Retrieves the Matrix's board.
     * @return the Matrix's board.
     */

    public char[][] getBoard() {
        return board;
    }

    /**
     * @TODO: Provide alternate getChar method that takes a Problem object as parameter.
     * Get the char value stored in a position of the Matrix's board.
     * @param row    Row index of the Problem
     * @param column column index of the Problem
     * @return value of a position
     */
    public char getChar(int row, int column) {
        return board[row][column];
    }

    /**
     * Support method to...
     * @return the number of columns of the Matrix.
     */
    public int getColumnCount() {
        return numberOfColumns;
    }

    /**
     * Support method to...
     * @return the number of Row of the Matrix.
     */
    public int getRowCount() {
        return numberOfRows;
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
     * @return the agent's char.
     */
    public char getMyChar() {
        return myChar;
    }

    /**
     * Support method to...
     * @return the opponent's tag
     */
    public char getLookingFor() {
        return lookingFor;
    }

    /**
     * Prints the board of chars. For evaluation purposes.
     * All calls for this method were removed before submitting the project.
     */
    public void printTheBoard() {
        System.out.println();
        for (int x = 0; x < board.length; x++) {
            for (int y = 0; y < board[x].length; y++) {
                System.out.print(board[x][y] + " ");
            }
            System.out.println();
        }
    }

}
