import java.util.LinkedList;

public class Transformations {

    /**
     * Transfers a LinkedList of Strings to an array of Strings with the same length.
     * @param ls
     * @return String[] str: the transformed LinkedList
     */
    public static String[] linkedListToArray (LinkedList<String> ls) {
        String[] str = new String[ls.size()];
        for (int i = 0; i < ls.size(); i++) {
            str[i] = ls.get(i);
        }
        return str;
    }

    public static double[] stringArrtoDoubleArr (String[] arr) {
        double[] ret = new double[arr.length];
        for (int i = 0; i < arr.length; i++) {
            ret[i] = Double.parseDouble(arr[i]);
        }
        return ret;
    }

    public static NetworkNode[] strToNetNode(LinkedList<String> str, LinkedList<NetworkNode> ls) {
        //Method to transform strings of names of network nodes to network nodes.
        NetworkNode[] ret = new NetworkNode[str.size()];
        for (int i = 0; i < str.size(); i++) {
            for (int j = 0; j < ls.size(); j++) {
                if (str.get(i).equals(ls.get(j).getName())) {
                    ret[i] = ls.get(j);
                    break;
                }
            }
        }
        return ret;
    }
}
