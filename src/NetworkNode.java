import java.util.ArrayList;
import java.util.LinkedList;

public class NetworkNode {
    private String name;
    private String[] outcomes;
    private NetworkNode[] parents;
    private NetworkNode[] children;
    private double[] tableValues;
    private String[][] tableKeys;
    private int timesVisited;


    public NetworkNode(String name, String[] outcomes) //string array of names of parents
    {
        this.name = name;
        this.outcomes = outcomes;
        this.parents = new NetworkNode[0];
        this.children = new NetworkNode[0];
        this.timesVisited = 0;
    }

    public String[] getOutcomes() {
        return outcomes;
    }

    public int getTimesVisited() {
        return timesVisited;
    }

    public void addTimesVisited() {
        this.timesVisited++;
    }

    public void emptyTimesVisited() {
        this.timesVisited = 0;
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

    public boolean equals(Object o) {
        if (!(o instanceof NetworkNode)) {
            return false;
        } else {
            NetworkNode n1 = (NetworkNode) o;
            return n1.getName().equals(this.name);
        }
    }

    public void scanForChildren(LinkedList<NetworkNode> nodes) {
        ArrayList<NetworkNode> childrenLst = new ArrayList<NetworkNode>();

        for (int i = 0; i < nodes.size(); i++) {
            NetworkNode[] parentsArr = nodes.get(i).getParents();
            for (int j = 0; j < parentsArr.length; j++) {
                if (parentsArr[j].getName().equals(this.name)) {
                    childrenLst.add(nodes.get(i));
                    break;
                }
            }
        }
        NetworkNode[] childrenArr = new NetworkNode[childrenLst.size()];
        for (int i = 0; i < childrenArr.length; i++) {
            childrenArr[i] = childrenLst.get(i);
        }
        this.children = childrenArr;
    }


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