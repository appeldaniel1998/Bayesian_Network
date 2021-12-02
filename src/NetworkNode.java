import java.util.ArrayList;
import java.util.LinkedList;

public class NetworkNode {
    private String name;
    private String[] outcomes;
    private NetworkNode[] parents;
    private NetworkNode[] children;
    private double[] tableValues;
    private String[][] tableKeys;


    public NetworkNode(String name, String[] outcomes) //string array of names of parents
    {
        this.name = name;
        this.outcomes = outcomes;
        this.parents = new NetworkNode[0];
        this.children = new NetworkNode[0];
    }

    public NetworkNode(String[][] keys, double[] values) //instantiating an unimportant node with an important factor
    {
        this.name = java.util.UUID.randomUUID().toString();
        ; //a certain String that will never be a name of a real node
        this.tableKeys = keys;
        this.tableValues = values;
    }

    public String[] getOutcomes() {
        return outcomes;
    }

    public String getName() {
        return name;
    }

    public NetworkNode[] getParents() {
        return parents;
    }

    public NetworkNode[] getChildren() {
        return children;
    }

    public void setParents(NetworkNode[] parents) {
        this.parents = parents;
    }

    public double[] getTableValues() {
        return tableValues;
    }

    public String[][] getTableKeys() {
        return tableKeys;
    }

    public void setWholeTable(double[] table) {
        this.tableValues = table;
        this.initTable(table);
    }

    public void setTableKeys(String[][] arr) {
        this.tableKeys = arr;
    }

    public void setTableValues(double[] arr) {
        this.tableValues = arr;
    }

    public boolean equals(Object o) { //equals iff names are the same
        if (!(o instanceof NetworkNode)) {
            return false;
        } else {
            NetworkNode n1 = (NetworkNode) o;
            return n1.getName().equals(this.name);
        }
    }

    public String toString() {
        return this.getName();
    }

    /**
     * Setting the children array, with the assumption that all parent arrays are full and complete.
     *
     * @param nodes list of all nodes
     */
    public void scanForChildren(LinkedList<NetworkNode> nodes) { //setting the children
        ArrayList<NetworkNode> childrenLst = new ArrayList<>();

        for (int i = 0; i < nodes.size(); i++) { //for each node
            NetworkNode[] parentsArr = nodes.get(i).getParents();
            for (int j = 0; j < parentsArr.length; j++) { //if this node exists in any node as a parent, then it is a child
                if (parentsArr[j].getName().equals(this.name)) {
                    childrenLst.add(nodes.get(i));
                    break;
                }
            }
        }
        NetworkNode[] childrenArr = new NetworkNode[childrenLst.size()]; //conversion to array
        for (int i = 0; i < childrenArr.length; i++) {
            childrenArr[i] = childrenLst.get(i);
        }
        this.children = childrenArr;
    }

    /**
     * Initializing keys of array. According to parents and the node itself, it being as the last column of each table (on the right).
     *
     * @param arr
     */
    public void initTable(double[] arr) {
        NetworkNode[] columnNodes = this.addCurrNodeToParents();
        String[][] keys = new String[arr.length + 1][columnNodes.length];

        for (int i = 0; i < columnNodes.length - 1; i++) //Iterating over columns of the new 2D array
        {
            keys[0][i] = this.getParents()[i].getName();
        }
        keys[0][columnNodes.length - 1] = this.name;

        int totalOutcomesUpToNow = 1;
        for (int i = 0; i < columnNodes.length; i++) //Iterating over columns of the new 2D array
        {
            totalOutcomesUpToNow *= columnNodes[i].getOutcomes().length;
            int timesOfEachOutput = arr.length / totalOutcomesUpToNow;
            int indexInArr = 1;
            while (indexInArr < arr.length) {
                for (int j = 0; j < columnNodes[i].getOutcomes().length; j++) { //Iterating over different outcomes

                    for (int k = 1; k <= timesOfEachOutput; k++) { //Iterating over number of times each outcome has to be added
                        keys[indexInArr][i] = columnNodes[i].getOutcomes()[j];
                        indexInArr++;
                    }

                }
            }
        }
        this.tableKeys = keys;
    }

    private NetworkNode[] addCurrNodeToParents() { //helper function for initTable()
        NetworkNode[] columnNodes = new NetworkNode[this.parents.length + 1];
        for (int i = 0; i < columnNodes.length - 1; i++) {
            columnNodes[i] = this.parents[i];
        }
        columnNodes[columnNodes.length - 1] = this;
        return columnNodes;
    }
}