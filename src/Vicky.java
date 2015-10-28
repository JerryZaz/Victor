import java.util.Random;
import java.util.ArrayList;

/**
 * Udacity's Intro to Java Nanodegree Course Final Project.
 * 
 * Vicky is an Agent that plays the game Connect 4.
 * Vicky is not a stand-alone object, as it requires a Connect 4 Game to be passed on,
 * and it relies on the BoardMatrix class to choose the next move.
 * Vicky also requires the Threat class, as Vicky holds all the possible Threat objects
 * and is constantly passing it on to the BoardMatrix class for analysis.
 * 
 * @author Henry Lopez-Ingram
 * @version 151006
 */
public class Vicky extends Agent
{
    Random r;
    BoardMatrix board;
    BoardMatrix check;

    boolean iGoFirst;
    boolean middleClaimed;

    ArrayList<Threat> threats;
    private int activeThreats;

    int suggestion;

    /**
     * Constructs a new agent, giving it the game and telling it whether it is Red or Yellow.
     * 
     * @param game The game the agent will be playing.
     * @param iAmRed True if the agent is Red, False if the agent is Yellow.
     */
    public Vicky(Connect4Game game, boolean iAmRed)
    {
        super(game, iAmRed);
        r = new Random();

        board = new BoardMatrix(game, iAmRed);
        if(board.getCountOfBlanks() == 42){
            iGoFirst = true;
        }
        middleClaimed = false;

        check = new BoardMatrix(game, iAmRed);
        threats = board.identifyThreats();
    }

    /**
     * This method sends the decision to the game.
     * If a suggestion has been found, it's sent. If not, a random move is generated.
     */
    public void move()
    {
        suggestion = -1;

        think();
        if(suggestion != -1){
            moveOnColumn(suggestion);
        }
        else{
            moveOnColumn(randomMove());
        }

        check = new BoardMatrix(myGame, iAmRed);
        threats = check.disableThreats(threats);
    }

    /**
     * The Think method holds the key. Vicky coordinates multiple analysis on the board to determine the best possible move.
     * It starts by claiming the middle, if it hasn't been claimed. There's no analysis here, only a check on the lowest index of the middle column.
     * Proceeds to play a winning move, if there's any. Followed by blocking a winning move, again, if there's any.
     * If no winning or losing move has been found, different methods are run looking for patterns that could be played or should be blocked.
     */
    public void think()
    {

        board = new BoardMatrix(myGame, iAmRed);
        board.checkOpponentMove(check, threats);

        if(board.getCountOfBlanks() > check.getCountOfBlanks()){
            reset();
        }

        //else{
        board.findPlayables();
        if(!middleClaimed){
            suggestion = claimTheMiddle();
        }
        else{

            if(suggestion == -1){
                suggestion = iCanWin();
                if(suggestion == -1){
                    suggestion = theyCanWin();
                    if(suggestion == -1){
                        suggestion = board.earlyWin(threats);
                        if(suggestion == -1){
                            if(board.hadToRevertTheUnplayables){
                                suggestion = theyCanWin();
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
    public int claimTheMiddle()
    {

        if(myGame.getColumnCount() % 2 != 0){ //determine if a board with even number of columns is being played
            int columns = myGame.getColumnCount(); //possible instance variable
            int rows = myGame.getRowCount(); // possible instance variable
            int middleColumnIndex = myGame.getColumnCount()/2; // THIS MIGHT NOT BE BE THE BEST WAY

            int lowestEmptySlotIndexOnMiddleColumn = getLowestEmptyIndex(myGame.getColumn(middleColumnIndex));
            if(lowestEmptySlotIndexOnMiddleColumn == rows - 1){ // determine if the middle has been claimed
                middleClaimed = true;
                return middleColumnIndex; // claims middle
            }
            else{
                middleClaimed = true;
            }
        }
        return -1;
    }

    /**
     * Returns the column that would allow the agent to win.
     * 
     * You might want your agent to check to see if it has a winning move available to it so that
     * it can go ahead and make that move. Implement this method to return what column would
     * allow the agent to win.
     *
     * @return the column that would allow the agent to win.
     */
    public int iCanWin()
    {
        board.evaluateSolutions(threats);
        return board.playWinningMove();
    }

    /**
     * Makes three different method calls, analyzing the board looking for a possible move.
     * Every method called will return a column number if it has found a sugestion, or -1 if it hasn't,
     * which will enable the next method call.
     *
     * @return the column that would allow the opponent to win.
     */
    public int theyCanWin()
    {
        activeThreats();
        board.evaluateThreats(threats, activeThreats);

        int suggestedColumn = board.blockWinningMove();
        if(suggestedColumn != -1){
            return suggestedColumn;
        }
        else{
            suggestedColumn = board.identifyPatterns();
            if(suggestedColumn != -1){
                return suggestedColumn;
            }else{
                return board.blockAdvance(threats);
            }
        }
    }

    /**
     * Drops a token into a particular column so that it will fall to the bottom of the column.
     * If the column is already full, nothing will change.
     * 
     * @param columnNumber The column into which to drop the token.
     */
    public void moveOnColumn(int columnNumber)
    {
        int lowestEmptySlotIndex = getLowestEmptyIndex(myGame.getColumn(columnNumber));   // Find the top empty slot in the column
        // If the column is full, lowestEmptySlot will be -1
        if (lowestEmptySlotIndex > -1)  // if the column is not full
        {
            Connect4Slot lowestEmptySlot = myGame.getColumn(columnNumber).getSlot(lowestEmptySlotIndex);  // get the slot in this column at this index

            if (iAmRed) // If the current agent is the Red player...
            {
                lowestEmptySlot.addRed(); // Place a red token into the empty slot
            }
            else // If the current agent is the Yellow player (not the Red player)...
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
    public int randomMove()
    {
        board.unplayableMove();

        int i = r.nextInt(myGame.getColumnCount());
        while (getLowestEmptyIndex(myGame.getColumn(i)) == -1 || board.foundPlayableInColumn(i) == false)
        {
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
        for  (int i = 0; i < column.getRowCount(); i++)
        {
            if (!column.getSlot(i).getIsFilled())
            {
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
    public void reset()
    {
        board = new BoardMatrix(myGame, iAmRed);
        if(board.getCountOfBlanks() == 42){
            iGoFirst = true;
        }
        middleClaimed = false;

        check = new BoardMatrix(myGame, iAmRed);
        reactivateThreats();
        activeThreats();
    }

    /**
     * Method that sets the number of remaining active threats.
     * It was created for evaluation and tracking purposes, used in the method EvaluateThreats of the BoardMatrix class to verify that
     * the threats were being sucessfully marked as inactive before evaluating.
     */
    private void activeThreats()
    {
        activeThreats = 0;
        for(Threat threat : threats){
            if(threat.isActive())
            {
                activeThreats++;
            }
        }
    }

    /**
     * Support method, called by Vicky's reset method.
     * The reactivate method of the Threat class returns the threat to its initial values.
     */
    public void reactivateThreats()
    {
        for(Threat threat : threats){
            threat.reactivate();
        }
    }

    /**
     * Returns the name of this agent.
     *
     * @return the agent's name
     */
    public String getName()
    {
        return "Vicky";
    }
}
