/**
 * @author Henry (me@hnry.us)
 */
public abstract class Board {
    final static char BLANK = 'B';

    protected char[][] board;
    protected int numberOfRows;
    protected int numberOfColumns;

    /**
     * This method might return different values depending on where the method is called,
     * as in the BoardMatrix class, not every unplayed space is tagged as blank.
     *
     * @return the count of spaces tagged as BLANK in the current Matrix's board.
     */
    public int getCountOfBlanks() {
        int countOfBlanks = 0;
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
     * Check if the board is full.
     *
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
     * Clears the board by tagging all positions as BLANK.
     */

    public void clear() {
        for (int i = 0; i < getColumnCount(); i++) {
            for (int j = 0; j < getRowCount(); j++) {
                board[j][i] = BLANK;
            }
        }
    }

    /**
     * Retrieves the Matrix's board.
     *
     * @return the Matrix's board.
     */

    public char[][] getBoard() {
        return board;
    }

    /**
     * @param row    Row index of the Problem
     * @param column column index of the Problem
     * @return value of a position
     * Get the char value stored in a position of the Matrix's board.
     */
    public char getChar(int row, int column) {
        return board[row][column];
    }

    public char getProblemChar(Problem problem){
        return board[problem.getRow()][problem.getColumn()];
    }

    /**
     * Support method to...
     *
     * @return the number of columns of the Matrix.
     */
    public int getColumnCount() {
        return numberOfColumns;
    }

    /**
     * Support method to...
     *
     * @return the number of Row of the Matrix.
     */
    public int getRowCount() {
        return numberOfRows;
    }

    /**
     * Prints the board of chars. For evaluation purposes.
     * All calls for this method were removed before submitting the project.
     */
    public void printTheBoard() {
        System.out.println();
        for (char[] aBoard : board) {
            for (char anABoard : aBoard) {
                System.out.print(anABoard + " ");
            }
            System.out.println();
        }
    }
}
