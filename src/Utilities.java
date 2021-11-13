import java.io.File;
import java.io.FileNotFoundException;
import java.util.LinkedList;
import java.util.Scanner;

public class Utilities {

    /**
     * Reading the XML file and converting it to an array of Strings, where each element is a line of the original XML.
     * Also used for reading of TXT file
     *
     * @param str: File path
     * @return LinkedList of Strings
     */
    public static LinkedList<String> fileReaderToLinkedList(String str) {
        LinkedList<String> ls = new LinkedList<String>();
        Scanner sc = null;
        try {
            sc = new Scanner(new File(str));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        while (sc.hasNext()) {
            ls.add(sc.nextLine());
        }
        return ls;
    }

    /**
     * Transfers a LinkedList of Strings to an array of Strings with the same length.
     * @param ls: LinkedList of Strings
     * @return String[] str: the transformed LinkedList
     */
    public static String[] linkedListToArray (LinkedList<String> ls) {
        String[] str = new String[ls.size()];
        for (int i = 0; i < ls.size(); i++) {
            str[i] = ls.get(i);
        }
        return str;
    }

    /**
     * Transforms a LinkedList of Network Nodes to an array of those nodes
     * @param ls: LinkedList of Nodes
     * @return Array of Nodes
     */
    public static NetworkNode[] linkedListToArrayNodes (LinkedList<NetworkNode> ls) {
        NetworkNode[] ret = new NetworkNode[ls.size()];
        for (int i = 0; i < ls.size(); i++) {
            ret[i] = ls.get(i);
        }
        return ret;
    }

    /**
     * Transforms an array of strings to and array of doubles (each string is a number to be parsed to double)
     * @param arr of strings
     * @return array of doubles
     */
    public static double[] stringArrToDoubleArr (String[] arr) {
        double[] ret = new double[arr.length];
        for (int i = 0; i < arr.length; i++) {
            ret[i] = Double.parseDouble(arr[i]);
        }
        return ret;
    }

    /**
     *
     * @param ls: list of Nodes
     * @param query: a String which is a name of some node
     * @return the node for which the name was given (in query)
     */
    public static NetworkNode searchNode(LinkedList<NetworkNode> ls, String query) {
        for (int i = 0; i < ls.size(); i++) {
            if (query.equals(ls.get(i).getName())) {
                return ls.get(i);
            }
        }
        return null;
    }

    /**
     * Returns whether a node exists in an array of nodes (according to the name of nodes)
     * @param arr of Nodes
     * @param node single Node
     * @return boolean value
     */
    public static boolean contains(NetworkNode[] arr, NetworkNode node) {
        String name = node.getName();
        for (int i = 0; i < arr.length; i++)
        {
            if (arr[i].getName().equals(name))
            {
                return true;
            }
        }
        return false;
    }

    public static void zeroToAllTimesVisited(LinkedList<NetworkNode> nodes) {
        for (int i = 0; i < nodes.size(); i++) {
            nodes.get(i).emptyTimesVisited();
        }
    }
}
