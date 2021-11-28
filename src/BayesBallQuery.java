import java.util.HashMap;
import java.util.LinkedList;

public class BayesBallQuery implements Query {
    public static final int ANY = 0, CHILDREN = 1, PARENTS = 2;
    private NetworkNode src; //src
    private NetworkNode dest; //dest
    private NetworkNode[] givenNodes;
    private String[] givenValues; //same length as givenNodes[]. Corresponds to values given as input in node


    public BayesBallQuery(NetworkNode node1, NetworkNode node2, NetworkNode[] givenNodes, String[] givenValues) {
        this.src = node1;
        this.dest = node2;
        this.givenNodes = givenNodes;
        this.givenValues = givenValues;
    }
    /**
     * Func will be done recursively:
     * Stop sings and return a specific value:
     * - the target node was reached
     * - the algorithm was "stuck" with no other previously unvisited nodes which it can visit.
     * - the algorithm returned to its starting point
     * - if either the src or target nodes are coloured, they are conditionally independent
     * <p>
     * <p>
     * Algorithm:
     * For every node accessed, we want to go to all other parent and child nodes. We check if it is possible, then run the
     * algorithm recursively on all nodes it has accessed.
     * Start from src node. we want to try and go to all parents and children.
     * - If we want to go to a parent node:
     * ____- check if it is coloured.
     * ________If coloured, does not go to that node (no available moves).
     * ________If not coloured, can go to any parent or child, including itself
     * - If we want to go to a child node:
     * ____- check if it is coloured.
     * ________If coloured, can only go to parents (including itself)
     * ________If not coloured, can go to any child
     */
    @Override
    public String resultForQuery(LinkedList<NetworkNode> nodes, String XMLFilepath) {
        //answers the question: are the nodes conditionally independent?

        HashMap<String, Integer> timesVisited = new HashMap<>();
        for (int i = 0; i < nodes.size(); i++) {
            timesVisited.put(nodes.get(i).getName(), 0);
        }
        if (this.srcOrDestGiven()) return "yes"; //nodes conditionally independent if one of them is given
        if (this.conditionallyIndependent(nodes, ANY, this.src, true, timesVisited)) return "yes";
        else return "no";
    }

    private boolean conditionallyIndependent(LinkedList<NetworkNode> nodes, int canGoTo, NetworkNode currentNode,
                                             boolean isFirstIter, HashMap<String, Integer> timesVisited) {
        // returns true if the nodes are independent: a path was not found
        // returns false otherwise: (path was found)

        //canGoTo == any = 0/children = 1/parents = 2
        if (currentNode.equals(this.dest)) { //stop condition - path was found
            return false;
        }

        if (currentNode.equals(this.src) && !isFirstIter) { //stop condition - a path is currently not found
            return true;
        }

        if (timesVisited.get(currentNode.getName()) >= 2) { //stop condition - a path is currently not found
            return true;
        }
        timesVisited.put(currentNode.getName(), timesVisited.get(currentNode.getName()) + 1);

        if (canGoTo == ANY) {
            if (!this.goToParents(nodes, currentNode, timesVisited) || !this.goToChildren(nodes, currentNode, timesVisited)) return false;
            else return true;
        }
        if (canGoTo == CHILDREN) {
            return this.goToChildren(nodes, currentNode, timesVisited);
        } else //canGoTo == PARENTS
        {
            return this.goToParents(nodes, currentNode, timesVisited);
        }
    }

    private boolean goToParents(LinkedList<NetworkNode> nodes, NetworkNode currentNode, HashMap<String, Integer> timesVisited) {
        NetworkNode[] parents = currentNode.getParents();
        if (parents.length == 0) return true;
        boolean ret = true;
        for (int i = 0; i < parents.length; i++) {
            if (Utilities.contains(this.givenNodes, parents[i])) //The parent node is coloured
            {
                continue;
            } else {
                ret = this.conditionallyIndependent(nodes, ANY, parents[i], false, timesVisited);
            }
            if (!ret) break;
        }
        return ret;
    }

    private boolean goToChildren(LinkedList<NetworkNode> nodes, NetworkNode currentNode, HashMap<String, Integer> timesVisited) {
        NetworkNode[] children = currentNode.getChildren();
        if (children.length == 0) return true;
        boolean ret = true;
        for (int i = 0; i < children.length; i++) {
            if (Utilities.contains(this.givenNodes, children[i])) //The parent node is coloured
            {
                ret = this.conditionallyIndependent(nodes, PARENTS, children[i], false, timesVisited);
            } else {
                ret = this.conditionallyIndependent(nodes, CHILDREN, children[i], false, timesVisited);
            }
            if (!ret) break;
        }
        return ret;
    }

    private boolean srcOrDestGiven() {
        if (Utilities.contains(this.givenNodes, this.src)) {
            return true;
        }
        if (Utilities.contains(this.givenNodes, this.dest)) {
            return true;
        }
        return false;
    }
}
