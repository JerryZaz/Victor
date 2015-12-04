import java.util.ArrayList;
/**
 * Write a description of class Position here.
 * 
 * @author Henry (me@hnry.us)
 * @version (a version number or a date)
 */
public class Position
{
    private boolean active;

    private Matrix node;
    private int stage;

    private int father;
    private ArrayList<Position> fathers;
    private ArrayList<Position> sons;

    private boolean positionWonByA;
    private boolean positionWonByB;
    private boolean positionDrawn;

    private boolean chanceCalculated;
    private double chanceOfA;
    private double chanceOfB;
    private double chanceOfD;

    /**
     * First Father
     */
    public Position(Matrix me, int stage)
    {
        active = true;
        
        this.stage = stage;        
        node = me;
        
        this.father = father;
        fathers = new ArrayList<Position>();
        sons = new ArrayList<Position>();
    
        positionWonByA = false;
        positionWonByB = false;
        positionDrawn = false;
        
        chanceCalculated = false;
        chanceOfA = 0.0;
        chanceOfB = 0.0;
        chanceOfD = 0.0;
    }
    
    public void addFather(Position father)
    {
        fathers.add(father);
    }
    
    public ArrayList<Position> getFathers()
    {
        return fathers;
    }

    public void addSon(Position son)
    {
        sons.add(son);
    }

    public ArrayList<Position> getSons()
    {
        return sons;
    }

    public void setChances(char resultTag)
    {
        if(resultTag == Matrix.MOVEDFIRST){
            chanceOfA = 1.0;
            chanceOfB = 0.0;
            chanceOfD = 0.0;
        }
        else if(resultTag == Matrix.MOVEDSECOND){
            chanceOfA = 0.0;
            chanceOfB = 1.0;
            chanceOfD = 0.0;
        }
        else if(resultTag == Matrix.DRAW){
            chanceOfA = 0.0;
            chanceOfB = 0.0;
            chanceOfD = 0.0;
        }
        chanceCalculated = true;
    }

    public void setChances(double[] chances)
    {
        chanceOfA = chances[0];
        chanceOfB = chances[1];
        chanceOfD = chances[2];
        chanceCalculated = true;
    }

    public char getResult()
    {
        if(positionWonByA == true){
            return Matrix.MOVEDFIRST;
        }
        else if(positionWonByB == true){
            return Matrix.MOVEDSECOND;
        }
        else{
            return Matrix.DRAW;
        }
    }

    /**
     * If the game has ended, this class will put a result mark to
     * this position.
     */
    public void setResult(char resultTag)
    {
        if(resultTag == Matrix.MOVEDFIRST){
            positionWonByA = true;
        }
        else if(resultTag == Matrix.MOVEDSECOND){
            positionWonByA = true;
        }
        else if(resultTag == Matrix.DRAW){
            positionDrawn = true;
        }
    }

    public boolean hasEnded()
    {
        boolean noMoreMoves = false;
        if(positionWonByA == true 
        || positionWonByB == true 
        || positionDrawn == true){
            noMoreMoves = true;
        }
        return noMoreMoves;
    }

    public double getChanceOfA()
    {
        return chanceOfA;
    }

    public double getChanceOfB()
    {
        return chanceOfB;
    }

    public double getChanceOfD()
    {
        return chanceOfD;
    }

    public boolean isActive()
    {
        return active;
    }

    public void deactivate()
    {
        active = false;
        
        if(sons.size() > 0){
            for(int x = 0; x < sons.size(); x++){
                Position son = sons.get(x);
                if(son.isActive()){
                    son.deactivate();
                }
            }
        }
    }

    public void reactivate()
    {
        active = true;
    }

    public Matrix getNode()
    {
        return node;
    }

    public int getFatherIndex()
    {
        return father;
    }

    public int getStage()
    {
        return stage;
    }

    public boolean hasChanceCalculated()
    {
        return chanceCalculated;
    }

    public void printPosition()
    {
        node.printTheBoard();
    }

}
