public class BayesBallQuery implements Query {
    private NetworkNode queryNode1;
    private NetworkNode queryNode2;
    private NetworkNode[] givenNodes;
    private String[] givenValues; //same length as givenNodes[]. Corresponds to values given as input in node

    public BayesBallQuery(NetworkNode node1, NetworkNode node2, NetworkNode[] givenNodes, String[] givenValues)
    {
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
}
