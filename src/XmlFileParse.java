import java.util.LinkedList;

public class XmlFileParse {

    /**
     * Main function of class and one of two public ones, parses the XML file.
     *
     * @param filePath: Relative path of file to be parsed (XML)
     * @return parsed list of Network Nodes
     */
    public static LinkedList<NetworkNode> xmlParser(String filePath) {

        LinkedList<String> ls = Utilities.fileReaderToLinkedList(filePath);
        LinkedList<NetworkNode> nodes = new LinkedList<>();
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

        for (int i = firstIndex + 1; i < lastIndex - 1; i++) { // Iterating over the lines (some iteration over them happens
            // inside the loop
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

    /**
     *
     * @param allLines list of Strings representing the XML file
     * @param firstIndex index to start the parsing from (first occurrence of <DEFINITION> tag
     * @param nodes list of nodes
     * @return a completed LinkedList of Network Nodes.
     */
    private static LinkedList<NetworkNode> DefinitionHandling(LinkedList<String> allLines, int firstIndex,
                                                              LinkedList<NetworkNode> nodes) {

        String queryStr = ""; // to be the name of the node
        NetworkNode queryNode = null; //init of the node to be worked on
        LinkedList<NetworkNode> givenNodes = new LinkedList<NetworkNode>(); //init of parents LinkedList of nodes
        double[] arrDou = new double[0]; //init of array to be table of the node

        for (int i = firstIndex; i < allLines.size(); i++) { //iterating over the lines. Some iteration happens inside the loop.
            if (allLines.get(i).equals("<DEFINITION>")) { //beginning of a block
                i++; // was over this line
                while (!allLines.get(i).equals("</DEFINITION>")) { //while not at end of block
                    String line = allLines.get(i);
                    if (line.substring(0, 6).equals("\t<FOR>")) {
                        queryStr = line.substring(6, line.length() - 6);
                        queryNode = Utilities.searchNode(nodes, queryStr);
                    }
                    if (line.substring(0, 8).equals("\t<GIVEN>")) { // found name of a parent node
                        queryStr = line.substring(8, line.length() - 8);  //setting it in a variable
                        NetworkNode temp = Utilities.searchNode(nodes, queryStr); // finding the node corresponding to the name.
                        givenNodes.addLast(temp); //adding node to list of parent nodes
                    }
                    if (line.substring(0, 8).equals("\t<TABLE>")) { //found table
                        String temp = line.substring(8, line.length() - 8);
                        String[] arrStr = temp.split(" "); //values seperated by space. putting in a String array
                        arrDou = Utilities.stringArrToDoubleArr(arrStr); //converting to a double array
                    }
                    i++;
                }

                NetworkNode[] parents = new NetworkNode[0]; //init of parents' array
                parents = Utilities.linkedListToArrayNodes(givenNodes); //converting list to array of nodes
                givenNodes = new LinkedList<NetworkNode>(); //emptying list
                queryNode.setParents(parents);
                queryNode.setWholeTable(arrDou); //setting the table to the instance of NetworkNode
            }
        }
        return nodes;
    }
}