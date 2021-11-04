import java.io.File;
import java.io.FileNotFoundException;
import java.util.LinkedList;
import java.util.Scanner;

public class FileRead {
    public static void main(String[] args) {
        LinkedList<NetworkNode> ls = xmlParser("src/Assignment/big_net.xml");
    }

    /**
     * Main function of class and the only public one
     *
     * @param filePath: Relative path of file to be parsed (XML)
     * @return parsed list of Nodes of the Network
     */
    public static LinkedList<NetworkNode> xmlParser(String filePath) {

        LinkedList<String> ls = fileReaderToLinkedList(filePath);
        LinkedList<NetworkNode> nodes = new LinkedList<NetworkNode>();
        int i;
        int startIndexVars = 0;
        //finding index of line with the first <VARIABLE> tag (should set startIndexVars to 1 if XML is as expected)
        for (i = 0; i < ls.size(); i++) {
            if (ls.get(i).equals("<VARIABLE>")) {
                startIndexVars = i;
                break;
            }
        }
        //finding index of line with first <DEFINITION> tag, calling the 2 needed functions
        for (; i < ls.size(); i++) {
            if (ls.get(i).equals("<DEFINITION>")) {
                nodes = VariableHandling(ls, startIndexVars, i);
                nodes = DefinitionHandling(ls, i, nodes); //the list is completed
                break;
            }
        }
        return nodes;
    }

    /**
     * Reading the XML file and converting it to an array of Strings, where each element is a line of the original XML
     *
     * @param str
     * @return LinkedList of Strings
     */
    private static LinkedList<String> fileReaderToLinkedList(String str) {
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
     * @param allLines   Lines of the XML file as strings
     * @param firstIndex index of first appearance of <VARIABLE> tag
     * @param lastIndex  index of first appearance of <DEFINITION> tag - up to where the function is iterating
     * @return a partially completed LinkedList of Network Nodes. TO BE DONE add the remaining fields in each instance of object
     */
    private static LinkedList<NetworkNode> VariableHandling(LinkedList<String> allLines, int firstIndex, int lastIndex) {
        // first line passed is the line with the first <VARIABLE> tag

        String name = "";
        LinkedList<String> outcomes = new LinkedList<String>();

        LinkedList<NetworkNode> nodes = new LinkedList<NetworkNode>();

        for (int i = firstIndex + 1; i < lastIndex - 1; i++) {
            while (!allLines.get(i).equals("</VARIABLE>")) { // while the closing tag wasn't seen
                String line = allLines.get(i);
                int tempLength = line.length();
                if (tempLength != 0) {
                    if (line.substring(0, 7).equals("\t<NAME>")) { // NAME tag appears
                        name = line.substring(7, tempLength - 7);
                    }
                    if (line.substring(0, 10).equals("\t<OUTCOME>")) { // OUTCOME tag appears
                        outcomes.add(line.substring(10, tempLength - 10));
                    }
                }
                i++;
            }

            String[] temp = new String[0]; //initializing array to refrain from bugs of possible non-existence of array
            temp = Utilities.linkedListToArray(outcomes); //converting LinkedList to array of same type
            nodes.addLast(new NetworkNode(name, temp));
            outcomes = new LinkedList<String>(); //renewing the list, to start afresh at the beginning of next iteration
        }
        return nodes;
    }


    private static LinkedList<NetworkNode> DefinitionHandling(LinkedList<String> allLines, int firstIndex,
                                                              LinkedList<NetworkNode> nodes) {

        String queryStr = "";
        NetworkNode queryNode = null;
        LinkedList<NetworkNode> givenNodes = new LinkedList<NetworkNode>();
        double[] arrDou = new double[0];

        for (int i = firstIndex; i < allLines.size(); i++) {
            if (allLines.get(i).equals("<DEFINITION>")) {
                i++;
                while (!allLines.get(i).equals("</DEFINITION>")) {
                    String line = allLines.get(i);
                    if (line.substring(0, 6).equals("\t<FOR>")) {
                        queryStr = line.substring(6, line.length() - 6);
                        queryNode = Utilities.searchNode(nodes, queryStr);
                    }
                    if (line.substring(0, 8).equals("\t<GIVEN>")) {
                        queryStr = line.substring(8, line.length() - 8);
                        NetworkNode temp = Utilities.searchNode(nodes, queryStr);
                        givenNodes.addLast(temp);
                    }
                    if (line.substring(0, 8).equals("\t<TABLE>")) {
                        String temp = line.substring(8, line.length() - 8);
                        String[] arrStr = temp.split(" ");
                        arrDou = Utilities.stringArrToDoubleArr(arrStr);
                    }
                    i++;
                }
                queryNode.setTable(arrDou); //setting the table to the instance of NetworkNode


                NetworkNode[] parents = new NetworkNode[0];
                parents = Utilities.linkedListToArrayNodes(givenNodes);
                givenNodes = new LinkedList<NetworkNode>();
                queryNode.setParents(parents);
            }
        }
        return nodes;
    }
}