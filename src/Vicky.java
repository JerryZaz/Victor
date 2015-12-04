import java.util.ArrayList;
import java.util.Random;

/**
 * Udacity's Intro to Java Nanodegree Course Final Project.
 * <p>
 * Vicky is an Agent that plays the game Connect 4.
 * Vicky is not a stand-alone object, as it requires a Connect 4 Game to be passed on,
 * and it relies on the BoardMatrix class to choose the next move.
 * Vicky also requires the Threat class, as Vicky holds all the possible Threat objects
 * and is constantly passing it on to the BoardMatrix class for analysis.
 *
 * @author Henry (me@hnry.us)
 * @version 151006
 */
public class Vicky extends Agent {
    private Random r;
    private BoardMatrix mBoard;
    private BoardMatrix mCheck;

    private boolean mIGoFirst;
    private boolean mMiddleClaimed;

    private ArrayList<Threat> mThreats;
    private int mSuggestion;
    private int mActiveThreats;

    /**
     * Constructs a new agent, giving it the game and telling it whether it is Red or Yellow.
     *
     * @param game   The game the agent will be playing.
     * @param iAmRed True if the agent is Red, False if the agent is Yellow.
     */
    public Vicky(Connect4Game game, boolean iAmRed) {
        super(game, iAmRed);
        r = new Random();

        mBoard = new BoardMatrix(game, iAmRed);
        if (mBoard.getCountOfBlanks() == 42) {
            mIGoFirst = true;
        }
        mMiddleClaimed = false;

        mCheck = new BoardMatrix(game, iAmRed);
        mThreats = Think.identifyThreats(mBoard);
    }

    /**
     * This method sends the decision to the game.
     * If a suggestion has been found, it's sent. If not, a random move is generated.
     */
    public void move() {
        mSuggestion = -1;

        think();
        if (mSuggestion != -1) {
            moveOnColumn(mSuggestion);
        } else {
            moveOnColumn(randomMove());
        }

        mCheck = new BoardMatrix(myGame, iAmRed);
        mThreats = mCheck.disableThreats(mThreats);
    }

    /**
     * The Think method holds the key. Vicky coordinates multiple analysis on the board to determine the best possible move.
     * It starts by claiming the middle, if it hasn't been claimed. There's no analysis here, only a check on the lowest index of the middle column.
     * Proceeds to play a winning move, if there's any. Followed by blocking a winning move, again, if there's any.
     * If no winning or losing move has been found, different methods are run looking for patterns that could be played or should be blocked.
     */

    public void think() {

        mBoard = new BoardMatrix(myGame, iAmRed);
        //mBoard.checkOpponentMove(mCheck, mThreats);
        Problem opponentMove = Think.checkOpponentMove(mBoard, mCheck);
        if (opponentMove != null) {
            Think.increaseThreatLevel(mThreats, opponentMove);
        }

        if (mBoard.getCountOfBlanks() > mCheck.getCountOfBlanks()) {
            reset();
        }

        //else{
        //mBoard.findPlayables();
        Think.findPlayables(mBoard);
        if (!mMiddleClaimed) {
            mSuggestion = claimTheMiddle();
        } else {

            if (mSuggestion == -1) {
                mSuggestion = iCanWin();
                if (mSuggestion == -1) {
                    mSuggestion = theyCanWin();
                    if (mSuggestion == -1) {
                        mSuggestion = mBoard.earlyWin(mThreats);
                        if (mSuggestion == -1) {
                            if (mBoard.hadToRevertTheUnplayables) {
                                mSuggestion = theyCanWin();
                            }

                        }
                    }
                }

            }
        }
        //}
    }

    /**
     * This method's only purpose is to claim the middle if it's yet to be taken.
     * This method anticipates that Vicky could be used in different board sizes.
     *
     * @return The middle column index, if it's available.
     */
    public int claimTheMiddle() {

        if (myGame.getColumnCount() % 2 != 0) { //determine if a board with even number of columns is being played
            int columns = myGame.getColumnCount(); //possible instance variable
            int rows = myGame.getRowCount(); // possible instance variable
            int middleColumnIndex = myGame.getColumnCount() / 2; // THIS MIGHT NOT BE BE THE BEST WAY

            int lowestEmptySlotIndexOnMiddleColumn = getLowestEmptyIndex(myGame.getColumn(middleColumnIndex));
            if (lowestEmptySlotIndexOnMiddleColumn == rows - 1) { // determine if the middle has been claimed
                mMiddleClaimed = true;
                return middleColumnIndex; // claims middle
            } else {
                mMiddleClaimed = true;
            }
        }
        return -1;
    }

