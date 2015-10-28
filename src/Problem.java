
/**
 * A Problem is meant to hold the coordinates of a slot, to be used by the 
 * Threat class to control each elements of the Threat, and by the BoardMatrix
 * class to turn any coordinates into an object for multiple purposes, or to 
 * drop the Threat object on top of the matrix to see what it's composed of.
 * 
 * @author Henry Lopez-Ingram
 * @version 151006
 */
public class Problem
{
    private int row;
    private int column;
    
    /**
     * Receives the row and column coordinates that constitute a Problem object.
     * 
     * @param row The row index of the slot.
     * @param column The column index of the slot.
     */
    public Problem(int row, int column)
    {
        this.row = row;
        this.column = column;
    }
    
    /**
     * Gets the row value stored in the object.
     * @return The row value stored in the Problem object.
     */    
    public int getRow()
    {
        return row;
    }
    
    /**
     * Gets the column value stored in the object.
     * @return The column value stored in the Problem object.
     */    
    public int getColumn()
    {
        return column;
    }
    
    /**
     * A String representation of the Problem, for evaluation purposes.
     * All calls for this method have been removed or disabled.
     * 
     * @return String representation of the Problem
     */
    public String toPrint()
    {
        return "[" + getRow() + "][" + getColumn() + "]";
    }
}
