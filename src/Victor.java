import java.util.Random;
import java.util.ArrayList;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Write a description of class Victor here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class Victor extends Agent
{
    Random r;

    private char myChar;

    private ArrayList<Problem> playables;
    private ArrayList<Position> nodes;
    private int fatherIndex;

    private char turn;
    private int stage;

    private Position firstFather;

    /**
     * Constructor for objects of class Victor
     */
    public Victor(Connect4Game game, boolean iAmRed)
    {
        super(game, iAmRed);
        r = new Random();
        playables = new ArrayList<Problem>();
        nodes = new ArrayList<Position>();
        firstFather();
        findSons();
    }

    /**
     * The move method is run every time it is this agent's turn in the game. You may assume that
     * when move() is called, the game has at least one open slot for a token, and the game has not
     * already been won.
     * 
     * By the end of the move method, the agent should have placed one token into the game at some
     * point.
     * 
     * After the move() method is called, the game engine will check to make sure the move was
     * valid. A move might be invalid if:
     * - No token was place into the game.
     * - More than one token was placed into the game.
     * - A previous token was removed from the game.
     * - The color of a previous token was changed.
     * - There are empty spaces below where the token was placed.
     * 
     * If an invalid move is made, the game engine will announce it and the game will be ended.
     * 
     */
    public void move()
    {
        Matrix check = new Matrix(myGame.getBoardMatrix());
        if(newGame(check)){
            reset();
        }

        if(check.getCountOfBlanks() < myGame.getRowCount() * myGame.getColumnCount()){
            deactivateNodes(check);
        }
        calculateChances();

        int currentStage = (myGame.getRowCount() * myGame.getColumnCount()) - check.getCountOfBlanks();
        moveOnColumn(iCanWin(currentStage));
    }

    private void deactivateNodes(Matrix check)
    {
        for(int x = 0; x < nodes.size(); x++){
            Position child = nodes.get(x);
            if(child.isActive()){
                Matrix childMatrix = child.getNode();
                if(!childMatrix.match(check)){
                    child.deactivate();
                }
            }
        }
    }

    private boolean newGame(Matrix check)
    {

        if(super.iAmRed && myGame.getRedPlayedFirst() 
        && check.getCountOfBlanks() == myGame.getRowCount() * myGame.getColumnCount()){
            return true;
        }
        else if(super.iAmRed && !myGame.getRedPlayedFirst() 
        && check.getCountOfBlanks() == ((myGame.getRowCount() * myGame.getColumnCount()) - 1)){
            return true;
        }
        else if(!super.iAmRed && myGame.getRedPlayedFirst() 
        && check.getCountOfBlanks() == ((myGame.getRowCount() * myGame.getColumnCount()) - 1)){
            return true;
        }
        else if(!super.iAmRed && !myGame.getRedPlayedFirst() 
        && check.getCountOfBlanks() == myGame.getRowCount() * myGame.getColumnCount()){
            return true;
        }
        return false;
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
     * Returns a random valid move. If your agent doesn't know what to do, making a random move
     * can allow the game to go on anyway.
     * 
     * @return a random valid move.
     */
    public int randomMove()
    {
        int i = r.nextInt(myGame.getColumnCount());
        while (getLowestEmptyIndex(myGame.getColumn(i)) == -1)
        {
            i = r.nextInt(myGame.getColumnCount());
        }
        return i;
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
    public int iCanWin(int currentStage)
    {

        Position best = firstFather;
        double bestChance = 0.0;
        for(int x = 0; x < nodes.size(); x++){
            Position check = nodes.get(x);
            if(check.getStage() == currentStage){
                if(check.isActive()){
                    if(myChar == Matrix.MOVEDFIRST){
                        double checkChance = check.getChanceOfA();
                        if(checkChance > bestChance){
                            bestChance = checkChance;
                            best = check;
                        }
                    }
                    else if(myChar == Matrix.MOVEDSECOND){
                        double checkChance = check.getChanceOfB();
                        if(checkChance > bestChance){
                            bestChance = checkChance;
                            best = check;
                        }
                    }
                }
            }
        }

        return bestColumn(best);
    }

    /**
     * @return the column of the best move iCanWin found
     */
    private int bestColumn(Position best)
    {
        ArrayList<Position> fathers = best.getFathers();
        if(fathers.size() > 0){
            Position father = fathers.get(0);
            Matrix fatherMatrix = father.getNode();
            char[][] fatherBoard = fatherMatrix.board;
            Matrix bestMatrix = best.getNode();
            char[][] bestBoard = bestMatrix.board;

            for(int i = 0; i < bestMatrix.getColumnCount(); i++){
                for(int j = 0; j < bestMatrix.getRowCount(); j++){
                    if(bestBoard[j][i] != fatherBoard[j][i]){
                        return i;
                    }
                }
            }
        }
        return -1;
    }

    private int findTheMissmatch()
    {
        return -1; 
    }

    public void identifyPlayer()
    {
        if(super.iAmRed && myGame.getRedPlayedFirst()){
            myChar = Matrix.MOVEDFIRST;
        }
        else if(!super.iAmRed && myGame.getRedPlayedFirst()){
            myChar = Matrix.MOVEDSECOND;
        }
        else if(super.iAmRed && !myGame.getRedPlayedFirst()){
            myChar = Matrix.MOVEDFIRST;
        }
    }

    /**
     * Returns the name of this agent.
     *
     * @return the agent's name
     */
    public String getName()
    {
        return "Victor";
    }

    public void firstFather()
    {
        Matrix firstFathersMatrix = new Matrix(myGame.getRowCount(), myGame.getColumnCount());
        firstFathersMatrix.fill();
        //fatherIndex = -1;
        int countOfBlanks = firstFathersMatrix.getCountOfBlanks();
        stage = getStage(countOfBlanks);
        //Position firstFather = new Position(firstFathersMatrix, fatherIndex, stage);
        firstFather = new Position(firstFathersMatrix, stage);
        nodes.add(firstFather);
    }

    public void findSons()
    {
        //char turn = Matrix.MOVEDFIRST;
        int x = 0;
        while(x < nodes.size()){
            Position father = nodes.get(x);
            if(!father.hasEnded()){
                Matrix fatherMatrix = father.getNode();
                int countOfBlanks = fatherMatrix.getCountOfBlanks();
                turn = turn(countOfBlanks);
                stage = getStage(countOfBlanks);
                playables = fatherMatrix.getPlayables();

                while(playables.size() > 0){
                    Matrix soil = new Matrix(fatherMatrix);
                    Problem seed = playables.get(0);
                    soil.plantTheSeed(seed, turn);
                    int sonCountOfBlanks = soil.getCountOfBlanks();
                    int sonStage = getStage(sonCountOfBlanks);
                    Position son = new Position(soil, sonStage);
                    //soil.revertPlayables(); 
                    //no need to revertPlayables as the values in the father's matrix are not modified, as it happends in the boardmatrix class

                    boolean sonFound = false;
                    Position check = firstFather;
                    int checkIndex = 0;
                    while(checkIndex < nodes.size() && !sonFound){
                        check = nodes.get(checkIndex);
                        if(check.getStage() == sonStage){
                            Matrix checkMatrix = check.getNode();
                            if(soil.match(checkMatrix)){
                                sonFound = true;
                            }
                        }
                        checkIndex++;
                    }

                    if(!sonFound){
                        char gameWon = soil.gameWon();
                        if(gameWon == Matrix.MOVEDFIRST || gameWon == Matrix.MOVEDSECOND){
                            son.setResult(gameWon);
                        }
                        else{
                            if(gameWon == Matrix.NOTWON && soil.boardFull()){
                                son.setResult(Matrix.DRAW);
                            }
                        }
                        son.hasEnded();
                        father.addSon(son);
                        son.addFather(father);
                        nodes.add(son);
                    }
                    else{
                        //IF a match is found, the new SON is not added to the list of nodes. Simply, the father is added to the list of fathers of the MATCH
                        //I still have to adjust the code so that the father is stored as a Position in an arraylist
                        //not as an index in an int
                        father.addSon(check);
                        check.addFather(father);
                    }

                    System.err.println(nodes.size());
                    playables.remove(0);
                }
            }
            currentTime();
            x++;
        }
    }

    /**
     * 
     */
    public void calculateChances()
    {
        int currentStage = myGame.getRowCount() * myGame.getColumnCount();
        while(currentStage >= 0){
            for(int x = 0; x < nodes.size(); x++){
                Position child = nodes.get(x);
                if(child.getStage() == currentStage && child.isActive()){ // if the code breaks, child.isActive goes out first
                    if(child.hasEnded() && !child.hasChanceCalculated()){
                        char result = child.getResult();
                        child.setChances(result);
                    }
                    else if(!child.hasEnded() && !child.hasChanceCalculated()){
                        double[] chances = reviewPositionChances(child.getSons());
                        child.setChances(chances);
                    }
                }
            }
            currentStage--;
        }
    }

    private double[] reviewPositionChances(ArrayList<Position> sons)
    {
        double accumulatedChanceOfA = 0.0;
        double accumulatedChanceOfB = 0.0;
        double accumulatedChanceOfD = 0.0;
        int numberOfSons = sons.size();

        for(int x = 0; x < sons.size(); x++){
            Position son = sons.get(x);
            accumulatedChanceOfA += son.getChanceOfA();
            accumulatedChanceOfB += son.getChanceOfB();
            accumulatedChanceOfD += son.getChanceOfD();
        }

        double[] chances = new double[3];
        chances[0] = accumulatedChanceOfA / numberOfSons;
        chances[1] = accumulatedChanceOfB / numberOfSons;
        chances[2] = accumulatedChanceOfD / numberOfSons;
        return chances;
    }

    private char turn(int countOfBlanks)
    {
        if(countOfBlanks % 2 == 0){
            turn = Matrix.MOVEDFIRST;
        }
        else{
            turn = Matrix.MOVEDSECOND;
        }
        return turn;
    }

    private int getStage(int countOfBlanks)
    {
        int slots = myGame.getRowCount() * myGame.getColumnCount();
        return slots - countOfBlanks;
    }

    public void reset()
    {
        for(int x = 0; x < nodes.size(); x++){
            Position toReactivate = nodes.get(x);
            if(!toReactivate.isActive()){
                toReactivate.reactivate();
            }
        }
    }

    public void currentTime()
    {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        System.out.println( sdf.format(cal.getTime()) );
    }
}
