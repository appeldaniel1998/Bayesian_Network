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
}