    /**
     * Returns the column that would allow the agent to win.
     * <p>
     * You might want your agent to check to see if it has a winning move available to it so that
     * it can go ahead and make that move. Implement this method to return what column would
     * allow the agent to win.
     *
     * @return the column that would allow the agent to win.
     */
    public int iCanWin() {
        mBoard.evaluateSolutions(mThreats);
        return mBoard.playWinningMove();
    }

    /**
     * Makes three different method calls, analyzing the board looking for a possible move.
     * Every method called will return a column number if it has found a sugestion, or -1 if it hasn't,
     * which will enable the next method call.
     *
     * @return the column that would allow the opponent to win.
     */
    public int theyCanWin() {
        activeThreats();
        mBoard.evaluateThreats(mThreats, mActiveThreats);

        int suggestedColumn = mBoard.blockWinningMove();
        if (suggestedColumn != -1) {
            return suggestedColumn;
        } else {
            suggestedColumn = mBoard.identifyPatterns();
            if (suggestedColumn != -1) {
                return suggestedColumn;
            } else {
                return mBoard.blockAdvance(mThreats);
            }
        }
    }

    /**
     * Drops a token into a particular column so that it will fall to the bottom of the column.
     * If the column is already full, nothing will change.
     *
     * @param columnNumber The column into which to drop the token.
     */
    public void moveOnColumn(int columnNumber) {
        int lowestEmptySlotIndex = getLowestEmptyIndex(myGame.getColumn(columnNumber));   // Find the top empty slot in the column
        // If the column is full, lowestEmptySlot will be -1
        if (lowestEmptySlotIndex > -1)  // if the column is not full
        {
            Connect4Slot lowestEmptySlot = myGame.getColumn(columnNumber).getSlot(lowestEmptySlotIndex);  // get the slot in this column at this index

            if (iAmRed) // If the current agent is the Red player...
            {
                lowestEmptySlot.addRed(); // Place a red token into the empty slot
            } else // If the current agent is the Yellow player (not the Red player)...
            {
                lowestEmptySlot.addYellow(); // Place a yellow token into the empty slot
            }
        }
    }

    /**
     * Returns a random valid move. If your agent doesn't know what to do, making a random move
     * can allow the game to go on anyway.
     *
     * @return a random valid move.
     */
    public int randomMove() {
        mBoard.unplayableMove();

        int i = r.nextInt(myGame.getColumnCount());
        while (getLowestEmptyIndex(myGame.getColumn(i)) == -1 || !mBoard.foundPlayableInColumn(i)) {
            i = r.nextInt(myGame.getColumnCount());
        }
        return i;
    }

    /**
     * Returns the index of the top empty slot in a particular column.
     *
     * @param column The column to check.
     * @return the index of the top empty slot in a particular column; -1 if the column is already full.
     */
    public int getLowestEmptyIndex(Connect4Column column) {
        int lowestEmptySlot = -1;
        for (int i = 0; i < column.getRowCount(); i++) {
            if (!column.getSlot(i).getIsFilled()) {
                lowestEmptySlot = i;
            }
        }
        return lowestEmptySlot;
    }

    /**
     * Restores the initial values of the instance variables getting
     * ready for a new game. The reset method is called when the count of
     * blanks increases, considering this as a sign that a new game is being played.
     */
    public void reset() {
        mBoard = new BoardMatrix(myGame, iAmRed);
        if (mBoard.getCountOfBlanks() == 42) {
            mIGoFirst = true;
        }
        mMiddleClaimed = false;

        mCheck = new BoardMatrix(myGame, iAmRed);
        reactivateThreats();
        activeThreats();
    }

    /**
     * Method that sets the number of remaining active threats.
     * It was created for evaluation and tracking purposes, used in the method EvaluateThreats of the BoardMatrix class to verify that
     * the threats were being sucessfully marked as inactive before evaluating.
     */
    private void activeThreats() {
        mActiveThreats = 0;
        for (Threat threat : mThreats) {
            if (threat.isActive()) {
                mActiveThreats++;
            }
        }
    }

    /**
     * Support method, called by Vicky's reset method.
     * The reactivate method of the Threat class returns the threat to its initial values.
     */
    public void reactivateThreats() {
        for (Threat threat : mThreats) {
            threat.reactivate();
        }
    }

    /**
     * Returns the name of this agent.
     *
     * @return the agent's name
     */
    public String getName() {
        return "Vicky";
    }

    private static abstract class Think {

        /**
         * Compares the backup board to the new board to locate the opponent's move.
         *
         * @param gameBoard  Current game board.
         * @param checkBoard Backup board.
         * @return Problem object that contains the Opponent's latest move.
         */
        private static Problem checkOpponentMove(BoardMatrix gameBoard, BoardMatrix checkBoard) {
            Problem opponentMove = null;
            if (Board.matchBoardSizes(gameBoard, checkBoard)) {
                char[][] board = gameBoard.getBoard();
                char[][] check = gameBoard.getBoard();

                for (int i = 0; i < board[0].length; i++) {
                    for (int j = 0; j < board.length; j++) {
                        if (board[j][i] != check[j][i]) {
                            opponentMove = new Problem(j, i);
                        }
                    }
                }
            }
            return opponentMove;
        }

