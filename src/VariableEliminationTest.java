import java.util.Arrays;
import java.util.LinkedList;

import static org.junit.jupiter.api.Assertions.*;

class VariableEliminationTest {

    public static final double DELTA = 0.001 * 0.001;

//    @org.junit.jupiter.api.Test
//    void resultForQuery() {
//    }
//
//    @org.junit.jupiter.api.Test
//    void removeAllIrrelevantValues() {
//    }
//
//    @org.junit.jupiter.api.Test
//    void joinAll() {
//    }
//


    private static String[][] initKeys1() {
        String[] arr1 = {"a", "b", "c", "d", "e", "f", "g", "h"};
        String[] arr2 = {"a1", "b1", "c1", "d1", "e1", "f1", "g1", "h1"};
        String[] arr3 = {"a2", "b2", "c2", "d2", "e2", "f2", "g2", "h2"};
        String[] arr4 = {"a3", "b3", "c3", "d3", "e3", "f3", "g3", "h3"};
        String[] arr5 = {"a4", "b4", "c4", "d4", "e4", "f4", "g4", "h4"};
        String[] arr6 = {"a5", "b5", "c5", "d5", "e5", "f5", "g5", "h5"};
        return new String[][]{arr1, arr2, arr3, arr4, arr5, arr6};
    }

    private static String[][] initKeys2() {
        String[] arr1 = {"i", "c", "j", "e", "k", "f", "l", "g"};
        String[] arr2 = {"i1", "c1", "j1", "e1", "k1", "f1", "l1", "g1"};
        String[] arr3 = {"i2", "c2", "j2", "e2", "k2", "f2", "l2", "g2"};
        String[] arr4 = {"i3", "c3", "j3", "e3", "k3", "f3", "l3", "g3"};
        String[] arr5 = {"i4", "c4", "j4", "e4", "k4", "f4", "l4", "g4"};
        String[] arr6 = {"i5", "c5", "j5", "e5", "k5", "f5", "l5", "g5"};
        return new String[][]{arr1, arr2, arr3, arr4, arr5, arr6};
    }

    private static LinkedList<NetworkNode> initBigNet() {
        LinkedList<NetworkNode> ret = XmlFileParse.xmlParser("src/Assignment/big_net.xml");
        return ret;
    }

    @org.junit.jupiter.api.Test
    void currValuesOfCorrelatingOrNot() {
        String[][] arr = initKeys1();
        String[] names = {"a", "e", "g"};
        int row = 3;
        String[] ret = VariableElimination.currValuesOfCorrelatingOrNot(arr, names, row);
        String[] expected = {"a3", "e3", "g3"};

        assertArrayEquals(expected, ret);
    }

    @org.junit.jupiter.api.Test
    void concatStringArrays() {
        String[] arr1 = {"a", "b", "c", "d"};
        String[] arr2 = {"a1", "a2"};
        String[] arr3 = {"a2", "b2", "c2"};
        String[] ret = VariableElimination.concatStringArrays(arr1, arr2, arr3);
        String[] expected = {"a", "b", "c", "d", "a1", "a2", "a2", "b2", "c2"};
        assertArrayEquals(expected, ret);
    }

    @org.junit.jupiter.api.Test
    void correlatingNames() {
        String[] arr1 = initKeys1()[0];
        String[] arr2 = initKeys2()[0];
        String[] expected = {"c", "e", "f", "g"};
        String[] ret = VariableElimination.correlatingNames(arr1, arr2);

        assertArrayEquals(expected, ret);
    }

    @org.junit.jupiter.api.Test
    void notCorrelating() {
        String[] arr1 = initKeys1()[0];
        String[] corr = {"a", "c", "g"};
        String[] ret = VariableElimination.notCorrelating(arr1, corr);
        String[] expected = {"b", "d", "e", "f", "h"};
        System.out.println(Arrays.toString(ret));

        assertArrayEquals(expected, ret);
    }

    @org.junit.jupiter.api.Test
    void findAllFactorsMentioning() {
        LinkedList<NetworkNode> nodes = XmlFileParse.xmlParser("src/Assignment/alarm_net.xml");
        LinkedList<NetworkNode> retE = VariableElimination.findAllFactorsMentioning(nodes, nodes.get(0));
        assertEquals(retE.size(), 2);
        assertEquals(retE.get(0).getName(), "E");
        assertEquals(retE.get(1).getName(), "A");

        LinkedList<NetworkNode> retB = VariableElimination.findAllFactorsMentioning(nodes, nodes.get(1));
        assertEquals(retB.size(), 2);
        assertEquals(retB.get(0).getName(), "B");
        assertEquals(retB.get(1).getName(), "A");

        LinkedList<NetworkNode> retA = VariableElimination.findAllFactorsMentioning(nodes, nodes.get(2));
        assertEquals(retA.size(), 3);
        assertEquals(retA.get(0).getName(), "J");
        assertEquals(retA.get(1).getName(), "M");
        assertEquals(retA.get(2).getName(), "A");
    }

    @org.junit.jupiter.api.Test
    void joinTwo() {
        LinkedList<NetworkNode> nodes = XmlFileParse.xmlParser("src/Assignment/alarm_net.xml");
        Object[] arr = VariableElimination.joinTwo(nodes.get(0).getTableKeys(), nodes.get(0).getTableValues(),
                nodes.get(2).getTableKeys(), nodes.get(2).getTableValues());
        assertArrayEquals(nodes.get(2).getTableKeys(), (String[][]) arr[0]);
        double[] arrDouble = {0.0019, 0.0001, 0.00058, 0.00142, 0.93812, 0.05988, 0.000998, 0.997002};
        assertArrayEquals(arrDouble, (double[]) arr[1], DELTA);
    }

    @org.junit.jupiter.api.Test
    void swap() {
        LinkedList<NetworkNode> nodes = initBigNet();
        LinkedList<NetworkNode> nodes2 = VariableElimination.swap(nodes, 3,6);
        assertEquals(nodes.get(3).getName(), nodes2.get(6).getName());
        assertEquals(nodes.get(6).getName(), nodes2.get(3).getName());
    }

    @org.junit.jupiter.api.Test
    void sortFactors() {
        LinkedList<NetworkNode> nodes = initBigNet();
        nodes = VariableElimination.sortFactors(nodes);
        for (int i = 1; i < nodes.size(); i++)
        {
            assertTrue(nodes.get(i-1).getTableKeys().length <= nodes.get(i).getTableKeys().length);
        }
    }
}















