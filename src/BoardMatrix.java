import java.util.ArrayList;

/**
 * Udacity's Intro to Java Nanodegree Course Final Project.
 * <p>
 * The class BoardMatrix creates a char matrix of the board
 * for the Connect 4 game. It is not a stand-alone class, it is tied deeply to Vicky.
 * It also relies deeply on the Threat and Problem classes.
 *
 * @author Henry Lopez-Ingram
 * @version 151006
 */
public class BoardMatrix extends Matrix {
    final char UNPLAYABLE = 'X';
    final char WINNER = 'W';
    final char THREAT = 'T';
    char[][] check;
    boolean foundWinningMove;
    int winInColumn;
    boolean foundOpponentWinningMove;
    boolean foundAnotherOpponentWinningMove;
    int blockInColumn;
    int solvedThreats;
    int solutionsPlayed;
    int playOnPattern;
    int blockAdvanceColumn;
    boolean hadToRevertTheUnplayables;
    private Connect4Game mLocalCopy;
    private int blankQ;
    private int myCharQ;
    private int lookingForQ;
    private int playableQ;
    private int unplayableQ;
    private int winnerQ;


    /**
     * Creates a char representation of the board for evaluation purposes
     *
     * @param aGame    receives the game from Vicky
     * @param redAgent Vicky identifies herself to the BoardMatrix class
     */
    public BoardMatrix(Connect4Game aGame, boolean redAgent) {
        mLocalCopy = aGame;
        identifyPlayer(redAgent);

        /* Initializing values */
        foundWinningMove = false;
        winInColumn = -1;
        foundOpponentWinningMove = false;
        blockInColumn = -1;

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
     * When the game starts, this method scans the board initializing all possible Threats
     * found in the board. This method is controlled by Vicky, and enabled by the Threat and
     * the Problem class. A Problem object holds the coordinates of a slot, a Threat object holds
     * an array of the four Problems that compose a Threat, and Vicky holds an ArrayList of Threats.
     *
     * @return All the possible threats
     */
    public ArrayList<Threat> identifyThreats() {
        ArrayList<Threat> threats = new ArrayList<>();
        for (int i = 0; i < mLocalCopy.getColumnCount(); i++) {
            for (int j = 0; j < mLocalCopy.getRowCount(); j++) {
                if (j + 3 < mLocalCopy.getRowCount()) {
                    if (board[j][i] == board[j + 1][i] && board[j][i] == board[j + 2][i] && board[j][i] == board[j + 3][i]) {
                        Problem one = new Problem(j, i);
                        Problem two = new Problem(j + 1, i);
                        Problem three = new Problem(j + 2, i);
                        Problem four = new Problem(j + 3, i);
                        threats.add(new Threat(one, two, three, four));
                    }
                }
                if (i + 3 < mLocalCopy.getColumnCount()) {
                    if (board[j][i] == board[j][i + 1] && board[j][i] == board[j][i + 2] && board[j][i] == board[j][i + 3]) {
                        Problem one = new Problem(j, i);
                        Problem two = new Problem(j, i + 1);
                        Problem three = new Problem(j, i + 2);
                        Problem four = new Problem(j, i + 3);
                        threats.add(new Threat(one, two, three, four));
                    }
                }
                if (i + 3 < mLocalCopy.getColumnCount() && j + 3 < mLocalCopy.getRowCount()) {
                    if (board[j][i] == board[j + 1][i + 1] && board[j][i] == board[j + 2][i + 2] && board[j][i] == board[j + 3][i + 3]) {
                        Problem one = new Problem(j, i);
                        Problem two = new Problem(j + 1, i + 1);
                        Problem three = new Problem(j + 2, i + 2);
                        Problem four = new Problem(j + 3, i + 3);
                        threats.add(new Threat(one, two, three, four));
                    }
                }
                if (i > 2 && j + 3 < mLocalCopy.getRowCount()) {
                    if (board[j][i] == board[j + 1][i - 1] && board[j][i] == board[j + 2][i - 2] && board[j][i] == board[j + 3][i - 3]) {
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
     * This method takes the list of threats and evaluates the possibilities of Vicky
     * to take advante of the board. It determines if there's a playable winning move and
     * passes it on to reevaluateThreats to play the winning move. It also detects not-yet-playable
     * winning threats, sends it to reevaluateThreat as a non-winning move, the missing slot is branded as
     * winner, and the position bellow is marked as unplayable to protect the winning move.
     *
     * @param threats Receives the threats from Vicky.
     */
    public void evaluateSolutions(ArrayList<Threat> threats) {

        //for(int x = 0; x < threats.size(); x++){
        int x = 0;
        do {
            Threat toEvaluate = threats.get(x);
            foundWinningMove = false;

            if (toEvaluate.getThreatLevel() == 0) {

                char[] charsInThreat = reevaluateThreatChars(toEvaluate);

                if (myCharQ == 3 && playableQ == 1) {
                    foundWinningMove = true;
                    reevaluateThreat(myChar, foundWinningMove, toEvaluate);
                }
                if (myCharQ == 3 && winnerQ == 1) {

                    int indexWinner = -1;
                    int rowOfWinner = -1;
                    int columnOfWinner = -1;
                    for (int a = 0; a < charsInThreat.length; a++) {
                        if (charsInThreat[a] == WINNER) {
                            indexWinner = a;
                            rowOfWinner = toEvaluate.problems[a].getRow();
                            columnOfWinner = toEvaluate.problems[a].getColumn();

                            if (rowOfWinner == -1 || columnOfWinner == -1) {
                                char[] charsInColumn = reevaluateColumnChars(columnOfWinner);
                                if (winnerQ > 0) {
                                    if (playableQ == 0 && unplayableQ == 0 && blankQ == 0) {
                                        Problem solver = new Problem(rowOfWinner, columnOfWinner);
                                        board[rowOfWinner][columnOfWinner] = PLAYABLE;
                                        foundWinningMove = true;
                                        reevaluateThreat(myChar, foundWinningMove, toEvaluate);
                                    }
                                } else {
                                }

                            } else {
                                Problem error = new Problem(rowOfWinner, columnOfWinner);
                            }
                        }
                    }

                }
                if (myCharQ == 3 && (blankQ > 0 || unplayableQ > 0)) {
                    reevaluateThreat(myChar, foundWinningMove, toEvaluate);
                }

            }
            if (winInColumn == -1) {
                x++;
            }
        } while (winInColumn == -1 && x < threats.size());
        //}

    }

    /**
     * Vicky has a winning move and needs to play it. This method tells her where.
     *
     * @return The column to be played to win the game.
     */
    public int playWinningMove() {
        scanForWinners();
        return winInColumn;
    }

    /**
     * This method scans the board looking for positions marked as winner, and if it finds nothing but winners
     * it means that the winner positions are playable, in which case: the revertTheUnplayables method is called
     * so that Vicky can play on any one of those winners.
     */
    public void scanForWinners() {
        for (int i = 0; i < board[0].length; i++) {
            for (int j = 0; j < board.length; j++) {
                if (board[j][i] == WINNER && !foundPlayableInColumn(i) && !foundUnplayableInColumn(i)) {
                    winInColumn = i;
                    revertTheUnplayables();
                }
            }
        }
    }

    /**
     * This method is basically the defensive stance of Vicky. Evaluates each active threat to determine
     * if there's a need to block a winning move, or a growing threat that needs to be kept under control.
     *
     * @param threats       Receives the threats from Vicky.
     * @param activeThreats The number of threats that remain active. For evaluation purposes.
     */
    public void evaluateThreats(ArrayList<Threat> threats, int activeThreats) {
        //for(int x = 0; x < threats.size(); x++)
        int x = 0;
        do {
            Threat toEvaluate = threats.get(x);
            foundOpponentWinningMove = false;

            if (toEvaluate.isActive()) {
                char[] charsInThreat = reevaluateThreatChars(toEvaluate);

                if (lookingForQ == 3 && playableQ == 1) {
                    foundOpponentWinningMove = true;
                    reevaluateThreat(lookingFor, foundOpponentWinningMove, toEvaluate);
                } else if (lookingForQ == 3 && winnerQ == 1) {
                    for (int y = 0; y < toEvaluate.problems.length; y++) {
                        Problem solver = new Problem(toEvaluate.problems[y].getRow(), toEvaluate.problems[y].getColumn());
                        char valueOfProblem = getChar(solver.getRow(), solver.getColumn());
                        if (valueOfProblem == WINNER) {
                            if (!foundPlayableInColumn(solver.getColumn()) && !foundUnplayableInColumn(solver.getColumn())) {

                                for (int d = 0; d < toEvaluate.problems.length; d++) {
                                    Problem anotherSolver = new Problem(toEvaluate.problems[d].getRow(), toEvaluate.problems[d].getColumn());
                                    char revisit = getChar(anotherSolver.getRow(), anotherSolver.getColumn());
                                    if (revisit != lookingFor) {
                                        int columnRevised = solver.getColumn();
                                        int playableInColumn = getPlayableInColumnIndex(columnRevised);
                                        if (playableInColumn != -1) {
                                            if (playableInColumn == solver.getRow()) {
                                                foundOpponentWinningMove = true;
                                                reevaluateThreat(lookingFor, foundOpponentWinningMove, toEvaluate);
                                            }
                                        }
                                    }

                                }
                            } else {
                                foundOpponentWinningMove = false;
                                reevaluateThreat(lookingFor, foundOpponentWinningMove, toEvaluate);
                            }
                        }
                    }

                } else if (lookingForQ == 3 && unplayableQ == 1) {
                    for (int y = 0; y < toEvaluate.problems.length; y++) {

                        Problem solver = new Problem(toEvaluate.problems[y].getRow(), toEvaluate.problems[y].getColumn());
                        char valueOfProblem = getChar(solver.getRow(), solver.getColumn());

                        if (valueOfProblem == UNPLAYABLE) {
                            int losingColumn = solver.getColumn();
                            int losingRow = solver.getRow();

                            if (losingRow == 5) {
                                board[losingRow][losingColumn] = PLAYABLE;
                                foundOpponentWinningMove = true;
                                reevaluateThreat(lookingFor, foundOpponentWinningMove, toEvaluate);
                            } else {
                                char[] charsInColumn = reevaluateColumnChars(losingColumn);
                                if (playableQ == 0) {
                                    if (unplayableQ >= 1) {
                                        int highestUnplayableIndex = -1;
                                        for (int u = 0; u < charsInColumn.length; u++) {
                                            if (charsInColumn[u] == UNPLAYABLE) {
                                                if (u > highestUnplayableIndex) {
                                                    highestUnplayableIndex = u;
                                                }
                                            }
                                        }
                                        if (highestUnplayableIndex == losingRow) {
                                            board[highestUnplayableIndex][losingColumn] = PLAYABLE;
                                            foundOpponentWinningMove = true;
                                            reevaluateThreat(lookingFor, foundOpponentWinningMove, toEvaluate);
                                        } else {
                                            reevaluateThreat(lookingFor, foundOpponentWinningMove, toEvaluate);
                                        }
                                    }
                                } else {
                                    reevaluateThreat(lookingFor, foundOpponentWinningMove, toEvaluate);
                                }
                            }
                        }

                    }
                } else if (lookingForQ == 3 && (blankQ > 0 || unplayableQ > 0 || winnerQ > 0)) {
                    reevaluateThreat(lookingFor, foundOpponentWinningMove, threats.get(x));
                }
            }
            if (blockInColumn == -1) {
                x++;
            }
        }
        while (blockInColumn == -1 && x < threats.size());
    }

    /**
     * if the method evaluateThreat has found a winning move, reevaluateThreat will assign
     * the specific column where to make a move in order to either make the final move to win the
     * game OR to block the winning move by the opponent.
     * <p>
     * If the method evaluateThreat has found a not-yet-playable winning move, the
     * column will be evaluated to determine if the column is to be avoided or if it's still
     * possible to move there without giving up the game or losing a created threat.
     * To achieve this, the slots the opponent requires to either win or disabling Vicky's threat
     * will be tagged as unplayable.
     * <p>
     * This method can be called to evaluate either Vicky's position or the opponent's, that's why
     * it's neccessary to identify the player who's threat needs to be reevaluated.
     *
     * @param player           The player who owns the threat
     * @param winningMoveFound Highlights the importance of the reevaluation.
     * @param toReevaluate     The threat that needs to be reevaluated
     */
    public void reevaluateThreat(char player, boolean winningMoveFound, Threat toReevaluate) {
        boolean winnerFound = winningMoveFound;
        if (winningMoveFound) {
            for (int i = 0; i < toReevaluate.problems.length; i++) {
                char valueOfProblem = getChar(toReevaluate.problems[i].getRow(), toReevaluate.problems[i].getColumn());
                if (player == myChar &&
                        ((valueOfProblem == PLAYABLE || valueOfProblem == UNPLAYABLE)
                                || valueOfProblem == WINNER)) {

                    board[toReevaluate.problems[i].getRow()][toReevaluate.problems[i].getColumn()] = PLAYABLE;
                    winInColumn = toReevaluate.problems[i].getColumn();

                } else if ((valueOfProblem == PLAYABLE
                        || valueOfProblem == UNPLAYABLE)
                        || valueOfProblem == WINNER) {
                    board[toReevaluate.problems[i].getRow()][toReevaluate.problems[i].getColumn()] = PLAYABLE;
                    blockInColumn = toReevaluate.problems[i].getColumn();
                }

            }
            if (blockInColumn == -1 && winInColumn == -1) {
                winnerFound = false;
            }
        }
        if (!winnerFound) {
            for (int i = 0; i < toReevaluate.problems.length; i++) {
                char valueOfProblem = getChar(toReevaluate.problems[i].getRow(), toReevaluate.problems[i].getColumn());
                if (valueOfProblem != player) {
                    int threatInColumn = toReevaluate.problems[i].getColumn();
                    int threatInRow = toReevaluate.problems[i].getRow();
                    if (player == myChar) {
                        if (board[threatInRow][threatInColumn] == WINNER) {
                            if (!foundPlayableInColumn(threatInColumn) && !foundUnplayableInColumn(threatInColumn)) {
                                winInColumn = toReevaluate.problems[i].getColumn();
                            }
                        } else {
                            board[threatInRow][threatInColumn] = WINNER;
                        }
                    }
                    if (threatInRow + 1 < board.length) {
                        if (board[threatInRow + 1][threatInColumn] != WINNER) { //CHECK THIS LINE
                            if (board[threatInRow + 1][threatInColumn] == BLANK
                                    || board[threatInRow + 1][threatInColumn] == PLAYABLE
                                    || board[threatInRow + 1][threatInColumn] == UNPLAYABLE) {
                                board[threatInRow + 1][threatInColumn] = UNPLAYABLE;
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * Method called by Vicky to know if there's a move that needs to be blocked.
     *
     * @return The column that needs to be played in order to block a winning move.
     */
    public int blockWinningMove() {
        if (blockInColumn != -1) {
            revertTheUnplayables();
        }
        return blockInColumn;
    }

    /**
     * This method will evaluate five consecutive slots, and act on this five slots threat depending on the values found in the
     * three middle slots. This method is considered both an offensive and a defensive strategy, as it will either play or block
     * a growing pattern.
     *
     * @return The middle column that needs to be played to create or destroy a double threat.
     */
    public int identifyPatterns() {
        playOnPattern = -1;

        for (int i = 0; i < board[0].length; i++) {
            for (int j = 0; j < board.length; j++) {

                if (i + 4 < board[0].length) {
                    if ((board[j][i] == BLANK || board[j][i] == PLAYABLE)
                            && (board[j][i + 4] == BLANK || board[j][i + 4] == PLAYABLE)) {

                        if ((board[j][i + 1] == myChar || board[j][i + 1] == lookingFor) && (board[j][i + 1] == board[j][i + 3]) && board[j][i + 2] == PLAYABLE) {
                            playOnPattern = i + 2;
                        }
                        if ((board[j][i + 1] == myChar || board[j][i + 1] == lookingFor) && (board[j][i + 1] == board[j][i + 2]) && board[j][i + 3] == PLAYABLE) {
                            playOnPattern = i + 3;
                        } else if ((board[j][i + 2] == myChar || board[j][i + 2] == lookingFor) && (board[j][i + 2] == board[j][i + 3]) && board[j][i + 1] == PLAYABLE) {
                            playOnPattern = i + 1;
                        }
                    }
                }
                if (j + 4 < board.length && i + 4 < board[0].length) {
                    if ((board[j][i] == BLANK || board[j][i] == PLAYABLE)
                            && (board[j + 4][i + 4] == BLANK || board[j + 4][i + 4] == PLAYABLE)) {

                        if ((board[j + 1][i + 1] == myChar || board[j + 1][i + 1] == lookingFor) && (board[j + 1][i + 1] == board[j + 3][i + 3]) && board[j + 2][i + 2] == PLAYABLE) {
                            playOnPattern = i + 2;
                        } else if ((board[j + 1][i + 1] == myChar || board[j + 1][i + 1] == lookingFor) && (board[j + 1][i + 1] == board[j + 2][i + 2]) && board[j + 3][i + 3] == PLAYABLE) {
                            playOnPattern = i + 3;
                        } else if ((board[j + 2][i + 2] == myChar || board[j + 2][i + 2] == lookingFor) && (board[j + 2][i + 2] == board[j + 3][i + 3]) && board[j + 1][i + 1] == PLAYABLE) {
                            playOnPattern = i + 1;
                        }
                    }
                }
                if (i > 3 && j + 4 < board.length) {
                    if ((board[j][i] == BLANK || board[j][i] == PLAYABLE)
                            && (board[j + 4][i - 4] == BLANK || board[j + 4][i - 4] == PLAYABLE)) {
                        if ((board[j + 1][i - 1] == myChar || board[j + 1][i - 1] == lookingFor) && (board[j + 1][i - 1] == board[j + 3][i - 3]) && board[j + 2][i - 2] == PLAYABLE) {
                            playOnPattern = i - 2;
                        } else if ((board[j + 1][i - 1] == myChar || board[j + 1][i - 1] == lookingFor) && (board[j + 1][i - 1] == board[j + 2][i - 2]) && board[j + 3][i - 3] == PLAYABLE) {
                            playOnPattern = i - 3;
                        } else if ((board[j + 2][i - 2] == myChar || board[j + 2][i - 2] == lookingFor) && (board[j + 2][i - 2] == board[j + 3][i - 3]) && board[j + 1][i - 1] == PLAYABLE) {
                            playOnPattern = i - 1;
                        }
                    }
                }
            }
        }
        return playOnPattern;
    }

    /**
     * This method scans the board looking for possible advances the opponent might be making
     * and stops them before they grow further.
     * <p>
     * First stop, identifying threats composed of two opponent slots, a playable slot and a blank slot.
     * If found, Vicky will take the playable spot, breaking the potential threat.
     * <p>
     * First part of the method ensure that priority is given to blocking an opponent's move on the board.
     * Second part will also consider Vicky's possible growing threats, and decide which one to play based on
     * the number of threats it can solve, or the number of claimed threats it can play in.
     *
     * @param threats Receives the threats from Vicky.
     */
    public int blockAdvance(ArrayList<Threat> threats) {
        Problem solver = new Problem(0, 0);
        solvedThreats = 0;
        solutionsPlayed = 0;
        blockAdvanceColumn = -1;

        for (int i = 0; i < threats.size(); i++) {

            Threat toEvaluate = threats.get(i);
            char[] charsInThreat = reevaluateThreatChars(toEvaluate);

            if (lookingForQ == 2 //|| (myCharQ + winnerQ) == 2)
                    && (playableQ == 2 || (playableQ + blankQ == 2))) {

                if (charsInThreat[0] == PLAYABLE && charsInThreat[1] == lookingFor && charsInThreat[2] == PLAYABLE && charsInThreat[3] == lookingFor) {
                    blockAdvanceColumn = toEvaluate.problems[2].getColumn();
                }
                if (charsInThreat[0] == lookingFor && charsInThreat[1] == PLAYABLE && charsInThreat[2] == lookingFor && charsInThreat[3] == PLAYABLE) {
                    blockAdvanceColumn = toEvaluate.problems[1].getColumn();
                }
            }

            if (blockAdvanceColumn == -1) {
                if (((lookingForQ == 2 || (myCharQ + winnerQ) == 2) && playableQ >= 1 && blankQ >= 1) && blockAdvanceColumn == -1) {

                    int a = 0;
                    char valueOfProblem = BLANK;
                    do {
                        solver = toEvaluate.problems[a];
                        valueOfProblem = getChar(solver.getRow(), solver.getColumn());
                        a++;
                    } while (valueOfProblem != PLAYABLE && a < toEvaluate.problems.length);


                    int solutionsPlayedBySolver = 0;
                    for (int j = 0; j < threats.size(); j++) {
                        if (threats.get(j).getThreatLevel() == 0) {
                            if (threats.get(j).containsProblem(solver)) {
                                solutionsPlayedBySolver++;
                            }
                        }
                    }

                    int threatsSolvedBySolver = 0;

                    for (int j = 0; j < threats.size(); j++) {
                        if (threats.get(j).isActive()) {
                            if (threats.get(j).containsProblem(solver)) {
                                threatsSolvedBySolver++;
                            }
                        }
                    }

                    int mostSolutions = 0;
                    if (solutionsPlayedBySolver > threatsSolvedBySolver) {
                        mostSolutions = solutionsPlayedBySolver;
                    } else {
                        mostSolutions = threatsSolvedBySolver;
                    }

                    if (threatsSolvedBySolver > solvedThreats) {
                        solvedThreats = threatsSolvedBySolver;
                        blockAdvanceColumn = solver.getColumn();
                    }
                }
            }

        }
        return blockAdvanceColumn;
    }

    /**
     * This method acts a safety net for Early-game moves where the lack of played slots
     * dont allow other methods to act. Basically, this method ensures that the initial moves are played
     * in an orderly fashion.
     *
     * @param threats Receives the threats from Vicky.
     * @return Best playable move in the bottom row.
     */
    public int earlyWin(ArrayList<Threat> threats) {
        int middleColumnIndex = board[0].length / 2;
        int lastRowIndex = board.length - 1;
        int myCharQ = 0;
        int playableQ = 0;

        int suggestedColumn = -1;
        int threatsGenerated = -1;

        //if(board[lastRowIndex][middleColumnIndex] == myChar){

        for (int i = 0; i < board[0].length; i++) {
            for (int j = board.length - 1; j < board.length; j++) {
                if (board[j][i] == PLAYABLE) {
                    Problem generator = new Problem(j, i);
                    int containedInThreats = 0;
                    int generatorColumn = generator.getColumn();
                    for (int x = 0; x < threats.size(); x++) {

                        if (threats.get(x).containsProblem(generator)) {
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
     * Method created for evaluation purposes. Left here for the curious.
     * Receives an ArrayList of Threats and prints them one by one.
     *
     * @param threats Receives the threats from Vicky
     */
    public void printTheThreats(ArrayList<Threat> threats) {
        if (threats.size() > 0) {
            for (int y = 0; y < threats.size(); y++) {
                Threat toPrint = threats.get(y);
                System.out.println(y + " " + toPrint.printTheThreat());
            }
        }
    }


    /**
     * This method checks the board for the Agent's move and disables all threats
     * solved by it.
     *
     * @param threats Receives the threats from Vicky
     * @return Sends the updated threats back to Vicky.
     */
    public ArrayList<Threat> disableThreats(ArrayList<Threat> threats) {
        for (int i = 0; i < board[0].length; i++) {
            for (int j = 0; j < board.length; j++) {
                if (board[j][i] == myChar) {
                    Problem solver = new Problem(j, i);
                    for (int x = 0; x < threats.size(); x++) {
                        if (threats.get(x).isActive()) {
                            if (threats.get(x).containsProblem(solver)) {
                                threats.get(x).setThreatLevel(0);
                                threats.get(x).disable();
                            }
                        }
                    }
                }
            }
        }
        return threats;
    }

    /**
     * Support method to make sure that Vicky doesn't play where she shouldn't.
     * There may be available slots in a column, but playing in it may either surrender the game
     * or blow an opportunity to win the game.
     *
     * @param column the column in question
     * @return true if there's a playable position in the column
     */
    public boolean foundPlayableInColumn(int column) {
        boolean foundPlayable = false;
        for (int j = 0; j < board.length; j++) {
            if (board[j][column] == PLAYABLE) {
                foundPlayable = true;
            }
        }
        return foundPlayable;
    }

    /**
     * Method used to unmask the playable move if it has been branded as unplayable by other methods.
     *
     * @param column The column to be browsed.
     * @return true if an unplayable slot has been found
     */
    public boolean foundUnplayableInColumn(int column) {
        boolean foundUnplayable = false;
        for (int j = 0; j < board.length; j++) {
            if (board[j][column] == UNPLAYABLE) {
                foundUnplayable = true;
            }
        }
        return foundUnplayable;
    }

    /**
     * Support method to get the String representation of a possible move.
     * Created for evaluation purposes. All calls for this method have been removed.
     *
     * @param column The column in question.
     * @return String representation of a move
     */
    public String getPlayableInColumn(int column) {
        if (foundPlayableInColumn(column) == true) {

            for (int j = 0; j < board.length; j++) {
                if (board[j][column] == PLAYABLE) {
                    Problem found = new Problem(j, column);
                    return found.toPrint();
                }
            }

        }
        return "";

    }

    /**
     * If a playable slot has been found in a column, this method scans the column looking for the index.
     *
     * @param column The column to be scanned.
     * @return The index of the playable slot.
     */
    public int getPlayableInColumnIndex(int column) {
        if (foundPlayableInColumn(column) == true) {

            for (int j = 0; j < board.length; j++) {
                if (board[j][column] == PLAYABLE) {

                    return j;
                }
            }

        }
        return -1;
    }

    /**
     * Method used to reevaluate a column when a threat is composed of three tokens of the same player.
     * This method is crucial to make sure that a winning move is played, or a losing move is blocked.
     *
     * @param column The column to be reealuated.
     * @return an array containing the char values of all the slots in the column
     */
    public char[] reevaluateColumnChars(int column) {
        blankQ = 0;
        myCharQ = 0;
        lookingForQ = 0;
        playableQ = 0;
        unplayableQ = 0;
        winnerQ = 0;

        Problem solver = new Problem(0, 0);
        char[] columnChars = new char[board.length];

        for (int j = 0; j < board.length; j++) {
            solver = new Problem(j, column);
            char valueOfProblem = getChar(solver.getRow(), solver.getColumn());
            columnChars[j] = valueOfProblem;

            if (valueOfProblem == myChar) {
                myCharQ++;
            }
            if (valueOfProblem == WINNER) {
                winnerQ++;
            }
            if (valueOfProblem == lookingFor) {
                lookingForQ++;
            }
            if (valueOfProblem == BLANK) {
                blankQ++;
            }
            if (valueOfProblem == PLAYABLE) {
                playableQ++;
            }
            if (valueOfProblem == UNPLAYABLE) {
                unplayableQ++;
            }
        }

        return columnChars;
    }

    /**
     * Method used to reevaluate a threat when it is found to be composed of three tokens of the same player.
     * This method is crucial to make sure that a winning move is played, or a losing move is blocked.
     *
     * @param toEvaluate The threat object to be sorted.
     * @return an array containing the char values of all the slots in the column
     */
    public char[] reevaluateThreatChars(Threat toEvaluate) {
        blankQ = 0;
        myCharQ = 0;
        lookingForQ = 0;
        playableQ = 0;
        unplayableQ = 0;
        winnerQ = 0;

        Problem solver = new Problem(0, 0);
        char[] threatChars = new char[toEvaluate.problems.length];

        for (int x = 0; x < toEvaluate.problems.length; x++) {
            solver = toEvaluate.problems[x];
            char valueOfProblem = getChar(solver.getRow(), solver.getColumn());
            threatChars[x] = valueOfProblem;

            if (valueOfProblem == myChar) {
                myCharQ++;
            }
            if (valueOfProblem == WINNER) {
                winnerQ++;
            }
            if (valueOfProblem == lookingFor) {
                lookingForQ++;
            }
            if (valueOfProblem == BLANK) {
                blankQ++;
            }
            if (valueOfProblem == PLAYABLE) {
                playableQ++;
            }
            if (valueOfProblem == UNPLAYABLE) {
                unplayableQ++;
            }

        }
        return threatChars;
    }

    /**
     * This method turns the positions tagged as unplayable into playable
     * when there are no other possible moves. It's actually very important so that Vicky
     * blocks a winning move by the opponent even if it'd enable another winning move by the opponent,
     * or if it'd give the opponent to opportunity to destroy a winning threat created by Vicky.
     */
    public void revertTheUnplayables() {
        for (int x = 0; x < board[0].length; x++) {
            for (int y = board.length - 1; y >= 0; y--) {
                if (board[y][x] == UNPLAYABLE
                        || board[y][x] == WINNER) {
                    if (!foundPlayableInColumn(x)) {
                        board[y][x] = PLAYABLE;
                    }
                }
            }
        }
    }

    /**
     * Method used by Vicky's random move generator to make sure that she doesn't play in a
     * column where she shouldn't.
     */
    public void unplayableMove() {
        hadToRevertTheUnplayables = false;
        boolean foundPlayable = false;
        int unplayableQ = 0;

        for (int x = 0; x < board[0].length; x++) {
            for (int y = 0; y < board.length; y++) {
                if (board[y][x] == PLAYABLE) {
                    foundPlayable = true;

                } else if (board[y][x] == UNPLAYABLE) {
                    unplayableQ++;
                }
            }
        }

        if (!foundPlayable && unplayableQ > 0) {
            revertTheUnplayables();
            hadToRevertTheUnplayables = true;
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
