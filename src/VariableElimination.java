import java.util.LinkedList;

public class VariableElimination implements Query {

    private NetworkNode queryNode;
    private String queryValue;
    private NetworkNode[] givenNodes;
    private String[] givenValues;
    private NetworkNode[] readingOrder;

    public VariableElimination(NetworkNode queryNode, String queryValue, NetworkNode[] givenNodes, String[] givenValues,
                               NetworkNode[] readingOrder) {
        this.queryNode = queryNode;
        this.queryValue = queryValue;
        this.givenNodes = givenNodes;
        this.givenValues = givenValues;
        this.readingOrder = readingOrder;
    }

    public NetworkNode getQueryNode() {
        return queryNode;
    }

    public void setQueryNode(NetworkNode queryNode) {
        this.queryNode = queryNode;
    }

    public String getQueryValue() {
        return queryValue;
    }

    public void setQueryValue(String queryValue) {
        this.queryValue = queryValue;
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

    @Override
    public String resultForQuery(LinkedList<NetworkNode> nodes, String XMLFilepath) {
        nodes = XmlFileParse.xmlParser(XMLFilepath);
        nodes = siphoningNotDependantNodes(nodes);
        for (int i = 0; i < this.readingOrder.length; i++) {
            nodes = this.joinAll(nodes, readingOrder[i]);
        }
    }

    private LinkedList<NetworkNode> joinAll(LinkedList<NetworkNode> nodes, NetworkNode by) {
        LinkedList<NetworkNode> nodeFactorsToJoin = findAllFactorsMentioning(nodes, by);
        String[][] currentFactorKeys = nodeFactorsToJoin.get(0).getTableKeys();
        double[] currFactorValues = nodeFactorsToJoin.get(0).getTableValues();
        int multiplyCount = 0;
        for (int i = 1; i < nodeFactorsToJoin.size(); i++) {

        }
    }

    /**
     * Function to join 2 specific factors into one and return it (keys and values, as well as how many multiplication
     * operations were done
     *
     * @param firstFactorKeys    keys of the first factor
     * @param firstFactorValues  values of the first factor
     * @param secondFactorKeys   keys of the second factor
     * @param secondFactorValues values of the second factor
     * @return an Object array containing the factor's keys and values and the number of multiplication operations
     * (in that order)
     */
    private Object[] joinTwo(String[][] firstFactorKeys, double[] firstFactorValues, String[][] secondFactorKeys,
                             double[] secondFactorValues) {
        String[] correlatingNames = correlatingNames(firstFactorKeys[0], secondFactorKeys[0]);
        String[] onlyInFirst = notCorrelating(firstFactorKeys[0], correlatingNames);
        String[] onlyInSecond = notCorrelating(secondFactorKeys[0], correlatingNames);
        String[] header = headerBuilder(correlatingNames, onlyInFirst, onlyInSecond);
        for (int i = 0; i < firstFactorKeys.length; i++) {
            String[] currCorrelatingValues = new String[correlatingNames.length];
            for (int j = 0; j < correlatingNames.length; j++) {

            }
        }
    }

    /**
     * Constructing and returning the first row (the header) of the keys of the new factor out of 3 arrays
     * (concat the 3 arrays)
     *
     * @param common
     * @param inFirst
     * @param inSecond
     * @return String[] header
     */
    private static String[] headerBuilder(String[] common, String[] inFirst, String[] inSecond) {
        String[] ret = new String[common.length + inFirst.length + inSecond.length];
        int ind = 0;
        for (int i = 0; i < common.length; i++) {
            ret[ind++] = common[i];
        }
        for (int i = 0; i < inFirst.length; i++) {
            ret[ind++] = inFirst[i];
        }
        for (int i = 0; i < inSecond.length; i++) {
            ret[ind++] = inSecond[i];
        }
        return  ret;
    }

    /**
     * Returns an array of Strings which are unique to this factor.
     * @param factorNames      array of Strings
     * @param correlatingNames array of non-unique strings in this array
     * @return String[]
     */
    private String[] notCorrelating(String[] factorNames, String[] correlatingNames) {
        String[] ret = new String[factorNames.length - correlatingNames.length];
        int ind = 0;
        for (int i = 0; i < factorNames.length; i++) {
            if (Utilities.contains(correlatingNames, factorNames[i])) {
                ret[ind] = factorNames[i];
                ind++;
            }
        }
        return ret;
    }

    /**
     * Function to determine which of the node names given in the string arrays are the same,
     * meaning which of the node values have to be considered at the join
     *
     * @param firstFactor  array of Strings with names of nodes
     * @param secondFactor array of Strings with names of nodes
     * @return String[] containing the correlating names
     */
    private String[] correlatingNames(String[] firstFactor, String[] secondFactor) {
        LinkedList<String> lstRet = new LinkedList<>();
        for (int i = 0; i < firstFactor.length; i++) {
            for (int j = 0; j < secondFactor.length; j++) {
                if (firstFactor[i].equals(secondFactor[j])) {
                    lstRet.addLast(firstFactor[i]);
                    break;
                }
            }
        }
        return Utilities.linkedListToArray(lstRet);
    }

    /**
     * function to remove nodes conditionally independent of the query node given all evidence nodes (done using the
     * Bayes Ball algorithm)
     *
     * @param nodes: LinkedList of Network Nodes
     * @return siphoned Linked List of nodes
     */
    private LinkedList<NetworkNode> siphoningNotDependantNodes(LinkedList<NetworkNode> nodes) {
        for (int i = 0; i < nodes.size(); i++) {
            BayesBallQuery temp = new BayesBallQuery(queryNode, nodes.get(i), givenNodes, givenValues);
            boolean flagBayesBall = temp.resultForQuery(nodes, null).equals("yes");
            boolean flagNodeGiven = !Utilities.contains(givenNodes, nodes.get(i));
            boolean flagNotQuery = !queryNode.equals(nodes.get(i));
            if (flagNodeGiven && flagNotQuery && flagBayesBall) {
                nodes.remove(i);
            }
        }
        return nodes;
    }

    /**
     * Function to return a list of nodes in which the "what" node is mentioned (received as input)
     *
     * @param nodes: LinkedList of all nodes
     * @param what:  node to search
     * @return list of nodes containing the current query
     */
    private LinkedList<NetworkNode> findAllFactorsMentioning(LinkedList<NetworkNode> nodes, NetworkNode what) {
        LinkedList<NetworkNode> ret = new LinkedList<NetworkNode>();
        for (int i = 0; i < nodes.size(); i++) {
            String[] names = nodes.get(i).getTableKeys()[0];
            for (int j = 0; j < names.length; j++) {
                if (names[j].equals(what.getName())) {
                    ret.addLast(nodes.get(i));
                    break;
                }
            }
        }
        return ret;
    }
}














