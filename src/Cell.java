import java.util.HashSet;
import java.util.Set;

public class Cell {

    private int value;
    private boolean fixed;
    private Set<Integer> candidates;

    public Cell(int value, boolean fixed){
        this.value = value;
        this.fixed = fixed;
        this.candidates = new HashSet<>();
    }

    public int getValue(){ return value; }
    public void setValue(int value){ this.value = value; }
    public boolean isFixed(){return fixed; }
    public Set<Integer> getCandidates(){ return candidates; }
    public void setFixed(boolean fixed){ this.fixed = fixed; }
}
