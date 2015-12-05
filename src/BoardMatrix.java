/**
 * Udacity's Intro to Java Nanodegree Course Final Project.
 * <p>
 * The class BoardMatrix creates a char matrix of the board
 * for the Connect 4 game. It is not a stand-alone class, it is tied deeply to Vicky.
 * It also relies deeply on the Threat and Problem classes.
 *
 * @author Henry Lopez-Ingram
 * @version 151205
 */
public class BoardMatrix extends Matrix {
    final static char UNPLAYABLE = 'X';
    final static char WINNER = 'W';
    final static char THREAT = 'T';
    private char[][] check;


    /**
     * Creates a char representation of the board for evaluation purposes
     *
     * @param aGame    receives the game from Vicky
     * @param redAgent Vicky identifies herself to the BoardMatrix class
     */
    public BoardMatrix(Connect4Game aGame, boolean redAgent) {
        identifyPlayer(redAgent);

        /* Creating and populating the board */
        board = new char[aGame.getRowCount()][aGame.getColumnCount()];

        for (int i = 0; i < aGame.getColumnCount(); i++) {
            for (int j = 0; j < aGame.getRowCount(); j++) {
                if (aGame.getColumn(i).getSlot(j).getIsFilled()) {
                    if (aGame.getColumn(i).getSlot(j).getIsRed()) {
                        board[j][i] = RED;
                    } else {
                        board[j][i] = YELLOW;
                    }
                } else {
                    board[j][i] = BLANK;
                }
            }
        }
    }

    /**
     * This method identifies the first move, if it was made by the opponent.
     *
     * @param madeFirstMove Vicky tells the class who moves first.
     * @param check         A matrix of chars representing the board to extract the move.
     * @return The column where the first move was made
     */
    public int whoMovedFirst(boolean madeFirstMove, BoardMatrix check) {
        if (!madeFirstMove) {

            for (int i = 0; i < board[0].length; i++) {
                for (int j = board.length - 1; j < board.length; j++) {
                    if (board[j][i] != check.getChar(j, i)) {
                        return i;
                    }
                }
            }

        }
        return -1;
    }


}
