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

    public NetworkNode getQuerySrc() {
        return src;
    }

    public void setQuerySrc(NetworkNode queryNode1) {
        this.src = queryNode1;
    }

    public NetworkNode getQueryDest() {
        return dest;
    }

    public void setQueryDest(NetworkNode queryNode2) {
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
        //answer the question: are the nodes conditionally independent?
        if (this.srcOrDestGiven()) return "yes"; //nodes conditionally independent if one of them is given
        if (this.conditionallyIndependent(nodes, ANY, this.src)) return "yes";
        else return "no";
    }

    private boolean conditionallyIndependent(LinkedList<NetworkNode> nodes, int canGoTo, NetworkNode currentNode) {
        // returns true if the nodes are independent: a path was not found
        // returns false otherwise: (path was found)

        //canGoTo == any = 0/children = 1/parents = 2
        if (currentNode.equals(this.dest)) { //stop condition - path was found
            return false;
        }
//        if (currentNode.equals(this.src)) { //stop condition - a path is currently not found        returns true every time because accesses the loop on
//                                                                                                      first iteration (when it shouldn't)
//            return true;
//        }

        if (canGoTo == ANY)
        {
            if (!this.goToParents(nodes, currentNode) || !this.goToChildren(nodes, currentNode)) return false;
            else return true;
        }
        if (canGoTo == CHILDREN)
        {
            return this.goToChildren(nodes, currentNode);
        }
        else //canGoTo == PARENTS
        {
            return this.goToParents(nodes, currentNode);
        }


    }

    private boolean goToParents(LinkedList<NetworkNode> nodes, NetworkNode currentNode)
    {
        NetworkNode[] parents = currentNode.getParents();
        if (parents.length == 0) return true;
        for (int i = 0; i < parents.length; i++)
        {
            if (Utilities.contains(this.givenNodes, parents[i])) //The parent node is coloured
            {
                continue;
            }
            else
            {
                return this.conditionallyIndependent(nodes, ANY, parents[i]);
            }
        }
        return true;
    }

    private boolean goToChildren(LinkedList<NetworkNode> nodes, NetworkNode currentNode)
    {
        NetworkNode[] children = currentNode.getChildren();
        if (children.length == 0) return true;
        for (int i = 0; i < children.length; i++)
        {
            if (Utilities.contains(this.givenNodes, children[i])) //The parent node is coloured
            {
                return this.conditionallyIndependent(nodes, PARENTS, children[i]);
            }
            else
            {
                return this.conditionallyIndependent(nodes, CHILDREN, children[i]);
            }
        }
        return true;
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
