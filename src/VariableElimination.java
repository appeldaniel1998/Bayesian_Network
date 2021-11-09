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
    public String resultForQuery(LinkedList<NetworkNode> nodes) { //TODO
        return null;
    }
}
