import java.util.ArrayList;

/**
 * Write a description of class Matrix here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class Matrix
{
    char[][] board;

    private int numberOfRows;
    private int numberOfColumns;

    protected char myChar;
    protected char lookingFor;

    private int countOfBlanks; //write a method to calculate this

    final static char BLANK = 'B';
    final static char RED = 'R';
    final static char YELLOW = 'Y';
    final static char MOVEDFIRST = 'A';
    final static char MOVEDSECOND = 'V';
    final static char PLAYABLE = 'P';
    final static char DRAW = 'D';
    final static char NOTWON = 'N';

    /**
     * Constructor for objects of class Matrix
     */
    public Matrix()
    {
        numberOfRows = 6;
        numberOfColumns = 7;
        board = new char[numberOfRows][numberOfColumns];
    }

    public Matrix(int passedRows, int passedColumns)
    {
        numberOfRows = passedRows;
        numberOfColumns = passedColumns;
        board = new char[numberOfRows][numberOfColumns];
    }
    
    public Matrix(Matrix father)
    {
        numberOfRows = father.getRowCount();
        numberOfColumns = father.getColumnCount();
        board = new char[numberOfRows][numberOfColumns];
        copy(father.getBoard());
    }
    
    public Matrix(char[][] boardMatrix)
    {
        numberOfRows = boardMatrix.length;
        numberOfColumns = boardMatrix[0].length;
        board = new char[numberOfRows][numberOfColumns];
        copy(boardMatrix);
    }

    public void fill()
    {
        for(int i = 0; i < getColumnCount(); i++)
        {
            for(int j = 0; j < getRowCount(); j++)
            {
                board[j][i] = BLANK;
            }
        }
    }
    
    public void copy(char[][] toClone)
    {
        for(int i = 0; i < getColumnCount(); i++)
        {
            for(int j = 0; j < getRowCount(); j++)
            {
                board[j][i] = toClone[j][i];
            }
        }        
    }
    
    /**
     * Scans the board and tags the lowest unplayed positions as playable.
     */
    public ArrayList<Problem> getPlayables() //getLowestEmptyIndex
    {
        ArrayList<Problem> sons = new ArrayList<Problem>();
        for(int x = 0; x < board[0].length; x++){
            int lowestBlank = -1;
            for(int y = 0; y < board.length; y++){
                if(board[y][x] == BLANK){
                    lowestBlank = y;
                }
            }
            if(lowestBlank != -1){
                Problem son = new Problem(lowestBlank, x);
                sons.add(son);
            }
        }
        return sons;
    }
    
    /**
     * 
     * This method mught return different values depending on where the method is called,
     * as in the BoardMatrix class, not every unplayed space is marked as blank.
     * 
     * @return the count of blank spaces in the board
     */
    public int getCountOfBlanks()
    {
        countOfBlanks = 0;
        for(int i = 0; i < getColumnCount(); i++)
        {
            for(int j = 0; j < getRowCount(); j++)
            {
                if(board[j][i] == BLANK){
                    countOfBlanks++;
                }
            }
        }        
        return countOfBlanks;
    }

    /**
     * One slot at a time
     */
    public void modify(int row, int column, char move)
    {
        if(move == RED || move == YELLOW){
            if(row < board.length && row >= 0){
                if(column < board[0].length && column >= 0){
                    board[row][column] = move;
                }
            }
        }
    }
    
    public void plantTheSeed(Problem seed, char turn)
    {
        int row = seed.getRow();
        int column = seed.getColumn();
        
        if(turn == MOVEDFIRST || turn == MOVEDSECOND){
            if(row < board.length && row >= 0){
                if(column < board[0].length && column >= 0){
                    board[row][column] = turn;
                    revertPlayables();
                }
            }
        }
    }
    
    public void revertPlayables()
    {
        for (int i = 0; i < getColumnCount(); i++)
        {
            for (int j = 0; j < getRowCount(); j++)
            {
                if (board[j][i] == PLAYABLE)
                {
                    board[j][i] = BLANK;
                }
            }
        }
    }

    public boolean match(Matrix check)
    {
        boolean match = true;
        char[][] local = check.board;

        for(int i = 0; i < getColumnCount(); i++){
            for(int j = 0; j < getRowCount(); j++){
                if(local[j][i] != board[j][i]){
                    match = false;
                }
            }
        }

        return match;
    }
    
    /**
     * Check if the board is full.
     * 
     * Your agent will not need to use this method.
     * 
     * @return true if the board is full, false otherwise.
     */
    public boolean boardFull()
    {
        for (int i = 0; i < getColumnCount(); i++)
        {
            for (int j = 0; j < getRowCount(); j++)
            {
                if (board[j][i] == BLANK)
                {
                    return false;
                }
            }
        }
        return true;
    }
    
    /**
     * Check if the game has been won.
     * 
     * Your agent will not need to use this method.
     * 
     * @return 'R' if red won, 'Y' if yellow won, 'N' if the game has not been won.
     */
    public char gameWon()
    {        
        for (int i = 0; i < getColumnCount(); i++)
        {
            for (int j = 0; j < getRowCount(); j++)
            {
                if(board[j][i] != BLANK)
                {
                    if (j + 3 < getRowCount())
                    {
                        if(board[j][i] == board[j + 1][i] && board[j][i] == board[j + 2][i] && board[j][i] == board[j + 3][i])
                        {
                            return board[j][i];
                        }
                    }
                    if (i + 3 < getColumnCount())
                    {
                        if (board[j][i] == board[j][i + 1] && board[j][i] == board[j][i + 2] && board[j][i] == board[j][i + 3])
                        {
                            return board[j][i];
                        }
                    }
                    if (i + 3 < getColumnCount() && j + 3 < getRowCount())
                    {
                        if(board[j][i] == board[j + 1][i + 1] && board[j][i] == board[j + 2][i + 2] && board[j][i] == board[j + 3][i + 3])
                        {
                            return board[j][i];
                        }
                    }
                    if (i > 2 && j + 3 < getRowCount())
                    {
                        if (board[j][i] == board[j + 1][i - 1] && board[j][i] == board[j + 2][i - 2] && board[j][i] == board[j + 3][i - 3])
                        {
                            return board[j][i];
                        }
                    }
                }
            }
        }

        return NOTWON;
    }

    public void clear()
    {
        for(int i = 0; i < numberOfColumns; i++){
            for(int j = 0; j < numberOfRows; j++){
                board[j][i] = BLANK;
            }
        }
    }
    
    public char[][] getBoard()
    {
        return board;
    }

    /**
     * The BoardMatrix class relies on the Threat and Problem class.
     * The Problem class holds the coordinates for the different threats,
     * and the getChar method enables the evaluation of the different Threats and Problems.
     * 
     * @param row Row index of the Problem
     * @param column column index of the Problem
     * 
     * @return value of a position
     */
    public char getChar(int row, int column)
    {
        return board[row][column];
    }
    
    public int getColumnCount()
    {
        return numberOfColumns;
    }
    
    public int getRowCount()
    {
        return numberOfRows;
    }
    
    /**
     * Vicky identifies herself to the BoardMatrix class.
     * 
     * @param redAgent true if Vicky plays red
     */
    public void identifyPlayer(boolean redAgent)
    {
        if(redAgent){
            myChar = RED;
            lookingFor = YELLOW;
        }else{
            myChar = YELLOW;
            lookingFor = RED;
        }
    }

    public char getMyChar()
    {
        return myChar;
    }
    
    /**
     * lookingFor is the opponent's tag throughout the BoardMatrix class
     * @return the opponent's tag
     */
    public char getLookingFor()
    {
        return lookingFor;
    }
    
    /**
     * Prints the board of chars. For evaluation purposes.
     * All calls for this method were removed before submitting the project.
     */
    public void printTheBoard()
    {
        System.out.println();
        for(int x = 0; x < board.length; x++){
            for(int y = 0; y < board[x].length; y++){
                System.out.print(board[x][y] + " ");
            }
            System.out.println();
        }
    }

}
