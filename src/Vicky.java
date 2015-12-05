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
 * @version 151205
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
        Think.init();
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
        Think.init();

        think();
        if (mSuggestion != -1) {
            moveOnColumn(mSuggestion);
        } else {
            moveOnColumn(randomMove());
        }

        mCheck = new BoardMatrix(myGame, iAmRed);
        mThreats = Think.disableThreats(mCheck, mThreats);
    }

    /**
     * The Think method holds the key.
     * Vicky coordinates multiple analysis on the board to determine the best possible move.
     * It starts by claiming the middle, if it hasn't been claimed. There's no analysis here,
     * only a check on the lowest index of the middle column.
     * Proceeds to play a winning move, if there's any. Followed by blocking a winning move, again, if there's any.
     * If no winning or losing move has been found,
     * different methods are run looking for patterns that could be played or should be blocked.
     */

    public void think() {

        mBoard = new BoardMatrix(myGame, iAmRed);
        Problem opponentMove = Think.checkOpponentMove(mBoard, mCheck);
        if (opponentMove != null) {
            mMiddleClaimed = true;
            Think.increaseThreatLevel(mThreats, opponentMove);
        }

        if (mBoard.getCountOfBlanks() > mCheck.getCountOfBlanks()) {
            reset();
        }

        Think.findPlayables(mBoard);
        if (!mMiddleClaimed) {
            mSuggestion = claimTheMiddle();
        } else {

            if (mSuggestion == -1) {
                mSuggestion = iCanWin();
                if (mSuggestion == -1) {
                    mSuggestion = theyCanWin();
                    if (mSuggestion == -1) {
                        mSuggestion = Think.earlyWin(mBoard, mThreats);
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

        //determine if a board with even number of columns is being played
        if (myGame.getColumnCount() % 2 != 0) {
            int columns = myGame.getColumnCount();
            int rows = myGame.getRowCount();
            int middleColumnIndex = myGame.getColumnCount() / 2;

            int lowestEmptySlotIndexOnMiddleColumn = getLowestEmptyIndex(myGame.getColumn(middleColumnIndex));
            // determine if the middle has been claimed
            if (lowestEmptySlotIndexOnMiddleColumn == rows - 1) {
                mMiddleClaimed = true;
                // claims middle
                return middleColumnIndex;
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
        Think.evaluateSolutions(mBoard, mThreats);
        return Think.playWinningMove(mBoard);
    }

    /**
     * Makes three different method calls, analyzing the board looking for a possible move.
     * Every method called will return a column number if it has found a suggestion, or -1 if it hasn't,
     * which will enable the next method call.
     *
     * @return the column that would allow the opponent to win.
     */
    public int theyCanWin() {
        activeThreats();
        Think.evaluateThreats(mBoard, mThreats, mActiveThreats);

        int suggestedColumn = Think.blockWinningMove(mBoard);
        if (suggestedColumn != -1) {
            return suggestedColumn;
        } else {
            suggestedColumn = Think.identifyPatterns(mBoard);
            if (suggestedColumn != -1) {
                return suggestedColumn;
            } else {
                return Think.blockAdvance(mBoard, mThreats);
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
        // Find the top empty slot in the column
        int lowestEmptySlotIndex = getLowestEmptyIndex(myGame.getColumn(columnNumber));
        // If the column is full, lowestEmptySlot will be -1
        if (lowestEmptySlotIndex > -1)  // if the column is not full
        {
            // get the slot in this column at this index
            Connect4Slot lowestEmptySlot = myGame.getColumn(columnNumber).getSlot(lowestEmptySlotIndex);

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
        if(Think.unplayableMove(mBoard)){
            Think.revertTheUnplayables(mBoard);
        }

        int i = r.nextInt(myGame.getColumnCount());
        while (getLowestEmptyIndex(myGame.getColumn(i)) == -1 || !Think.foundPlayableInColumn(mBoard, i)) {
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
        Think.init();
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
     * It was created for evaluation and tracking purposes, used in the method
     * EvaluateThreats of the BoardMatrix class to verify that
     * the threats were being successfully marked as inactive before evaluating.
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

    /**
     * Method created for evaluation purposes. Left here for the curious.
     * Receives an ArrayList of Threats and prints them one by one.
     *
     */
    private void printTheThreats() {
        if (mThreats.size() > 0) {
            for (int y = 0; y < mThreats.size(); y++) {
                Threat toPrint = mThreats.get(y);
                System.out.println(y + " " + toPrint.printTheThreat());
            }
        }
    }

    /**
     * The Think inner class contains all the evaluation methods used to break down
     * the game board and determine the best move.
     */
    private static abstract class Think {

        private static boolean sFoundWinningMove;
        private static int sWinInColumn;
        private static boolean sFoundOpponentWinningMove;
        private static boolean sFoundAnotherOpponentWinningMove;
        private static int sBlockInColumn;
        private static int sSolvedThreats;
        private static int sSolutionsPlayed;
        private static int sPlayOnPattern;
        private static int sBlockAdvanceColumn;

        private static int sBlankQ;
        private static int sMyCharQ;
        private static int sLookingForQ;
        private static int sPlayableQ;
        private static int sUnplayableQ;
        private static int sWinnerQ;

        /**
         * Returns control variables to default
         */
        private static void init(){
            sFoundWinningMove = false;
            sWinInColumn = -1;
            sFoundOpponentWinningMove = false;
            sBlockInColumn = -1;
        }

        private static int earlyWin(BoardMatrix gameBoard, ArrayList<Threat> threats){
            int middleColumnIndex = gameBoard.board[0].length / 2;
            int lastRowIndex = gameBoard.board.length - 1;
            sMyCharQ = 0;
            sPlayableQ = 0;

            int suggestedColumn = -1;
            int threatsGenerated = -1;

            //if(board[lastRowIndex][middleColumnIndex] == myChar){

            for (int i = 0; i < gameBoard.board[0].length; i++) {
                for (int j = gameBoard.board.length - 1; j < gameBoard.board.length; j++) {
                    if (gameBoard.board[j][i] == BoardMatrix.PLAYABLE) {
                        Problem generator = new Problem(j, i);
                        int containedInThreats = 0;
                        int generatorColumn = generator.getColumn();
                        for (Threat threat : threats) {

                            if (threat.containsProblem(generator)) {
                                containedInThreats++;
                            }

                        }
                        if (containedInThreats > threatsGenerated) {
                            threatsGenerated = containedInThreats;
                            suggestedColumn = generatorColumn;
                        }

                    }

                }
            }

            return suggestedColumn;
        }

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

                for (int i = 0; i < gameBoard.board[0].length; i++) {
                    for (int j = 0; j < gameBoard.board.length; j++) {
                        if (gameBoard.getChar(j,i) != checkBoard.getChar(j,i)) {
                            opponentMove = new Problem(j, i);
                        }
                    }
                }
            }
            return opponentMove;
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
                        if (gameBoard.board[j][i] == gameBoard.board[j + 1][i]
                                && gameBoard.board[j][i] == gameBoard.board[j + 2][i]
                                && gameBoard.board[j][i] == gameBoard.board[j + 3][i]) {
                            Problem one = new Problem(j, i);
                            Problem two = new Problem(j + 1, i);
                            Problem three = new Problem(j + 2, i);
                            Problem four = new Problem(j + 3, i);
                            threats.add(new Threat(one, two, three, four));
                        }
                    }
                    if (i + 3 < gameBoard.getColumnCount()) {
                        if (gameBoard.board[j][i] == gameBoard.board[j][i + 1]
                                && gameBoard.board[j][i] == gameBoard.board[j][i + 2]
                                && gameBoard.board[j][i] == gameBoard.board[j][i + 3]) {
                            Problem one = new Problem(j, i);
                            Problem two = new Problem(j, i + 1);
                            Problem three = new Problem(j, i + 2);
                            Problem four = new Problem(j, i + 3);
                            threats.add(new Threat(one, two, three, four));
                        }
                    }
                    if (i + 3 < gameBoard.getColumnCount() && j + 3 < gameBoard.getRowCount()) {
                        if (gameBoard.board[j][i] == gameBoard.board[j + 1][i + 1]
                                && gameBoard.board[j][i] == gameBoard.board[j + 2][i + 2]
                                && gameBoard.board[j][i] == gameBoard.board[j + 3][i + 3]) {
                            Problem one = new Problem(j, i);
                            Problem two = new Problem(j + 1, i + 1);
                            Problem three = new Problem(j + 2, i + 2);
                            Problem four = new Problem(j + 3, i + 3);
                            threats.add(new Threat(one, two, three, four));
                        }
                    }
                    if (i > 2 && j + 3 < gameBoard.getRowCount()) {
                        if (gameBoard.board[j][i] == gameBoard.board[j + 1][i - 1]
                                && gameBoard.board[j][i] == gameBoard.board[j + 2][i - 2]
                                && gameBoard.board[j][i] == gameBoard.board[j + 3][i - 3]) {
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
         * After making a move, this method is called to disable all the threats solved by it.
         * @param gameBoard The board after the move has been sent.
         * @param threats The list of threats to be scanned.
         * @return the list of threats updated.
         */
        private static ArrayList<Threat> disableThreats(BoardMatrix gameBoard, ArrayList<Threat> threats){
            for (int i = 0; i < gameBoard.board[0].length; i++) {
                for (int j = 0; j < gameBoard.board.length; j++) {
                    if (gameBoard.board[j][i] == BoardMatrix.myChar) {
                        Problem solver = new Problem(j, i);
                        for (Threat threat : threats) {
                            if (threat.isActive()) {
                                if (threat.containsProblem(solver)) {
                                    threat.setThreatLevel(0);
                                    threat.disable();
                                }
                            }
                        }
                    }
                }
            }
            return threats;
        }

        /**
         * This method turns the positions tagged as unplayable into playable
         * when there are no other possible moves. It's actually very important so that Vicky
         * blocks a winning move by the opponent even if it'd enable another winning move by the opponent,
         * or if it'd give the opponent to opportunity to destroy a winning threat created by Vicky.
         */
        private static void revertTheUnplayables(BoardMatrix gameBoard){
            for (int x = 0; x < gameBoard.board[0].length; x++) {
                for (int y = gameBoard.board.length - 1; y >= 0; y--) {
                    if (gameBoard.board[y][x] == BoardMatrix.UNPLAYABLE
                            || gameBoard.board[y][x] == BoardMatrix.WINNER) {
                        if (!foundPlayableInColumn(gameBoard, x)) {
                            gameBoard.board[y][x] = BoardMatrix.PLAYABLE;
                        }
                    }
                }
            }
        }

        /**
         * Method called by the random move generator to determine if it's necessary to unlock
         * the unplayable moves so that a move can be sent to the board.
         * This method does not revert the unplayables.
         *
         * @param processedBoard this method requires a Matrix that has already gone through the system.
         * @return true if all open slots are tagged as unplayable.
         */
        private static boolean unplayableMove(BoardMatrix processedBoard){
            boolean haveToRevertTheUnplayables = false;
            boolean foundPlayable = false;
            int unplayableQ = 0;

            for(int x = 0; x < processedBoard.board[0].length; x++){
                for (int y = 0; y < processedBoard.board.length; y++){
                    if(processedBoard.board[y][x] == BoardMatrix.PLAYABLE){
                        foundPlayable = true;
                    }
                    else if (processedBoard.board[y][x] == BoardMatrix.UNPLAYABLE){
                        unplayableQ++;
                    }
                }
            }

            if(!foundPlayable && unplayableQ > 0){
                haveToRevertTheUnplayables = true;
            }

            return haveToRevertTheUnplayables;
        }

        /**
         * Support method to make sure that Vicky doesn't play where she shouldn't.
         * There may be available slots in a column, but playing it may surrender the game
         * or blow an opportunity to win the game.
         * @param gameBoard the processed game board.
         * @param column to be scanned.
         * @return true if a playable position has been found in the column.
         */
        private static boolean foundPlayableInColumn(BoardMatrix gameBoard, int column){
            boolean foundPlayable = false;
            for (int j = 0; j < gameBoard.board.length; j++){
                if (gameBoard.board[j][column] == BoardMatrix.PLAYABLE){
                    foundPlayable = true;
                }
            }
            return foundPlayable;
        }

        /**
         * Support method to scan a column looking for unplayable moves.
         * @param gameBoard the processed game board.
         * @param column to be scanned.
         * @return true if an unplayable position has been found.
         */
        private static boolean foundUnplayableInColumn(BoardMatrix gameBoard, int column){
            boolean foundUnplayable = false;
            for (int j = 0; j < gameBoard.board.length; j++) {
                if (gameBoard.board[j][column] == BoardMatrix.UNPLAYABLE) {
                    foundUnplayable = true;
                }
            }
            return foundUnplayable;
        }

        /**
         * Support method to get the String representation of a possible move.
         * Created for evaluation purposes. All calls for this method have been removed.
         * @param processedBoard The game board.
         * @param column The column in question.
         * @return String representation of a position.
         */
        private static String getPlayableInColumn(BoardMatrix processedBoard, int column){
            if (foundPlayableInColumn(processedBoard, column)) {

                for (int j = 0; j < processedBoard.board.length; j++) {
                    if (processedBoard.board[j][column] == BoardMatrix.PLAYABLE) {
                        Problem found = new Problem(j, column);
                        return found.toPrint();
                    }
                }

            }
            return "";
        }

        /**
         * If a playable slot has been found in a column, this method scans the column looking for the index.
         * @param processedBoard the current game board to be scanned.
         * @param column The specific column in the board to be scanned.
         * @return The index of the playable slot.
         */
        private static int getPlayableInColumnIndex(BoardMatrix processedBoard, int column){
            if (foundPlayableInColumn(processedBoard, column)) {

                for (int j = 0; j < processedBoard.board.length; j++) {
                    if (processedBoard.board[j][column] == BoardMatrix.PLAYABLE) {

                        return j;
                    }
                }

            }
            return -1;
        }

        /**
         * This method is basically the defensive stance of Vicky. Evaluates each active threat to determine
         * if there's a need to block a winning move, or a growing threat that needs to be kept under control.
         *
         * @param gameBoard The current game board to be evaluated
         * @param threats Receives the threats from Vicky.
         * @param activeThreats The number of threats that remain active. For evaluation purposes.
         */
        private static void evaluateThreats(BoardMatrix gameBoard, ArrayList<Threat> threats, int activeThreats){
            int x = 0;
            do {
                Threat toEvaluate = threats.get(x);
                sFoundOpponentWinningMove = false;

                if (toEvaluate.isActive()) {
                    char[] charsInThreat = reevaluateThreatChars(gameBoard, toEvaluate);

                    if (sLookingForQ == 3 && sPlayableQ == 1) {
                        sFoundOpponentWinningMove = true;
                        reevaluateThreat(gameBoard, BoardMatrix.lookingFor, sFoundOpponentWinningMove, toEvaluate);
                    } else if (sLookingForQ == 3 && sWinnerQ == 1) {
                        for (int y = 0; y < toEvaluate.problems.length; y++) {
                            Problem solver = new Problem(toEvaluate.problems[y].getRow(), toEvaluate.problems[y].getColumn());
                            char valueOfProblem = gameBoard.getChar(solver.getRow(), solver.getColumn());
                            if (valueOfProblem == BoardMatrix.WINNER) {
                                if (!foundPlayableInColumn(gameBoard, solver.getColumn()) && !foundUnplayableInColumn(gameBoard, solver.getColumn())) {

                                    for (int d = 0; d < toEvaluate.problems.length; d++) {
                                        Problem anotherSolver = new Problem(toEvaluate.problems[d].getRow(), toEvaluate.problems[d].getColumn());
                                        char revisit = gameBoard.getChar(anotherSolver.getRow(), anotherSolver.getColumn());
                                        if (revisit != BoardMatrix.lookingFor) {
                                            int columnRevised = solver.getColumn();
                                            int playableInColumn = getPlayableInColumnIndex(gameBoard, columnRevised);
                                            if (playableInColumn != -1) {
                                                if (playableInColumn == solver.getRow()) {
                                                    sFoundOpponentWinningMove = true;
                                                    reevaluateThreat(gameBoard, BoardMatrix.lookingFor, sFoundOpponentWinningMove, toEvaluate);
                                                }
                                            }
                                        }

                                    }
                                } else {
                                    sFoundOpponentWinningMove = false;
                                    reevaluateThreat(gameBoard, BoardMatrix.lookingFor, sFoundOpponentWinningMove, toEvaluate);
                                }
                            }
                        }

                    } else if (sLookingForQ == 3 && sUnplayableQ == 1) {
                        for (int y = 0; y < toEvaluate.problems.length; y++) {

                            Problem solver = new Problem(toEvaluate.problems[y].getRow(), toEvaluate.problems[y].getColumn());
                            char valueOfProblem = gameBoard.getChar(solver.getRow(), solver.getColumn());

                            if (valueOfProblem == BoardMatrix.UNPLAYABLE) {
                                int losingColumn = solver.getColumn();
                                int losingRow = solver.getRow();

                                if (losingRow == 5) {
                                    gameBoard.board[losingRow][losingColumn] = BoardMatrix.PLAYABLE;
                                    sFoundOpponentWinningMove = true;
                                    reevaluateThreat(gameBoard, BoardMatrix.lookingFor, sFoundOpponentWinningMove, toEvaluate);
                                } else {
                                    char[] charsInColumn = reevaluateColumnChars(gameBoard, losingColumn);
                                    if (sPlayableQ == 0) {
                                        if (sUnplayableQ >= 1) {
                                            int highestUnplayableIndex = -1;
                                            for (int u = 0; u < charsInColumn.length; u++) {
                                                if (charsInColumn[u] == BoardMatrix.UNPLAYABLE) {
                                                    if (u > highestUnplayableIndex) {
                                                        highestUnplayableIndex = u;
                                                    }
                                                }
                                            }
                                            if (highestUnplayableIndex == losingRow) {
                                                gameBoard.board[highestUnplayableIndex][losingColumn] = BoardMatrix.PLAYABLE;
                                                sFoundOpponentWinningMove = true;
                                                reevaluateThreat(gameBoard, BoardMatrix.lookingFor, sFoundOpponentWinningMove, toEvaluate);
                                            } else {
                                                reevaluateThreat(gameBoard, BoardMatrix.lookingFor, sFoundOpponentWinningMove, toEvaluate);
                                            }
                                        }
                                    } else {
                                        reevaluateThreat(gameBoard, BoardMatrix.lookingFor, sFoundOpponentWinningMove, toEvaluate);
                                    }
                                }
                            }

                        }
                    } else if (sLookingForQ == 3 && (sBlankQ > 0 || sUnplayableQ > 0 || sWinnerQ > 0)) {
                        reevaluateThreat(gameBoard, BoardMatrix.lookingFor, sFoundOpponentWinningMove, threats.get(x));
                    }
                }
                if (sBlockInColumn == -1) {
                    x++;
                }
            }
            while (sBlockInColumn == -1 && x < threats.size());
        }

        /**
         * This method takes the list of threats and evaluates the possibilities of Vicky
         * to take advantage of the board. It determines if there's a playable winning move and
         * passes it on to reevaluateThreats to play the winning move. It also detects not-yet-playable
         * winning threats, sends it to reevaluateThreat as a non-winning move, the missing slot is branded as
         * winner, and the position bellow is marked as unplayable to protect the winning move.
         *
         * @param gameBoard The current game board to be evaluated
         * @param threats Receives the threats from Vicky.
         */
        private static void evaluateSolutions(BoardMatrix gameBoard, ArrayList<Threat> threats){
            int x = 0;
            do {
                Threat toEvaluate = threats.get(x);
                sFoundWinningMove = false;

                if (toEvaluate.getThreatLevel() == 0) {

                    char[] charsInThreat = reevaluateThreatChars(gameBoard, toEvaluate);

                    if (sMyCharQ == 3 && sPlayableQ == 1) {
                        sFoundWinningMove = true;
                        reevaluateThreat(gameBoard, BoardMatrix.myChar, sFoundWinningMove, toEvaluate);
                    }
                    if (sMyCharQ == 3 && sWinnerQ == 1) {

                        int indexWinner = -1;
                        int rowOfWinner;
                        int columnOfWinner;
                        for (int a = 0; a < charsInThreat.length; a++) {
                            if (charsInThreat[a] == BoardMatrix.WINNER) {
                                rowOfWinner = toEvaluate.problems[a].getRow();
                                columnOfWinner = toEvaluate.problems[a].getColumn();

                                if (rowOfWinner == -1 || columnOfWinner == -1) {
                                    char[] charsInColumn = reevaluateColumnChars(gameBoard, columnOfWinner);
                                    if (sWinnerQ > 0) {
                                        if (sPlayableQ == 0 && sUnplayableQ == 0 && sBlankQ == 0) {
                                            Problem solver = new Problem(rowOfWinner, columnOfWinner);
                                            gameBoard.board[rowOfWinner][columnOfWinner] = BoardMatrix.PLAYABLE;
                                            sFoundWinningMove = true;
                                            reevaluateThreat(gameBoard, BoardMatrix.myChar, sFoundWinningMove, toEvaluate);
                                        }
                                    }
                                } else {
                                    Problem error = new Problem(rowOfWinner, columnOfWinner);
                                }
                            }
                        }

                    }
                    if (sMyCharQ == 3 && (sBlankQ > 0 || sUnplayableQ > 0)) {
                        reevaluateThreat(gameBoard, BoardMatrix.myChar, sFoundWinningMove, toEvaluate);
                    }

                }
                if (sWinInColumn == -1) {
                    x++;
                }
            } while (sWinInColumn == -1 && x < threats.size());
        }

        /**
         * if the method evaluateThreat has found a winning move, reevaluateThreat will assign
         * the specific column where to make a move in order to either make the final move to win the
         * game OR to block the winning move by the opponent.
         *
         * If the method evaluateThreat has found a not-yet-playable winning move, the
         * column will be evaluated to determine if the column is to be avoided or if it's still
         * possible to move there without giving up the game or losing a created threat.
         * To achieve this, the slots the opponent requires to either win or disabling Vicky's threat
         * will be tagged as unplayable.
         *
         * This method can be called to evaluate either Vicky's position or the opponent's, that's why
         * it's necessary to identify the player who's threat needs to be reevaluated.
         *
         * @param gameBoard the current game board to be evaluated
         * @param player The player who owns the threat
         * @param winningMoveFound Highlights the importance of the reevaluation.
         * @param toReevaluate The threat that needs to be reevaluated
         */
        private static void reevaluateThreat(BoardMatrix gameBoard, char player, boolean winningMoveFound, Threat toReevaluate){

            boolean winnerFound = winningMoveFound;
            if (winningMoveFound) {
                for (int i = 0; i < toReevaluate.problems.length; i++) {
                    char valueOfProblem = gameBoard.getChar(toReevaluate.problems[i].getRow(), toReevaluate.problems[i].getColumn());
                    if (player == BoardMatrix.myChar &&
                            ((valueOfProblem == BoardMatrix.PLAYABLE || valueOfProblem == BoardMatrix.UNPLAYABLE)
                                    || valueOfProblem == BoardMatrix.WINNER)) {

                        gameBoard.board[toReevaluate.problems[i].getRow()][toReevaluate.problems[i].getColumn()] = BoardMatrix.PLAYABLE;
                        sWinInColumn = toReevaluate.problems[i].getColumn();

                    } else if ((valueOfProblem == BoardMatrix.PLAYABLE
                            || valueOfProblem == BoardMatrix.UNPLAYABLE)
                            || valueOfProblem == BoardMatrix.WINNER) {
                        gameBoard.board[toReevaluate.problems[i].getRow()][toReevaluate.problems[i].getColumn()] = BoardMatrix.PLAYABLE;
                        sBlockInColumn = toReevaluate.problems[i].getColumn();
                    }

                }
                if (sBlockInColumn == -1 && sWinInColumn == -1) {
                    winnerFound = false;
                }
            }
            if (!winnerFound) {
                for (int i = 0; i < toReevaluate.problems.length; i++) {
                    char valueOfProblem = gameBoard.getChar(toReevaluate.problems[i].getRow(), toReevaluate.problems[i].getColumn());
                    if (valueOfProblem != player) {
                        int threatInColumn = toReevaluate.problems[i].getColumn();
                        int threatInRow = toReevaluate.problems[i].getRow();
                        if (player == BoardMatrix.myChar) {
                            if (gameBoard.board[threatInRow][threatInColumn] == BoardMatrix.WINNER) {
                                if (!foundPlayableInColumn(gameBoard, threatInColumn) && !foundUnplayableInColumn(gameBoard, threatInColumn)) {
                                    sWinInColumn = toReevaluate.problems[i].getColumn();
                                }
                            } else {
                                gameBoard.board[threatInRow][threatInColumn] = BoardMatrix.WINNER;
                            }
                        }
                        if (threatInRow + 1 < gameBoard.board.length) {
                            if (gameBoard.board[threatInRow + 1][threatInColumn] != BoardMatrix.WINNER) {
                                if (gameBoard.board[threatInRow + 1][threatInColumn] == BoardMatrix.BLANK
                                        || gameBoard.board[threatInRow + 1][threatInColumn] == BoardMatrix.PLAYABLE
                                        || gameBoard.board[threatInRow + 1][threatInColumn] == BoardMatrix.UNPLAYABLE) {
                                    gameBoard.board[threatInRow + 1][threatInColumn] = BoardMatrix.UNPLAYABLE;
                                }
                            }
                        }
                    }
                }
            }
        }

        /**
         * Method used to reevaluate a column when a threat is composed of three tokens of the same player.
         * This method is crucial to make sure that a winning move is played, or a losing move is blocked.
         *
         * @param gameBoard The current game board to be evaluated.
         * @param column The column to be reevaluated.
         * @return an array containing the char values of all the slots in the column
         */
        private static char[] reevaluateColumnChars(BoardMatrix gameBoard, int column){
            sBlankQ = 0;
            sMyCharQ = 0;
            sLookingForQ = 0;
            sPlayableQ = 0;
            sUnplayableQ = 0;
            sWinnerQ = 0;

            Problem solver;
            char[] columnChars = new char[gameBoard.board.length];

            for (int j = 0; j < gameBoard.board.length; j++) {
                solver = new Problem(j, column);
                char valueOfProblem = gameBoard.getChar(solver.getRow(), solver.getColumn());
                columnChars[j] = valueOfProblem;

                if (valueOfProblem == BoardMatrix.myChar) {
                    sMyCharQ++;
                }
                if (valueOfProblem == BoardMatrix.WINNER) {
                    sWinnerQ++;
                }
                if (valueOfProblem == BoardMatrix.lookingFor) {
                    sLookingForQ++;
                }
                if (valueOfProblem == BoardMatrix.BLANK) {
                    sBlankQ++;
                }
                if (valueOfProblem == BoardMatrix.PLAYABLE) {
                    sPlayableQ++;
                }
                if (valueOfProblem == BoardMatrix.UNPLAYABLE) {
                    sUnplayableQ++;
                }
            }
            return columnChars;
        }

        /**
         * Method used to reevaluate a threat when it is found to be composed of three tokens of the same player.
         * This method is crucial to make sure that a winning move is played, or a losing move is blocked.
         *
         * @param gameBoard The current game board to be evaluated.
         * @param toEvaluate The threat object to be sorted.
         * @return an array containing the char values of all the slots in the column
         */
        private static char[] reevaluateThreatChars(BoardMatrix gameBoard, Threat toEvaluate){
            sBlankQ = 0;
            sMyCharQ = 0;
            sLookingForQ = 0;
            sPlayableQ = 0;
            sUnplayableQ = 0;
            sWinnerQ = 0;

            Problem solver;
            char[] threatChars = new char[toEvaluate.problems.length];

            for (int x = 0; x < toEvaluate.problems.length; x++) {
                solver = toEvaluate.problems[x];
                char valueOfProblem = gameBoard.getChar(solver.getRow(), solver.getColumn());
                threatChars[x] = valueOfProblem;

                if (valueOfProblem == BoardMatrix.myChar) {
                    sMyCharQ++;
                }
                if (valueOfProblem == BoardMatrix.WINNER) {
                    sWinnerQ++;
                }
                if (valueOfProblem == BoardMatrix.lookingFor) {
                    sLookingForQ++;
                }
                if (valueOfProblem == BoardMatrix.BLANK) {
                    sBlankQ++;
                }
                if (valueOfProblem == BoardMatrix.PLAYABLE) {
                    sPlayableQ++;
                }
                if (valueOfProblem == BoardMatrix.UNPLAYABLE) {
                    sUnplayableQ++;
                }

            }
            return threatChars;
        }

        /**
         * Vicky has a winning move and needs to play it. This method tells her where.
         *
         * @param gameBoard the current game board to be evaluated.
         * @return The column to be played to win the game.
         */
        private static int playWinningMove(BoardMatrix gameBoard){
            scanForWinners(gameBoard);
            return sWinInColumn;
        }

        /**
         * This method scans the board looking for positions marked as winner, and if it finds nothing but winners
         * it means that the winner positions are playable, in which case: the revertTheUnplayables method is called
         * so that Vicky can play on any one of those winners.
         */
        private static void scanForWinners(BoardMatrix gameBoard){
            for (int i = 0; i < gameBoard.board[0].length; i++) {
                for (int j = 0; j < gameBoard.board.length; j++) {
                    if (gameBoard.board[j][i] == BoardMatrix.WINNER && !foundPlayableInColumn(gameBoard, i) && !foundUnplayableInColumn(gameBoard, i)) {
                        sWinInColumn = i;
                        revertTheUnplayables(gameBoard);
                    }
                }
            }
        }

        /**
         * This method scans the board looking for possible advances the opponent might be making
         * and stops them before they grow further.
         *
         * First stop, identifying threats composed of two opponent slots, a playable slot and a blank slot.
         * If found, Vicky will take the playable spot, breaking the potential threat.
         *
         * First part of the method ensure that priority is given to blocking an opponent's move on the board.
         * Second part will also consider Vicky's possible growing threats, and decide which one to play based on
         * the number of threats it can solve, or the number of claimed threats it can play in.
         *
         * @param gameBoard The current game board to be evaluated.
         * @param threats Receives the threats from Vicky.
         */
        private static int blockAdvance(BoardMatrix gameBoard, ArrayList<Threat> threats){
            Problem solver;
            sSolvedThreats = 0;
            sSolutionsPlayed = 0;
            sBlockAdvanceColumn = -1;

            for (int i = 0; i < threats.size(); i++) {

                Threat toEvaluate = threats.get(i);
                char[] charsInThreat = reevaluateThreatChars(gameBoard, toEvaluate);

                if (sLookingForQ == 2 //|| (myCharQ + winnerQ) == 2)
                        && (sPlayableQ == 2 || (sPlayableQ + sBlankQ == 2))) {

                    if (charsInThreat[0] == BoardMatrix.PLAYABLE && charsInThreat[1] == BoardMatrix.lookingFor
                            && charsInThreat[2] == BoardMatrix.PLAYABLE && charsInThreat[3] == BoardMatrix.lookingFor) {
                        sBlockAdvanceColumn = toEvaluate.problems[2].getColumn();
                    }
                    if (charsInThreat[0] == BoardMatrix.lookingFor && charsInThreat[1] == BoardMatrix.PLAYABLE
                            && charsInThreat[2] == BoardMatrix.lookingFor && charsInThreat[3] == BoardMatrix.PLAYABLE) {
                        sBlockAdvanceColumn = toEvaluate.problems[1].getColumn();
                    }
                }

                if (sBlockAdvanceColumn == -1) {
                    if ((
                            (sLookingForQ == 2 || (sMyCharQ + sWinnerQ) == 2) && sPlayableQ >= 1 && sBlankQ >= 1)
                            && sBlockAdvanceColumn == -1) {

                        int a = 0;
                        char valueOfProblem/* = BLANK*/;
                        do {
                            solver = toEvaluate.problems[a];
                            valueOfProblem = gameBoard.getChar(solver.getRow(), solver.getColumn());
                            a++;
                        } while (valueOfProblem != BoardMatrix.PLAYABLE && a < toEvaluate.problems.length);


                        int solutionsPlayedBySolver = 0;
                        for (Threat threat : threats) {
                            if (threat.getThreatLevel() == 0) {
                                if (threat.containsProblem(solver)) {
                                    solutionsPlayedBySolver++;
                                }
                            }
                        }

                        int threatsSolvedBySolver = 0;

                        for (Threat threat : threats) {
                            if (threat.isActive()) {
                                if (threat.containsProblem(solver)) {
                                    threatsSolvedBySolver++;
                                }
                            }
                        }

                        int mostSolutions;
                        if (solutionsPlayedBySolver > threatsSolvedBySolver) {
                            mostSolutions = solutionsPlayedBySolver;
                        } else {
                            mostSolutions = threatsSolvedBySolver;
                        }

                        if(mostSolutions > sSolvedThreats){
                            sSolvedThreats = mostSolutions;
                            sBlockAdvanceColumn = solver.getColumn();
                        }
                    }
                }

            }
            return sBlockAdvanceColumn;
        }

        /**
         * Method called by Vicky to know if there's a move that needs to be blocked.
         * @param gameBoard The current game board.
         * @return The column that needs to be played in order to block a winning move.
         */
        private static int blockWinningMove(BoardMatrix gameBoard){
            if(sBlockInColumn != -1){
                revertTheUnplayables(gameBoard);
            }
            return sBlockInColumn;
        }

        /**
         * This method will evaluate five consecutive slots, and act on this five slots threat depending on the values found in the
         * three middle slots. This method is considered both an offensive and a defensive strategy, as it will either play or block
         * a growing pattern.
         *
         * @return The middle column that needs to be played to create or destroy a double threat.
         */
        private static int identifyPatterns(BoardMatrix gameBoard){

            sPlayOnPattern = -1;

            for (int i = 0; i < gameBoard.board[0].length; i++) {
                for (int j = 0; j < gameBoard.board.length; j++) {

                    if (i + 4 < gameBoard.board[0].length) {
                        if ((gameBoard.board[j][i] == BoardMatrix.BLANK || gameBoard.board[j][i] == BoardMatrix.PLAYABLE)
                                && (gameBoard.board[j][i + 4] == BoardMatrix.BLANK || gameBoard.board[j][i + 4] == BoardMatrix.PLAYABLE)) {

                            if ((gameBoard.board[j][i + 1] == BoardMatrix.myChar || gameBoard.board[j][i + 1] == BoardMatrix.lookingFor) && (gameBoard.board[j][i + 1] == gameBoard.board[j][i + 3]) && gameBoard.board[j][i + 2] == BoardMatrix.PLAYABLE) {
                                sPlayOnPattern = i + 2;
                            }
                            if ((gameBoard.board[j][i + 1] == BoardMatrix.myChar || gameBoard.board[j][i + 1] == BoardMatrix.lookingFor) && (gameBoard.board[j][i + 1] == gameBoard.board[j][i + 2]) && gameBoard.board[j][i + 3] == BoardMatrix.PLAYABLE) {
                                sPlayOnPattern = i + 3;
                            } else if ((gameBoard.board[j][i + 2] == BoardMatrix.myChar || gameBoard.board[j][i + 2] == BoardMatrix.lookingFor) && (gameBoard.board[j][i + 2] == gameBoard.board[j][i + 3]) && gameBoard.board[j][i + 1] == BoardMatrix.PLAYABLE) {
                                sPlayOnPattern = i + 1;
                            }
                        }
                    }
                    if (j + 4 < gameBoard.board.length && i + 4 < gameBoard.board[0].length) {
                        if ((gameBoard.board[j][i] == BoardMatrix.BLANK || gameBoard.board[j][i] == BoardMatrix.PLAYABLE)
                                && (gameBoard.board[j + 4][i + 4] == BoardMatrix.BLANK || gameBoard.board[j + 4][i + 4] == BoardMatrix.PLAYABLE)) {

                            if ((gameBoard.board[j + 1][i + 1] == BoardMatrix.myChar || gameBoard.board[j + 1][i + 1] == BoardMatrix.lookingFor) && (gameBoard.board[j + 1][i + 1] == gameBoard.board[j + 3][i + 3]) && gameBoard.board[j + 2][i + 2] == BoardMatrix.PLAYABLE) {
                                sPlayOnPattern = i + 2;
                            } else if ((gameBoard.board[j + 1][i + 1] == BoardMatrix.myChar || gameBoard.board[j + 1][i + 1] == BoardMatrix.lookingFor) && (gameBoard.board[j + 1][i + 1] == gameBoard.board[j + 2][i + 2]) && gameBoard.board[j + 3][i + 3] == BoardMatrix.PLAYABLE) {
                                sPlayOnPattern = i + 3;
                            } else if ((gameBoard.board[j + 2][i + 2] == BoardMatrix.myChar || gameBoard.board[j + 2][i + 2] == BoardMatrix.lookingFor) && (gameBoard.board[j + 2][i + 2] == gameBoard.board[j + 3][i + 3]) && gameBoard.board[j + 1][i + 1] == BoardMatrix.PLAYABLE) {
                                sPlayOnPattern = i + 1;
                            }
                        }
                    }
                    if (i > 3 && j + 4 < gameBoard.board.length) {
                        if ((gameBoard.board[j][i] == BoardMatrix.BLANK || gameBoard.board[j][i] == BoardMatrix.PLAYABLE)
                                && (gameBoard.board[j + 4][i - 4] == BoardMatrix.BLANK || gameBoard.board[j + 4][i - 4] == BoardMatrix.PLAYABLE)) {
                            if ((gameBoard.board[j + 1][i - 1] == BoardMatrix.myChar || gameBoard.board[j + 1][i - 1] == BoardMatrix.lookingFor) && (gameBoard.board[j + 1][i - 1] == gameBoard.board[j + 3][i - 3]) && gameBoard.board[j + 2][i - 2] == BoardMatrix.PLAYABLE) {
                                sPlayOnPattern = i - 2;
                            } else if ((gameBoard.board[j + 1][i - 1] == BoardMatrix.myChar || gameBoard.board[j + 1][i - 1] == BoardMatrix.lookingFor) && (gameBoard.board[j + 1][i - 1] == gameBoard.board[j + 2][i - 2]) && gameBoard.board[j + 3][i - 3] == BoardMatrix.PLAYABLE) {
                                sPlayOnPattern = i - 3;
                            } else if ((gameBoard.board[j + 2][i - 2] == BoardMatrix.myChar || gameBoard.board[j + 2][i - 2] == BoardMatrix.lookingFor) && (gameBoard.board[j + 2][i - 2] == gameBoard.board[j + 3][i - 3]) && gameBoard.board[j + 1][i - 1] == BoardMatrix.PLAYABLE) {
                                sPlayOnPattern = i - 1;
                            }
                        }
                    }
                }
            }
            return sPlayOnPattern;
        }
    }
}
