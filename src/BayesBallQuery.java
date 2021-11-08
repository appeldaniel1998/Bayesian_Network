public class BayesBallQuery implements Query {
    public static final int FROMCHILD = 0, FROMPARENT = 1;
    private NetworkNode queryNode1; //src
    private NetworkNode queryNode2; //dest
    private NetworkNode[] givenNodes;
    private String[] givenValues; //same length as givenNodes[]. Corresponds to values given as input in node

    public BayesBallQuery(NetworkNode node1, NetworkNode node2, NetworkNode[] givenNodes, String[] givenValues) {
        this.queryNode1 = node1;
        this.queryNode2 = node2;
        this.givenNodes = givenNodes;
        this.givenValues = givenValues;
    }

    public NetworkNode getQueryNode1() {
        return queryNode1;
    }

    public void setQueryNode1(NetworkNode queryNode1) {
        this.queryNode1 = queryNode1;
    }

    public NetworkNode getQueryNode2() {
        return queryNode2;
    }

    public void setQueryNode2(NetworkNode queryNode2) {
        this.queryNode2 = queryNode2;
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
     * Allowed moves:
     * - move to child:
     * - if node is coloured, WE CANNOT
     * - if node is not coloured,
     */
    @Override
    public String resultForQuery() {
        if (this.srcOrDestGiven()) return "yes";

    }

    private boolean conditionallyIndependent(NetworkNode currentNode) {
    }


    private boolean srcOrDestGiven() {
        if (Utilities.contains(this.givenNodes, this.queryNode1)) {
            return true;
        }
        if (Utilities.contains(this.givenNodes, this.queryNode2)) {
            return true;
        }
        return false;
    }
}
