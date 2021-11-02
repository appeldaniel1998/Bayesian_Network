public class NetworkNode {
    private String name;
    private String[] outcomes;
    private NetworkNode[] parents;
    private NetworkNode[] children;
    double[] table;


    public NetworkNode(String name, String[] outcomes) //string array of names of parents
    {
        this.name = name;
        this.outcomes = outcomes;
        this.parents = new NetworkNode[0];
        this.children = new NetworkNode[0];
    }

    public String getName() {
        return name;
    }

    public NetworkNode[] getParents() {
        return parents;
    }

    public void setParents(NetworkNode[] parents) {
        this.parents = parents;
    }

    public double[] getTable() {
        return table;
    }

    public void setTable(double[] table) {
        this.table = table;
    }
}
