import java.util.LinkedList;

public interface Query {
    /**
     * @return a string representing a result for the specific query: a "yes" or "no" for BayesBall and a probability + number of operations for
     * Variable Elimination
     */
    public String resultForQuery(LinkedList<NetworkNode> nodes, String XMLFilepath);
}
