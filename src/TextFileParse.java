import java.util.LinkedList;

public class TextFileParse {

    /**
     * main function of the class. The only one called from outside it.
     *
     * @param filePath path of the file to parse
     * @return array of objects representing: nodes, queries, path of XML file
     */
    public static Object[] textParse(String filePath) {
        LinkedList<String> ls = Utilities.fileReaderToLinkedList(filePath); //reading from text file to list of strings
        LinkedList<NetworkNode> nodes = XmlFileParse.xmlParser(ls.get(0)); //parsing the XML file
        initChildrenOfAllNodes(nodes); //for all nodes
        LinkedList<Query> finalQueries = new LinkedList<>();


        for (int i = 1; i < ls.size(); i++) { //for every string in the array
            String line = ls.get(i);
            String sub = line.substring(0, 2);

            if (sub.equals("P(")) { //if it starts with P( then its VE Query
                finalQueries.addLast(varEliminationParse(line, nodes));
            } else { //otherwise, its Bayes Ball Query
                finalQueries.addLast(BayesParse(line, nodes));
            }
        }

        //constructing array to return
        Object[] ret = new Object[3];
        ret[0] = nodes;
        ret[1] = finalQueries;
        ret[2] = ls.get(0);
        return ret;
    }

    /**
     * Initializing children for all nodes
     *
     * @param nodes list of all nodes
     */
    private static void initChildrenOfAllNodes(LinkedList<NetworkNode> nodes) {
        for (int i = 0; i < nodes.size(); i++) {
            nodes.get(i).scanForChildren(nodes);
        }
    }

    /**
     * Variable Elimination Query. Starts with P(. returns the constructed object of VariableElimination.
     *
     * @param line  string to parse
     * @param nodes list of nodes
     * @return VariableElimination object
     */
    private static VariableElimination varEliminationParse(String line, LinkedList<NetworkNode> nodes) {
        final int indexOpenBracket = 1; //for the sake of clarity in later uses. Unchanged throughout the code
        int indexCloseBracket = line.indexOf(')'); //find closing bracket
        String givenOutcomesAndQuery = line.substring(indexOpenBracket + 1, indexCloseBracket);
        String[] splitByDefinition = givenOutcomesAndQuery.split("\\|"); //should return an array with size 2
        String[] queryNodeAndValue = splitByDefinition[0].split("=");

        NetworkNode queryNode = Utilities.searchNode(nodes, queryNodeAndValue[0]);

        Object[] arr;
        NetworkNode[] givenNodesArr = new NetworkNode[0]; //init of array
        String[] givenValuesArr = new String[0]; //init of array
        if (splitByDefinition.length > 1) {  //else there are no evidence nodes
            arr = givenListParse(nodes, splitByDefinition[1]);
            givenNodesArr = (NetworkNode[]) arr[0];
            givenValuesArr = (String[]) arr[1];
        }


        //Reading order Parse:
        String orderOfReadingString;
        try {
            orderOfReadingString = line.substring(indexCloseBracket + 2);
        } catch (StringIndexOutOfBoundsException e) // in case there are no hidden variables to read
        {
            orderOfReadingString = "";
        }
        String readingOrderStrings[] = orderOfReadingString.split("-");
        NetworkNode[] readingOrder = new NetworkNode[readingOrderStrings.length];
        for (int j = 0; j < readingOrder.length; j++) {
            readingOrder[j] = Utilities.searchNode(nodes, readingOrderStrings[j]);
        }

        return new VariableElimination(queryNode, queryNodeAndValue[1], givenNodesArr, givenValuesArr, readingOrder); //constructing relevant object
    }

    /**
     * If not VE, then the query is Bayes Ball. Parsing accordingly. Returns a BayesBallQuery object
     *
     * @param line  String to parse
     * @param nodes list of nodes
     * @return BayesBallQuery object
     */
    private static BayesBallQuery BayesParse(String line, LinkedList<NetworkNode> nodes) {
        String[] splitByDefinition = line.split("\\|"); //splitting query and evidence

        String[] queryNodesSplit = splitByDefinition[0].split("-"); //query nodes split (the 2)

        NetworkNode queryNode1 = Utilities.searchNode(nodes, queryNodesSplit[0]); //finding 1st query node
        NetworkNode queryNode2 = Utilities.searchNode(nodes, queryNodesSplit[1]); //finding 2nd query node
        Object[] arr = new Object[0];
        try {
            arr = givenListParse(nodes, splitByDefinition[1]); //parse of list of evidence
        } catch (ArrayIndexOutOfBoundsException e) {
            arr = givenListParse(nodes, "");
        }
        //parsing of received array of objects
        NetworkNode[] givenNodesArr = (NetworkNode[]) arr[0];
        String[] givenValuesArr = (String[]) arr[1];

        return new BayesBallQuery(queryNode1, queryNode2, givenNodesArr, givenValuesArr);
    }

    /**
     * Parsing the list of evidence
     *
     * @param nodes list of nodes
     * @param line  String to parse
     * @return nodes and values arrays of evidence
     */
    private static Object[] givenListParse(LinkedList<NetworkNode> nodes, String line) {
        LinkedList<NetworkNode> givenNodes = new LinkedList<>();
        LinkedList<String> givenValues = new LinkedList<>();

        NetworkNode[] givenNodesArr = new NetworkNode[0];
        String[] givenValuesArr = new String[0];

        //splitting given pairs (node + variable), each of them splitting by "=" and adding each to the appropriate list
        //for future use and initialization of object
        if (line.length() != 0) {
            if (line.contains(",")) { //multiple pairs of nodes and values
                String[] givenPairs = line.split(",");
                for (int j = 0; j < givenPairs.length; j++) {
                    String[] temp = givenPairs[j].split("="); //array of size 2

                    NetworkNode node = Utilities.searchNode(nodes, temp[0]);
                    givenNodes.addLast(node);

                    givenValues.addLast(temp[1]);
                }
                givenNodesArr = Utilities.linkedListToArrayNodes(givenNodes); //converting to array
                givenValuesArr = Utilities.linkedListToArray(givenValues); //converting to array
            } else //only one pair of node and value
            {
                String[] temp = line.split("=");
                NetworkNode node = Utilities.searchNode(nodes, temp[0]);
                givenNodesArr = new NetworkNode[1];
                givenNodesArr[0] = node;
                givenValuesArr = new String[1];
                givenValuesArr[0] = temp[1];
            }
        }
        //constructing array to return
        Object[] ret = new Object[2];
        ret[0] = givenNodesArr;
        ret[1] = givenValuesArr;

        return ret;
    }
}