import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
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
        LinkedList<String> ls = new LinkedList<>();
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

    public static void toFile(String[] arr, String filepath)
    {
        try {
            FileWriter fw = new FileWriter(filepath);
            for (int i = 0; i < arr.length; i++)
            {
                fw.write(arr[i]+"\n");
            }
            fw.close();
        } catch (IOException e) {
            System.err.println("While writing to file, an error occurred.");
            e.printStackTrace();
        }
    }

    /**
     * Transfers a LinkedList of Strings to an array of Strings with the same length.
     *
     * @param ls: LinkedList of Strings
     * @return String[] str: the transformed LinkedList
     */
    public static String[] linkedListToArray(LinkedList<String> ls) {
        String[] str = new String[ls.size()];
        for (int i = 0; i < ls.size(); i++) {
            str[i] = ls.get(i);
        }
        return str;
    }

    /**
     * Transforms a LinkedList of Network Nodes to an array of those nodes
     *
     * @param ls: LinkedList of Nodes
     * @return Array of Nodes
     */
    public static NetworkNode[] linkedListToArrayNodes(LinkedList<NetworkNode> ls) {
        NetworkNode[] ret = new NetworkNode[ls.size()];
        for (int i = 0; i < ls.size(); i++) {
            ret[i] = ls.get(i);
        }
        return ret;
    }

    /**
     * Function to convert a linked list of arrays of strings to a 2D array of Strings
     *
     * @param lst
     * @return String[][]
     */
    public static String[][] linkedListTo2DArray(LinkedList<String[]> lst) {
        String[][] ret = new String[lst.size()][lst.get(0).length];
        for (int i = 0; i < ret.length; i++) {
            ret[i] = lst.get(i);
        }
        return ret;
    }

    /**
     * Function to convert a linked list of doubles to an array of doubles
     *
     * @param lst
     * @return double[]
     */
    public static double[] linkedListToDoubleArray(LinkedList<Double> lst) {
        double[] ret = new double[lst.size()];
        for (int i = 0; i < ret.length; i++) {
            ret[i] = lst.get(i);
        }
        return ret;
    }

    /**
     * Transforms an array of strings to and array of doubles (each string is a number to be parsed to double)
     *
     * @param arr of strings
     * @return array of doubles
     */
    public static double[] stringArrToDoubleArr(String[] arr) {
        double[] ret = new double[arr.length];
        for (int i = 0; i < arr.length; i++) {
            ret[i] = Double.parseDouble(arr[i]);
        }
        return ret;
    }

    /**
     * @param ls:    list of Nodes
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
     *
     * @param arr  of Nodes
     * @param node single Node
     * @return boolean value
     */
    public static boolean contains(NetworkNode[] arr, NetworkNode node) {
        String name = node.getName();
        for (int i = 0; i < arr.length; i++) {
            if (arr[i].getName().equals(name)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks and returns whether the String exists in an array
     *
     * @param arr of Strings
     * @param str a single String
     * @return
     */
    public static boolean contains(String[] arr, String str) {
        for (int i = 0; i < arr.length; i++) {
            if (arr[i].equals(str)) {
                return true;
            }
        }
        return false;
    }

    /**
     * The times visited field in each node in the list is set to 0 for future use
     *
     * @param nodes
     */
    public static LinkedList<NetworkNode> zeroToAllTimesVisited(LinkedList<NetworkNode> nodes) {
        for (int i = 0; i < nodes.size(); i++) {
            nodes.get(i).emptyTimesVisited();
        }
        return nodes;
    }

    public static NetworkNode[] zeroToAllTimesVisited(NetworkNode[] nodes) {
        for (int i = 0; i < nodes.length; i++) {
            nodes[i].emptyTimesVisited();
        }
        return nodes;
    }

    /**
     * Returning the index of the str String in the arr array of Strings. If such doesn't exist, return -1.
     *
     * @param arr array of Strings
     * @param str String
     * @return index of str in arr
     */
    public static int indexOf(String[] arr, String str) {
        for (int i = 0; i < arr.length; i++) {
            if (arr[i].equals(str)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Returning the index of the Node in the list of nodes. If such doesn't exist, return -1.
     *
     * @param lst list of Nodes
     * @param node Network Node
     * @return index of node in lst
     */
    public static int indexOf(LinkedList<NetworkNode> lst, NetworkNode node) {
        for (int i = 0; i < lst.size(); i++) {
            if (lst.get(i).equals(node)) return i;
        }
        return -1;
    }

    /**
     * Implementing equals for two arrays of Strings
     *
     * @param arr1 first array
     * @param arr2 second array
     * @return returns true if all the array values are identical
     */
    public static boolean equals(String[] arr1, String[] arr2) {
        if (arr1.length != arr2.length) {
            return false;
        } else {
            for (int i = 0; i < arr1.length; i++) {
                if (!arr1[i].equals(arr2[i])) {
                    return false;
                }
            }
            return true;
        }
    }

    /**
     * Function to calculate the ascii value of a String array (the sum of all elements) and return it
     *
     * @param arr array to traverse
     * @return int value
     */
    public static int asciiSize(String[] arr) {
        int sum = 0;
        for (int i = 0; i < arr.length; i++) {
            for (int j = 0; j < arr[i].length(); j++) {
                sum += (int) arr[i].charAt(j);
            }
        }
        return sum;
    }

    /**
     * Removing the element in index ind from the array and returning it.
     *
     * @param arr array of Strings
     * @param ind index to remove
     * @return corrected array
     */
    public static String[] removeElement(String[] arr, int ind) {
        String[] ret = new String[arr.length - 1];
        int retInd = 0;
        for (int i = 0; i < arr.length; i++) {
            if (i != ind) {
                ret[retInd++] = arr[i];
            }
        }
        return ret;
    }

    /**
     * Returns true if a linked list of String[] contains a String[] as passed in arr
     *
     * @param lst LinkedList<String[]>
     * @param arr String[]
     * @return corresponding boolean value
     */
    public static boolean contains(LinkedList<String[]> lst, String[] arr) {
        for (int i = 0; i < lst.size(); i++) {
            if (equals(lst.get(i), arr)) {
                return true;
            }
        }
        return false;
    }

    public static boolean contains(LinkedList<NetworkNode> lst, NetworkNode node) {
        for (int i = 0; i < lst.size(); i++) {
            if (node.equals(lst.get(i))) {
                return true;
            }
        }
        return false;
    }

    public static boolean contains(LinkedList<NetworkNode> lst, String str) {
        for (int i = 0; i < lst.size(); i++) {
            if (contains(lst.get(i).getTableKeys()[0], str)) return true;
        }
        return false;
    }

    public static LinkedList<NetworkNode> arrToLinkedListNodes(NetworkNode[] arr) {
        LinkedList<NetworkNode> ret = new LinkedList<>();
        for (int i = 0; i < arr.length; i++) {
            ret.addLast(arr[i]);
        }
        return ret;
    }
}