        /**
         * Scans through the Active Threats looking for threats containing the opponent's latest move.
         *
         * @param threats      List containing the active threats.
         * @param opponentMove Latest move by the Opponent
         */
        private static void increaseThreatLevel(ArrayList<Threat> threats, Problem opponentMove) {
            if (opponentMove != null) {
                for (Threat threat : threats) {
                    if (threat.isActive()) {
                        if (threat.containsProblem(opponentMove)) {
                            int threatLevel = threat.getThreatLevel();
                            if (threatLevel < 4) {
                                threatLevel += 1;
                                threat.setThreatLevel(threatLevel);
                            }
                        }
                    }
                }
            }
        }

        /**
         * Scans the board identifying the playable positions
         * @param gameBoard A BoardMatrix object representing the current game board.
         */
        private static void findPlayables(BoardMatrix gameBoard) {
            for (int x = 0; x < gameBoard.board[0].length; x++) {
                int lowestBlank = -1;
                for (int y = 0; y < gameBoard.board.length; y++) {
                    if (gameBoard.board[y][x] == Board.BLANK) {
                        lowestBlank = y;
                    }
                }
                if (lowestBlank != -1) {
                    gameBoard.board[lowestBlank][x] = BoardMatrix.PLAYABLE;
                }
            }
        }

        /**
         * When the game starts, this method scans the board initializing all possible Threats
         * found in the board. This method is controlled by Vicky, and enabled by the Threat and
         * the Problem class. A Problem object holds the coordinates of a slot, a Threat object holds
         * an array of the four Problems that compose a Threat, and Vicky holds an ArrayList of Threats.
         *
         * @return All the possible threats
         */
        private static ArrayList<Threat> identifyThreats(BoardMatrix gameBoard) {
            ArrayList<Threat> threats = new ArrayList<>();
            for (int i = 0; i < gameBoard.getColumnCount(); i++) {
                for (int j = 0; j < gameBoard.getRowCount(); j++) {
                    if (j + 3 < gameBoard.getRowCount()) {
                        if (gameBoard.board[j][i] == gameBoard.board[j + 1][i] && gameBoard.board[j][i] == gameBoard.board[j + 2][i] && gameBoard.board[j][i] == gameBoard.board[j + 3][i]) {
                            Problem one = new Problem(j, i);
                            Problem two = new Problem(j + 1, i);
                            Problem three = new Problem(j + 2, i);
                            Problem four = new Problem(j + 3, i);
                            threats.add(new Threat(one, two, three, four));
                        }
                    }
                    if (i + 3 < gameBoard.getColumnCount()) {
                        if (gameBoard.board[j][i] == gameBoard.board[j][i + 1] && gameBoard.board[j][i] == gameBoard.board[j][i + 2] && gameBoard.board[j][i] == gameBoard.board[j][i + 3]) {
                            Problem one = new Problem(j, i);
                            Problem two = new Problem(j, i + 1);
                            Problem three = new Problem(j, i + 2);
                            Problem four = new Problem(j, i + 3);
                            threats.add(new Threat(one, two, three, four));
                        }
                    }
                    if (i + 3 < gameBoard.getColumnCount() && j + 3 < gameBoard.getRowCount()) {
                        if (gameBoard.board[j][i] == gameBoard.board[j + 1][i + 1] && gameBoard.board[j][i] == gameBoard.board[j + 2][i + 2] && gameBoard.board[j][i] == gameBoard.board[j + 3][i + 3]) {
                            Problem one = new Problem(j, i);
                            Problem two = new Problem(j + 1, i + 1);
                            Problem three = new Problem(j + 2, i + 2);
                            Problem four = new Problem(j + 3, i + 3);
                            threats.add(new Threat(one, two, three, four));
                        }
                    }
                    if (i > 2 && j + 3 < gameBoard.getRowCount()) {
                        if (gameBoard.board[j][i] == gameBoard.board[j + 1][i - 1] && gameBoard.board[j][i] == gameBoard.board[j + 2][i - 2] && gameBoard.board[j][i] == gameBoard.board[j + 3][i - 3]) {
                            Problem one = new Problem(j, i);
                            Problem two = new Problem(j + 1, i - 1);
                            Problem three = new Problem(j + 2, i - 2);
                            Problem four = new Problem(j + 3, i - 3);
                            threats.add(new Threat(one, two, three, four));
                        }
                    }
                }
            }

            return threats;
        }
    }
}
