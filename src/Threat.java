
/**
 * The Threat represents a winning move in the game of Connect 4.
 * It holds an array of the FOUR positions which represent a problem.
 * 
 * The Threat class will have two methods which will disable threats, or change 
 * the Threat Level depending on the moves by either player. This methods will be triggered by
 * the BoardMatrix class, which will be in charge of studying every move.
 * 
 * @author Henry Lopez-Ingram 
 * @version 151006
 */
public class Threat
{
    boolean active;
    Problem[] problems;
    private int threatLevel;
    //threatLevel = 0 - Threat claimed by Vicky
    //threatLevel = 1 - potential threat
    //threatLevel = 2 - enabled (when first token of the threat is played
    //threatLevel = 3 - (two tokens played)
    //threatLevel = 4 - (three tokens played)
    //threatLevel = 5 - Three tokens played, fourth token playable
    
    /**
     * Receives a set of four problems from the BoardMatrix class and initializes the threat
     * which is then sent back to Vicky.
     * 
     * @param one First Problem object
     * @param two Second Problem object
     * @param three Third Problem object
     * @param four Fourth Problem object
     */
    public Threat(Problem one, Problem two, Problem three, Problem four)
    {
        active = true;
        problems = new Problem[4];
        problems[0] = one;
        problems[1] = two;
        problems[2] = three;
        problems[3] = four;
        threatLevel = 1;
    }
    
    /**
     * Method called by the BoardMatrix class when a slot has been claimed by either player.
     * @param newLevel Receives the new threat level from the BoardMatrix class.
     */
    public void setThreatLevel(int newLevel)
    {
        threatLevel = newLevel;
    }
    
    /**
     * Method mostly used to determine if Vicky has claimed a threat.
     * @return The number zero if the threat has been claimed by Vicky.
     */    
    public int getThreatLevel()
    {
        return threatLevel;
    }
    
    /**
     * Method used to determine if such threat still represents a danger for Vicky.
     * @return true if the threat remains active for the opponent
     */
    public boolean isActive()
    {
        return active;
    }
    
    /**
     * Method called by Vicky's reset method to return all threats to their initial values
     */
    public void reactivate()
    {
        active = true;
        threatLevel = 1;
    }
    
    /**
     * Method called when Vicky has made a move on a slot that contains an Active threat.
     */    
    public void disable()
    {
        active = false;
    }
    
    /**
     * The most important method, used to determine if a Problem is contained in a threat.
     * Game-changer when there are two possible moves and it's neccessary to choose one or the other,
     * the move that contains the most active threats for either player is chosen
     * 
     * @param token Problem object sent from the BoardMatrix class
     * @return true if the Threat contains the problem
     */    
    public boolean containsProblem(Problem token)
    {
        boolean found = false;
        int tokenRow = token.getRow();
        int tokenColumn = token.getColumn();
        
        for(int i = 0; i < problems.length; i++){
            if(problems[i].getRow() == tokenRow 
            && problems[i].getColumn() == tokenColumn){
                found = true;
            }
        }
        return found;
    }
    
    /**
     * Method created for evaluation purposes to print the threat that was being evaluated,
     * was accepted, or had failed an evaluation process.
     * All calls for this method have been disabled.
     * 
     * @return A String representation of the threat.
     */    
    public String printTheThreat()
    {
        String print = "";
        for(int x = 0; x < problems.length; x++){
            String thisProblem = "[" + problems[x].getRow() + "," + problems[x].getColumn() + "]";
            print += thisProblem;
        }
        return print;
    }
}
