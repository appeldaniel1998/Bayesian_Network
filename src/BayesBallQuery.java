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

    public NetworkNode getQueryNode1() {
        return src;
    }

    public void setQueryNode1(NetworkNode queryNode1) {
        this.src = queryNode1;
    }

    public NetworkNode getQueryNode2() {
        return dest;
    }

    public void setQueryNode2(NetworkNode queryNode2) {
        this.dest = queryNode2;
    }

    public NetworkNode[] getGivenNodes() {
        return givenNodes;
    }

    public void setGivenNodes(NetworkNode[] givenNodes) {
        this.givenNodes = givenNodes;
    }

    public String[] getGivenValues() {
        return givenValues;
    }

    public void setGivenValues(String[] givenValues) {
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
    public String resultForQuery(LinkedList<NetworkNode> nodes) {
        if (this.srcOrDestGiven()) return "yes"; //nodes conditionally independent if one of them is given
        if (this.conditionallyIndependent(nodes, ANY, this.src)) return "yes";
        else return "no";
    }

    private boolean conditionallyIndependent(LinkedList<NetworkNode> nodes, int canGoTo, NetworkNode currentNode) {
        //canGoTo == any = 0/children = 1/parents = 2
        if (currentNode.equals(this.dest)) {
            return false;
        }
        if (currentNode.equals(this.src)) {
            return true;
        }


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
