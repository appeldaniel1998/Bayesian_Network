import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;

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

    public String toString() {
        return (this.name + ": \nOutcomes: " + Arrays.toString(this.outcomes) + "\n\n");
    }

    public void scanForChildren(LinkedList<NetworkNode> nodes) {
        ArrayList<NetworkNode> childrenLst = new ArrayList<NetworkNode>();

        for (int i = 0; i < nodes.size(); i++)
        {
            NetworkNode[] parentsArr = nodes.get(i).getParents();
            for (int j = 0; j < parentsArr.length; j++)
            {
                if (parentsArr[j].getName().equals(this.name))
                {
                    childrenLst.add(nodes.get(i));
                    break;
                }
            }
        }
        NetworkNode[] childrenArr = new NetworkNode[childrenLst.size()];
        for (int i = 0; i < childrenArr.length; i++)
        {
            childrenArr[i] = childrenLst.get(i);
        }
        this.children = childrenArr;
    }
}
